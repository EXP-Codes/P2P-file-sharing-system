package JxtaService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.Timer;

import Toolkit.GlobalConstAndTag;
import Toolkit.NumberFormat;
import Windows.MainWindow;

/*
 * ���ؽڵ�Peer��Server����Ĵ����̲߳���
 * ��Ҫ������������Peer�ĵ�����������
 * ���ڲ��д������Բ�ͬPeer�Ա��ز�ͬ��Դ�����ݴ���
 * 
 * =======================================================
 * 
 * �����������������������ܵ�������ܵ�:
 * 1�����������ط�[����/��ȡ]����ʱ��Ӧ�ô���������ȡ������������ͨ������ܵ���ȡ��
 * 2�����������ط�[����/д��]����ʱ��Ӧ���������д�룬���������ͨ������ܵ���ȡ��
 * 
 */
public class JxtaServerForSend implements Runnable, ActionListener {

	private Socket serverScoket = null;			//�����׽��֣����ڱ���Server��Client֮������Ӳ��������ݴ���
	private OutputStream socketOutStream = null;//�����׽��ֵĻ��������
	private OutputStream outStream = null;		//ʹ��ָ���Ļ���������������������	
	private FileInputStream fileStream = null;	//�ļ�����������ȡ��������Դ������
	
	private long ResSize;	//��������Դ�Ĵ�С���ֽ�����
	private String ResPath;	//��������Դ�ڱ��صľ���λ��
	private String ResName;	//��������Դ����Դ����
	
	private Vector<String> thisRecord;	//���η�������ļ�¼��Ϣ
	int recordIndex = -1;				//���η���������"�����б�"�ļ�¼λ�ã�������
	
	private Timer timer;			//��ʱ�������ڼ��ܵ���ͨ��
	private int NoResponseTime = 0;	//�ͻ���������Ӧʱ��
	
	public JxtaServerForSend(Socket serverScoket, String resName, long resSize, String resPath) {
		this.serverScoket = serverScoket;
		this.ResSize = resSize;
		this.ResPath = resPath;
		this.ResName = resName;
		timer = new Timer(1000, this);
	}
	
	//�����̣߳�����Դ����ڵ㷢������
	@Override
	public void run() {
		SendData();
	}

	//����Դ����ڵ㷢������
	private void SendData() {
		try {
			//������Դ����·���򿪴����͵Ĺ�����Դ�ļ�sharingResFile
			File sharingResFile = new File(ResPath);
			
			//��������sharingResFile���ļ�����������ȡ���ļ�������
			fileStream = new FileInputStream(sharingResFile);

			//��ȡ�����׽��ֵĻ��������
			socketOutStream = serverScoket.getOutputStream();
			
			//ʹ��ָ���Ļ��������socketOutStream����һ�������outStream
			outStream = new DataOutputStream(new BufferedOutputStream(socketOutStream));
			
			//��������ֽ��������ڷ��͹̶�������Ϣ
			DataOutput WriteMsg = new DataOutputStream(socketOutStream);
			
			//��������ֽ������ʹ������ļ��Ĵ�С
			WriteMsg.writeLong(ResSize);
			
			//����40KB���ļ���ȡ������
			byte[] ReadCache = new byte[40960];
			
			//������ʱ��¼
			thisRecord = new Vector<String>();
			
			//��ȡ������������ļ�¼λ��
			recordIndex = MainWindow.taskListWindow.GetNewRecordIndex();
			
			thisRecord.add(0, String.valueOf(recordIndex + 1));				//��¼���
			thisRecord.add(1, ResName);										//��Դ����
			thisRecord.add(2, NumberFormat.getSizeFormat((double)ResSize)); //��Դ��С
			thisRecord.add(3, "00.00%");									//�������
			thisRecord.add(4, "00.00B/s");									//��������
			thisRecord.add(5, "00:00:00");									//����ʱ��
			thisRecord.add(6, "������");										//����״̬
			thisRecord.add(7, "������Դ");									//��������
			
			//���Ϳ�ʼ��������ʱ��
			timer.start();
			
			//������ʼʱ��
			long startTime = System.currentTimeMillis();
			
			//�Էְ���ʽѭ����ȡ������Դ�����ݲ�����
			for(long sendSize = 0, count = 0; ; count++) {
				
				NoResponseTime = 0;		//ˢ������Ӧʱ��
				
				//���ļ��������ж�ȡ������Դ�����ݣ��洢���ļ���ȡ������ReadCache
				int byteNum = fileStream.read(ReadCache);
				
				//�ѷ��͵�������
				sendSize += byteNum;
				
				//�ѵ���������ĩβ�����ݶ�ȡ���
				if(byteNum == -1) {
					break;
				}
				
				//�ѻ�����ReadCache��byteNum���ֽ�����д�������outStream
				outStream.write(ReadCache, 0, byteNum);
				
				//ˢ�������outStream��������ǿ�Ʒ��͵�Client
				outStream.flush();
				
				//����Ƶ��ˢ�±����̶�Ƶ�ʸ���һ��"�����б�"��
				if(count % GlobalConstAndTag.refreshHz == 0) {
					long currentTime = System.currentTimeMillis();	//��ȡ��ǰʱ��
					long consumeTime = currentTime - startTime;		//���㵱ǰ��ʱ
					
					double DouReceiveSize = (double)sendSize;		//����ת��
					
					double percent = DouReceiveSize/ResSize*100;	//��������ɰٷֱ�
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
			
			//���±��δ����¼��Ϣ
			thisRecord.set(3, "100.00%");
			thisRecord.set(4, "00.00B/s");
			thisRecord.set(5, NumberFormat.getTimeFormat(totalTime));
			thisRecord.set(6, "���ͳɹ�");
			MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
			
			MainWindow.SysMsgAdv.MsgAdv("\n��Դ " + ResName + " ���ͳɹ���");
			
			fileStream.close();		//�ر��ļ�������
			outStream.close();		//�ر������
			socketOutStream.close();//�رջ��������
			serverScoket.close();	//�ر��׽���
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//��Լ�ʱ���������¼�����
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == timer) {
			NoResponseTime++;
			
			//���ͻ���������Ӧʱ�䳬���涨�����ʱ�ӵ�һ�룬����Ϊ����������ȴ����½�������
			if(NoResponseTime >= (GlobalConstAndTag.DelayTime/2)) {
				//���±��η��ͼ�¼��Ϣ
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "����Ӧ");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
			}
			
			//���ͻ���������Ӧʱ�䳬���涨�����ʱ�ӣ�����Ϊ�ܵ��ѶϿ�������ʧ��
			if(NoResponseTime >= GlobalConstAndTag.DelayTime) {
				timer.stop();	//����ʧ�ܣ�ֹͣ��ʱ
				
				//���±������ؼ�¼��Ϣ
				thisRecord.set(4, "00.00B/s");
				thisRecord.set(6, "����ʧ��");
				MainWindow.taskListWindow.UpdataRecord(recordIndex, thisRecord);
				
				MainWindow.SysMsgAdv.MsgAdv("\n��Դ " + ResName + " �Ľ����������ߣ�ǿ���ж����أ�");
			}
		}
	}

}
