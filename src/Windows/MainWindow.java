package Windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Time;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;

import JxtaService.JxtaClientForReceive;
import JxtaService.JxtaClientForSearch;
import JxtaService.JxtaPlatform;
import JxtaService.JxtaServer;
import Toolkit.FileFilters;
import Toolkit.Fonts;
import Toolkit.GlobalConstAndTag;
import Toolkit.GlobalMsgAdv;
import Toolkit.MyTableComponent;

/*
 * P2P文件共享系统的主界面（主窗口，顶层容器）
 * 本类是本系统的核心类
 * 用于展示及提供P2P文件共享系统的核心功能
 */
public class MainWindow extends JFrame implements ActionListener {

	//用于标识本类（本对象）的唯一性，由系统自动生成
	private static final long serialVersionUID = 1163693238743966735L;
		
	//主面板（主窗口下的第一层组件）
	private JPanel mainPanel;
	
	//计时器（在主窗口主要提供实时系统功能）
	private Timer timer;

	//标记是否处于"登陆中"状态（刚运行系统时）
	private boolean isLogin = true;
	
	//标记是否已接入网络
	private boolean isLinkNetwork = false;
	
	//系统全局消息广播器
	public static GlobalMsgAdv SysMsgAdv;
	
	//"下载任务列表"窗口
	public static TaskListWindow taskListWindow;
	
	//描述本地登陆用户信息的组件
	private JLabel UserImageLabel;			//用户头像标签
	private JTextField UserNameText;		//用户名称文本框
	private String UserName;				//用户名称
	private ImageIcon UserIcon;				//用户名称前的小图标
	private ImageIcon UserNotLoginImage;	//用户的未登陆状态头像
	private ImageIcon UserHasLoginImage;	//用户的已登陆状态头像
	
	//本地共享资源表单
	private Vector<String>	LocalResColName;			//表单列名（表头）
	public static Vector<Vector<String>> LocalResData;	//表单模型（二维表）
	public static MyTableComponent LocalResTable;		//表单组件
	
	//网络共享资源表单
	private Vector<String>	NetResColName;				//表单列名（表头）
	public static Vector<Vector<String>> NetResData;	//表单模型（二维表）
	public static MyTableComponent NetResTable;			//表单组件

	//标签（描述性组件）
	private JLabel LocalShareLabel;		//"本地共享资源"标签
	private JLabel SearchResultLabel;	//"资源搜索结果"标签
	private JLabel GlobalMsgLabel;		//"系统消息"标签
	private JLabel SysTimeIlluLabel;	//"系统时间"标签
	private JLabel SystemTimeLabel;		//显示实时系统时间的标签
	
	//搜索组件
	private JComboBox SearchStyleComboBox;	//"搜索方式"下拉框
	private JTextField SearchTextField;		//搜索文本框
	private String SearchText = null;		//搜索文本
	private JButton SearchBtn;				//"搜索"按钮
	
	//按钮
	private JButton ShareBtn;		//"共享文件"按钮
	private JButton CancelBtn;		//"撤销共享"按钮
	private JButton DownloadBtn;	//"下载文件"按钮
	private JButton TaskListBtn;	//"任务列表"按钮
	
	//菜单栏的菜单项
	private JMenuItem breakItem;	//"断开网络"菜单项
	private JMenuItem linkItem;		//"接入网络"菜单项
	private JMenuItem exitItem;		//"退出"菜单项
	private JMenuItem shareItem;	//"共享文件"菜单项
	private JMenuItem cancelItem;	//"撤销共享"菜单项
	private JMenuItem downloadItem;	//"下载文件"菜单项
	private JMenuItem helpItem;		//"使用说明"菜单项
	private JMenuItem authorItem;	//"作者信息"菜单项
	
	//菜单项图标
	private ImageIcon breakIcon;	//"断开网络"图标
	private ImageIcon linkIcon;		//"接入网络"图标
	private ImageIcon exitIcon;		//"退出"图标
	private ImageIcon shareIcon;	//"共享文件"图标
	private ImageIcon cancelIcon;	//"撤销共享"图标
	private ImageIcon downloadIcon;	//"下载文件"图标
	private ImageIcon helpIcon;		//"使用说明"图标
	private ImageIcon authorIcon;	//"作者信息"图标
	
	//标签图片
	private ImageIcon LocalResNotloginImage;	//"本地共享资源"登录前图片
	private ImageIcon LocalResHasloginImage;	//"本地共享资源"已登录图片
	private ImageIcon SearchResNotloginImage;	//"资源搜索结果"登录前图片
	private ImageIcon SearchResHasloginImage;	//"资源搜索结果"已登录图片
	private ImageIcon SysMsgImage;				//"系统消息"图片
	private ImageIcon SysTimeImage;				//"系统时间"图片
	
	//构造函数
	public MainWindow(String windowTitle) {
		InitInfo();				//初始化全局资源信息
		InitWindow(windowTitle);//初始化主界面
		AddComponents();		//往主面板添加必要组件
		LoginManager();			//用户登陆处理
	}

	//初始化全局资源信息
	public void InitInfo() {
		taskListWindow = new TaskListWindow();	//初始化下载任务列表窗口
		timer = new Timer(1000, this);			//初始化计时器,每秒监听一次主窗口
		InitTable();							//初始化表单模型及表单属性
		ImportImage();							//导入图片资源
		return;
	}

	//初始化主界面
	private void InitWindow(String windowTitle) {
		//设置主界面图标
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		this.setTitle(windowTitle);			//设置主界面标题
		this.setResizable(false);			//禁止拉伸窗口,最大化按钮无效
		this.setJMenuBar(MakeMenuBar());	//创建并设置主界面的菜单栏
		
		mainPanel = new JPanel();				//初始化主面板
		mainPanel.setBackground(Color.white);	//设置主面板背景色:白色
		this.getContentPane().add(mainPanel);	//把主面板添加到顶层容器
		mainPanel.setLayout(null);				//撤销主面板相对布局方式，这是为了使用绝对布局方式设置组件坐标
		
		try {
			//允许主窗体具有当前系统提供的窗口装饰
			MainWindow.setDefaultLookAndFeelDecorated(true);
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

		//取消JFrame窗口的默认关闭机制
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//重新设置"窗口关闭"监听
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				
				if(taskListWindow.GetCurrentRecordNum() > 0) {
					String[] option = {"确定关闭", "取消操作"};
					int selectId = JOptionPane.showOptionDialog(mainPanel, "当前有任务正在运行，你确定要关闭窗口吗？\n任务一旦中断则不可恢复！", "关闭窗口消息", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
					if(selectId == 1) {
						return;
					}
				}
				
				SysMsgAdv.MsgAdv("\n正在退出系统...");	//广播
				timer.stop();						//停止计时器
				e.getWindow().setVisible(false);	//设置窗口不可见
				e.getWindow().dispose();			//注销窗口中的所有组件
				JxtaPlatform.stopJxtaPlatform();	//完全退出Jxta网络
				System.exit(0);						//退出
			}
		});
		return;
	}
	
	//往主面板添加必要组件
	private void AddComponents() {
		AddUserHasLogin();	//添加用户登陆信息框
		AddLabel();			//添加标签
		AddTextFiled();		//添加文本框/区
		AddButton();		//添加按钮
		AddComboBox();		//添加下拉框
		AddTable();			//添加表单
		return;
	}
	
	//用户登陆处理
	private void LoginManager() {
		this.setFocusableWindowState(false);//主窗口失去焦点
		this.setEnabled(false);				//锁定主窗口，禁止操作
		new LoginWindow(UserNameText);		//打开登陆界面
		new JxtaPlatform();					//启动Jxta网络平台
		isLinkNetwork = true;				//标记已接入网络
		timer.start();						//启动计时器
		return;
	}
	
	//初始化表单模型及表单属性
	private void InitTable() {
		//本地共享资源表单
		LocalResColName = new Vector<String>();		//初始化表单列名
		LocalResColName.add(0, "ID");				//设置表单第0列的列名：ID 索引号
		LocalResColName.add(1, "      资源名称      ");	//设置表单第1列的列名：资源名称
		
		LocalResData = new Vector<Vector<String>>();//初始化表单
		for(int pr = 0; pr < GlobalConstAndTag.TableLength; pr++) {
			//设置表单第pr行首列索引为pr+1
			Vector<String> row = new Vector<String>();
			row.add(0, String.valueOf(pr + 1));
			
			//设置第pr行的其余列值为空值
			for(int pc = 1; pc < LocalResColName.size(); pc++) {
				row.add(pc, "");
			}
			//把row添加到LocalResInfo表单的行末
			LocalResData.add(row);
		}
		
		//网络共享资源表单
		NetResColName = new Vector<String>();	//初始化表单列名
		NetResColName.add(0, "ID");				//设置表单第0列的列名：ID索引号
		NetResColName.add(1, "      资源名称      ");	//设置表单第1列的列名：资源名称
		NetResColName.add(2, "发布者");			//设置表单第2列的列名：发布者
		
		NetResData = new Vector<Vector<String>>();	//初始化表单
		for(int fr = 0; fr < GlobalConstAndTag.TableLength; fr++) {
			//设置表单第fr行首列索引为fr+1
			Vector<String> row = new Vector<String>();
			row.add(0, String.valueOf(fr + 1));
			
			//设置第fr行的其余列值为空值
			for(int fc = 1; fc < NetResColName.size(); fc++) {
				row.add(fc, "");
			}
			//把row添加到NetResInfo表单的行末
			NetResData.add(row);
		}
		return;
	}

	//导入图片资源
	private void ImportImage() {
		//导入登陆相关图片
		UserNotLoginImage = new ImageIcon(MainWindow.class.getResource("/UserNotLogin.png"));
		UserHasLoginImage = new ImageIcon(MainWindow.class.getResource("/UserHasLogin.png"));
		UserIcon = new ImageIcon(MainWindow.class.getResource("/UserIcon.png"));
		
		//导入菜单项图标
		breakIcon = new ImageIcon(MainWindow.class.getResource("/breakIcon.png"));
		linkIcon = new ImageIcon(MainWindow.class.getResource("/linkIcon.png"));
		exitIcon = new ImageIcon(MainWindow.class.getResource("/exitIcon.png"));
		shareIcon = new ImageIcon(MainWindow.class.getResource("/shareIcon.png"));
		cancelIcon = new ImageIcon(MainWindow.class.getResource("/cancelIcon.png"));
		downloadIcon = new ImageIcon(MainWindow.class.getResource("/downloadIcon.png"));
		helpIcon = new ImageIcon(MainWindow.class.getResource("/helpIcon.png"));
		authorIcon = new ImageIcon(MainWindow.class.getResource("/authorIcon.png"));
		
		//导入标签图片
		LocalResNotloginImage = new ImageIcon(MainWindow.class.getResource("/LocalResText_Notlogin.png"));
		LocalResHasloginImage = new ImageIcon(MainWindow.class.getResource("/LocalResText_Haslogin.png"));
		SearchResNotloginImage = new ImageIcon(MainWindow.class.getResource("/SearchResText_Notlogin.png"));
		SearchResHasloginImage = new ImageIcon(MainWindow.class.getResource("/SearchResText_Haslogin.png"));
		SysMsgImage = new ImageIcon(MainWindow.class.getResource("/SysMsg.png"));
		SysTimeImage = new ImageIcon(MainWindow.class.getResource("/SysTime.png"));
		return;
	}
		
	//添加用户登陆信息窗
	private void AddUserHasLogin() {
		//初始化
		UserImageLabel = new JLabel(UserNotLoginImage, JLabel.CENTER);	//用户头像标签（未登陆状态）
		JLabel UserIconLabel = new JLabel(UserIcon, JLabel.CENTER);		//用户名称前的小图标标签
		UserNameText = new JTextField("", 12);							//用户名称文本框，设置默认长度为12
		UserNameText.setEditable(false);								//禁止修改用户名称文本框内容

		//为用户头像标签创建面板
		JPanel UserImagePanel = new JPanel();
		UserImagePanel.add(UserImageLabel);
		UserImagePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));//设定面板边界
		
		//为用户名称前的小图标标签创建面板
		JPanel UserIconPanel = new JPanel();
		UserIconPanel.add(UserIconLabel);
		UserIconPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		
		//为用户名称文本框创建面板，并设置文本字体风格
		JPanel UserNamePanel = new JPanel();
		UserNamePanel.add(UserNameText);
		UserNamePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		UserNameText.setFont(Fonts.ChineseStyle_SongTi_LR_16);
		
		//创建[用户图标名称面板], 把[用户图标面板]及[用户名称面板]添加到其中
		JPanel UserIconNamePanel = new JPanel(new BorderLayout());
		UserIconNamePanel.add(UserIconPanel, "West");
		UserIconNamePanel.add(UserNamePanel, "Center");
		
		//把[用户头像面板]、[用户图标名称面板]添加到[用户信息面板]
		JPanel UserInfoPanel = new JPanel(new BorderLayout());	//用户信息面板，边缘布局方式
		UserInfoPanel.add(UserImagePanel, "Center");			//[用户头像面板]放在面板中心
		UserInfoPanel.add(UserIconNamePanel, "South");			//[用户图标名称面板]放在面板南边
		
		//把[用户信息面板]添加到[主面板]
		mainPanel.add(UserInfoPanel);
		UserInfoPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		UserInfoPanel.setBounds(15, 15, 150, 165);		//设置组件位置和大小: (x,y,w,h) 左上角位置(x,y) 组件尺寸w*h
		return;
	}

	//添加标签组件
	private void AddLabel() {
		//"本地共享资源"列表标签
		LocalShareLabel = new JLabel(LocalResNotloginImage);	//设置标签缺省内容为图片
		mainPanel.add(LocalShareLabel);
		LocalShareLabel.setBounds(190, 55, 200, 41);
		
		//"资源搜索结果"列表标签
		SearchResultLabel = new JLabel(SearchResNotloginImage);
		mainPanel.add(SearchResultLabel);
		SearchResultLabel.setBounds(460, 55, 200, 41);
		
		//"系统消息"标签
		GlobalMsgLabel = new JLabel(SysMsgImage);
		mainPanel.add(GlobalMsgLabel);
		GlobalMsgLabel.setBounds(15, 440, 65, 75);
		
		//"系统时间"标签
		SysTimeIlluLabel = new JLabel(SysTimeImage);
		mainPanel.add(SysTimeIlluLabel);
		SysTimeIlluLabel.setBounds(545, 440, 150, 45);
		
		//"实时系统时间"标签
		Time SystemTime = new Time(System.currentTimeMillis());	//获取系统当前时间（精确到毫秒）
		SystemTimeLabel = new JLabel(SystemTime.toString());	//把当前系统时间写入到标签文本
		SystemTimeLabel.setFont(Fonts.EnglishStyle_Consolas_I_22);
		mainPanel.add(SystemTimeLabel);
		SystemTimeLabel.setBounds(570, 490, 150, 20);
		return;
	}

	//添加文本框/区组件
	private void AddTextFiled() {
		//"系统消息"文本区
		SysMsgAdv = new GlobalMsgAdv(mainPanel);
		
		//"搜索"文本框
		SearchTextField = new JTextField("请填写搜索内容");	//设置文本框缺省内容
		SearchTextField.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		mainPanel.add(SearchTextField);	
		SearchTextField.setBounds(360, 15, 255, 30);
		SearchTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				//当按下回车键时
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					SearchEventManager();	//处理"搜索资源"事件
				}
			}
		});
		return;
	}
	
	//添加按钮组件
	private void AddButton() {
		//"共享文件"按钮
		ShareBtn = new JButton("共享文件");
		ShareBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(ShareBtn);
		ShareBtn.setBounds(15, 195, 150, 50);
		ShareBtn.addActionListener(this);		//设置按钮动作监听
		
		//"撤销共享"按钮
		CancelBtn = new JButton("撤销共享");
		CancelBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(CancelBtn);
		CancelBtn.setBounds(15, 255, 150, 50);
		CancelBtn.addActionListener(this);
				
		//"下载文件"按钮
		DownloadBtn = new JButton("下载文件");
		DownloadBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(DownloadBtn);
		DownloadBtn.setBounds(15, 315, 150, 50);
		DownloadBtn.addActionListener(this);
		
		//"任务列表"按钮
		TaskListBtn = new JButton("任务列表");
		TaskListBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(TaskListBtn);
		TaskListBtn.setBounds(15, 375, 150, 50);
		TaskListBtn.addActionListener(this);
		
		//"搜索"按钮
		SearchBtn = new JButton("搜索");
		SearchBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		mainPanel.add(SearchBtn);
		SearchBtn.setBounds(620, 15, 80, 30);
		SearchBtn.addActionListener(this);
		return;
	}
	
	//添加"搜索方式"下拉框
	private void AddComboBox() {
		String[] SearchStyle = {"按资源搜索", 
								"按发布者搜索",
								"搜索全网资源"};		//下拉选择项
		SearchStyleComboBox = new JComboBox(SearchStyle);
		SearchStyleComboBox.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		mainPanel.add(SearchStyleComboBox);
		SearchStyleComboBox.setBounds(185, 15, 170, 30);
		SearchStyleComboBox.addActionListener(this);
		return;
	}
		
	//添加表单组件
	private void AddTable() {
		/*
		 * 本地共享资源表单
		 */
		//用表单模型及表单列名初始化表单组件
		LocalResTable = new MyTableComponent(LocalResData, LocalResColName);
		//为表单设置带滚动条的面板，垂直和水平滚动条为需要时显示
		JScrollPane LocalResPanel = new JScrollPane(LocalResTable, 
													JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(LocalResPanel);
		LocalResPanel.setBounds(185, 105, 220, 320);
		
		/*
		 * 资源搜索结果表单
		 */
		//用表单模型及表单列名初始化表单组件
		NetResTable = new MyTableComponent(NetResData, NetResColName);
		//为表单设置带滚动条的面板，垂直和水平滚动条为需要时显示
		JScrollPane NetResPanel = new JScrollPane(NetResTable, 
													JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(NetResPanel);	
		NetResPanel.setBounds(425, 105, 280, 320);
		return;
	}
	
	//设置窗口菜单栏，并返回菜单栏组件
	private JMenuBar MakeMenuBar() {
		//设置菜单始终在最顶层，不被其他组件遮挡
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		JMenuBar menuBar = new JMenuBar();	//创建菜单栏
		
		JMenu sysMenu = new JMenu("系统");	//创建"系统"菜单
		JMenu toolMenu = new JMenu("工具");	//创建"工具"菜单
		JMenu helpMenu = new JMenu("帮助");	//创建"帮助"菜单
		
		breakItem = new JMenuItem("断开网络", breakIcon);		//初始化"断开网络"菜单项
		linkItem = new JMenuItem("接入网络", linkIcon);		//初始化"接入网络"菜单项
		exitItem = new JMenuItem("退出", exitIcon);			//初始化"退出"菜单项
		shareItem = new JMenuItem("共享资源", shareIcon);		//初始化"共享资源"菜单项
		cancelItem = new JMenuItem("撤销共享", cancelIcon);	//初始化"撤销共享"菜单项
		downloadItem = new JMenuItem("下载文件", downloadIcon);//初始化"下载资源"菜单项
		helpItem = new JMenuItem("使用说明", helpIcon);		//初始化"使用说明"菜单项
		authorItem = new JMenuItem("作者信息", authorIcon);	//初始化"作者信息"菜单项
		
		menuBar.add(sysMenu);		//把"系统"菜单添加到菜单栏
		menuBar.add(toolMenu);		//把"工具"菜单添加到菜单栏
		menuBar.add(helpMenu);		//把"帮助"菜单添加到菜单栏

		sysMenu.add(breakItem);		//把 "断开网络"菜单项添加到"系统"菜单
		sysMenu.add(linkItem);		//把 "接入网络"菜单项添加到"系统"菜单
		sysMenu.add(exitItem);		//把 "退出"菜单项添加到"系统"菜单
		toolMenu.add(shareItem);	//把 "共享资源"菜单项添加到"工具"菜单
		toolMenu.add(cancelItem);	//把 "撤销共享"菜单项添加到"工具"菜单
		toolMenu.add(downloadItem);	//把 "下载资源"菜单项添加到"工具"菜单
		helpMenu.add(helpItem);		//把 "使用说明"菜单项添加到"帮助"菜单
		helpMenu.add(authorItem);	//把 "作者信息"菜单项添加到"帮助"菜单

		breakItem.addActionListener(this);		//为"断开网络"菜单项注册监听器
		breakItem.setAccelerator(KeyStroke.getKeyStroke('B', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+B
		
		linkItem.addActionListener(this);		//为"接入网络"菜单项注册监听器
		linkItem.setAccelerator(KeyStroke.getKeyStroke('L', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+L
		
		exitItem.addActionListener(this);		//为"退出"菜单项注册监听器
		exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+Q
		
		shareItem.addActionListener(this);		//为"共享资源"菜单项注册监听器
		shareItem.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+S
		
		cancelItem.addActionListener(this);		//为"撤销共享"菜单项注册监听器
		cancelItem.setAccelerator(KeyStroke.getKeyStroke('C', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+C
	
		downloadItem.addActionListener(this);	//为"下载资源"菜单项注册监听器
		downloadItem.setAccelerator(KeyStroke.getKeyStroke('D', java.awt.Event.ALT_MASK, true));//设置组合键ALT+D

		helpItem.addActionListener(this);		//为"使用说明"菜单项注册监听器
		helpItem.setAccelerator(KeyStroke.getKeyStroke('H', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+H
		
		authorItem.addActionListener(this);		//为"作者信息"菜单项注册监听器
		authorItem.setAccelerator(KeyStroke.getKeyStroke('A', java.awt.Event.ALT_MASK, true));	//设置组合键ALT+A
		return menuBar;
	}

	//事件触发处理
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == timer) {
			TimerEventManager();		//处理计时器事件
		}
		else if(e.getSource() == breakItem) {
			BreakItemEventManager();	//处理"断开网络"菜单项事件
		}
		else if(e.getSource() == linkItem) {
			linkItemEventManager();		//处理"接入网络"菜单项事件
		}
		else if(e.getSource() == exitItem) {
			ExitItemEventManager();		//处理"退出"菜单项事件
		}
		else if(e.getSource() == shareItem) {
			ShareEventManager();		//处理"共享资源"菜单项事件
		}
		else if(e.getSource() == cancelItem) {
			CancelEventManager();		//处理"撤销共享"菜单项事件
		}
		else if(e.getSource() == downloadItem) {
			DownLoadEventManager();		//处理"下载文件"菜单项事件
		}
		else if(e.getSource() == helpItem) {
			HelpItemEventManager();		//处理"帮助文档"菜单项事件
		}
		else if(e.getSource() == authorItem) {
			AuthorItemEventManager();	//处理"作者信息"菜单项事件
		}
		else if(e.getSource() == SearchStyleComboBox) {
			ComboBoxEventManager();		//处理"下拉组件"事件
		}
		else if((JButton)e.getSource() == SearchBtn) {
			SearchEventManager();		//处理"搜索资源"事件
		}
		else if((JButton)e.getSource() == ShareBtn) {
			ShareEventManager();		//处理"共享资源"按钮事件
		}
		else if((JButton)e.getSource() == CancelBtn) {
			CancelEventManager();		//处理"撤销共享"按钮事件
		}
		else if((JButton)e.getSource() == DownloadBtn) {
			DownLoadEventManager();		//处理"下载文件"按钮事件
		}
		else if(e.getSource() == TaskListBtn) {
			TaskListEventManager();		//处理"任务列表"按钮事件
		}
	}
	
	//处理计时器事件
	private void TimerEventManager() {
		//定时获取系统时间，更新到主窗口实时时间标签
		Time SystemTime = new Time(System.currentTimeMillis());
		SystemTimeLabel.setText(SystemTime.toString());

		//检查当前状态是否为"登录中"
		if(isLogin == true) {
			UserName = UserNameText.getText();	//获取文本框文本，即登陆用户名
			if(UserName.equals("") == true) {	//若用户名为空，则返回，否则登陆成功
				return;
			}
			
			isLogin = false;									//修改登录状态为"已登陆"
			UserImageLabel.setIcon(UserHasLoginImage);			//设置登陆头像为"已登陆"状态
			LocalShareLabel.setIcon(LocalResHasloginImage);		//切换[本地资源表单标签]为"已登陆"状态
			SearchResultLabel.setIcon(SearchResHasloginImage);	//切换[网络资源表单标签]为"已登陆"状态
			
			SysMsgAdv.MsgAdv("\n欢迎新用户 " + UserName + " 加入P2P网络！");	//系统消息广播
			
			this.setFocusableWindowState(true);	//恢复主窗口焦点
			this.setEnabled(true);				//恢复主窗口可操作
			this.setAlwaysOnTop(true);			//设置窗口置顶
		}
		
		this.setAlwaysOnTop(false);		//取消窗口置顶
		return;
	}
	
	//处理"断开网络"菜单项事件
	private void BreakItemEventManager() {
		
		if(taskListWindow.GetCurrentRecordNum() > 0) {
			String[] option = {"断开网络", "取消操作"};
			int selectId = JOptionPane.showOptionDialog(mainPanel, "当前有任务正在运行，你确定要断开网络吗？\n任务一旦中断则不可恢复！", "退出程序消息", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
			if(selectId == 1) {
				return;
			}
		}
		
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "网络已断开！请勿重复操作。");
			return;
		}
		//完全断开网络
		JxtaPlatform.stopJxtaPlatform();
		isLinkNetwork = false;
		
		UserImageLabel.setIcon(UserNotLoginImage);			//设置登陆头像为"未登陆"状态
		LocalShareLabel.setIcon(LocalResNotloginImage);		//切换[本地资源表单标签]为"未登陆"状态
		SearchResultLabel.setIcon(SearchResNotloginImage);	//切换[网络资源表单标签]为"未登陆"状态
		return;
	}
	
	//处理"接入网络"菜单项事件
	private void linkItemEventManager() {
		if(isLinkNetwork == true) {
			JOptionPane.showMessageDialog(null, "已接入网络！请勿重复操作。");
			return;
		}
		//重新接入网络
		JxtaPlatform.startJxtaPlatform();
		isLinkNetwork = true;
		
		//刷新本地共享资源表单
		JxtaServer.RefreshLocalResTable();
		
		UserImageLabel.setIcon(UserHasLoginImage);			//设置登陆头像为"已登陆"状态
		LocalShareLabel.setIcon(LocalResHasloginImage);		//切换[本地资源表单标签]为"已登陆"状态
		SearchResultLabel.setIcon(SearchResHasloginImage);	//切换[网络资源表单标签]为"已登陆"状态
		
		JOptionPane.showMessageDialog(null, "由于重新接入网络，已清空本地缓存。\n此前配置文件已丢失，本地发布的所有共享资源已失效。");
		return;
	}
	
	//处理"退出"菜单项事件
	private void ExitItemEventManager() {
		
		if(taskListWindow.GetCurrentRecordNum() > 0) {
			String[] option = {"确定退出", "取消操作"};
			int selectId = JOptionPane.showOptionDialog(mainPanel, "当前有任务正在运行，你确定要退出程序吗？\n任务一旦中断则不可恢复！", "退出程序消息", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
			if(selectId == 1) {
				return;
			}
		}
		
		SysMsgAdv.MsgAdv("\n正在退出系统...");	//广播
		timer.stop();						//停止计时器
		this.dispose();						//注销主窗口组件
		this.setVisible(false);				//设置主窗口不可见
		JxtaPlatform.stopJxtaPlatform();	//完全退出Jxta网络
		System.exit(0);						//退出
		return;
	}
	
	//处理"帮助文档"菜单项事件	
	private void HelpItemEventManager() {
		new HelpDocWindow();	//打开帮助窗口
		return;
	}

	//处理"作者信息"菜单项事件
	private void AuthorItemEventManager() {
		JOptionPane.showMessageDialog(null, 
				"作者   : 林蔓虹\n" +
				"班级   : 08计算机科学与技术1班\n" +
				"学校   : 2008314122\n" +
				"项目   : 基于P2P技术的文件共享系统\n"
				);
		return;
	}
		
	//处理下拉组件事件
	private void ComboBoxEventManager() {
		//若当前选择"搜索所有资源"，置搜索框文本为通配符"*"
		if(SearchStyleComboBox.getSelectedIndex() == 2) {
			SearchTextField.setText("*");
			SearchTextField.setEditable(false);
		}
		else {
			SearchTextField.setEditable(true);
		}
		return;
	}
	
	//处理"搜索资源"事件
	private void SearchEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "请先接入网络！");
			return;
		}
		
		//获取搜索框文本，若为空则返回
		String stext = SearchTextField.getText();
		if(stext.equals("")) {
			SysMsgAdv.MsgAdv("\n请输入搜索内容！");
			return;
		}
		
		//获取当前下拉选项的索引index，根据index决定搜索方式
		int index = SearchStyleComboBox.getSelectedIndex();
		switch(index) {
			case 0:		//按资源搜索
				SearchText = "*" + stext + "*";
				break;
			case 1:		//按发布者搜索
				SearchText = "*" + stext + "*";
				break;
			case 2:		//搜索全网资源
				SearchText = "*";
				break;
		} 
		
		//取消资源搜索结果表单中的下载记录标记
		for(int i = 0; i < GlobalConstAndTag.TableLength; i++) {
			GlobalConstAndTag.isDownloading[i] = false;
		}
		SysMsgAdv.MsgAdv("\n搜索资源中...");

		//调用本地节点的Client服务的搜索功能，搜索网络资源
		new JxtaClientForSearch(SearchText).start();
		return;
	}

	//处理"共享资源"按钮事件
	private void ShareEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "请先接入网络！");
			return;
		}
		
		JFileChooser chooseFile = new JFileChooser();	//初始化并打开"打开文件"对话框
		chooseFile.setDialogTitle("选择共享文件");			//设置对话框标题
		chooseFile.setApproveButtonText("确定");			//设置对话框功能按钮文本为"确定"
		FileFilters.addFileFilters(chooseFile);			//为对话框设置文件过滤器
		
		//当用户按下"打开文件"对话框的"确定"按钮时
		if(chooseFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooseFile.getSelectedFile();	//获取所选择的文件
			long FileSize = file.length();				//获取文件大小（字节数）
			String FilePath = file.getPath();			//获取所选择文件的抽象路径
		    String FileName = file.getName();			//获取所选择文件的文件名
		    String AdvName = UserName + ":" + FileName;	//组装待共享文件的管道广告的<Name>属性值
		    
		    //调用本地节点的Server服务的共享功能，共享本地资源
		    new JxtaServer(AdvName, FileSize, FilePath).start();
		}
		return;
	}
	
	//处理"撤销共享"按钮事件
	private void CancelEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "请先接入网络！");
			return;
		}
		
		//获取本地资源表单被选中的行，若没有选中任何行，则返回
		int selectRow = LocalResTable.getSelectedRow();
		if(selectRow == -1) {
			return;
		}
		
		//获取被选择行的资源名，若资源名为空（即该行为空），则返回
		String ResName = LocalResData.get(selectRow).get(1);
		if(ResName.equals("")) {
			return;
		}
		
		int choose = JOptionPane.showConfirmDialog(null, "确定撤销该共享资源吗？", "信息", JOptionPane.YES_NO_OPTION);
		if(choose == JOptionPane.YES_OPTION) {
			//移除指定的本地广告
			JxtaPlatform.removeLocalAdvertisement(UserName + ":" + ResName);
			
			//更新本地共享资源表单
			int ResNum = JxtaServer.RefreshLocalResTable();
			
			SysMsgAdv.MsgAdv("\n成功移除共享资源 " + ResName + " 。");
			MainWindow.SysMsgAdv.MsgAdv("\n当前剩余 " + ResNum + " 个共享资源。");
		}
		return;
	}

	//处理"下载文件"按钮事件
	private void DownLoadEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "请先接入网络！");
			return;
		}
		
		//获取搜索结果表单被选中的行，若没有选中任何行，则返回
		int selectRow = NetResTable.getSelectedRow();
		if(selectRow == -1) {
			return;
		}
		
		//若选中的资源已经在本地下载中，则警告并返回
		if(GlobalConstAndTag.isDownloading[selectRow] == true) {
			SysMsgAdv.MsgAdv("\n该资源正在下载中，请勿重复下载！");
			JOptionPane.showMessageDialog(null, "该资源正在下载中，请勿重复下载");
			return;
		}
		
		//获取被选择行的资源名，若资源名为空（即该行为空），则返回
		String ResName = NetResData.get(selectRow).get(1);
		if(ResName.equals("")) {
			return;
		}
		
		//获取被选择资源的发布者姓名，并与ResName组合为广告<Name>属性的值
		String PublisherName = NetResData.get(selectRow).get(2);
		String AdvName = PublisherName + ":" + ResName;
		
		//调用本地节点的Client服务的连接功能，请求资源提供者的Server端提供数据传输
		new JxtaClientForReceive(AdvName, selectRow, this).start();
		return;
	}

	//处理"任务列表"按钮事件
	private void TaskListEventManager() {
		taskListWindow.setVisible(true);	//使得任务列表可见
	}
}
