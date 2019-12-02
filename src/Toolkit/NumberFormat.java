package Toolkit;

import java.text.DecimalFormat;

/*
 * ���ݸ�ʽ������
 */
public class NumberFormat {
	/**
	 * �ļ���С�ĸ�ʽ��
	 */
	//byteSizeΪ�ļ��Ĵ�С����λΪ"B"
	public static String getSizeFormat(double byteSize) {
		
		if(byteSize <= 0) {
			return "0B";
		}
		
		//��ʽ��double���ݣ�������ʾ��λС��
		DecimalFormat doubleFormat = new DecimalFormat("#.##");
		
		double kiloByte = byteSize/1024;
		if(kiloByte < 1) {
			return doubleFormat.format(byteSize) + "B";
		}
		
		double megaByte = kiloByte/1024;
		if(megaByte < 1) {
			return doubleFormat.format(kiloByte) + "KB";
		}
		
		double gigaByte = megaByte/1024;
		if(gigaByte < 1) {
			return doubleFormat.format(megaByte) + "MB";
		}
		
		double teraBytes = gigaByte/1024;
		if(teraBytes < 1) {
			return doubleFormat.format(gigaByte) + "GB";
		}
		
		return doubleFormat.format(teraBytes) + "TB";
	}
	
	/**
	 * �������ʵĸ�ʽ��
	 */
	//byteSpeedΪ�������ʣ���λΪ"B/s"
	public static String getSpeedFormat(double byteSpeed) {
		
		if(byteSpeed <= 0) {
			return "0B/s";
		}
		
		//��ʽ��double���ݣ�������ʾ��λС��
		DecimalFormat doubleFormat = new DecimalFormat("#.##");
		
		double kiloSpeed = byteSpeed/1024;
		if(kiloSpeed < 1) {
			return doubleFormat.format(byteSpeed) + "B/s";
		}
		
		double megaSpeed = kiloSpeed/1024;
		if(megaSpeed < 1) {
			return doubleFormat.format(kiloSpeed) + "KB/s";
		}
		
		double gigaSpeed = megaSpeed/1024;
		if(gigaSpeed < 1) {
			return doubleFormat.format(megaSpeed) + "MB/s";
		}
		
		double teraSpeed = gigaSpeed/1024;
		if(teraSpeed < 1) {
			return doubleFormat.format(gigaSpeed) + "GB/s";
		}
		
		return doubleFormat.format(teraSpeed) + "TB/s";
	} 
	
	/**
	 * �����ʱ�ĸ�ʽ��
	 */
	//msTimeΪ������ִ��ʱ�䣬��λΪ"ms"
	public static String getTimeFormat(long msTime) {
		
		if(msTime <= 0) {
			return "00:00:00";
		}
		
		long sTime = msTime/1000;
		long mTime = sTime/60;
		long hTime = mTime/60;
		
		sTime %= 60;
		mTime %= 60;
		hTime %= 24;
		
		String sTimeStr = sTime<9? "0"+sTime : ""+sTime;
		String mTimeStr = mTime<9? "0"+mTime : ""+mTime;
		String hTimeStr = hTime<9? "0"+hTime : ""+hTime;
		
		return hTimeStr + ":" + mTimeStr + ":" + sTimeStr;
	} 
}
