package JxtaService;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import Windows.MainWindow;


/*
 * 配置Jxta网络平台
 * 用于为Peer的Server和Client服务提供peer-to-peer的网络环境
 * 
 * ===================================================================================================
 * 
 * Ad-Hoc : 这种节点在点对点网络中是最基本的一类特殊节点，它不具备基础设施节点（集合节点或中继节点）的属性。
 * 边缘节点EDGE　: 除了支持Ad-Hoc的行为，还会依附于基础设施节点（集合节点或中继节点，或两者）。
 * 集合节点RENDEZVOUS : 支持网络初期的引导服务，例如发现服务、管道解析等。
 * 中继节点RENDEZVOUS_RELAY : 提供信息中继服务，使其能穿越防火墙。
 * 代理节点PROXY : 为J2ME代理服务提供J2ME JXTA。
 * 超级节点SUPER : 提供集合节点、中继节点、代理节点的所有功能。
 */
public class JxtaPlatform {

	//Jxta网络的Manager，用于配置Jxta网络的各种属性
	public static NetworkManager networkManager = null;
	
	//Jxta网络中的对等组
	public static PeerGroup netPeerGroup = null;
		
	//对等节点Peer的发现服务
	public static DiscoveryService discoveryService = null;
	
	//本地缓存的根文件夹名称
	private static String RootCache = ".cache";
	
	//创建本地二级目录".cache"-->"JxtaPlatform"
	//用于保存Jxta网络的配置、广告、ID等信息
	//且由home.toURI()方法提供的标识符作为本地Peer在Jxta网络上的统一资源标识符
	private static File home = new File(new File(RootCache), "JxtaPlatform");
		
	public JxtaPlatform() {
		startJxtaPlatform();
	}

	//接入Jxta网络
	public static void startJxtaPlatform() {
		try {
			//设置本节点为边缘节点EDGE
			//节点名称为"JxtaPlatform"
			//以home目录位置（本地cache）作为本节点在Jxta网络上的统一资源标识符URI
			networkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "JxtaPlatform", home.toURI());
			
			//启动Jxta网络（有则加入，无则创建）
			networkManager.startNetwork();

			MainWindow.SysMsgAdv.MsgAdv("\n成功创建/加入P2P网络！");
			
		} catch (IOException e) {
			MainWindow.SysMsgAdv.MsgAdv("\n配置本地Peer属性失败！");
			e.printStackTrace();
		} catch (PeerGroupException e) {
			MainWindow.SysMsgAdv.MsgAdv("\n创建/加入P2P网络失败！");
			e.printStackTrace();
		}
		
		netPeerGroup = networkManager.getNetPeerGroup();		//获取默认对等组
		discoveryService = netPeerGroup.getDiscoveryService();	//获取默认对等组的发现服务
		return;
	}
	
	//完全退出Jxta网络
	public static void stopJxtaPlatform() {
		//清空本地广告
		removeAllLoaclAdvertisement();
		
		//退出Jxta网络
		networkManager.stopNetwork();
		MainWindow.SysMsgAdv.MsgAdv("\n已退出P2P网络...");
		
		//删除本地所有缓存文件
		delFolder(RootCache);
		return;
	}
	
	//移除本地缓存中<Name>属性值为AdvName的管道广告
	public static void removeLocalAdvertisement(String AdvName) {
		try {
			//提取本地所有管道广告
			Enumeration<Advertisement> AdvEnum = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", null);
			//检查所有广告的<Name>属性值，移除匹配项
			if(AdvEnum != null) {
				while(AdvEnum.hasMoreElements()) {
					PipeAdvertisement LocalAdv = (PipeAdvertisement) AdvEnum.nextElement();
					if(LocalAdv.getName().equals(AdvName)) {
						discoveryService.flushAdvertisement(LocalAdv);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//清空本地所有管道广告
	public static void removeAllLoaclAdvertisement() {
		try {
			//提取本地所有管道广告
			Enumeration<Advertisement> AdvEnum = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", null);
			//逐一移除
			if(AdvEnum != null) {
				while(AdvEnum.hasMoreElements()) {
					Advertisement LocalAdv = AdvEnum.nextElement();
					discoveryService.flushAdvertisement(LocalAdv);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//删除文件目录
	//RootFolderPath为根文件夹的相对/绝对路径
	public static void delFolder(String RootFolderPath) {
		try {
			//删除根文件夹里面所有文件
			delAllFile(RootFolderPath);
			
			//指定根文件夹的抽象路径，然后删除空的根文件夹
			File RootFolder = new File(RootFolderPath);	
			RootFolder.delete();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//删除指定目录下的所有文件
	//path为文件/文件夹的相对/绝对路径
	public static void delAllFile(String path) {
		//指定文件/文件夹的抽象路径，若该路径的文件/文件夹不存在，返回
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		
		//若该路径不是文件目录，返回
		if (!file.isDirectory()) {
			return;
		}
		
		//获取该文件目录下第一层的所有文件和文件夹，返回它们的名字列表
		String[] fileList = file.list();
		
		//逐一指定该目录下第一层的所有文件和文件夹，并删除
		File temp = null;
		for (int i = 0; i < fileList.length; i++) {
			/*
			 * 测试路径path的后缀是否为"/"
			 * 根据后缀不同始终创建一个一级路径 "path/fileList[i]"
			 * 再利用此路径指定文件/文件夹temp
			 */
			if (path.endsWith(File.separator)) {
				temp = new File(path + fileList[i]);
			}
			else {
				temp = new File(path + File.separator + fileList[i]);
			}
			
			//若temp为文件，直接删除
			if (temp.isFile()) {
				temp.delete();
			}
			
			//若temp为目录，则继续递归删除
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + fileList[i]);//先删除文件夹里面的文件
				delFolder(path + File.separator + fileList[i]);	//再删除空文件夹
			}
		}
		return;
	}
}
