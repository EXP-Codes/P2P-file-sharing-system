package JxtaService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.Timer;

import Toolkit.GlobalConstAndTag;
import Toolkit.NumberFormat;
import Windows.MainWindow;

/*
 * 本地节点Peer的Server服务的从属线程部分
 * 主要负责来自其他Peer的单个连接请求
 * 用于并行处理来自不同Peer对本地不同资源的数据传输
 * 
 * =======================================================
 * 
 * 关于输入流、输出流，输入管道、输出管道:
 * 1、当从其他地方[接收/读取]数据时，应该从输入流读取，而输入流可通过输入管道获取；
 * 2、当向其他地方[发送/写入]数据时，应该向输出流写入，而输出流可通过输出管道获取。
 * 
 */
public class JxtaServerForSend implements Runnable, ActionListener {

	private Socket serverScoket = null;			//服务套接字，用于保持Server与Client之间的连接并进行数据传输
	private OutputStream socketOutStream = null;//服务套接字的基础输出流
	private OutputStream outStream = null;		//使用指定的基础输入流创建的输入流	
	private FileInputStream fileStream = null;	//文件输入流，读取待发送资源的数据
	
	private long ResSize;	//被共享资源的大小（字节数）
	private String ResPath;	//被共享资源在本地的绝对位置
	private String ResName;	//被共享资源的资源名称
	
	private Vector<String> thisRecord;	//本次发送任务的记录信息
	int recordIndex = -1;				//本次发送任务在"任务列表"的记录位置（行数）
	
	private Timer timer;			//计时器，用于检测管道连通性
	private int NoResponseTime = 0;	//客户端已无响应时间
	
	public JxtaServerForSend(Socket serverScoket, String resName, long resSize, String resPath) {
		this.serverScoket = serverScoket;
		this.ResSize = resSize;
		this.ResPath = resPath;
		this.ResName = resName;
		timer = new Timer(1000, this);
	}
	
	//启动线程，向资源请求节点发送数据
	@Override
	public void run() {
		SendData();
	}

	//向资源请求节点发送数据
	private void SendData() {
		try {
			//根据资源抽象路径打开待发送的共享资源文件sharingResFile
			File sharingResFile = new File(ResPath);
			
			//创建关于sharingResFile的文件输入流，读取该文件的数据
			fileStream = new FileInputStream(sharingResFile);

			//获取服务套接字的基础输出流
			socketOutStream = serverScoket.getOutputStream();
			
			//使用指定的基础输出流socketOutStream创建一个输出流outStream
			outStream = new DataOutputStream(new BufferedOutputStream(socketOutStream));
			
			//创建输出字节流，用于发送固定类型消息
			DataOutput WriteMsg = new DataOutputStream(socketOutStream);
			
			//利用输出字节流发送待传输文件的大小
			WriteMsg.writeLong(ResSize);
			
			//创建40KB的文件读取缓冲区
			byte[] ReadCache = new byte[40960];
			
			//创建临时记录
			thisRecord = new Vector<String>();
			
			//获取本次下载任务的记录位置
			recordIndex = MainWindow.taskListWindow.GetNewRecordIndex();
			
			thisRecord.add(0, String.valueOf(recordIndex + 1));				//记录序号
			thisRecord.add(1, ResName);										//资源名称
			thisRecord.add(2, NumberFormat.getSizeFormat((double)ResSize)); //资源大小
			thisRecord.add(3, "00.00%");									//传输进度
			thisRecord.add(4, "00.00B/s");									//传输速率
			thisRecord.add(5, "00:00:00");									//已用时间
			thisRecord.add(6, "发送中");										//任务状态
			thisRecord.add(7, "发送资源");									//任务属性
			
			//发送开始，启动计时器
			timer.start();
			
			//传输起始时间
			long startTime = System.currentTimeMillis();
			
			//以分包方式循环读取共享资源的数据并发送
			for(long sendSize = 0, count = 0; ; count++) {
				
				NoResponseTime = 0;		//刷新无响应时间
				
				//从文件输入流中读取共享资源的数据，存储到文件读取缓冲区ReadCache
				int byteNum = fileStream.read(ReadCache);
				
				//已发送的数据量
				sendSize += byteNum;
				
				//已到达输入流末尾，数据读取完毕
				if(byteNum == -1) {
					break;
				}
				
				//把缓冲区ReadCache的byteNum个字节数据写到输出流outStream
				outStream.write(ReadCache, 0, byteNum);
				
				//刷新输出流outStream，把数据强制发送到Client
				outStream.flush();
				
				//避免频繁刷新表单，固定频率更新一次"任务列表"表单
				if(count % GlobalConstAndTag.refreshHz == 0) {
					long currentTime = System.currentTimeMillis();	//获取当前时间
					long consumeTime = currentTime - startTime;		//计算当前耗时
					
					double DouReceiveSize = (double)sendSize;		//类型转换
					
					double percent = DouReceiveSize/ResSize*100;	//计算已完成百分比
					double speed = DouReceiveSize/consumeTime*1000;	//计算当前传输速率(B/s)
					
					thisRecord.set(3, new DecimalFormat("#.##").format(percent) + "%");
					thisRecord.set(4, NumberFormat.getSpeedFormat(speed));
					thisRecord.set(5, NumberFormat.getTimeFormat(consumeTime));
					
					//更新下载任务列表表单
					MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				}
			}
			
			long endTime = System.currentTimeMillis();	//获取任务完成时间
			long totalTime = endTime - startTime;		//计算任务总耗时
			
			//发送完成，停止计时器
			timer.stop();
			
			//更新本次传输记录信息
			thisRecord.set(3, "100.00%");
			thisRecord.set(4, "00.00B/s");
			thisRecord.set(5, NumberFormat.getTimeFormat(totalTime));
			thisRecord.set(6, "发送成功");
			MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
			
			MainWindow.SysMsgAdv.MsgAdv("\n资源 " + ResName + " 发送成功！");
			
			fileStream.close();		//关闭文件输入流
			outStream.close();		//关闭输出流
			socketOutStream.close();//关闭基础输出流
			serverScoket.close();	//关闭套接字
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//针对计时器监听的事件处理
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == timer) {
			NoResponseTime++;
			
			//若客户端已无响应时间超过规定的最大时延的一半，则认为网络堵塞，等待重新建立连接
			if(NoResponseTime >= (GlobalConstAndTag.DelayTime/2)) {
				//更新本次发送记录信息
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "无响应");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
			}
			
			//若客户端已无响应时间超过规定的最大时延，则认为管道已断开，发送失败
			if(NoResponseTime >= GlobalConstAndTag.DelayTime) {
				timer.stop();	//发送失败，停止计时
				
				//更新本次下载记录信息
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "发送失败");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				
				MainWindow.SysMsgAdv.MsgAdv("\n资源 " + ResName + " 的接收者已离线，强制中断下载！");
			}
		}
	}

}
