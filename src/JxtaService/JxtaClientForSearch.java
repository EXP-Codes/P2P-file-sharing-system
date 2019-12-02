package JxtaService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import Toolkit.GlobalConstAndTag;
import Windows.MainWindow;

/*
 * 本地节点Peer的Client服务
 * 主要在Jxta网络内的搜索其他Peer共享的资源
 * 
 * =======================================================
 * 
 * 核心API: 
 * int getRemoteAdvertisements(String peerid, int type, String attribute, String value, int threshold)
 * 	发现远程广告，当匹配成功后将广告保存到本地。
 * 	发现广告的方式为"尽最大努力交付"方式。
 * 	此方法在搜索时，每有一个响应该搜索请求的节点，则留下一个线程进行处理。
 * @Parameters:
 * 	peerid - 指定获取远程广告的节点。null表示对等网络上所有节点。
 * 	type - 指定获取的广告类型。节点广告PEER，节点组广告GROUP，其他广告ADV(包括管道广告)。
 * 	attribute - 属性。以此作为搜索xml广告的依据。管道广告的常用属性有"ID"，"Type"和"Name"。
 * 	value - 属性值。如属性为"Name"，值为"P2P"，则搜索Name为P2P的广告。可在搜索文本前后添加通配符"*"进行模糊搜索。
 * 	threshold - 允许响应搜索的每个节点返回的最多广告数。注意是每个节点最多返回threshold条广告，而不是所有节点一共最多返回threshold条广告。
 * 
 */
public class JxtaClientForSearch extends Thread implements DiscoveryListener {
	
	//对等节点Peer的发现服务
	private DiscoveryService discoveryService = null;
	
	//为搜索资源所输入的文本
	private String SearchText;
	
	//标记是否发现了资源
	private boolean isDiscoveryRes = false;
	
	//管道广告列表，记录本次搜索到的所有广告
	private List<PipeAdvertisement> SearchPipeAdvList;
			
	//广告计数器，记录本次搜索共获得的广告数
	private int AdvCount = 0;
	
	public JxtaClientForSearch(String searchText) {
		
		this.SearchText = searchText;
		
		SearchPipeAdvList = new ArrayList<PipeAdvertisement>();
				
		this.discoveryService = JxtaPlatform.discoveryService;	//获取默认对等组的发现服务
		discoveryService.addDiscoveryListener(this);			//为发现服务配置监听
	}
	
	//运行JxtaClient服务线程，搜索网络资源
	public void run() {
		SearchNetRes();
	}
	
	//开始搜索网络资源
	public void SearchNetRes() {
		//搜索起始时间
		long startTime = System.currentTimeMillis();
		
		//以"尽最大努力交付"方式发现所需匹配的远程管道广告
		discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", SearchText, GlobalConstAndTag.MaxPeerAdvNum);
			
		try {
			//等待searchTime，使得尽可能多的节点回复搜索请求
			Thread.sleep(GlobalConstAndTag.searchTime);

			long endTime = System.currentTimeMillis();	//搜索结束时间
			long consumeTime = endTime - startTime;		//搜索耗时
			
			//限时searchTime内没有任何节点响应搜索请求
			if(!isDiscoveryRes) {
				MainWindow.SysMsgAdv.MsgAdv("\n找不到任何资源！可能当前网络内无其他用户共享资源。");
				MainWindow.SysMsgAdv.MsgAdv("\n本次搜索耗时: " + consumeTime + " ms。");
				JOptionPane.showMessageDialog(null, "找不到相关资源！", "搜索资源失败", JOptionPane.ERROR_MESSAGE);
			}
			else {
				MainWindow.SysMsgAdv.MsgAdv("\n成功搜索到 " + AdvCount + " 个资源！");
				MainWindow.SysMsgAdv.MsgAdv("\n本次搜索耗时: " + consumeTime + " ms。");
			}
			
			//根据本次搜索到的资源更新网络资源表单
			RefreshNetResTable();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//根据本次搜索到的资源更新网络资源表单
	private void RefreshNetResTable() {
		/**
		 * 本程序规定广告<Name>属性的格式为: 
		 *     PublisherName:ResName
		 * PublisherName即各个节点本地的UserName； ResName即资源名称，即被共享的文件名
		 * 由于OS已规定PublisherName可以出现特殊字符，而ResName不能出现特殊字符，如":"
		 * 因此这里利用":"作为区分两个字符串的间隔符
		 * 
		 * 下面将利用这一特性提取<Name>中的这2个字符串，显示到主界面的"网络资源表单"中。
		 */
		
		//重设表单各行的值为广告内容
		for(int i = 0; i < AdvCount; i++) {
			String AdvName = SearchPipeAdvList.get(i).getName();	//获取广告<Name>字段值
			Vector<String> OldTableRow = GetTableRow(i+1, AdvName);	//提取PublisherName和ResName，以此设置表单第i+1行的内容
			MainWindow.NetResData.set(i, OldTableRow);				//把设置的内容写入表单
		}
		
		//重设表单剩余行的值为空
		for(int i = AdvCount; i < GlobalConstAndTag.TableLength; i++) {
			Vector<String> RestTableRow = new Vector<String>();
			RestTableRow.add(0, String.valueOf(i+1));
			RestTableRow.add(1, "");
			RestTableRow.add(2, "");
			MainWindow.NetResData.set(i, RestTableRow);
		}
		
		//刷新网络表单
		MainWindow.NetResTable.repaint();
		return;
	}

	//从AdvName中提取PublisherName和ResName，以此设置表单第Row行的内容
	private Vector<String> GetTableRow(int Row, String AdvName) {
		int SeparatorIndex = AdvName.lastIndexOf(":");				//获取分隔符":"在AdvName中的索引
		String PublisherName = AdvName.substring(0, SeparatorIndex);//提取发布者名称
		String ResName = AdvName.substring(SeparatorIndex+1);		//提取资源名称
		
		Vector<String> tmp = new Vector<String>();
		tmp.add(0, String.valueOf(Row));
		tmp.add(1, ResName);
		tmp.add(2, PublisherName);
		return tmp;
	}
		
	//针对发现远程广告的事件处理
	@Override
	public void discoveryEvent(DiscoveryEvent e) {
		//获取某个节点回复的消息
		DiscoveryResponseMsg ResMsg = e.getResponse();
		
		//获取回复信息中的所有广告，放入广告枚举列表AdvEnmu中
		Enumeration<Advertisement> AdvEnmu = ResMsg.getAdvertisements();
		
		//当AdvEnmu中至少有1个广告时
		if(AdvEnmu != null) {
			while(AdvEnmu.hasMoreElements()) {
				PipeAdvertisement tmpPipeAdv = (PipeAdvertisement) AdvEnmu.nextElement();	//提取管道广告tmpPipeAdv
				SearchPipeAdvList.add(tmpPipeAdv);					//把管道广告tmpPipeAdv存放到管道广告列表pipeAdvList
				AdvCount++;
				
				try {
					//由于getRemoteAdvertisements方法会把远程广告的副本保存到本地缓存
					//为了避免其他节点不必要的搜索错误，这里清除该广告在本地缓存的副本
					discoveryService.flushAdvertisement(tmpPipeAdv);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		//标记已找到资源
		isDiscoveryRes = true;
		
		//若找到的资源不是所需要的资源（管道广告），则搜索失败
		if(AdvCount == 0) {
			isDiscoveryRes = false;
		}
	}
}
