package Toolkit;

/*
 * 全局常量和全局标识
 */
public class GlobalConstAndTag {

	//搜索资源时，允许每次搜索从一个Peer发现资源广告的数量
	public static int MaxPeerAdvNum = 50;
		
	//表单长度
	public static final int TableLength = 100;
	
	//标记资源搜索结果表单列表中正在下载的资源，避免重复下载
	public static boolean[] isDownloading = new boolean[TableLength];
	
	//下载任务列表的更新频率（每传输refreshHz个数据包更新一次列表）
	public static final int refreshHz = 100;
	
	//判定管道断开的最大超时时延（5秒）
	public static final int DelayTime = 5;
	
	//资源文件的最大共享时限（1小时）
	public static final long shareTime = 3600000;
	
	//尽最大努力搜索资源的限时（1秒）
	public static final long searchTime = 1000;
	
	//Peer之间成功创建JxtaSocket传输管道的最大限时（3秒）
	public static final int createPipeTime = 3000;
	
	//是否使用TCP传输协议（true为TCP，false为UDP）
	public static final boolean useTCP = true;
}
