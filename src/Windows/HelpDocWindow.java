package Windows;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Toolkit.GetScreenSize;

/*
 * 帮助文档界面
 */
public class HelpDocWindow extends JFrame {

	//用于标识本类（本对象）的唯一性，由系统自动生成
	private static final long serialVersionUID = -5425221620702307077L;
	
	//设定窗口的宽高
	private final int winWidth = 500;
	private final int winHeight = 550;
	
	public HelpDocWindow() {
		this.setTitle("帮助");	//设置窗口标题
		this.setVisible(true);	//窗体可见
		this.setSize(winWidth, winHeight);//设置窗体尺寸
		
		//设置窗体位置
		this.setLocation((int)(GetScreenSize.screenWidth/2 - winWidth/2), (int)(GetScreenSize.screenHeight/2 - winHeight/2));
		
		//设置窗体图标
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		JPanel rootPanel = new JPanel();				//初始化根面板
		JTextArea helpFileArea = new JTextArea();		//初始化文本区
		helpFileArea.setEditable(false);				//禁止编辑文本区的内容
		
		rootPanel.setBackground(Color.white);			//设置根面板背景色
		rootPanel.setLayout(new GridLayout(1,1));		//根面板为1x1网格布局管理方式
		rootPanel.add(new JScrollPane(helpFileArea));	//为文本区添加滚动条，并把文本区放入rootPanel面板
		this.getContentPane().add(rootPanel);			//把根面板添加到顶层容器
		
		try {
			//允许窗口具有当前系统提供的窗口装饰
			HelpDocWindow.setDefaultLookAndFeelDecorated(true);
			//根据程序运行平台，设置窗口外观
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		//设置"窗口关闭"监听
		this.addWindowFocusListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		 
		//设置帮助文档内容
		helpFileArea.setText("        -------《基于P2P技术的文件共享系统》使用说明书-------\n" +
							 "\n" +
							 " 1、系统简介：\n" +
							 "        本系统采用了“完全分布式”的peer-to-peer网络架构，一个程序端\n" +
							 "    就是网络中的一个对等节点（peer）。每个peer都具备Server端与Client\n" +
							 "    端的服务功能，其中Server端主要提供资源的发布与传输功能，Client端\n" +
							 "    主要提供资源的搜索与下载功能。\n" +
							 "        由于是完全分布式的对等网络，因此并不存在类似C/S网络架构的超级\n" +
							 "    服务器对整个网络进行监控。也正由于此，当有peer加入/离开网络，或在\n" +
							 "    网络中发布/撤销共享资源时，其他peer都是不可马上预知的。\n" +
							 "\n" +
							 " 2、系统登陆：\n" +
							 "        启动系统后，本地程序自动以新peer身份接入P2P网络的默认对等组，\n" +
							 "    用户无需配置除登录名以外的任何信息。所谓的“接入”是指：有则加入，\n" + 
							 "    无则创建。\n" +
							 "\n" +
							 " 3、资源共享：\n" +
							 "        点击“共享文件”按钮，则可在弹出的对话框选择需要共享到网络的文\n" +
							 "    件资源。被共享的文件会将其已共享的消息发布到网络，同时显示在“本地\n" +
							 "    共享资源”列表中。所有被共享的文件均可以被其他peer搜索发现，并随时\n" +
							 "    进行下载。\n" +
							 "        在“本地共享资源”列表中选择某个资源后，点击“撤销共享”则可在\n" +
							 "    网络上删除其已共享的消息，并从“本地共享资源”列表中移除。其他peer\n" +
							 "    无法再发现已被撤销共享的资源。\n" +
							 "\n" +
							 " 4、资源搜索：\n" +
							 "        先通过下拉组件选择搜索方式，再在搜索框输入要搜索的内容，然后点\n" +
							 "    击“搜索”按钮，则可在P2P网络中以“尽最大努力交付”的方法搜索匹配\n" +
							 "    资源。搜索到的资源会显示在“资源搜索结果”列表中。\n" +
							 "        其中提供的搜索方式有4种（设输入的搜索文本为str）：\n" +
							 "        （1）局部匹配搜索：搜索所有资源/发布者名称中包含str的的资源；\n" +
							 "        （2）搜索所有网络资源：str自动为通配符“*”，无需输入任何；\n" +
							 "             内容，此方式能搜索网络中所有共享的资源列表。\n" +
							 "        （3）按资源名称搜索：搜索所有资源名称中包含str的的资源；\n" +
							 "        （4）按发布者名称搜索：搜索所有发布者名称中包含str的的资源。\n" +
							 "\n" +
							 " 5、资源下载：\n" +
							 "        在“资源搜索结果”列表中选择某个资源，点击“下载文件”按钮，然\n" +
							 "    后在弹出的对话框选择保存文件的位置及文件名，则自动与该资源的发布者\n" +
							 "    进行连接，继而接收数据传输。为了保证文件传输的可靠性，本系统采用了\n" +
							 "    TCP协议进行数据传输。\n" +
							 "        不同的资源可同时下载，同一资源不允许同时下载，所有资源都允许重\n" +
							 "    复下载。即使资源一致，只要发布者不同，也视为不同资源。点击“任务列\n" +
							 "    表”按钮可查询当前所有的下载详情。\n" +
							 "\n" +
							 " 6、广播机制：\n" +
							 "        广播器位于主界面下方，用于实时反馈用户的各种操作情况。\n" +
							 "\n" +
							 " 7、安全机制：\n" +
							 "        第1节已提及，由于peer之间的行为是不可马上预知的，因此在用户确\n" +
							 "    认下载资源前，会针对待下载资源在网内进行第二次搜索，当且仅当至少还\n" +
							 "    有一个peer在共享该资源时，才允许下载。\n" +
							 "        在资源传输过程中，若其中一方突然离开网络导致传输中断，而另一方\n" +
							 "    由于无法预知究竟是网络堵塞还是对方真的离线，从而可能导致负责控制资\n" +
							 "    源传输的线程无限期阻塞等待。为了避免发生这种情况，本系统设置了最大\n" +
							 "    延时时间DelayTime，只要在DelayTime内恢复传输，则是网络堵塞缘故，\n" +
							 "    依然继续下载资源；否则认为对方已离线，注销等待线程。\n" +
							 "        本系统能够穿越防火墙，但由于本系统是完全分布式的，因此在局域网\n" +
							 "    内使用时可靠性最高；在外网使用时可靠性相对变低。这里的“可靠性”是\n" +
							 "    指搜索的可靠性，即在内网搜索某资源失败时，很可能真的没有任何peer共\n" +
							 "    享了该资源；而在内网搜索某资源失败时，未必就是没有任何peer共享了该\n" +
							 "    资源，毕竟搜索方式仅是“尽最大努力交付”而已。当然，随着使用本系统\n" +
							 "    的用户数量的增多，不管是局域网还是外网，其可靠度都会相应提升。\n"
							 );
		//设置光标位置，使得滚动条置顶
		helpFileArea.select(0, 0);
	}

}
