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
 * P2P�ļ�����ϵͳ�������棨�����ڣ�����������
 * �����Ǳ�ϵͳ�ĺ�����
 * ����չʾ���ṩP2P�ļ�����ϵͳ�ĺ��Ĺ���
 */
public class MainWindow extends JFrame implements ActionListener {

	//���ڱ�ʶ���ࣨ�����󣩵�Ψһ�ԣ���ϵͳ�Զ�����
	private static final long serialVersionUID = 1163693238743966735L;
		
	//����壨�������µĵ�һ�������
	private JPanel mainPanel;
	
	//��ʱ��������������Ҫ�ṩʵʱϵͳ���ܣ�
	private Timer timer;

	//����Ƿ���"��½��"״̬��������ϵͳʱ��
	private boolean isLogin = true;
	
	//����Ƿ��ѽ�������
	private boolean isLinkNetwork = false;
	
	//ϵͳȫ����Ϣ�㲥��
	public static GlobalMsgAdv SysMsgAdv;
	
	//"���������б�"����
	public static TaskListWindow taskListWindow;
	
	//�������ص�½�û���Ϣ�����
	private JLabel UserImageLabel;			//�û�ͷ���ǩ
	private JTextField UserNameText;		//�û������ı���
	private String UserName;				//�û�����
	private ImageIcon UserIcon;				//�û�����ǰ��Сͼ��
	private ImageIcon UserNotLoginImage;	//�û���δ��½״̬ͷ��
	private ImageIcon UserHasLoginImage;	//�û����ѵ�½״̬ͷ��
	
	//���ع�����Դ��
	private Vector<String>	LocalResColName;			//����������ͷ��
	public static Vector<Vector<String>> LocalResData;	//��ģ�ͣ���ά��
	public static MyTableComponent LocalResTable;		//�����
	
	//���繲����Դ��
	private Vector<String>	NetResColName;				//����������ͷ��
	public static Vector<Vector<String>> NetResData;	//��ģ�ͣ���ά��
	public static MyTableComponent NetResTable;			//�����

	//��ǩ�������������
	private JLabel LocalShareLabel;		//"���ع�����Դ"��ǩ
	private JLabel SearchResultLabel;	//"��Դ�������"��ǩ
	private JLabel GlobalMsgLabel;		//"ϵͳ��Ϣ"��ǩ
	private JLabel SysTimeIlluLabel;	//"ϵͳʱ��"��ǩ
	private JLabel SystemTimeLabel;		//��ʾʵʱϵͳʱ��ı�ǩ
	
	//�������
	private JComboBox SearchStyleComboBox;	//"������ʽ"������
	private JTextField SearchTextField;		//�����ı���
	private String SearchText = null;		//�����ı�
	private JButton SearchBtn;				//"����"��ť
	
	//��ť
	private JButton ShareBtn;		//"�����ļ�"��ť
	private JButton CancelBtn;		//"��������"��ť
	private JButton DownloadBtn;	//"�����ļ�"��ť
	private JButton TaskListBtn;	//"�����б�"��ť
	
	//�˵����Ĳ˵���
	private JMenuItem breakItem;	//"�Ͽ�����"�˵���
	private JMenuItem linkItem;		//"��������"�˵���
	private JMenuItem exitItem;		//"�˳�"�˵���
	private JMenuItem shareItem;	//"�����ļ�"�˵���
	private JMenuItem cancelItem;	//"��������"�˵���
	private JMenuItem downloadItem;	//"�����ļ�"�˵���
	private JMenuItem helpItem;		//"ʹ��˵��"�˵���
	private JMenuItem authorItem;	//"������Ϣ"�˵���
	
	//�˵���ͼ��
	private ImageIcon breakIcon;	//"�Ͽ�����"ͼ��
	private ImageIcon linkIcon;		//"��������"ͼ��
	private ImageIcon exitIcon;		//"�˳�"ͼ��
	private ImageIcon shareIcon;	//"�����ļ�"ͼ��
	private ImageIcon cancelIcon;	//"��������"ͼ��
	private ImageIcon downloadIcon;	//"�����ļ�"ͼ��
	private ImageIcon helpIcon;		//"ʹ��˵��"ͼ��
	private ImageIcon authorIcon;	//"������Ϣ"ͼ��
	
	//��ǩͼƬ
	private ImageIcon LocalResNotloginImage;	//"���ع�����Դ"��¼ǰͼƬ
	private ImageIcon LocalResHasloginImage;	//"���ع�����Դ"�ѵ�¼ͼƬ
	private ImageIcon SearchResNotloginImage;	//"��Դ�������"��¼ǰͼƬ
	private ImageIcon SearchResHasloginImage;	//"��Դ�������"�ѵ�¼ͼƬ
	private ImageIcon SysMsgImage;				//"ϵͳ��Ϣ"ͼƬ
	private ImageIcon SysTimeImage;				//"ϵͳʱ��"ͼƬ
	
	//���캯��
	public MainWindow(String windowTitle) {
		InitInfo();				//��ʼ��ȫ����Դ��Ϣ
		InitWindow(windowTitle);//��ʼ��������
		AddComponents();		//���������ӱ�Ҫ���
		LoginManager();			//�û���½����
	}

	//��ʼ��ȫ����Դ��Ϣ
	public void InitInfo() {
		taskListWindow = new TaskListWindow();	//��ʼ�����������б���
		timer = new Timer(1000, this);			//��ʼ����ʱ��,ÿ�����һ��������
		InitTable();							//��ʼ����ģ�ͼ�������
		ImportImage();							//����ͼƬ��Դ
		return;
	}

	//��ʼ��������
	private void InitWindow(String windowTitle) {
		//����������ͼ��
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		this.setTitle(windowTitle);			//�������������
		this.setResizable(false);			//��ֹ���촰��,��󻯰�ť��Ч
		this.setJMenuBar(MakeMenuBar());	//����������������Ĳ˵���
		
		mainPanel = new JPanel();				//��ʼ�������
		mainPanel.setBackground(Color.white);	//��������屳��ɫ:��ɫ
		this.getContentPane().add(mainPanel);	//���������ӵ���������
		mainPanel.setLayout(null);				//�����������Բ��ַ�ʽ������Ϊ��ʹ�þ��Բ��ַ�ʽ�����������
		
		try {
			//������������е�ǰϵͳ�ṩ�Ĵ���װ��
			MainWindow.setDefaultLookAndFeelDecorated(true);
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

		//ȡ��JFrame���ڵ�Ĭ�Ϲرջ���
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//��������"���ڹر�"����
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				
				if(taskListWindow.GetCurrentRecordNum() > 0) {
					String[] option = {"ȷ���ر�", "ȡ������"};
					int selectId = JOptionPane.showOptionDialog(mainPanel, "��ǰ�������������У���ȷ��Ҫ�رմ�����\n����һ���ж��򲻿ɻָ���", "�رմ�����Ϣ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
					if(selectId == 1) {
						return;
					}
				}
				
				SysMsgAdv.MsgAdv("\n�����˳�ϵͳ...");	//�㲥
				timer.stop();						//ֹͣ��ʱ��
				e.getWindow().setVisible(false);	//���ô��ڲ��ɼ�
				e.getWindow().dispose();			//ע�������е��������
				JxtaPlatform.stopJxtaPlatform();	//��ȫ�˳�Jxta����
				System.exit(0);						//�˳�
			}
		});
		return;
	}
	
	//���������ӱ�Ҫ���
	private void AddComponents() {
		AddUserHasLogin();	//����û���½��Ϣ��
		AddLabel();			//��ӱ�ǩ
		AddTextFiled();		//����ı���/��
		AddButton();		//��Ӱ�ť
		AddComboBox();		//���������
		AddTable();			//��ӱ�
		return;
	}
	
	//�û���½����
	private void LoginManager() {
		this.setFocusableWindowState(false);//������ʧȥ����
		this.setEnabled(false);				//���������ڣ���ֹ����
		new LoginWindow(UserNameText);		//�򿪵�½����
		new JxtaPlatform();					//����Jxta����ƽ̨
		isLinkNetwork = true;				//����ѽ�������
		timer.start();						//������ʱ��
		return;
	}
	
	//��ʼ����ģ�ͼ�������
	private void InitTable() {
		//���ع�����Դ��
		LocalResColName = new Vector<String>();		//��ʼ��������
		LocalResColName.add(0, "ID");				//���ñ���0�е�������ID ������
		LocalResColName.add(1, "      ��Դ����      ");	//���ñ���1�е���������Դ����
		
		LocalResData = new Vector<Vector<String>>();//��ʼ����
		for(int pr = 0; pr < GlobalConstAndTag.TableLength; pr++) {
			//���ñ���pr����������Ϊpr+1
			Vector<String> row = new Vector<String>();
			row.add(0, String.valueOf(pr + 1));
			
			//���õ�pr�е�������ֵΪ��ֵ
			for(int pc = 1; pc < LocalResColName.size(); pc++) {
				row.add(pc, "");
			}
			//��row��ӵ�LocalResInfo������ĩ
			LocalResData.add(row);
		}
		
		//���繲����Դ��
		NetResColName = new Vector<String>();	//��ʼ��������
		NetResColName.add(0, "ID");				//���ñ���0�е�������ID������
		NetResColName.add(1, "      ��Դ����      ");	//���ñ���1�е���������Դ����
		NetResColName.add(2, "������");			//���ñ���2�е�������������
		
		NetResData = new Vector<Vector<String>>();	//��ʼ����
		for(int fr = 0; fr < GlobalConstAndTag.TableLength; fr++) {
			//���ñ���fr����������Ϊfr+1
			Vector<String> row = new Vector<String>();
			row.add(0, String.valueOf(fr + 1));
			
			//���õ�fr�е�������ֵΪ��ֵ
			for(int fc = 1; fc < NetResColName.size(); fc++) {
				row.add(fc, "");
			}
			//��row��ӵ�NetResInfo������ĩ
			NetResData.add(row);
		}
		return;
	}

	//����ͼƬ��Դ
	private void ImportImage() {
		//�����½���ͼƬ
		UserNotLoginImage = new ImageIcon(MainWindow.class.getResource("/UserNotLogin.png"));
		UserHasLoginImage = new ImageIcon(MainWindow.class.getResource("/UserHasLogin.png"));
		UserIcon = new ImageIcon(MainWindow.class.getResource("/UserIcon.png"));
		
		//����˵���ͼ��
		breakIcon = new ImageIcon(MainWindow.class.getResource("/breakIcon.png"));
		linkIcon = new ImageIcon(MainWindow.class.getResource("/linkIcon.png"));
		exitIcon = new ImageIcon(MainWindow.class.getResource("/exitIcon.png"));
		shareIcon = new ImageIcon(MainWindow.class.getResource("/shareIcon.png"));
		cancelIcon = new ImageIcon(MainWindow.class.getResource("/cancelIcon.png"));
		downloadIcon = new ImageIcon(MainWindow.class.getResource("/downloadIcon.png"));
		helpIcon = new ImageIcon(MainWindow.class.getResource("/helpIcon.png"));
		authorIcon = new ImageIcon(MainWindow.class.getResource("/authorIcon.png"));
		
		//�����ǩͼƬ
		LocalResNotloginImage = new ImageIcon(MainWindow.class.getResource("/LocalResText_Notlogin.png"));
		LocalResHasloginImage = new ImageIcon(MainWindow.class.getResource("/LocalResText_Haslogin.png"));
		SearchResNotloginImage = new ImageIcon(MainWindow.class.getResource("/SearchResText_Notlogin.png"));
		SearchResHasloginImage = new ImageIcon(MainWindow.class.getResource("/SearchResText_Haslogin.png"));
		SysMsgImage = new ImageIcon(MainWindow.class.getResource("/SysMsg.png"));
		SysTimeImage = new ImageIcon(MainWindow.class.getResource("/SysTime.png"));
		return;
	}
		
	//����û���½��Ϣ��
	private void AddUserHasLogin() {
		//��ʼ��
		UserImageLabel = new JLabel(UserNotLoginImage, JLabel.CENTER);	//�û�ͷ���ǩ��δ��½״̬��
		JLabel UserIconLabel = new JLabel(UserIcon, JLabel.CENTER);		//�û�����ǰ��Сͼ���ǩ
		UserNameText = new JTextField("", 12);							//�û������ı�������Ĭ�ϳ���Ϊ12
		UserNameText.setEditable(false);								//��ֹ�޸��û������ı�������

		//Ϊ�û�ͷ���ǩ�������
		JPanel UserImagePanel = new JPanel();
		UserImagePanel.add(UserImageLabel);
		UserImagePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));//�趨���߽�
		
		//Ϊ�û�����ǰ��Сͼ���ǩ�������
		JPanel UserIconPanel = new JPanel();
		UserIconPanel.add(UserIconLabel);
		UserIconPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		
		//Ϊ�û������ı��򴴽���壬�������ı�������
		JPanel UserNamePanel = new JPanel();
		UserNamePanel.add(UserNameText);
		UserNamePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		UserNameText.setFont(Fonts.ChineseStyle_SongTi_LR_16);
		
		//����[�û�ͼ���������], ��[�û�ͼ�����]��[�û��������]��ӵ�����
		JPanel UserIconNamePanel = new JPanel(new BorderLayout());
		UserIconNamePanel.add(UserIconPanel, "West");
		UserIconNamePanel.add(UserNamePanel, "Center");
		
		//��[�û�ͷ�����]��[�û�ͼ���������]��ӵ�[�û���Ϣ���]
		JPanel UserInfoPanel = new JPanel(new BorderLayout());	//�û���Ϣ��壬��Ե���ַ�ʽ
		UserInfoPanel.add(UserImagePanel, "Center");			//[�û�ͷ�����]�����������
		UserInfoPanel.add(UserIconNamePanel, "South");			//[�û�ͼ���������]��������ϱ�
		
		//��[�û���Ϣ���]��ӵ�[�����]
		mainPanel.add(UserInfoPanel);
		UserInfoPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		UserInfoPanel.setBounds(15, 15, 150, 165);		//�������λ�úʹ�С: (x,y,w,h) ���Ͻ�λ��(x,y) ����ߴ�w*h
		return;
	}

	//��ӱ�ǩ���
	private void AddLabel() {
		//"���ع�����Դ"�б��ǩ
		LocalShareLabel = new JLabel(LocalResNotloginImage);	//���ñ�ǩȱʡ����ΪͼƬ
		mainPanel.add(LocalShareLabel);
		LocalShareLabel.setBounds(190, 55, 200, 41);
		
		//"��Դ�������"�б��ǩ
		SearchResultLabel = new JLabel(SearchResNotloginImage);
		mainPanel.add(SearchResultLabel);
		SearchResultLabel.setBounds(460, 55, 200, 41);
		
		//"ϵͳ��Ϣ"��ǩ
		GlobalMsgLabel = new JLabel(SysMsgImage);
		mainPanel.add(GlobalMsgLabel);
		GlobalMsgLabel.setBounds(15, 440, 65, 75);
		
		//"ϵͳʱ��"��ǩ
		SysTimeIlluLabel = new JLabel(SysTimeImage);
		mainPanel.add(SysTimeIlluLabel);
		SysTimeIlluLabel.setBounds(545, 440, 150, 45);
		
		//"ʵʱϵͳʱ��"��ǩ
		Time SystemTime = new Time(System.currentTimeMillis());	//��ȡϵͳ��ǰʱ�䣨��ȷ�����룩
		SystemTimeLabel = new JLabel(SystemTime.toString());	//�ѵ�ǰϵͳʱ��д�뵽��ǩ�ı�
		SystemTimeLabel.setFont(Fonts.EnglishStyle_Consolas_I_22);
		mainPanel.add(SystemTimeLabel);
		SystemTimeLabel.setBounds(570, 490, 150, 20);
		return;
	}

	//����ı���/�����
	private void AddTextFiled() {
		//"ϵͳ��Ϣ"�ı���
		SysMsgAdv = new GlobalMsgAdv(mainPanel);
		
		//"����"�ı���
		SearchTextField = new JTextField("����д��������");	//�����ı���ȱʡ����
		SearchTextField.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		mainPanel.add(SearchTextField);	
		SearchTextField.setBounds(360, 15, 255, 30);
		SearchTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				//�����»س���ʱ
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					SearchEventManager();	//����"������Դ"�¼�
				}
			}
		});
		return;
	}
	
	//��Ӱ�ť���
	private void AddButton() {
		//"�����ļ�"��ť
		ShareBtn = new JButton("�����ļ�");
		ShareBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(ShareBtn);
		ShareBtn.setBounds(15, 195, 150, 50);
		ShareBtn.addActionListener(this);		//���ð�ť��������
		
		//"��������"��ť
		CancelBtn = new JButton("��������");
		CancelBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(CancelBtn);
		CancelBtn.setBounds(15, 255, 150, 50);
		CancelBtn.addActionListener(this);
				
		//"�����ļ�"��ť
		DownloadBtn = new JButton("�����ļ�");
		DownloadBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(DownloadBtn);
		DownloadBtn.setBounds(15, 315, 150, 50);
		DownloadBtn.addActionListener(this);
		
		//"�����б�"��ť
		TaskListBtn = new JButton("�����б�");
		TaskListBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_25);
		mainPanel.add(TaskListBtn);
		TaskListBtn.setBounds(15, 375, 150, 50);
		TaskListBtn.addActionListener(this);
		
		//"����"��ť
		SearchBtn = new JButton("����");
		SearchBtn.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		mainPanel.add(SearchBtn);
		SearchBtn.setBounds(620, 15, 80, 30);
		SearchBtn.addActionListener(this);
		return;
	}
	
	//���"������ʽ"������
	private void AddComboBox() {
		String[] SearchStyle = {"����Դ����", 
								"������������",
								"����ȫ����Դ"};		//����ѡ����
		SearchStyleComboBox = new JComboBox(SearchStyle);
		SearchStyleComboBox.setFont(Fonts.ChineseStyle_KaiTi_LR_17);
		mainPanel.add(SearchStyleComboBox);
		SearchStyleComboBox.setBounds(185, 15, 170, 30);
		SearchStyleComboBox.addActionListener(this);
		return;
	}
		
	//��ӱ����
	private void AddTable() {
		/*
		 * ���ع�����Դ��
		 */
		//�ñ�ģ�ͼ���������ʼ�������
		LocalResTable = new MyTableComponent(LocalResData, LocalResColName);
		//Ϊ�����ô�����������壬��ֱ��ˮƽ������Ϊ��Ҫʱ��ʾ
		JScrollPane LocalResPanel = new JScrollPane(LocalResTable, 
													JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(LocalResPanel);
		LocalResPanel.setBounds(185, 105, 220, 320);
		
		/*
		 * ��Դ���������
		 */
		//�ñ�ģ�ͼ���������ʼ�������
		NetResTable = new MyTableComponent(NetResData, NetResColName);
		//Ϊ�����ô�����������壬��ֱ��ˮƽ������Ϊ��Ҫʱ��ʾ
		JScrollPane NetResPanel = new JScrollPane(NetResTable, 
													JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(NetResPanel);	
		NetResPanel.setBounds(425, 105, 280, 320);
		return;
	}
	
	//���ô��ڲ˵����������ز˵������
	private JMenuBar MakeMenuBar() {
		//���ò˵�ʼ������㣬������������ڵ�
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		JMenuBar menuBar = new JMenuBar();	//�����˵���
		
		JMenu sysMenu = new JMenu("ϵͳ");	//����"ϵͳ"�˵�
		JMenu toolMenu = new JMenu("����");	//����"����"�˵�
		JMenu helpMenu = new JMenu("����");	//����"����"�˵�
		
		breakItem = new JMenuItem("�Ͽ�����", breakIcon);		//��ʼ��"�Ͽ�����"�˵���
		linkItem = new JMenuItem("��������", linkIcon);		//��ʼ��"��������"�˵���
		exitItem = new JMenuItem("�˳�", exitIcon);			//��ʼ��"�˳�"�˵���
		shareItem = new JMenuItem("������Դ", shareIcon);		//��ʼ��"������Դ"�˵���
		cancelItem = new JMenuItem("��������", cancelIcon);	//��ʼ��"��������"�˵���
		downloadItem = new JMenuItem("�����ļ�", downloadIcon);//��ʼ��"������Դ"�˵���
		helpItem = new JMenuItem("ʹ��˵��", helpIcon);		//��ʼ��"ʹ��˵��"�˵���
		authorItem = new JMenuItem("������Ϣ", authorIcon);	//��ʼ��"������Ϣ"�˵���
		
		menuBar.add(sysMenu);		//��"ϵͳ"�˵���ӵ��˵���
		menuBar.add(toolMenu);		//��"����"�˵���ӵ��˵���
		menuBar.add(helpMenu);		//��"����"�˵���ӵ��˵���

		sysMenu.add(breakItem);		//�� "�Ͽ�����"�˵�����ӵ�"ϵͳ"�˵�
		sysMenu.add(linkItem);		//�� "��������"�˵�����ӵ�"ϵͳ"�˵�
		sysMenu.add(exitItem);		//�� "�˳�"�˵�����ӵ�"ϵͳ"�˵�
		toolMenu.add(shareItem);	//�� "������Դ"�˵�����ӵ�"����"�˵�
		toolMenu.add(cancelItem);	//�� "��������"�˵�����ӵ�"����"�˵�
		toolMenu.add(downloadItem);	//�� "������Դ"�˵�����ӵ�"����"�˵�
		helpMenu.add(helpItem);		//�� "ʹ��˵��"�˵�����ӵ�"����"�˵�
		helpMenu.add(authorItem);	//�� "������Ϣ"�˵�����ӵ�"����"�˵�

		breakItem.addActionListener(this);		//Ϊ"�Ͽ�����"�˵���ע�������
		breakItem.setAccelerator(KeyStroke.getKeyStroke('B', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+B
		
		linkItem.addActionListener(this);		//Ϊ"��������"�˵���ע�������
		linkItem.setAccelerator(KeyStroke.getKeyStroke('L', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+L
		
		exitItem.addActionListener(this);		//Ϊ"�˳�"�˵���ע�������
		exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+Q
		
		shareItem.addActionListener(this);		//Ϊ"������Դ"�˵���ע�������
		shareItem.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+S
		
		cancelItem.addActionListener(this);		//Ϊ"��������"�˵���ע�������
		cancelItem.setAccelerator(KeyStroke.getKeyStroke('C', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+C
	
		downloadItem.addActionListener(this);	//Ϊ"������Դ"�˵���ע�������
		downloadItem.setAccelerator(KeyStroke.getKeyStroke('D', java.awt.Event.ALT_MASK, true));//������ϼ�ALT+D

		helpItem.addActionListener(this);		//Ϊ"ʹ��˵��"�˵���ע�������
		helpItem.setAccelerator(KeyStroke.getKeyStroke('H', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+H
		
		authorItem.addActionListener(this);		//Ϊ"������Ϣ"�˵���ע�������
		authorItem.setAccelerator(KeyStroke.getKeyStroke('A', java.awt.Event.ALT_MASK, true));	//������ϼ�ALT+A
		return menuBar;
	}

	//�¼���������
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == timer) {
			TimerEventManager();		//�����ʱ���¼�
		}
		else if(e.getSource() == breakItem) {
			BreakItemEventManager();	//����"�Ͽ�����"�˵����¼�
		}
		else if(e.getSource() == linkItem) {
			linkItemEventManager();		//����"��������"�˵����¼�
		}
		else if(e.getSource() == exitItem) {
			ExitItemEventManager();		//����"�˳�"�˵����¼�
		}
		else if(e.getSource() == shareItem) {
			ShareEventManager();		//����"������Դ"�˵����¼�
		}
		else if(e.getSource() == cancelItem) {
			CancelEventManager();		//����"��������"�˵����¼�
		}
		else if(e.getSource() == downloadItem) {
			DownLoadEventManager();		//����"�����ļ�"�˵����¼�
		}
		else if(e.getSource() == helpItem) {
			HelpItemEventManager();		//����"�����ĵ�"�˵����¼�
		}
		else if(e.getSource() == authorItem) {
			AuthorItemEventManager();	//����"������Ϣ"�˵����¼�
		}
		else if(e.getSource() == SearchStyleComboBox) {
			ComboBoxEventManager();		//����"�������"�¼�
		}
		else if((JButton)e.getSource() == SearchBtn) {
			SearchEventManager();		//����"������Դ"�¼�
		}
		else if((JButton)e.getSource() == ShareBtn) {
			ShareEventManager();		//����"������Դ"��ť�¼�
		}
		else if((JButton)e.getSource() == CancelBtn) {
			CancelEventManager();		//����"��������"��ť�¼�
		}
		else if((JButton)e.getSource() == DownloadBtn) {
			DownLoadEventManager();		//����"�����ļ�"��ť�¼�
		}
		else if(e.getSource() == TaskListBtn) {
			TaskListEventManager();		//����"�����б�"��ť�¼�
		}
	}
	
	//�����ʱ���¼�
	private void TimerEventManager() {
		//��ʱ��ȡϵͳʱ�䣬���µ�������ʵʱʱ���ǩ
		Time SystemTime = new Time(System.currentTimeMillis());
		SystemTimeLabel.setText(SystemTime.toString());

		//��鵱ǰ״̬�Ƿ�Ϊ"��¼��"
		if(isLogin == true) {
			UserName = UserNameText.getText();	//��ȡ�ı����ı�������½�û���
			if(UserName.equals("") == true) {	//���û���Ϊ�գ��򷵻أ������½�ɹ�
				return;
			}
			
			isLogin = false;									//�޸ĵ�¼״̬Ϊ"�ѵ�½"
			UserImageLabel.setIcon(UserHasLoginImage);			//���õ�½ͷ��Ϊ"�ѵ�½"״̬
			LocalShareLabel.setIcon(LocalResHasloginImage);		//�л�[������Դ����ǩ]Ϊ"�ѵ�½"״̬
			SearchResultLabel.setIcon(SearchResHasloginImage);	//�л�[������Դ����ǩ]Ϊ"�ѵ�½"״̬
			
			SysMsgAdv.MsgAdv("\n��ӭ���û� " + UserName + " ����P2P���磡");	//ϵͳ��Ϣ�㲥
			
			this.setFocusableWindowState(true);	//�ָ������ڽ���
			this.setEnabled(true);				//�ָ������ڿɲ���
			this.setAlwaysOnTop(true);			//���ô����ö�
		}
		
		this.setAlwaysOnTop(false);		//ȡ�������ö�
		return;
	}
	
	//����"�Ͽ�����"�˵����¼�
	private void BreakItemEventManager() {
		
		if(taskListWindow.GetCurrentRecordNum() > 0) {
			String[] option = {"�Ͽ�����", "ȡ������"};
			int selectId = JOptionPane.showOptionDialog(mainPanel, "��ǰ�������������У���ȷ��Ҫ�Ͽ�������\n����һ���ж��򲻿ɻָ���", "�˳�������Ϣ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
			if(selectId == 1) {
				return;
			}
		}
		
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "�����ѶϿ��������ظ�������");
			return;
		}
		//��ȫ�Ͽ�����
		JxtaPlatform.stopJxtaPlatform();
		isLinkNetwork = false;
		
		UserImageLabel.setIcon(UserNotLoginImage);			//���õ�½ͷ��Ϊ"δ��½"״̬
		LocalShareLabel.setIcon(LocalResNotloginImage);		//�л�[������Դ����ǩ]Ϊ"δ��½"״̬
		SearchResultLabel.setIcon(SearchResNotloginImage);	//�л�[������Դ����ǩ]Ϊ"δ��½"״̬
		return;
	}
	
	//����"��������"�˵����¼�
	private void linkItemEventManager() {
		if(isLinkNetwork == true) {
			JOptionPane.showMessageDialog(null, "�ѽ������磡�����ظ�������");
			return;
		}
		//���½�������
		JxtaPlatform.startJxtaPlatform();
		isLinkNetwork = true;
		
		//ˢ�±��ع�����Դ��
		JxtaServer.RefreshLocalResTable();
		
		UserImageLabel.setIcon(UserHasLoginImage);			//���õ�½ͷ��Ϊ"�ѵ�½"״̬
		LocalShareLabel.setIcon(LocalResHasloginImage);		//�л�[������Դ����ǩ]Ϊ"�ѵ�½"״̬
		SearchResultLabel.setIcon(SearchResHasloginImage);	//�л�[������Դ����ǩ]Ϊ"�ѵ�½"״̬
		
		JOptionPane.showMessageDialog(null, "�������½������磬����ձ��ػ��档\n��ǰ�����ļ��Ѷ�ʧ�����ط��������й�����Դ��ʧЧ��");
		return;
	}
	
	//����"�˳�"�˵����¼�
	private void ExitItemEventManager() {
		
		if(taskListWindow.GetCurrentRecordNum() > 0) {
			String[] option = {"ȷ���˳�", "ȡ������"};
			int selectId = JOptionPane.showOptionDialog(mainPanel, "��ǰ�������������У���ȷ��Ҫ�˳�������\n����һ���ж��򲻿ɻָ���", "�˳�������Ϣ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
			if(selectId == 1) {
				return;
			}
		}
		
		SysMsgAdv.MsgAdv("\n�����˳�ϵͳ...");	//�㲥
		timer.stop();						//ֹͣ��ʱ��
		this.dispose();						//ע�����������
		this.setVisible(false);				//���������ڲ��ɼ�
		JxtaPlatform.stopJxtaPlatform();	//��ȫ�˳�Jxta����
		System.exit(0);						//�˳�
		return;
	}
	
	//����"�����ĵ�"�˵����¼�	
	private void HelpItemEventManager() {
		new HelpDocWindow();	//�򿪰�������
		return;
	}

	//����"������Ϣ"�˵����¼�
	private void AuthorItemEventManager() {
		JOptionPane.showMessageDialog(null, 
				"����   : ������\n" +
				"�༶   : 08�������ѧ�뼼��1��\n" +
				"ѧУ   : 2008314122\n" +
				"��Ŀ   : ����P2P�������ļ�����ϵͳ\n"
				);
		return;
	}
		
	//������������¼�
	private void ComboBoxEventManager() {
		//����ǰѡ��"����������Դ"�����������ı�Ϊͨ���"*"
		if(SearchStyleComboBox.getSelectedIndex() == 2) {
			SearchTextField.setText("*");
			SearchTextField.setEditable(false);
		}
		else {
			SearchTextField.setEditable(true);
		}
		return;
	}
	
	//����"������Դ"�¼�
	private void SearchEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "���Ƚ������磡");
			return;
		}
		
		//��ȡ�������ı�����Ϊ���򷵻�
		String stext = SearchTextField.getText();
		if(stext.equals("")) {
			SysMsgAdv.MsgAdv("\n�������������ݣ�");
			return;
		}
		
		//��ȡ��ǰ����ѡ�������index������index����������ʽ
		int index = SearchStyleComboBox.getSelectedIndex();
		switch(index) {
			case 0:		//����Դ����
				SearchText = "*" + stext + "*";
				break;
			case 1:		//������������
				SearchText = "*" + stext + "*";
				break;
			case 2:		//����ȫ����Դ
				SearchText = "*";
				break;
		} 
		
		//ȡ����Դ����������е����ؼ�¼���
		for(int i = 0; i < GlobalConstAndTag.TableLength; i++) {
			GlobalConstAndTag.isDownloading[i] = false;
		}
		SysMsgAdv.MsgAdv("\n������Դ��...");

		//���ñ��ؽڵ��Client������������ܣ�����������Դ
		new JxtaClientForSearch(SearchText).start();
		return;
	}

	//����"������Դ"��ť�¼�
	private void ShareEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "���Ƚ������磡");
			return;
		}
		
		JFileChooser chooseFile = new JFileChooser();	//��ʼ������"���ļ�"�Ի���
		chooseFile.setDialogTitle("ѡ�����ļ�");			//���öԻ������
		chooseFile.setApproveButtonText("ȷ��");			//���öԻ����ܰ�ť�ı�Ϊ"ȷ��"
		FileFilters.addFileFilters(chooseFile);			//Ϊ�Ի��������ļ�������
		
		//���û�����"���ļ�"�Ի����"ȷ��"��ťʱ
		if(chooseFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooseFile.getSelectedFile();	//��ȡ��ѡ����ļ�
			long FileSize = file.length();				//��ȡ�ļ���С���ֽ�����
			String FilePath = file.getPath();			//��ȡ��ѡ���ļ��ĳ���·��
		    String FileName = file.getName();			//��ȡ��ѡ���ļ����ļ���
		    String AdvName = UserName + ":" + FileName;	//��װ�������ļ��Ĺܵ�����<Name>����ֵ
		    
		    //���ñ��ؽڵ��Server����Ĺ����ܣ���������Դ
		    new JxtaServer(AdvName, FileSize, FilePath).start();
		}
		return;
	}
	
	//����"��������"��ť�¼�
	private void CancelEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "���Ƚ������磡");
			return;
		}
		
		//��ȡ������Դ����ѡ�е��У���û��ѡ���κ��У��򷵻�
		int selectRow = LocalResTable.getSelectedRow();
		if(selectRow == -1) {
			return;
		}
		
		//��ȡ��ѡ���е���Դ��������Դ��Ϊ�գ�������Ϊ�գ����򷵻�
		String ResName = LocalResData.get(selectRow).get(1);
		if(ResName.equals("")) {
			return;
		}
		
		int choose = JOptionPane.showConfirmDialog(null, "ȷ�������ù�����Դ��", "��Ϣ", JOptionPane.YES_NO_OPTION);
		if(choose == JOptionPane.YES_OPTION) {
			//�Ƴ�ָ���ı��ع��
			JxtaPlatform.removeLocalAdvertisement(UserName + ":" + ResName);
			
			//���±��ع�����Դ��
			int ResNum = JxtaServer.RefreshLocalResTable();
			
			SysMsgAdv.MsgAdv("\n�ɹ��Ƴ�������Դ " + ResName + " ��");
			MainWindow.SysMsgAdv.MsgAdv("\n��ǰʣ�� " + ResNum + " ��������Դ��");
		}
		return;
	}

	//����"�����ļ�"��ť�¼�
	private void DownLoadEventManager() {
		if(isLinkNetwork == false) {
			JOptionPane.showMessageDialog(null, "���Ƚ������磡");
			return;
		}
		
		//��ȡ�����������ѡ�е��У���û��ѡ���κ��У��򷵻�
		int selectRow = NetResTable.getSelectedRow();
		if(selectRow == -1) {
			return;
		}
		
		//��ѡ�е���Դ�Ѿ��ڱ��������У��򾯸沢����
		if(GlobalConstAndTag.isDownloading[selectRow] == true) {
			SysMsgAdv.MsgAdv("\n����Դ���������У������ظ����أ�");
			JOptionPane.showMessageDialog(null, "����Դ���������У������ظ�����");
			return;
		}
		
		//��ȡ��ѡ���е���Դ��������Դ��Ϊ�գ�������Ϊ�գ����򷵻�
		String ResName = NetResData.get(selectRow).get(1);
		if(ResName.equals("")) {
			return;
		}
		
		//��ȡ��ѡ����Դ�ķ���������������ResName���Ϊ���<Name>���Ե�ֵ
		String PublisherName = NetResData.get(selectRow).get(2);
		String AdvName = PublisherName + ":" + ResName;
		
		//���ñ��ؽڵ��Client��������ӹ��ܣ�������Դ�ṩ�ߵ�Server���ṩ���ݴ���
		new JxtaClientForReceive(AdvName, selectRow, this).start();
		return;
	}

	//����"�����б�"��ť�¼�
	private void TaskListEventManager() {
		taskListWindow.setVisible(true);	//ʹ�������б�ɼ�
	}
}
