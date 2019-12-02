package JxtaService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;
import Toolkit.FileFilters;
import Toolkit.GlobalConstAndTag;
import Toolkit.NumberFormat;
import Windows.MainWindow;

/*
 * ���ؽڵ�Peer��Client����
 * ��Ҫ��ȷ��Jxta�����ڵ�ĳ��Peer�������Դ������������Ӳ�������Դ����
 * 
 * ========================================================
 * 
 * ����API:
 * public JxtaSocket(PeerGroup group, PeerID peerid, PipeAdvertisement pipeAdv, int timeout, boolean reliable)
 * ����Scoket�ܵ����pipeAdv��������Peer����һ��JxtaSocket�ܵ���
 * �ùܵ���ͼ��group����Ե��黷���£�����ʱtimeout�ڴ���һ�����ӵ�ָ���ܵ�����pipeAdv�����������ӡ� 
 * @Parameters:
 * 	group - �ڵ���
 * 	peerid - �����ӵ��Ľڵ㡣
 * 	pipeAdv - �ܵ����
 * 	timeout - �޶�JxtaSocket���������ʱ���ڱ��ɹ�����. 0��ʾ����ʱ�� ��ʱ�������׳�����
 * 	reliable - true:ʹ��TCPЭ�顣 false:ʹ��UDPЭ�顣
 * 
 */
public class JxtaClientForReceive extends Thread implements DiscoveryListener, ActionListener {
	
	//Jxta�����еĶԵ���
	private PeerGroup netPeerGroup = null;
	
	//�ԵȽڵ�Peer�ķ��ַ���
	private DiscoveryService discoveryService = null;
	
	//"�����ļ�"�Ի���ĸ�����
	private MainWindow parentWindow;
	
	//�ܵ�����б���¼���������������й��
	private List<PipeAdvertisement> SearchPipeAdvList;
	
	//������������¼������������õĹ����
	private int AdvCount = 0;
	
	//����Ƿ�������Դ
	private boolean isDiscoveryRes = false;
	
	private JxtaSocket clientSocket = null;				//���ӵ�serverSocket���׽���
	private InputStream socketInStream = null;			//�׽��ֵĻ���������
	private InputStream inStream = null;				//ʹ��ָ���Ļ���������������������
	private RandomAccessFile randomFileStream = null;	//�����ȡ�ļ���
	
	private String ResAdvText;	//��������Դ�Ĺ���<Name>����ֵ
	private String ResName;		//��������Դ����Դ����
	private int NetResTableRow;	//��������Դ��"��Դ�������"����λ�ã�������
	
	private Vector<String> thisRecord;	//������������ļ�¼��Ϣ
	int recordIndex = -1;				//��������������"�����б�"�ļ�¼λ�ã�������
	
	private Timer timer;			//��ʱ�������ڼ��ܵ���ͨ��
	private int NoResponseTime = 0;	//������������Ӧʱ��
	
	public JxtaClientForReceive(String resAdvText, int netResTableRow, MainWindow mainWindow) {

		this.ResAdvText = resAdvText;
		this.ResName = ResAdvText.substring(ResAdvText.lastIndexOf(":")+1);
		this.NetResTableRow = netResTableRow;
		this.parentWindow = mainWindow;
		
		timer = new Timer(1000, this);
		SearchPipeAdvList = new ArrayList<PipeAdvertisement>();
				
		this.netPeerGroup = JxtaPlatform.netPeerGroup;			//��ȡĬ�϶Ե���
		this.discoveryService = JxtaPlatform.discoveryService;	//��ȡĬ�϶Ե���ķ��ַ���
		
		discoveryService.addDiscoveryListener(this);			//Ϊ���ַ������ü���
	}
	
	//����JxtaClient�̣߳����ж�����������ȷ����������Դ����Ч��
	public void run() {
		TwiceSearch();
	}
	
	//���ж�����������ȷ����������Դ����Ч��
	public void TwiceSearch() {
		//Զ��������������Դ
		discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", ResAdvText, 1);
			
		try {
			//�ȴ�searchTime��������ʹ��������1���ڵ�ظ���������
			Thread.sleep(GlobalConstAndTag.searchTime);
			
			//��ʱsearchTime��û���κνڵ���Ӧ��������
			if(!isDiscoveryRes) {
				MainWindow.SysMsgAdv.MsgAdv("\n��Դ " + ResName + " ��ʧЧ�������¼���������Դ��");
				JOptionPane.showMessageDialog(null, "����Դ��ʧЧ�������¼���������Դ��", "��Դ��ʧЧ", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			//�ҵ�����һ����棬��������Դ����Ч
			//������Դ������ӵ���Դ�ṩ�ߣ���������Դ
			ConnectToServer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//������Դ������ӵ���Դ�ṩ�ߣ���������Դ
	private void ConnectToServer() {
		//���ж���ڵ��ṩ����ͬ����Դ���ʱ����ȡ��һ�����
		PipeAdvertisement pipeAdv = SearchPipeAdvList.get(0);
		
		try {
			JFileChooser choosePath = new JFileChooser();	//��ʼ������"�����ļ�"�Ի���
			choosePath.setSelectedFile(new File(ResName)); 	//���ñ���ʱ��Ĭ���ļ���
			choosePath.setApproveButtonText("����");			//���öԻ����ܰ�ť�ı�Ϊ"ȷ��"
			choosePath.setDialogTitle("ѡ�񱣴�λ��");		//���öԻ������
			FileFilters.addFileFilters(choosePath);			//Ϊ�Ի��������ļ�������
			
			//���û�����"�����ļ�"�Ի����"ȷ��"��ťʱ
			if(choosePath.showSaveDialog(parentWindow) == JFileChooser.APPROVE_OPTION) {
				
				File saveFile = choosePath.getSelectedFile();	//��"�����ļ�"�Ի����ȡ�������ļ��ĳ���·��λ��
				saveFile.createNewFile();						//����λ�ò�����ָ���ļ����򴴽�һ��
				
				//�������ж�ȡ��������д��������ȡ�ļ�������д����ΪsaveFile
				randomFileStream = new RandomAccessFile(saveFile, "rw");
				
				MainWindow.SysMsgAdv.MsgAdv("\n���ڳ���������Դ�ṩ��...");
				
				//�������ӵ�serverSocket���׽���
				clientSocket = new JxtaSocket(netPeerGroup, null, pipeAdv, GlobalConstAndTag.createPipeTime, GlobalConstAndTag.useTCP);
				
				MainWindow.SysMsgAdv.MsgAdv("\n���ӳɹ������ڴ�������...�����ĵȺ�...");
				GlobalConstAndTag.isDownloading[NetResTableRow] = true;	//��Ǹ���Դ�������أ������ظ�����
				
				//��ȡ�׽��ֵĻ���������
				socketInStream = clientSocket.getInputStream();
				
				//ʹ��ָ���Ļ���������socketInStream����һ��������inStream
				inStream = new DataInputStream(new BufferedInputStream(socketInStream));
				
				//���������ֽ��������ڽ��չ̶�������Ϣ
				DataInput ReadMsg = new DataInputStream(socketInStream);
				
				//�������ֽ�����ȡ�������ļ��Ĵ�С
				long fileSize = ReadMsg.readLong();
				
				//����40KB���������뻺����
				byte[] InputCache = new byte[40960];

				//������ʱ��¼
				thisRecord = new Vector<String>();
				
				//��ȡ������������ļ�¼λ��
				recordIndex = MainWindow.taskListWindow.GetNewRecordIndex();
				
				thisRecord.add(0, String.valueOf(recordIndex + 1));				//��¼���
				thisRecord.add(1, ResName);										//��Դ����
				thisRecord.add(2, NumberFormat.getSizeFormat((double)fileSize));//��Դ��С
				thisRecord.add(3, "00.00%");									//���ؽ���
				thisRecord.add(4, "00.00B/s");									//��������
				thisRecord.add(5, "00:00:00");									//����ʱ��
				thisRecord.add(6, "������");										//����״̬
				thisRecord.add(7, "������Դ");									//��������
				
				//���ؿ�ʼ��������ʱ��
				timer.start();
				
				//������ʼʱ��
				long startTime = System.currentTimeMillis();
				
				for(long receiveSize = 0, count = 0; receiveSize < fileSize; count++) {
					
					NoResponseTime = 0;		//ˢ������Ӧʱ��
					
					int byteNum = inStream.read(InputCache);		//�������������ж�ȡ���ݵ����뻺��������InputCacheΪ��ʱ���÷�������
					receiveSize += byteNum;							//�ѽ��յ�������
					randomFileStream.write(InputCache, 0, byteNum);	//�ѻ�������������д������ļ���
					randomFileStream.skipBytes(byteNum);			//����byteNum���ֽڣ���֤����˳��д���ļ�
		
					//����Ƶ��ˢ�±����̶�Ƶ�ʸ���һ��"�����б�"��
					if(count % GlobalConstAndTag.refreshHz == 0) {
						long currentTime = System.currentTimeMillis();	//��ȡ��ǰʱ��
						long consumeTime = currentTime - startTime;		//���㵱ǰ��ʱ
						
						double DouReceiveSize = (double)receiveSize;	//����ת��
						
						double percent = DouReceiveSize/fileSize*100;	//��������ɰٷֱ�
						double speed = DouReceiveSize/consumeTime*1000;	//���㵱ǰ��������(B/s)
						
						thisRecord.set(3, new DecimalFormat("#.##").format(percent) + "%");
						thisRecord.set(4, NumberFormat.getSpeedFormat(speed));
						thisRecord.set(5, NumberFormat.getTimeFormat(consumeTime));
						
						//�������������б��
						MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
					}
				}
				
				long endTime = System.currentTimeMillis();	//��ȡ�������ʱ��
				long totalTime = endTime - startTime;		//���������ܺ�ʱ
				
				//������ɣ�ֹͣ��ʱ��
				timer.stop();
				
				//���±������ؼ�¼��Ϣ
				thisRecord.set(3, "100.00%");
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(5, NumberFormat.getTimeFormat(totalTime));
				thisRecord.set(6, "���سɹ�");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				
				MainWindow.SysMsgAdv.MsgAdv("\n��Դ " + ResName + " ���سɹ���");
				JOptionPane.showMessageDialog(null, "��Դ " + ResName + " ���سɹ���");
				
				//��Ǹ���Դ�������
				GlobalConstAndTag.isDownloading[NetResTableRow] = false;
				
				randomFileStream.close();	//�ر�����ļ���
				inStream.close();			//�ر�������
				socketInStream.close();		//�رջ���������
				clientSocket.close();		//�ر��׽���
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
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

	//��Լ�ʱ���������¼�����
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == timer) {
			NoResponseTime++;
			
			//��������������Ӧʱ�䳬���涨�����ʱ�ӵ�һ�룬����Ϊ����������ȴ����½�������
			if(NoResponseTime >= (GlobalConstAndTag.DelayTime/2)) {
				//���±������ؼ�¼��Ϣ
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "����Ӧ");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
			}
			
			//��������������Ӧʱ�䳬���涨�����ʱ�ӣ�����Ϊ�ܵ��ѶϿ�������ʧ��
			if(NoResponseTime >= GlobalConstAndTag.DelayTime) {
				timer.stop();	//����ʧ�ܣ�ֹͣ��ʱ
				GlobalConstAndTag.isDownloading[NetResTableRow] = false;	//��Ǹ���Դ����������
				
				//���±������ؼ�¼��Ϣ
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "����ʧ��");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				
				MainWindow.SysMsgAdv.MsgAdv("\n��Դ " + ResName + " ���ṩ�������ߣ�ǿ���ж����أ�");
				JOptionPane.showMessageDialog(null, "��Դ " + ResName + " ���ṩ�������ߣ�ǿ���ж����أ�");
				
				try {
					randomFileStream.close();	//�ر�����ļ���
					inStream.close();			//�ر�������
					socketInStream.close();		//�رջ���������
					clientSocket.close();		//�ر��׽���
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
