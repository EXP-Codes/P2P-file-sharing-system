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
 * ���������б���
 */
public class TaskListWindow extends JFrame implements ActionListener {
	
	//���ڱ�ʶ���ࣨ�����󣩵�Ψһ�ԣ���ϵͳ�Զ�����
	private static final long serialVersionUID = 8793139046596895361L;

	//�趨���ڵĿ��
	private final int winWidth = 700;
	private final int winHeight = 400;
	
	private JPanel rootPanel;		//�����
	
	private JButton RefreshTaskBtn;	//ˢ�������б�ť
	private JButton CancelTaskBtn;	//��������б�ť
	
	private int TaskNum = 0;		//��ǰ�б��¼��������
	
	//���������
	private Vector<String>	TaskColName;	//����������ͷ��
	private Vector<Vector<String>> TaskData;//��ģ�ͣ���ά��
	private MyTableComponent TaskTable;		//�����
	
	public TaskListWindow() {
		InitWindow();	//��ʼ������
		InitTable();	//��ʼ�������ݼ�����
		AddComponents();//���������ӱ�Ҫ���
	}
	
	//��ʼ������
	private void InitWindow() {
		this.setTitle("�����б�");			//���ô��ڱ���
		this.setResizable(false);			//��ֹ���촰��,��󻯰�ť��Ч
		this.setVisible(false);				//���岻�ɼ�
		this.setSize(winWidth, winHeight);	//���ô���ߴ�
		
		//���ô���λ��
		this.setLocation((int)(GetScreenSize.screenWidth/2 - winWidth/2), (int)(GetScreenSize.screenHeight/2 - winHeight/2));

		//���ô���ͼ��
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		rootPanel = new JPanel();				//�����
		rootPanel.setBackground(Color.white);	//���ø���屳��ɫ
		this.getContentPane().add(rootPanel);	//�Ѹ������ӵ�����
		rootPanel.setLayout(null);				//�����������Բ��ַ�ʽ��Ϊ��ʹ�þ��Բ��ַ�ʽ
		
		try {
			//�����ھ��е�ǰϵͳ�ṩ�Ĵ���װ��
			TaskListWindow.setDefaultLookAndFeelDecorated(true);
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
		this.addWindowFocusListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);	//���ش��ڣ������ر�
			}
		});
	}
	
	//��ʼ�������ݼ�����
	private void InitTable() {
		//��ʼ��������
		TaskColName = new Vector<String>();
		TaskColName.add(0, "ID");							//���ñ���0�е�������ID ������
		TaskColName.add(1, "            ��Դ����            ");	//���ñ���1�е���������Դ����
		TaskColName.add(2, "��Դ��С");						//���ñ���2�е���������Դ��С
		TaskColName.add(3, "�������");						//���ñ���3�е����������ؽ���
		TaskColName.add(4, "��������");						//���ñ���4�е������������ٶ�
		TaskColName.add(5, "����ʱ��");						//���ñ���4�е�����������ʱ��
		TaskColName.add(6, "����״̬");						//���ñ���5�е�����������״̬
		TaskColName.add(7, "��������");						//���ñ���6�е���������������
		
		TaskData = new Vector<Vector<String>>();	//��ʼ����
		for(int pr = 0; pr < GlobalConstAndTag.TableLength; pr++) {
			//���ñ���pr����������Ϊpr+1
			Vector<String> row = new Vector<String>();
			row.add(0, String.valueOf(pr + 1));
			
			//���õ�pr�е�������ֵΪ��ֵ
			for(int pc = 1; pc < TaskColName.size(); pc++) {
				row.add(pc, "");
			}
			//����row��ӵ�LocalResInfo������ĩ
			TaskData.add(row);
		}
		return;
	}
	
	//���������ӱ�Ҫ���
	private void AddComponents() {
		//"ˢ�������б�"��ť
		RefreshTaskBtn = new JButton("ˢ���б�");
		RefreshTaskBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(RefreshTaskBtn);
		RefreshTaskBtn.setBounds(20, 10, 150, 50);
		RefreshTaskBtn.addActionListener(this);
		
		//"��������б�"��ť
		CancelTaskBtn = new JButton("����б�");
		CancelTaskBtn.setFont(Fonts.ChineseStyle_SongTi_LR_22);
		rootPanel.add(CancelTaskBtn);
		CancelTaskBtn.setBounds(190, 10, 150, 50);
		CancelTaskBtn.addActionListener(this);
		
		/*
		 * ���������
		 */
		//�ñ�ģ�ͼ���������ʼ�������
		TaskTable = new MyTableComponent(TaskData, TaskColName);
		//Ϊ�����ô�����������壬��ֱ��ˮƽ������Ϊ��Ҫʱ��ʾ
		JScrollPane TaskPanel = new JScrollPane(TaskTable, 
												JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
												JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		rootPanel.add(TaskPanel);
		TaskPanel.setBounds(20, 70, 645, 270);
		return;
	}

	//���ص�ǰ�����¼��
	public int GetCurrentRecordNum() {
		return TaskNum;
	}
	
	//�ǰ������+1�������������¼�������������λ��
	public int GetNewRecordIndex() {
		return TaskNum++;
	}
	
	//��newRecord����ָ����indexλ�õļ�¼����ˢ�±�
	public void UpdataRecord(int index, Vector<String> newRecord) {
		TaskData.set(index, newRecord);
		TaskTable.repaint();
		return;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//ˢ�±�
		if((JButton)e.getSource() == RefreshTaskBtn) {
			TaskTable.repaint();
		}
		//��ձ�
		else if((JButton)e.getSource() == CancelTaskBtn) {
			//�������Ƿ������������������ֹ���
			for(boolean i: GlobalConstAndTag.isDownloading) {
				if(i == true) {
					MainWindow.SysMsgAdv.MsgAdv("\n��ǰ����������У���ȴ������������������б�");
					JOptionPane.showMessageDialog(null, "��ǰ����������У���ȴ������������������б�");
					return;
				}
			}
			//���ñ�������Ϊ��
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
