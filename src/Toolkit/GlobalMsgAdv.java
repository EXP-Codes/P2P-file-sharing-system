package Toolkit;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * 系统全局消息广播器
 */
public class GlobalMsgAdv {

	private JPanel MsgPanel;		//广播消息文本区面板
	public JTextArea MsgAdvTextArea;//广播消息文本区
	
	public GlobalMsgAdv(JPanel mainPanel) {
		//初始化
		MsgPanel = new JPanel();
		MsgAdvTextArea = new JTextArea("P2P文件共享系统 - 已启动");
		
		//设置文本区内容不可修改
		MsgAdvTextArea.setEditable(false);
		
		//把广播器添加到主窗口
		MsgPanel.setLayout(new GridLayout(1, 1));
		MsgPanel.add(new JScrollPane(MsgAdvTextArea));
		mainPanel.add(MsgPanel);
		MsgPanel.setBounds(90, 440, 435, 75);
	}
	
	//对消息text进行广播
	public void MsgAdv(String text) {
		MsgAdvTextArea.append(text);	//在文本区末尾追加内容
		MsgAdvTextArea.selectAll();		//强制把文本光标移到最后，以达到滚动条置底的目的
		return;
	}
}
