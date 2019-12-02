package JxtaService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import Toolkit.GlobalConstAndTag;
import Windows.MainWindow;

/*
 * 本地节点Peer的Server服务的主线程部分
 * 主要向Jxta网络内的其他Peer提供本地的资源进行共享
 * 包括本地资源的广告发布，应答连接请求两种功能
 * 
 * ===================================================
 * 
 * 本系统约定：
 * 本地节点发布本地资源时，必须要确定该资源的管道广告PipeAdv
 * 其中管道广告的<Name>属性字段的值为:
 * 		<Name> = PublisherName:ResName
 * 亦即
 *      <Name> = UserName:ResName
 *      
 * ===================================================
 * 
 * 核心API:
 * void publish(Advertisement adv, long lifetime, long expiration)
 * 	发布本地广告adv，并保存到本地缓存。 该广告的生存期为lifetime，并在expiration后失效，然后自动在本地缓存删除该广告。
 * @Parameters:
 * 	adv - 发布的广告。
 * 	lifetime - 该广告的生存期，单位ms。
 * 	expiration - 该广告的死亡期，单位ms。 
 * 
 */
public class JxtaServer extends Thread {
	
	//Jxta网络中的对等组
	private PeerGroup netPeerGroup = null;
	
	//对等节点Peer的发现服务
	private DiscoveryService discoveryService = null;

	//管道广告Adv的<Name>属性值
	private String AdvName;
	
	private long ResByteSize;	//被共享资源的大小（字节数）
	private String ResPath;		//被共享资源在本地的绝对位置
	private String ResName;		//被共享资源的资源名称
	private long ShareTime;		//被资源文件的最大共享时限
	
	public JxtaServer(String advName, long fileSize, String resPath) {
		this.AdvName = advName;
		this.ResName = advName.substring(advName.lastIndexOf(":")+1);
		this.ResByteSize = fileSize;
		this.ResPath = resPath;
		this.ShareTime = GlobalConstAndTag.shareTime;
		
		this.netPeerGroup = JxtaPlatform.netPeerGroup;			//获取默认对等组
		this.discoveryService = JxtaPlatform.discoveryService;	//获取默认对等组的发现服务
	}

	//运行JxtaServer服务线程
	public void run() {
		startServer();
	}
	
	//启动Peer的Server功能，选择共享资源并等待连接请求
	private void startServer() {
		//为待发布的资源创建一个管道广告
		PipeAdvertisement pipeAdv = creatPipeAdvertisement();
		
		try {
			//在接下来ShareTime时间内把这个广告发布到本地，等待其他Peer搜索发现
			//在ShareTime后自动删除这个广告
			discoveryService.publish(pipeAdv, ShareTime, ShareTime);
			
			//更新本地共享资源表单，获取此时本地共享资源数
			int ResNum = RefreshLocalResTable();
			MainWindow.SysMsgAdv.MsgAdv("\n成功共享新资源 " + ResName + " 。");
			MainWindow.SysMsgAdv.MsgAdv("\n当前已共享 " + ResNum + " 个资源。");
			
			//创建关于pipeAdv的服务套接字，接受所有申请下载这个资源的Client的连接请求
			JxtaServerSocket jxtaServerSocket = new JxtaServerSocket(netPeerGroup, pipeAdv);
			
			//设置Client接入Server的超时时限为永不超时
			jxtaServerSocket.setSoTimeout(0);
			
			//循环等待其他Peer的Client端连接
			while(true) {
				//在与Client成功连接前，mainSocket将一直保持阻塞
				Socket mainSocket = jxtaServerSocket.accept();
				
				//每成功连接一个Client，Server自动为其分配一个线程以支持数据传输服务
				if(mainSocket != null) {
					MainWindow.SysMsgAdv.MsgAdv("\n资源 " + ResName + " 被请求下载。已接受请求。");
					new Thread(new JxtaServerForSend(mainSocket, ResName, ResByteSize, ResPath)).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//从本地获取所有已共享的资源广告，更新本地共享资源表单
	//返回值为当前本地所有共享的资源数
	public static int RefreshLocalResTable() {
		List<String> LocalPipeAdvNameList = new ArrayList<String>();	//记录所有本地管道广告的<Name>属性的值的列表
		int AdvCount = 0;			//广告计数器，记录本次从本地获取的管道广告数，亦即为列表长度
		
		try {
			//搜索本地所有具有<Name>属性的广告，但<Name>的值不限制，则亦即为搜索本地所有共享的资源的广告
			//由于管道广告一定有<Name>属性，其他广告未必有<Name>属性，因此这是为了搜索本地管道广告的一种局部筛选方法
			Enumeration<Advertisement> AdvEnum = JxtaPlatform.discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", null);
			
			//枚举并提取所有管道广告<Name>属性中的ResName存储到LocalPipeAdvNameList
			if(AdvEnum != null) {
				while(AdvEnum.hasMoreElements()) {
					String advName = ((PipeAdvertisement) AdvEnum.nextElement()).getName();	//提取管道广告<Name>的值
					String ResName = advName.substring(advName.lastIndexOf(":")+1);			//截取<Name>中的ResName
					LocalPipeAdvNameList.add(ResName);						//把ResName添加到LocalPipeAdvNameList
					
					AdvCount++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//重设表单各行的值为广告内容
		for(int i = 0; i < AdvCount; i++) {
			String ResName = LocalPipeAdvNameList.get(i);			//获取本地资源Name
			Vector<String> OldTableRow = SetTableRow(i+1, ResName);	//设置表单第i+1行的内容
			MainWindow.LocalResData.set(i, OldTableRow);			//把设置的内容写入表单
		}
		
		//重设表单剩余行的值为空
		for(int i = AdvCount; i < GlobalConstAndTag.TableLength; i++) {
			Vector<String> RestTableRow = SetTableRow(i+1, "");
			MainWindow.LocalResData.set(i, RestTableRow);
		}
		
		//重绘本地共享资源表单
		MainWindow.LocalResTable.repaint();
		return AdvCount;
	}
	
	//设置本地共享资源表单第Row行的内容
	public static Vector<String> SetTableRow(int Row, String ResName) {
		Vector<String> tmp = new Vector<String>();
		tmp.add(0, String.valueOf(Row));
		tmp.add(1, ResName);
		return tmp;
	}
		
	//创建并返回管道广告
	private PipeAdvertisement creatPipeAdvertisement() {
		//创建管道广告PipeAdv
		PipeAdvertisement PipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		
		//创建随机管道ID
		PipeID pipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID);
		
		PipeAdv.setPipeID(pipeID);					//设置管道广告的管道ID
		PipeAdv.setType(PipeService.UnicastType);	//设置管道类型为单播管道
		PipeAdv.setName(AdvName);					//设置管道<Name>属性的值
		return PipeAdv;
	}
}
