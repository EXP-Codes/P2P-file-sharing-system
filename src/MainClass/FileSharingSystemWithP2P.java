package MainClass;

import Toolkit.GetScreenSize;
import Windows.MainWindow;

/**
 * @Object: 基于P2P技术的文件共享系统
 * @author  EXP: 272629724@qq.com
 */

/*
 * P2P文件共享系统的Main-Class
 */
public class FileSharingSystemWithP2P {

	//主界面的窗口标题
	private String windowTitle = "P2P文件共享系统";
	
	//主界面的窗口宽高
	private final int winWidth = 725;
	private final int winHeight = 580;
	
	public FileSharingSystemWithP2P() {
		//计算主界面左上角在屏幕上的落点坐标，使得主界面在屏幕居中显示
		int LocationX = (int)(GetScreenSize.screenWidth/2 - winWidth/2);
		int LocationY = (int)(GetScreenSize.screenHeight/2 - winHeight/2);
		
		MainWindow mainWindow = new MainWindow(windowTitle);//创建主界面实例
		mainWindow.setLocation(LocationX, LocationY);		//设置主界面位置
		mainWindow.setSize(winWidth, winHeight);			//设置主界面尺寸
		mainWindow.setVisible(true);						//设置主界面可见
	}
	
	public static void main(String[] args) {
		//运行P2P文件共享系统
		new FileSharingSystemWithP2P();
	}

}
