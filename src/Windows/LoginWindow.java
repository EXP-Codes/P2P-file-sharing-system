package Windows;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import JxtaService.JxtaPlatform;
import Toolkit.Fonts;
import Toolkit.GetScreenSize;

/*
 * 登陆框
 */
public class LoginWindow extends JFrame implements ActionListener {

	//用于标识本类（本对象）的唯一性，由系统自动生成
	private static final long serialVersionUID = 4711304864899610818L;

	//登陆框宽高
	private final int winWidth = 300;
	private final int winHeight = 150;
	
	private JPanel rootPanel;	//登陆框根面板
	
	private JTextField UserNameTextLogin;	//登陆框用户名文本框
	private JTextField UserNameTextMain;	//主界面用户名文本框
	
	private JButton OKBtn;	//"确认"按钮
	private JButton ExitBtn;//"退出"按钮
	
	public LoginWindow(JTextField userNameTextMain) {
		this.UserNameTextMain = userNameTextMain;
		InitLoginWindow();	//初始化登陆窗口
		AddComponents();	//添加组件
	}

	//初始化登陆窗口
	private void InitLoginWindow() {
		this.setTitle("登陆");		//设置窗口标题
		this.setVisible(true);		//窗体可见
		this.setAlwaysOnTop(true);	//设置登录窗口总是在置顶
		this.setResizable(false);	//禁止拉伸窗口,最大化按钮无效
		this.setSize(winWidth, winHeight);//设置窗口尺寸
		
		//设置窗口位置（屏幕正中）
		this.setLocation((int)(GetScreenSize.screenWidth/2 - winWidth/2), (int)(GetScreenSize.screenHeight/2 - winHeight/2));
		
		//设置窗体图标
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		//设置缺省关闭窗口操作(不登陆则关闭所有窗口)
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		rootPanel = new JPanel();				//初始化根面板
		rootPanel.setBackground(Color.white);	//设置根面板背景色:白色
		this.getContentPane().add(rootPanel);	//把根面板添加到登陆框
		rootPanel.setLayout(null);				//撤销根面板相对布局方式，为了使用绝对布局方式
		
		try {
			//允许登陆框具有当前系统提供的窗口装饰
			LoginWindow.setDefaultLookAndFeelDecorated(true);
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
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				MainWindow.SysMsgAdv.MsgAdv("\n正在退出系统...");
				e.getWindow().setVisible(false);//设置窗口不可见
				e.getWindow().dispose();		//注销窗口中的所有组件
				JxtaPlatform.stopJxtaPlatform();//完全退出Jxta网络
				System.exit(0);					//退出
			}
		});
		return;
	}
	
	//添加组件
	private void AddComponents() {
		//添加提示标签"用户名"
		JLabel tipsLabel = new JLabel(new ImageIcon(LoginWindow.class.getResource("/LoginTips.png")));
		tipsLabel.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		rootPanel.add(tipsLabel);
		tipsLabel.setBounds(15, 15, 80, 30);
		
		//添加用户名文本框
		UserNameTextLogin = new JTextField("UserName");
		UserNameTextLogin.setFont(Fonts.ChineseStyle_SongTi_LR_16);
		rootPanel.add(UserNameTextLogin);
		UserNameTextLogin.setBounds(100, 15, 170, 30);
		UserNameTextLogin.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				//当按下回车键时
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					//获取文本框文本，若用户名为空，则返回
					String UserName = UserNameTextLogin.getText();
					if(UserName.equals("") == true) {
						MainWindow.SysMsgAdv.MsgAdv("\n用户名不能为空！");
						return;
					}
					//设置主窗口的用户名文本框的内容
					UserNameTextMain.setText(UserName);
					
					setVisible(false);	//设置登陆框不可见
					dispose();			//注销登陆框组件
					return;
				}
			}
		});
		
		//添加"确认"按钮
		OKBtn = new JButton("确定");
		OKBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(OKBtn);
		OKBtn.setBounds(30, 73, 90, 40);
		OKBtn.addActionListener(this);
		
		//添加"退出"按钮
		ExitBtn = new JButton("退出");
		ExitBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(ExitBtn);
		ExitBtn.setBounds(165, 73, 90, 40);
		ExitBtn.addActionListener(this);
		return;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//处理"确定"按钮事件
		if((JButton) e.getSource() == OKBtn) {
			//获取文本框文本，若用户名为空，则返回
			String UserName = UserNameTextLogin.getText();
			if(UserName.equals("") == true) {
				MainWindow.SysMsgAdv.MsgAdv("\n用户名不能为空！");
				return;
			}
			//设置主窗口的用户名文本框的内容
			UserNameTextMain.setText(UserName);
			
			this.setVisible(false);	//设置登陆框不可见
			this.dispose();			//注销登陆框组件
			return;
		}
		//处理"退出"按钮事件
		else if((JButton) e.getSource() == ExitBtn) {
			MainWindow.SysMsgAdv.MsgAdv("\n正在退出系统...");
			this.setVisible(false);			//设置登陆框不可见
			this.dispose();					//注销登陆框组件
			JxtaPlatform.stopJxtaPlatform();//完全退出Jxta网络
			System.exit(0);					//退出
		}
	}
}
