package JxtaService;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import Windows.MainWindow;


/*
 * ����Jxta����ƽ̨
 * ����ΪPeer��Server��Client�����ṩpeer-to-peer�����绷��
 * 
 * ===================================================================================================
 * 
 * Ad-Hoc : ���ֽڵ��ڵ�Ե����������������һ������ڵ㣬�����߱�������ʩ�ڵ㣨���Ͻڵ���м̽ڵ㣩�����ԡ�
 * ��Ե�ڵ�EDGE��: ����֧��Ad-Hoc����Ϊ�����������ڻ�����ʩ�ڵ㣨���Ͻڵ���м̽ڵ㣬�����ߣ���
 * ���Ͻڵ�RENDEZVOUS : ֧��������ڵ������������緢�ַ��񡢹ܵ������ȡ�
 * �м̽ڵ�RENDEZVOUS_RELAY : �ṩ��Ϣ�м̷���ʹ���ܴ�Խ����ǽ��
 * ����ڵ�PROXY : ΪJ2ME��������ṩJ2ME JXTA��
 * �����ڵ�SUPER : �ṩ���Ͻڵ㡢�м̽ڵ㡢����ڵ�����й��ܡ�
 */
public class JxtaPlatform {

	//Jxta�����Manager����������Jxta����ĸ�������
	public static NetworkManager networkManager = null;
	
	//Jxta�����еĶԵ���
	public static PeerGroup netPeerGroup = null;
		
	//�ԵȽڵ�Peer�ķ��ַ���
	public static DiscoveryService discoveryService = null;
	
	//���ػ���ĸ��ļ�������
	private static String RootCache = ".cache";
	
	//�������ض���Ŀ¼".cache"-->"JxtaPlatform"
	//���ڱ���Jxta��������á���桢ID����Ϣ
	//����home.toURI()�����ṩ�ı�ʶ����Ϊ����Peer��Jxta�����ϵ�ͳһ��Դ��ʶ��
	private static File home = new File(new File(RootCache), "JxtaPlatform");
		
	public JxtaPlatform() {
		startJxtaPlatform();
	}

	//����Jxta����
	public static void startJxtaPlatform() {
		try {
			//���ñ��ڵ�Ϊ��Ե�ڵ�EDGE
			//�ڵ�����Ϊ"JxtaPlatform"
			//��homeĿ¼λ�ã�����cache����Ϊ���ڵ���Jxta�����ϵ�ͳһ��Դ��ʶ��URI
			networkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "JxtaPlatform", home.toURI());
			
			//����Jxta���磨������룬���򴴽���
			networkManager.startNetwork();

			MainWindow.SysMsgAdv.MsgAdv("\n�ɹ�����/����P2P���磡");
			
		} catch (IOException e) {
			MainWindow.SysMsgAdv.MsgAdv("\n���ñ���Peer����ʧ�ܣ�");
			e.printStackTrace();
		} catch (PeerGroupException e) {
			MainWindow.SysMsgAdv.MsgAdv("\n����/����P2P����ʧ�ܣ�");
			e.printStackTrace();
		}
		
		netPeerGroup = networkManager.getNetPeerGroup();		//��ȡĬ�϶Ե���
		discoveryService = netPeerGroup.getDiscoveryService();	//��ȡĬ�϶Ե���ķ��ַ���
		return;
	}
	
	//��ȫ�˳�Jxta����
	public static void stopJxtaPlatform() {
		//��ձ��ع��
		removeAllLoaclAdvertisement();
		
		//�˳�Jxta����
		networkManager.stopNetwork();
		MainWindow.SysMsgAdv.MsgAdv("\n���˳�P2P����...");
		
		//ɾ���������л����ļ�
		delFolder(RootCache);
		return;
	}
	
	//�Ƴ����ػ�����<Name>����ֵΪAdvName�Ĺܵ����
	public static void removeLocalAdvertisement(String AdvName) {
		try {
			//��ȡ�������йܵ����
			Enumeration<Advertisement> AdvEnum = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", null);
			//������й���<Name>����ֵ���Ƴ�ƥ����
			if(AdvEnum != null) {
				while(AdvEnum.hasMoreElements()) {
					PipeAdvertisement LocalAdv = (PipeAdvertisement) AdvEnum.nextElement();
					if(LocalAdv.getName().equals(AdvName)) {
						discoveryService.flushAdvertisement(LocalAdv);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//��ձ������йܵ����
	public static void removeAllLoaclAdvertisement() {
		try {
			//��ȡ�������йܵ����
			Enumeration<Advertisement> AdvEnum = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", null);
			//��һ�Ƴ�
			if(AdvEnum != null) {
				while(AdvEnum.hasMoreElements()) {
					Advertisement LocalAdv = AdvEnum.nextElement();
					discoveryService.flushAdvertisement(LocalAdv);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//ɾ���ļ�Ŀ¼
	//RootFolderPathΪ���ļ��е����/����·��
	public static void delFolder(String RootFolderPath) {
		try {
			//ɾ�����ļ������������ļ�
			delAllFile(RootFolderPath);
			
			//ָ�����ļ��еĳ���·����Ȼ��ɾ���յĸ��ļ���
			File RootFolder = new File(RootFolderPath);	
			RootFolder.delete();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//ɾ��ָ��Ŀ¼�µ������ļ�
	//pathΪ�ļ�/�ļ��е����/����·��
	public static void delAllFile(String path) {
		//ָ���ļ�/�ļ��еĳ���·��������·�����ļ�/�ļ��в����ڣ�����
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		
		//����·�������ļ�Ŀ¼������
		if (!file.isDirectory()) {
			return;
		}
		
		//��ȡ���ļ�Ŀ¼�µ�һ��������ļ����ļ��У��������ǵ������б�
		String[] fileList = file.list();
		
		//��һָ����Ŀ¼�µ�һ��������ļ����ļ��У���ɾ��
		File temp = null;
		for (int i = 0; i < fileList.length; i++) {
			/*
			 * ����·��path�ĺ�׺�Ƿ�Ϊ"/"
			 * ���ݺ�׺��ͬʼ�մ���һ��һ��·�� "path/fileList[i]"
			 * �����ô�·��ָ���ļ�/�ļ���temp
			 */
			if (path.endsWith(File.separator)) {
				temp = new File(path + fileList[i]);
			}
			else {
				temp = new File(path + File.separator + fileList[i]);
			}
			
			//��tempΪ�ļ���ֱ��ɾ��
			if (temp.isFile()) {
				temp.delete();
			}
			
			//��tempΪĿ¼��������ݹ�ɾ��
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + fileList[i]);//��ɾ���ļ���������ļ�
				delFolder(path + File.separator + fileList[i]);	//��ɾ�����ļ���
			}
		}
		return;
	}
}
