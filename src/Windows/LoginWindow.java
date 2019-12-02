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
 * ��½��
 */
public class LoginWindow extends JFrame implements ActionListener {

	//���ڱ�ʶ���ࣨ�����󣩵�Ψһ�ԣ���ϵͳ�Զ�����
	private static final long serialVersionUID = 4711304864899610818L;

	//��½����
	private final int winWidth = 300;
	private final int winHeight = 150;
	
	private JPanel rootPanel;	//��½������
	
	private JTextField UserNameTextLogin;	//��½���û����ı���
	private JTextField UserNameTextMain;	//�������û����ı���
	
	private JButton OKBtn;	//"ȷ��"��ť
	private JButton ExitBtn;//"�˳�"��ť
	
	public LoginWindow(JTextField userNameTextMain) {
		this.UserNameTextMain = userNameTextMain;
		InitLoginWindow();	//��ʼ����½����
		AddComponents();	//������
	}

	//��ʼ����½����
	private void InitLoginWindow() {
		this.setTitle("��½");		//���ô��ڱ���
		this.setVisible(true);		//����ɼ�
		this.setAlwaysOnTop(true);	//���õ�¼�����������ö�
		this.setResizable(false);	//��ֹ���촰��,��󻯰�ť��Ч
		this.setSize(winWidth, winHeight);//���ô��ڳߴ�
		
		//���ô���λ�ã���Ļ���У�
		this.setLocation((int)(GetScreenSize.screenWidth/2 - winWidth/2), (int)(GetScreenSize.screenHeight/2 - winHeight/2));
		
		//���ô���ͼ��
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		//����ȱʡ�رմ��ڲ���(����½��ر����д���)
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		rootPanel = new JPanel();				//��ʼ�������
		rootPanel.setBackground(Color.white);	//���ø���屳��ɫ:��ɫ
		this.getContentPane().add(rootPanel);	//�Ѹ������ӵ���½��
		rootPanel.setLayout(null);				//�����������Բ��ַ�ʽ��Ϊ��ʹ�þ��Բ��ַ�ʽ
		
		try {
			//�����½����е�ǰϵͳ�ṩ�Ĵ���װ��
			LoginWindow.setDefaultLookAndFeelDecorated(true);
			//���ݳ�������ƽ̨�����ô������
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
		//����"���ڹر�"����
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				MainWindow.SysMsgAdv.MsgAdv("\n�����˳�ϵͳ...");
				e.getWindow().setVisible(false);//���ô��ڲ��ɼ�
				e.getWindow().dispose();		//ע�������е��������
				JxtaPlatform.stopJxtaPlatform();//��ȫ�˳�Jxta����
				System.exit(0);					//�˳�
			}
		});
		return;
	}
	
	//������
	private void AddComponents() {
		//�����ʾ��ǩ"�û���"
		JLabel tipsLabel = new JLabel(new ImageIcon(LoginWindow.class.getResource("/LoginTips.png")));
		tipsLabel.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		rootPanel.add(tipsLabel);
		tipsLabel.setBounds(15, 15, 80, 30);
		
		//����û����ı���
		UserNameTextLogin = new JTextField("UserName");
		UserNameTextLogin.setFont(Fonts.ChineseStyle_SongTi_LR_16);
		rootPanel.add(UserNameTextLogin);
		UserNameTextLogin.setBounds(100, 15, 170, 30);
		UserNameTextLogin.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				//�����»س���ʱ
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					//��ȡ�ı����ı������û���Ϊ�գ��򷵻�
					String UserName = UserNameTextLogin.getText();
					if(UserName.equals("") == true) {
						MainWindow.SysMsgAdv.MsgAdv("\n�û�������Ϊ�գ�");
						return;
					}
					//���������ڵ��û����ı��������
					UserNameTextMain.setText(UserName);
					
					setVisible(false);	//���õ�½�򲻿ɼ�
					dispose();			//ע����½�����
					return;
				}
			}
		});
		
		//���"ȷ��"��ť
		OKBtn = new JButton("ȷ��");
		OKBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(OKBtn);
		OKBtn.setBounds(30, 73, 90, 40);
		OKBtn.addActionListener(this);
		
		//���"�˳�"��ť
		ExitBtn = new JButton("�˳�");
		ExitBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(ExitBtn);
		ExitBtn.setBounds(165, 73, 90, 40);
		ExitBtn.addActionListener(this);
		return;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//����"ȷ��"��ť�¼�
		if((JButton) e.getSource() == OKBtn) {
			//��ȡ�ı����ı������û���Ϊ�գ��򷵻�
			String UserName = UserNameTextLogin.getText();
			if(UserName.equals("") == true) {
				MainWindow.SysMsgAdv.MsgAdv("\n�û�������Ϊ�գ�");
				return;
			}
			//���������ڵ��û����ı��������
			UserNameTextMain.setText(UserName);
			
			this.setVisible(false);	//���õ�½�򲻿ɼ�
			this.dispose();			//ע����½�����
			return;
		}
		//����"�˳�"��ť�¼�
		else if((JButton) e.getSource() == ExitBtn) {
			MainWindow.SysMsgAdv.MsgAdv("\n�����˳�ϵͳ...");
			this.setVisible(false);			//���õ�½�򲻿ɼ�
			this.dispose();					//ע����½�����
			JxtaPlatform.stopJxtaPlatform();//��ȫ�˳�Jxta����
			System.exit(0);					//�˳�
		}
	}
}
