package Toolkit;

import java.awt.Toolkit;

/*
 * 屏幕尺寸获取器
 */
public class GetScreenSize {
	//获取屏幕宽高
	public static final double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
}
