package Toolkit;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * ϵͳȫ����Ϣ�㲥��
 */
public class GlobalMsgAdv {

	private JPanel MsgPanel;		//�㲥��Ϣ�ı������
	public JTextArea MsgAdvTextArea;//�㲥��Ϣ�ı���
	
	public GlobalMsgAdv(JPanel mainPanel) {
		//��ʼ��
		MsgPanel = new JPanel();
		MsgAdvTextArea = new JTextArea("P2P�ļ�����ϵͳ - ������");
		
		//�����ı������ݲ����޸�
		MsgAdvTextArea.setEditable(false);
		
		//�ѹ㲥����ӵ�������
		MsgPanel.setLayout(new GridLayout(1, 1));
		MsgPanel.add(new JScrollPane(MsgAdvTextArea));
		mainPanel.add(MsgPanel);
		MsgPanel.setBounds(90, 440, 435, 75);
	}
	
	//����Ϣtext���й㲥
	public void MsgAdv(String text) {
		MsgAdvTextArea.append(text);	//���ı���ĩβ׷������
		MsgAdvTextArea.selectAll();		//ǿ�ư��ı�����Ƶ�����Դﵽ�������õ׵�Ŀ��
		return;
	}
}
