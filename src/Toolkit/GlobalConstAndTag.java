package Toolkit;

/*
 * ȫ�ֳ�����ȫ�ֱ�ʶ
 */
public class GlobalConstAndTag {

	//������Դʱ������ÿ��������һ��Peer������Դ��������
	public static int MaxPeerAdvNum = 50;
		
	//������
	public static final int TableLength = 100;
	
	//�����Դ����������б����������ص���Դ�������ظ�����
	public static boolean[] isDownloading = new boolean[TableLength];
	
	//���������б�ĸ���Ƶ�ʣ�ÿ����refreshHz�����ݰ�����һ���б�
	public static final int refreshHz = 100;
	
	//�ж��ܵ��Ͽ������ʱʱ�ӣ�5�룩
	public static final int DelayTime = 5;
	
	//��Դ�ļ��������ʱ�ޣ�1Сʱ��
	public static final long shareTime = 3600000;
	
	//�����Ŭ��������Դ����ʱ��1�룩
	public static final long searchTime = 1000;
	
	//Peer֮��ɹ�����JxtaSocket����ܵ��������ʱ��3�룩
	public static final int createPipeTime = 3000;
	
	//�Ƿ�ʹ��TCP����Э�飨trueΪTCP��falseΪUDP��
	public static final boolean useTCP = true;
}
