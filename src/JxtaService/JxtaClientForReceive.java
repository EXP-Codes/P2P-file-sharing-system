package JxtaService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;
import Toolkit.FileFilters;
import Toolkit.GlobalConstAndTag;
import Toolkit.NumberFormat;
import Windows.MainWindow;

/*
 * 本地节点Peer的Client服务
 * 主要在确定Jxta网络内的某个Peer共享的资源后，与其进行连接并接收资源数据
 * 
 * ========================================================
 * 
 * 核心API:
 * public JxtaSocket(PeerGroup group, PeerID peerid, PipeAdvertisement pipeAdv, int timeout, boolean reliable)
 * 利用Scoket管道广告pipeAdv面向任意Peer创建一个JxtaSocket管道。
 * 该管道试图在group网络对等组环境下，在限时timeout内创建一个连接到指定管道（由pipeAdv决定）的连接。 
 * @Parameters:
 * 	group - 节点组
 * 	peerid - 待连接到的节点。
 * 	pipeAdv - 管道广告
 * 	timeout - 限定JxtaSocket必须在这段时间内被成功创建. 0表示不限时。 超时创建将抛出错误。
 * 	reliable - true:使用TCP协议。 false:使用UDP协议。
 * 
 */
public class JxtaClientForReceive extends Thread implements DiscoveryListener, ActionListener {
	
	//Jxta网络中的对等组
	private PeerGroup netPeerGroup = null;
	
	//对等节点Peer的发现服务
	private DiscoveryService discoveryService = null;
	
	//"保存文件"对话框的父窗口
	private MainWindow parentWindow;
	
	//管道广告列表，记录本次搜索到的所有广告
	private List<PipeAdvertisement> SearchPipeAdvList;
	
	//广告计数器，记录本次搜索共获得的广告数
	private int AdvCount = 0;
	
	//标记是否发现了资源
	private boolean isDiscoveryRes = false;
	
	private JxtaSocket clientSocket = null;				//连接到serverSocket的套接字
	private InputStream socketInStream = null;			//套接字的基础输入流
	private InputStream inStream = null;				//使用指定的基础输入流创建的输入流
	private RandomAccessFile randomFileStream = null;	//随机存取文件流
	
	private String ResAdvText;	//待下载资源的广告的<Name>属性值
	private String ResName;		//待下载资源的资源名称
	private int NetResTableRow;	//待下载资源在"资源搜索结果"表单的位置（行数）
	
	private Vector<String> thisRecord;	//本次下载任务的记录信息
	int recordIndex = -1;				//本次下载任务在"任务列表"的记录位置（行数）
	
	private Timer timer;			//计时器，用于检测管道连通性
	private int NoResponseTime = 0;	//服务器已无响应时间
	
	public JxtaClientForReceive(String resAdvText, int netResTableRow, MainWindow mainWindow) {

		this.ResAdvText = resAdvText;
		this.ResName = ResAdvText.substring(ResAdvText.lastIndexOf(":")+1);
		this.NetResTableRow = netResTableRow;
		this.parentWindow = mainWindow;
		
		timer = new Timer(1000, this);
		SearchPipeAdvList = new ArrayList<PipeAdvertisement>();
				
		this.netPeerGroup = JxtaPlatform.netPeerGroup;			//获取默认对等组
		this.discoveryService = JxtaPlatform.discoveryService;	//获取默认对等组的发现服务
		
		discoveryService.addDiscoveryListener(this);			//为发现服务配置监听
	}
	
	//运行JxtaClient线程，进行二次搜索，以确保待下载资源的有效性
	public void run() {
		TwiceSearch();
	}
	
	//进行二次搜索，以确保待下载资源的有效性
	public void TwiceSearch() {
		//远程搜索待下载资源
		discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", ResAdvText, 1);
			
		try {
			//等待searchTime，尽可能使得至少有1个节点回复搜索请求
			Thread.sleep(GlobalConstAndTag.searchTime);
			
			//限时searchTime内没有任何节点响应搜索请求
			if(!isDiscoveryRes) {
				MainWindow.SysMsgAdv.MsgAdv("\n资源 " + ResName + " 已失效！请重新检索可用资源。");
				JOptionPane.showMessageDialog(null, "该资源已失效！请重新检索可用资源！", "资源已失效", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			//找到至少一个广告，待下载资源尚有效
			//根据资源广告连接到资源提供者，并下载资源
			ConnectToServer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//根据资源广告连接到资源提供者，并下载资源
	private void ConnectToServer() {
		//当有多个节点提供的相同的资源广告时，仅取第一个广告
		PipeAdvertisement pipeAdv = SearchPipeAdvList.get(0);
		
		try {
			JFileChooser choosePath = new JFileChooser();	//初始化并打开"保存文件"对话框
			choosePath.setSelectedFile(new File(ResName)); 	//设置保存时的默认文件名
			choosePath.setApproveButtonText("保存");			//设置对话框功能按钮文本为"确定"
			choosePath.setDialogTitle("选择保存位置");		//设置对话框标题
			FileFilters.addFileFilters(choosePath);			//为对话框设置文件过滤器
			
			//当用户按下"保存文件"对话框的"确定"按钮时
			if(choosePath.showSaveDialog(parentWindow) == JFileChooser.APPROVE_OPTION) {
				
				File saveFile = choosePath.getSelectedFile();	//从"保存文件"对话框获取待保存文件的抽象路径位置
				saveFile.createNewFile();						//若该位置不存在指定文件，则创建一个
				
				//创建从中读取和向其中写入的随机存取文件流，读写对象为saveFile
				randomFileStream = new RandomAccessFile(saveFile, "rw");
				
				MainWindow.SysMsgAdv.MsgAdv("\n正在尝试连接资源提供者...");
				
				//创建连接到serverSocket的套接字
				clientSocket = new JxtaSocket(netPeerGroup, null, pipeAdv, GlobalConstAndTag.createPipeTime, GlobalConstAndTag.useTCP);
				
				MainWindow.SysMsgAdv.MsgAdv("\n连接成功！正在传输数据...请耐心等候...");
				GlobalConstAndTag.isDownloading[NetResTableRow] = true;	//标记该资源正在下载，避免重复下载
				
				//获取套接字的基础输入流
				socketInStream = clientSocket.getInputStream();
				
				//使用指定的基础输入流socketInStream创建一个输入流inStream
				inStream = new DataInputStream(new BufferedInputStream(socketInStream));
				
				//创建输入字节流，用于接收固定类型消息
				DataInput ReadMsg = new DataInputStream(socketInStream);
				
				//从输入字节流获取待接收文件的大小
				long fileSize = ReadMsg.readLong();
				
				//创建40KB的网络输入缓冲区
				byte[] InputCache = new byte[40960];

				//创建临时记录
				thisRecord = new Vector<String>();
				
				//获取本次下载任务的记录位置
				recordIndex = MainWindow.taskListWindow.GetNewRecordIndex();
				
				thisRecord.add(0, String.valueOf(recordIndex + 1));				//记录序号
				thisRecord.add(1, ResName);										//资源名称
				thisRecord.add(2, NumberFormat.getSizeFormat((double)fileSize));//资源大小
				thisRecord.add(3, "00.00%");									//下载进度
				thisRecord.add(4, "00.00B/s");									//传输速率
				thisRecord.add(5, "00:00:00");									//已用时间
				thisRecord.add(6, "下载中");										//任务状态
				thisRecord.add(7, "接收资源");									//任务属性
				
				//下载开始，启动计时器
				timer.start();
				
				//下载起始时间
				long startTime = System.currentTimeMillis();
				
				for(long receiveSize = 0, count = 0; receiveSize < fileSize; count++) {
					
					NoResponseTime = 0;		//刷新无响应时间
					
					int byteNum = inStream.read(InputCache);		//从网络输入流中读取数据到输入缓冲区。当InputCache为空时，该方法阻塞
					receiveSize += byteNum;							//已接收的数据量
					randomFileStream.write(InputCache, 0, byteNum);	//把缓冲区所有数据写入随机文件流
					randomFileStream.skipBytes(byteNum);			//跳过byteNum个字节，保证数据顺序写入文件
		
					//避免频繁刷新表单，固定频率更新一次"任务列表"表单
					if(count % GlobalConstAndTag.refreshHz == 0) {
						long currentTime = System.currentTimeMillis();	//获取当前时间
						long consumeTime = currentTime - startTime;		//计算当前耗时
						
						double DouReceiveSize = (double)receiveSize;	//类型转换
						
						double percent = DouReceiveSize/fileSize*100;	//计算已完成百分比
						double speed = DouReceiveSize/consumeTime*1000;	//计算当前下载速率(B/s)
						
						thisRecord.set(3, new DecimalFormat("#.##").format(percent) + "%");
						thisRecord.set(4, NumberFormat.getSpeedFormat(speed));
						thisRecord.set(5, NumberFormat.getTimeFormat(consumeTime));
						
						//更新下载任务列表表单
						MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
					}
				}
				
				long endTime = System.currentTimeMillis();	//获取任务完成时间
				long totalTime = endTime - startTime;		//计算任务总耗时
				
				//下载完成，停止计时器
				timer.stop();
				
				//更新本次下载记录信息
				thisRecord.set(3, "100.00%");
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(5, NumberFormat.getTimeFormat(totalTime));
				thisRecord.set(6, "下载成功");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				
				MainWindow.SysMsgAdv.MsgAdv("\n资源 " + ResName + " 下载成功！");
				JOptionPane.showMessageDialog(null, "资源 " + ResName + " 下载成功！");
				
				//标记该资源下载完毕
				GlobalConstAndTag.isDownloading[NetResTableRow] = false;
				
				randomFileStream.close();	//关闭随机文件流
				inStream.close();			//关闭输入流
				socketInStream.close();		//关闭基础输入流
				clientSocket.close();		//关闭套接字
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
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

	//针对计时器监听的事件处理
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == timer) {
			NoResponseTime++;
			
			//若服务器已无响应时间超过规定的最大时延的一半，则认为网络堵塞，等待重新建立连接
			if(NoResponseTime >= (GlobalConstAndTag.DelayTime/2)) {
				//更新本次下载记录信息
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "无响应");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
			}
			
			//若服务器已无响应时间超过规定的最大时延，则认为管道已断开，下载失败
			if(NoResponseTime >= GlobalConstAndTag.DelayTime) {
				timer.stop();	//下载失败，停止计时
				GlobalConstAndTag.isDownloading[NetResTableRow] = false;	//标记该资源可重新下载
				
				//更新本次下载记录信息
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "下载失败");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				
				MainWindow.SysMsgAdv.MsgAdv("\n资源 " + ResName + " 的提供者已离线，强制中断下载！");
				JOptionPane.showMessageDialog(null, "资源 " + ResName + " 的提供者已离线，强制中断下载！");
				
				try {
					randomFileStream.close();	//关闭随机文件流
					inStream.close();			//关闭输入流
					socketInStream.close();		//关闭基础输入流
					clientSocket.close();		//关闭套接字
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
