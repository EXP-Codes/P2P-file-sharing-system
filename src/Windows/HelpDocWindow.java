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
 * �����ĵ�����
 */
public class HelpDocWindow extends JFrame {

	//���ڱ�ʶ���ࣨ�����󣩵�Ψһ�ԣ���ϵͳ�Զ�����
	private static final long serialVersionUID = -5425221620702307077L;
	
	//�趨���ڵĿ��
	private final int winWidth = 500;
	private final int winHeight = 550;
	
	public HelpDocWindow() {
		this.setTitle("����");	//���ô��ڱ���
		this.setVisible(true);	//����ɼ�
		this.setSize(winWidth, winHeight);//���ô���ߴ�
		
		//���ô���λ��
		this.setLocation((int)(GetScreenSize.screenWidth/2 - winWidth/2), (int)(GetScreenSize.screenHeight/2 - winHeight/2));
		
		//���ô���ͼ��
		this.setIconImage(this.getToolkit().getImage(getClass().getResource("/P2PSystemIcon.png")));
		
		JPanel rootPanel = new JPanel();				//��ʼ�������
		JTextArea helpFileArea = new JTextArea();		//��ʼ���ı���
		helpFileArea.setEditable(false);				//��ֹ�༭�ı���������
		
		rootPanel.setBackground(Color.white);			//���ø���屳��ɫ
		rootPanel.setLayout(new GridLayout(1,1));		//�����Ϊ1x1���񲼾ֹ���ʽ
		rootPanel.add(new JScrollPane(helpFileArea));	//Ϊ�ı�����ӹ������������ı�������rootPanel���
		this.getContentPane().add(rootPanel);			//�Ѹ������ӵ���������
		
		try {
			//�����ھ��е�ǰϵͳ�ṩ�Ĵ���װ��
			HelpDocWindow.setDefaultLookAndFeelDecorated(true);
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
				e.getWindow().setVisible(false);
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		 
		//���ð����ĵ�����
		helpFileArea.setText("        -------������P2P�������ļ�����ϵͳ��ʹ��˵����-------\n" +
							 "\n" +
							 " 1��ϵͳ��飺\n" +
							 "        ��ϵͳ�����ˡ���ȫ�ֲ�ʽ����peer-to-peer����ܹ���һ�������\n" +
							 "    ���������е�һ���ԵȽڵ㣨peer����ÿ��peer���߱�Server����Client\n" +
							 "    �˵ķ����ܣ�����Server����Ҫ�ṩ��Դ�ķ����봫�书�ܣ�Client��\n" +
							 "    ��Ҫ�ṩ��Դ�����������ع��ܡ�\n" +
							 "        ��������ȫ�ֲ�ʽ�ĶԵ����磬��˲�����������C/S����ܹ��ĳ���\n" +
							 "    ������������������м�ء�Ҳ�����ڴˣ�����peer����/�뿪���磬����\n" +
							 "    �����з���/����������Դʱ������peer���ǲ�������Ԥ֪�ġ�\n" +
							 "\n" +
							 " 2��ϵͳ��½��\n" +
							 "        ����ϵͳ�󣬱��س����Զ�����peer��ݽ���P2P�����Ĭ�϶Ե��飬\n" +
							 "    �û��������ó���¼��������κ���Ϣ����ν�ġ����롱��ָ��������룬\n" + 
							 "    ���򴴽���\n" +
							 "\n" +
							 " 3����Դ����\n" +
							 "        ����������ļ�����ť������ڵ����ĶԻ���ѡ����Ҫ�����������\n" +
							 "    ����Դ����������ļ��Ὣ���ѹ������Ϣ���������磬ͬʱ��ʾ�ڡ�����\n" +
							 "    ������Դ���б��С����б�������ļ������Ա�����peer�������֣�����ʱ\n" +
							 "    �������ء�\n" +
							 "        �ڡ����ع�����Դ���б���ѡ��ĳ����Դ�󣬵�����������������\n" +
							 "    ������ɾ�����ѹ������Ϣ�����ӡ����ع�����Դ���б����Ƴ�������peer\n" +
							 "    �޷��ٷ����ѱ������������Դ��\n" +
							 "\n" +
							 " 4����Դ������\n" +
							 "        ��ͨ���������ѡ��������ʽ����������������Ҫ���������ݣ�Ȼ���\n" +
							 "    ������������ť�������P2P�������ԡ������Ŭ���������ķ�������ƥ��\n" +
							 "    ��Դ������������Դ����ʾ�ڡ���Դ����������б��С�\n" +
							 "        �����ṩ��������ʽ��4�֣�������������ı�Ϊstr����\n" +
							 "        ��1���ֲ�ƥ������������������Դ/�����������а���str�ĵ���Դ��\n" +
							 "        ��2����������������Դ��str�Զ�Ϊͨ�����*�������������κΣ�\n" +
							 "             ���ݣ��˷�ʽ���������������й������Դ�б�\n" +
							 "        ��3������Դ��������������������Դ�����а���str�ĵ���Դ��\n" +
							 "        ��4���������������������������з����������а���str�ĵ���Դ��\n" +
							 "\n" +
							 " 5����Դ���أ�\n" +
							 "        �ڡ���Դ����������б���ѡ��ĳ����Դ������������ļ�����ť��Ȼ\n" +
							 "    ���ڵ����ĶԻ���ѡ�񱣴��ļ���λ�ü��ļ��������Զ������Դ�ķ�����\n" +
							 "    �������ӣ��̶��������ݴ��䡣Ϊ�˱�֤�ļ�����Ŀɿ��ԣ���ϵͳ������\n" +
							 "    TCPЭ��������ݴ��䡣\n" +
							 "        ��ͬ����Դ��ͬʱ���أ�ͬһ��Դ������ͬʱ���أ�������Դ��������\n" +
							 "    �����ء���ʹ��Դһ�£�ֻҪ�����߲�ͬ��Ҳ��Ϊ��ͬ��Դ�������������\n" +
							 "    ����ť�ɲ�ѯ��ǰ���е��������顣\n" +
							 "\n" +
							 " 6���㲥���ƣ�\n" +
							 "        �㲥��λ���������·�������ʵʱ�����û��ĸ��ֲ��������\n" +
							 "\n" +
							 " 7����ȫ���ƣ�\n" +
							 "        ��1�����ἰ������peer֮�����Ϊ�ǲ�������Ԥ֪�ģ�������û�ȷ\n" +
							 "    ��������Դǰ������Դ�������Դ�����ڽ��еڶ������������ҽ������ٻ�\n" +
							 "    ��һ��peer�ڹ������Դʱ�����������ء�\n" +
							 "        ����Դ��������У�������һ��ͻȻ�뿪���絼�´����жϣ�����һ��\n" +
							 "    �����޷�Ԥ֪����������������ǶԷ�������ߣ��Ӷ����ܵ��¸��������\n" +
							 "    Դ������߳������������ȴ���Ϊ�˱��ⷢ�������������ϵͳ���������\n" +
							 "    ��ʱʱ��DelayTime��ֻҪ��DelayTime�ڻָ����䣬�����������Ե�ʣ�\n" +
							 "    ��Ȼ����������Դ��������Ϊ�Է������ߣ�ע���ȴ��̡߳�\n" +
							 "        ��ϵͳ�ܹ���Խ����ǽ�������ڱ�ϵͳ����ȫ�ֲ�ʽ�ģ�����ھ�����\n" +
							 "    ��ʹ��ʱ�ɿ�����ߣ�������ʹ��ʱ�ɿ�����Ա�͡�����ġ��ɿ��ԡ���\n" +
							 "    ָ�����Ŀɿ��ԣ�������������ĳ��Դʧ��ʱ���ܿ������û���κ�peer��\n" +
							 "    ���˸���Դ��������������ĳ��Դʧ��ʱ��δ�ؾ���û���κ�peer�����˸�\n" +
							 "    ��Դ���Ͼ�������ʽ���ǡ������Ŭ�����������ѡ���Ȼ������ʹ�ñ�ϵͳ\n" +
							 "    ���û����������࣬�����Ǿ�����������������ɿ��ȶ�����Ӧ������\n"
							 );
		//���ù��λ�ã�ʹ�ù������ö�
		helpFileArea.select(0, 0);
	}

}
