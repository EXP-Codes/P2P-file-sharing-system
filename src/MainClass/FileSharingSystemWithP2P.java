package MainClass;

import Toolkit.GetScreenSize;
import Windows.MainWindow;

/**
 * @Object: ����P2P�������ļ�����ϵͳ
 * @author  EXP: 272629724@qq.com
 */

/*
 * P2P�ļ�����ϵͳ��Main-Class
 */
public class FileSharingSystemWithP2P {

	//������Ĵ��ڱ���
	private String windowTitle = "P2P�ļ�����ϵͳ";
	
	//������Ĵ��ڿ��
	private final int winWidth = 725;
	private final int winHeight = 580;
	
	public FileSharingSystemWithP2P() {
		//�������������Ͻ�����Ļ�ϵ�������꣬ʹ������������Ļ������ʾ
		int LocationX = (int)(GetScreenSize.screenWidth/2 - winWidth/2);
		int LocationY = (int)(GetScreenSize.screenHeight/2 - winHeight/2);
		
		MainWindow mainWindow = new MainWindow(windowTitle);//����������ʵ��
		mainWindow.setLocation(LocationX, LocationY);		//����������λ��
		mainWindow.setSize(winWidth, winHeight);			//����������ߴ�
		mainWindow.setVisible(true);						//����������ɼ�
	}
	
	public static void main(String[] args) {
		//����P2P�ļ�����ϵͳ
		new FileSharingSystemWithP2P();
	}

}
