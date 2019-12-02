package JxtaService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import Toolkit.GlobalConstAndTag;
import Windows.MainWindow;

/*
 * ���ؽڵ�Peer��Server��������̲߳���
 * ��Ҫ��Jxta�����ڵ�����Peer�ṩ���ص���Դ���й���
 * ����������Դ�Ĺ�淢����Ӧ�������������ֹ���
 * 
 * ===================================================
 * 
 * ��ϵͳԼ����
 * ���ؽڵ㷢��������Դʱ������Ҫȷ������Դ�Ĺܵ����PipeAdv
 * ���йܵ�����<Name>�����ֶε�ֵΪ:
 * 		<Name> = PublisherName:ResName
 * �༴
 *      <Name> = UserName:ResName
 *      
 * ===================================================
 * 
 * ����API:
 * void publish(Advertisement adv, long lifetime, long expiration)
 * 	�������ع��adv�������浽���ػ��档 �ù���������Ϊlifetime������expiration��ʧЧ��Ȼ���Զ��ڱ��ػ���ɾ���ù�档
 * @Parameters:
 * 	adv - �����Ĺ�档
 * 	lifetime - �ù��������ڣ���λms��
 * 	expiration - �ù��������ڣ���λms�� 
 * 
 */
public class JxtaServer extends Thread {
	
	//Jxta�����еĶԵ���
	private PeerGroup netPeerGroup = null;
	
	//�ԵȽڵ�Peer�ķ��ַ���
	private DiscoveryService discoveryService = null;

	//�ܵ����Adv��<Name>����ֵ
	private String AdvName;
	
	private long ResByteSize;	//��������Դ�Ĵ�С���ֽ�����
	private String ResPath;		//��������Դ�ڱ��صľ���λ��
	private String ResName;		//��������Դ����Դ����
	private long ShareTime;		//����Դ�ļ��������ʱ��
	
	public JxtaServer(String advName, long fileSize, String resPath) {
		this.AdvName = advName;
		this.ResName = advName.substring(advName.lastIndexOf(":")+1);
		this.ResByteSize = fileSize;
		this.ResPath = resPath;
		this.ShareTime = GlobalConstAndTag.shareTime;
		
		this.netPeerGroup = JxtaPlatform.netPeerGroup;			//��ȡĬ�϶Ե���
		this.discoveryService = JxtaPlatform.discoveryService;	//��ȡĬ�϶Ե���ķ��ַ���
	}

	//����JxtaServer�����߳�
	public void run() {
		startServer();
	}
	
	//����Peer��Server���ܣ�ѡ������Դ���ȴ���������
	private void startServer() {
		//Ϊ����������Դ����һ���ܵ����
		PipeAdvertisement pipeAdv = creatPipeAdvertisement();
		
		try {
			//�ڽ�����ShareTimeʱ���ڰ������淢�������أ��ȴ�����Peer��������
			//��ShareTime���Զ�ɾ��������
			discoveryService.publish(pipeAdv, ShareTime, ShareTime);
			
			//���±��ع�����Դ������ȡ��ʱ���ع�����Դ��
			int ResNum = RefreshLocalResTable();
			MainWindow.SysMsgAdv.MsgAdv("\n�ɹ���������Դ " + ResName + " ��");
			MainWindow.SysMsgAdv.MsgAdv("\n��ǰ�ѹ��� " + ResNum + " ����Դ��");
			
			//��������pipeAdv�ķ����׽��֣����������������������Դ��Client����������
			JxtaServerSocket jxtaServerSocket = new JxtaServerSocket(netPeerGroup, pipeAdv);
			
			//����Client����Server�ĳ�ʱʱ��Ϊ������ʱ
			jxtaServerSocket.setSoTimeout(0);
			
			//ѭ���ȴ�����Peer��Client������
			while(true) {
				//����Client�ɹ�����ǰ��mainSocket��һֱ��������
				Socket mainSocket = jxtaServerSocket.accept();
				
				//ÿ�ɹ�����һ��Client��Server�Զ�Ϊ�����һ���߳���֧�����ݴ������
				if(mainSocket != null) {
					MainWindow.SysMsgAdv.MsgAdv("\n��Դ " + ResName + " ���������ء��ѽ�������");
					new Thread(new JxtaServerForSend(mainSocket, ResName, ResByteSize, ResPath)).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//�ӱ��ػ�ȡ�����ѹ������Դ��棬���±��ع�����Դ��
	//����ֵΪ��ǰ�������й������Դ��
	public static int RefreshLocalResTable() {
		List<String> LocalPipeAdvNameList = new ArrayList<String>();	//��¼���б��عܵ�����<Name>���Ե�ֵ���б�
		int AdvCount = 0;			//������������¼���δӱ��ػ�ȡ�Ĺܵ���������༴Ϊ�б���
		
		try {
			//�����������о���<Name>���ԵĹ�棬��<Name>��ֵ�����ƣ����༴Ϊ�����������й������Դ�Ĺ��
			//���ڹܵ����һ����<Name>���ԣ��������δ����<Name>���ԣ��������Ϊ���������عܵ�����һ�־ֲ�ɸѡ����
			Enumeration<Advertisement> AdvEnum = JxtaPlatform.discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", null);
			
			//ö�ٲ���ȡ���йܵ����<Name>�����е�ResName�洢��LocalPipeAdvNameList
			if(AdvEnum != null) {
				while(AdvEnum.hasMoreElements()) {
					String advName = ((PipeAdvertisement) AdvEnum.nextElement()).getName();	//��ȡ�ܵ����<Name>��ֵ
					String ResName = advName.substring(advName.lastIndexOf(":")+1);			//��ȡ<Name>�е�ResName
					LocalPipeAdvNameList.add(ResName);						//��ResName��ӵ�LocalPipeAdvNameList
					
					AdvCount++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//��������е�ֵΪ�������
		for(int i = 0; i < AdvCount; i++) {
			String ResName = LocalPipeAdvNameList.get(i);			//��ȡ������ԴName
			Vector<String> OldTableRow = SetTableRow(i+1, ResName);	//���ñ���i+1�е�����
			MainWindow.LocalResData.set(i, OldTableRow);			//�����õ�����д���
		}
		
		//�����ʣ���е�ֵΪ��
		for(int i = AdvCount; i < GlobalConstAndTag.TableLength; i++) {
			Vector<String> RestTableRow = SetTableRow(i+1, "");
			MainWindow.LocalResData.set(i, RestTableRow);
		}
		
		//�ػ汾�ع�����Դ��
		MainWindow.LocalResTable.repaint();
		return AdvCount;
	}
	
	//���ñ��ع�����Դ����Row�е�����
	public static Vector<String> SetTableRow(int Row, String ResName) {
		Vector<String> tmp = new Vector<String>();
		tmp.add(0, String.valueOf(Row));
		tmp.add(1, ResName);
		return tmp;
	}
		
	//���������عܵ����
	private PipeAdvertisement creatPipeAdvertisement() {
		//�����ܵ����PipeAdv
		PipeAdvertisement PipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		
		//��������ܵ�ID
		PipeID pipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID);
		
		PipeAdv.setPipeID(pipeID);					//���ùܵ����Ĺܵ�ID
		PipeAdv.setType(PipeService.UnicastType);	//���ùܵ�����Ϊ�����ܵ�
		PipeAdv.setName(AdvName);					//���ùܵ�<Name>���Ե�ֵ
		return PipeAdv;
	}
}
