package JxtaService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import Toolkit.GlobalConstAndTag;
import Windows.MainWindow;

/*
 * ���ؽڵ�Peer��Client����
 * ��Ҫ��Jxta�����ڵ���������Peer�������Դ
 * 
 * =======================================================
 * 
 * ����API: 
 * int getRemoteAdvertisements(String peerid, int type, String attribute, String value, int threshold)
 * 	����Զ�̹�棬��ƥ��ɹ��󽫹�汣�浽���ء�
 * 	���ֹ��ķ�ʽΪ"�����Ŭ������"��ʽ��
 * 	�˷���������ʱ��ÿ��һ����Ӧ����������Ľڵ㣬������һ���߳̽��д���
 * @Parameters:
 * 	peerid - ָ����ȡԶ�̹��Ľڵ㡣null��ʾ�Ե����������нڵ㡣
 * 	type - ָ����ȡ�Ĺ�����͡��ڵ���PEER���ڵ�����GROUP���������ADV(�����ܵ����)��
 * 	attribute - ���ԡ��Դ���Ϊ����xml�������ݡ��ܵ����ĳ���������"ID"��"Type"��"Name"��
 * 	value - ����ֵ��������Ϊ"Name"��ֵΪ"P2P"��������NameΪP2P�Ĺ�档���������ı�ǰ�����ͨ���"*"����ģ��������
 * 	threshold - ������Ӧ������ÿ���ڵ㷵�ص����������ע����ÿ���ڵ���෵��threshold����棬���������нڵ�һ����෵��threshold����档
 * 
 */
public class JxtaClientForSearch extends Thread implements DiscoveryListener {
	
	//�ԵȽڵ�Peer�ķ��ַ���
	private DiscoveryService discoveryService = null;
	
	//Ϊ������Դ��������ı�
	private String SearchText;
	
	//����Ƿ�������Դ
	private boolean isDiscoveryRes = false;
	
	//�ܵ�����б���¼���������������й��
	private List<PipeAdvertisement> SearchPipeAdvList;
			
	//������������¼������������õĹ����
	private int AdvCount = 0;
	
	public JxtaClientForSearch(String searchText) {
		
		this.SearchText = searchText;
		
		SearchPipeAdvList = new ArrayList<PipeAdvertisement>();
				
		this.discoveryService = JxtaPlatform.discoveryService;	//��ȡĬ�϶Ե���ķ��ַ���
		discoveryService.addDiscoveryListener(this);			//Ϊ���ַ������ü���
	}
	
	//����JxtaClient�����̣߳�����������Դ
	public void run() {
		SearchNetRes();
	}
	
	//��ʼ����������Դ
	public void SearchNetRes() {
		//������ʼʱ��
		long startTime = System.currentTimeMillis();
		
		//��"�����Ŭ������"��ʽ��������ƥ���Զ�̹ܵ����
		discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", SearchText, GlobalConstAndTag.MaxPeerAdvNum);
			
		try {
			//�ȴ�searchTime��ʹ�þ����ܶ�Ľڵ�ظ���������
			Thread.sleep(GlobalConstAndTag.searchTime);

			long endTime = System.currentTimeMillis();	//��������ʱ��
			long consumeTime = endTime - startTime;		//������ʱ
			
			//��ʱsearchTime��û���κνڵ���Ӧ��������
			if(!isDiscoveryRes) {
				MainWindow.SysMsgAdv.MsgAdv("\n�Ҳ����κ���Դ�����ܵ�ǰ�������������û�������Դ��");
				MainWindow.SysMsgAdv.MsgAdv("\n����������ʱ: " + consumeTime + " ms��");
				JOptionPane.showMessageDialog(null, "�Ҳ��������Դ��", "������Դʧ��", JOptionPane.ERROR_MESSAGE);
			}
			else {
				MainWindow.SysMsgAdv.MsgAdv("\n�ɹ������� " + AdvCount + " ����Դ��");
				MainWindow.SysMsgAdv.MsgAdv("\n����������ʱ: " + consumeTime + " ms��");
			}
			
			//���ݱ�������������Դ����������Դ��
			RefreshNetResTable();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//���ݱ�������������Դ����������Դ��
	private void RefreshNetResTable() {
		/**
		 * ������涨���<Name>���Եĸ�ʽΪ: 
		 *     PublisherName:ResName
		 * PublisherName�������ڵ㱾�ص�UserName�� ResName����Դ���ƣ�����������ļ���
		 * ����OS�ѹ涨PublisherName���Գ��������ַ�����ResName���ܳ��������ַ�����":"
		 * �����������":"��Ϊ���������ַ����ļ����
		 * 
		 * ���潫������һ������ȡ<Name>�е���2���ַ�������ʾ���������"������Դ��"�С�
		 */
		
		//��������е�ֵΪ�������
		for(int i = 0; i < AdvCount; i++) {
			String AdvName = SearchPipeAdvList.get(i).getName();	//��ȡ���<Name>�ֶ�ֵ
			Vector<String> OldTableRow = GetTableRow(i+1, AdvName);	//��ȡPublisherName��ResName���Դ����ñ���i+1�е�����
			MainWindow.NetResData.set(i, OldTableRow);				//�����õ�����д���
		}
		
		//�����ʣ���е�ֵΪ��
		for(int i = AdvCount; i < GlobalConstAndTag.TableLength; i++) {
			Vector<String> RestTableRow = new Vector<String>();
			RestTableRow.add(0, String.valueOf(i+1));
			RestTableRow.add(1, "");
			RestTableRow.add(2, "");
			MainWindow.NetResData.set(i, RestTableRow);
		}
		
		//ˢ�������
		MainWindow.NetResTable.repaint();
		return;
	}

	//��AdvName����ȡPublisherName��ResName���Դ����ñ���Row�е�����
	private Vector<String> GetTableRow(int Row, String AdvName) {
		int SeparatorIndex = AdvName.lastIndexOf(":");				//��ȡ�ָ���":"��AdvName�е�����
		String PublisherName = AdvName.substring(0, SeparatorIndex);//��ȡ����������
		String ResName = AdvName.substring(SeparatorIndex+1);		//��ȡ��Դ����
		
		Vector<String> tmp = new Vector<String>();
		tmp.add(0, String.valueOf(Row));
		tmp.add(1, ResName);
		tmp.add(2, PublisherName);
		return tmp;
	}
		
	//��Է���Զ�̹����¼�����
	@Override
	public void discoveryEvent(DiscoveryEvent e) {
		//��ȡĳ���ڵ�ظ�����Ϣ
		DiscoveryResponseMsg ResMsg = e.getResponse();
		
		//��ȡ�ظ���Ϣ�е����й�棬������ö���б�AdvEnmu��
		Enumeration<Advertisement> AdvEnmu = ResMsg.getAdvertisements();
		
		//��AdvEnmu��������1�����ʱ
		if(AdvEnmu != null) {
			while(AdvEnmu.hasMoreElements()) {
				PipeAdvertisement tmpPipeAdv = (PipeAdvertisement) AdvEnmu.nextElement();	//��ȡ�ܵ����tmpPipeAdv
				SearchPipeAdvList.add(tmpPipeAdv);					//�ѹܵ����tmpPipeAdv��ŵ��ܵ�����б�pipeAdvList
				AdvCount++;
				
				try {
					//����getRemoteAdvertisements�������Զ�̹��ĸ������浽���ػ���
					//Ϊ�˱��������ڵ㲻��Ҫ������������������ù���ڱ��ػ���ĸ���
					discoveryService.flushAdvertisement(tmpPipeAdv);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		//������ҵ���Դ
		isDiscoveryRes = true;
		
		//���ҵ�����Դ��������Ҫ����Դ���ܵ���棩��������ʧ��
		if(AdvCount == 0) {
			isDiscoveryRes = false;
		}
	}
}
