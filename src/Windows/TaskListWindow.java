package Windows;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Toolkit.Fonts;
import Toolkit.GetScreenSize;
import Toolkit.GlobalConstAndTag;
import Toolkit.MyTableComponent;

/*
 * 下载任务列表窗口
 */
public class TaskListWindow extends JFrame implements ActionListener {
	
	//用于标识本类（本对象）的唯一性，由系统自动生成
	private static final long serialVersionUID = 8793139046596895361L;

	//设定窗口的宽高
	private final int winWidth = 700;
	private final int winHeight = 400;
	
	private JPanel rootPanel;		//根面板
	
	private JButton RefreshTaskBtn;	//刷新任务列表按钮
	private JButton CancelTaskBtn;	//清空任务列表按钮
	
	private int TaskNum = 0;		//当前列表记录的任务数
	
	//下载任务表单
	private Vector<String>	TaskColName;	//表单列名（表头）
	private Vector<Vector<String>> TaskData;//表单模型（二维表）
	private MyTableComponent TaskTable;		//表单组件
	
	public TaskListWindow() {
		InitWindow();	//初始化窗口
		InitTable();	//初始化表单内容及属性
		AddComponents();//往根面板添加必要组件
	}
	
	//初始化窗口
	private void InitWindow() {
		this.setTitle("任务列表");			//设置窗口标题
		this.setResizable(false);			//禁止拉伸窗口,最大化按钮无效
		this.setVisible(false);				//窗体不可见
		this.setSize(winWidth, winHeight);	//设置窗体尺寸
		
		//设置窗体位置
		this.setLocation((int)(GetScreenSize.screenWidth/2 - winWidth/2), (int)(GetScreenSize.screenHeight/2 - winHeight/2));

		//设置窗体图标
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		rootPanel = new JPanel();				//根面板
		rootPanel.setBackground(Color.white);	//设置根面板背景色
		this.getContentPane().add(rootPanel);	//把根面板添加到窗口
		rootPanel.setLayout(null);				//撤销根面板相对布局方式，为了使用绝对布局方式
		
		try {
			//允许窗口具有当前系统提供的窗口装饰
			TaskListWindow.setDefaultLookAndFeelDecorated(true);
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
				e.getWindow().setVisible(false);	//隐藏窗口，但不关闭
			}
		});
	}
	
	//初始化表单内容及属性
	private void InitTable() {
		//初始化表单列名
		TaskColName = new Vector<String>();
		TaskColName.add(0, "ID");							//设置表单第0列的列名：ID 索引号
		TaskColName.add(1, "            资源名称            ");	//设置表单第1列的列名：资源名称
		TaskColName.add(2, "资源大小");						//设置表单第2列的列名：资源大小
		TaskColName.add(3, "传输进度");						//设置表单第3列的列名：下载进度
		TaskColName.add(4, "传输速率");						//设置表单第4列的列名：下载速度
		TaskColName.add(5, "已用时间");						//设置表单第4列的列名：已用时间
		TaskColName.add(6, "任务状态");						//设置表单第5列的列名：任务状态
		TaskColName.add(7, "任务属性");						//设置表单第6列的列名：任务属性
		
		TaskData = new Vector<Vector<String>>();	//初始化表单
		for(int pr = 0; pr < GlobalConstAndTag.TableLength; pr++) {
			//设置表单第pr行首列索引为pr+1
			Vector<String> row = new Vector<String>();
			row.add(0, String.valueOf(pr + 1));
			
			//设置第pr行的其余列值为空值
			for(int pc = 1; pc < TaskColName.size(); pc++) {
				row.add(pc, "");
			}
			//把行row添加到LocalResInfo表单的行末
			TaskData.add(row);
		}
		return;
	}
	
	//往根面板添加必要组件
	private void AddComponents() {
		//"刷新任务列表"按钮
		RefreshTaskBtn = new JButton("刷新列表");
		RefreshTaskBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(RefreshTaskBtn);
		RefreshTaskBtn.setBounds(20, 10, 150, 50);
		RefreshTaskBtn.addActionListener(this);
		
		//"清空任务列表"按钮
		CancelTaskBtn = new JButton("清空列表");
		CancelTaskBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(CancelTaskBtn);
		CancelTaskBtn.setBounds(190, 10, 150, 50);
		CancelTaskBtn.addActionListener(this);
		
		/*
		 * 下载任务表单
		 */
		//用表单模型及表单列名初始化表单组件
		TaskTable = new MyTableComponent(TaskData, TaskColName);
		//为表单设置带滚动条的面板，垂直和水平滚动条为需要时显示
		JScrollPane TaskPanel = new JScrollPane(TaskTable, 
												JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
												JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		rootPanel.add(TaskPanel);
		TaskPanel.setBounds(20, 70, 645, 270);
		return;
	}

	//返回当前任务记录数
	public int GetCurrentRecordNum() {
		return TaskNum;
	}
	
	//令当前任务数+1，返回新任务记录在任务表单的索引位置
	public int GetNewRecordIndex() {
		return TaskNum++;
	}
	
	//用newRecord更新指定表单index位置的记录，并刷新表单
	public void UpdataRecord(int index, Vector<String> newRecord) {
		TaskData.set(index, newRecord);
		TaskTable.repaint();
		return;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//刷新表单
		if((JButton)e.getSource() == RefreshTaskBtn) {
			TaskTable.repaint();
		}
		//清空表单
		else if((JButton)e.getSource() == CancelTaskBtn) {
			//检查表单中是否有下载任务，若有则禁止清空
			for(boolean i: GlobalConstAndTag.isDownloading) {
				if(i == true) {
					MainWindow.SysMsgAdv.MsgAdv("\n当前有任务进行中！请等待所有任务结束再清空列表！");
					JOptionPane.showMessageDialog(null, "当前有任务进行中！请等待所有任务结束再清空列表！");
					return;
				}
			}
			//重置表单所有行为空
			for(int i = 0; i < GlobalConstAndTag.TableLength; i++) {
				Vector<String> tmp = new Vector<String>();
				tmp.add(0, String.valueOf(i + 1));
				tmp.add(1, "");
				tmp.add(2, "");
				tmp.add(3, "");
				tmp.add(4, "");
				tmp.add(5, "");
				tmp.add(6, "");
				tmp.add(7, "");
				TaskData.set(i, tmp);
			}
			TaskNum = 0;
			TaskTable.repaint();
		}
	}
}
