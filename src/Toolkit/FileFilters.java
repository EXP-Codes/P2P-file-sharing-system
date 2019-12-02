package Toolkit;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/*
 * 文件过滤器
 * 用于过滤文件选择框的文件类型
 */
public class FileFilters {

	//为文件选择框Dialog设置文件过滤器
	public static void addFileFilters(JFileChooser Dialog) {
		Dialog.setFileFilter(fileFilter_txt);
		Dialog.setFileFilter(fileFilter_doc);
		Dialog.setFileFilter(fileFilter_xls);
		Dialog.setFileFilter(fileFilter_ppt);
		Dialog.setFileFilter(fileFilter_pdf);
		Dialog.setFileFilter(fileFilter_bmp);
		Dialog.setFileFilter(fileFilter_mp3);
		Dialog.setFileFilter(fileFilter_mp4);
		Dialog.setFileFilter(fileFilter_exe);
		Dialog.setFileFilter(fileFilter_iso);
		Dialog.setFileFilter(fileFilter_rar);
		Dialog.setFileFilter(fileFilter_cpp);
		Dialog.setFileFilter(fileFilter_java);
		Dialog.setFileFilter(fileFilter_html);
		Dialog.setFileFilter(fileFilter_all);
		return;
	}
		
	//设置"*.txt"过滤器
	public static FileFilter fileFilter_txt = new FileFilter() {
		
		//设置过滤的文件类型
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".txt");
		}
		
		//设置该类型的描述，显示在对话框的文件类型下拉列表中
		public String getDescription() {
			return "*.txt";
		}
	};
	
	//设置"*.doc, *.docx"过滤器
	public static FileFilter fileFilter_doc = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".doc") | file.getName().endsWith(".docx");
		}
		
		public String getDescription() {
			return "*.doc, *.docx";
		}
	};
	
	//设置"*.xls, *.xlsx"过滤器
	public static FileFilter fileFilter_xls = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".xls") | file.getName().endsWith(".xlsx");
		}
		
		public String getDescription() {
			return "*.xls, *.xlsx";
		}
	};
	
	//设置"*.ppt, *.pptx"过滤器
	public static FileFilter fileFilter_ppt = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".ppt") | file.getName().endsWith(".pptx");
		}
		
		public String getDescription() {
			return "*.ppt, *.pptx";
		}
	};
	
	//设置"*.pdf, *.chm"过滤器
	public static FileFilter fileFilter_pdf = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".pdf") | file.getName().endsWith(".chm");
		}
		
		public String getDescription() {
			return "*.pdf, *.chm";
		}
	};
	
	//设置"*.bmp, *.jpg, *.jpeg, *.png, *.gif"过滤器
	public static FileFilter fileFilter_bmp = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".bmp") | 
					file.getName().endsWith(".jpg") | file.getName().endsWith(".jpeg") | 
					file.getName().endsWith(".png") | file.getName().endsWith(".gif");
		}
		
		public String getDescription() {
			return "*.bmp, *.jpg, *.jpeg, *.png, *.gif";
		}
	};
	
	//设置"*.mp3, *.avi"过滤器
	public static FileFilter fileFilter_mp3 = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".mp3") | file.getName().endsWith(".wav");
		}
		
		public String getDescription() {
			return "*.mp3, *.avi";
		}
	};
	
	//设置"*.mp4, *.wmv, *.avi, *.rmvb"过滤器
	public static FileFilter fileFilter_mp4 = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".mp4") | file.getName().endsWith(".wmv") | 
					file.getName().endsWith(".avi") |file.getName().endsWith(".rmvb");
		}
		
		public String getDescription() {
			return "*.mp4, *.wmv, *.avi, *.rmvb";
		}
	};
	
	//设置"*.exe, *.msi"过滤器
	public static FileFilter fileFilter_exe = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".exe") | file.getName().endsWith(".msi");
		}
		
		public String getDescription() {
			return "*.exe, *.msi";
		}
	};
	
	//设置"*.iso"过滤器
	public static FileFilter fileFilter_iso = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".iso");
		}
		
		public String getDescription() {
			return "*.iso";
		}
	};
	
	//设置"*.rar"过滤器
	public static FileFilter fileFilter_rar = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".rar") | file.getName().endsWith(".zip") | file.getName().endsWith(".7z");
		}
		
		public String getDescription() {
			return "*.rar, *.zip, *.7z";
		}
	};
	
	//设置"*.cpp"过滤器
	public static FileFilter fileFilter_cpp = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".cpp") | file.getName().endsWith(".hpp") | file.getName().endsWith(".c") | file.getName().endsWith(".h");
		}
		
		public String getDescription() {
			return "*.cpp, *.hpp, *.c, *.h";
		}
	};
	
	//设置"*.java"过滤器
	public static FileFilter fileFilter_java = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".java") | file.getName().endsWith(".class");
		}
		
		public String getDescription() {
			return "*.java, *.class";
		}
	};
	
	//设置"*.html, *.htm, *.css, *.xml"过滤器
	public static FileFilter fileFilter_html = new FileFilter() {
		
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(".html") | file.getName().endsWith(".htm") | file.getName().endsWith(".css") | file.getName().endsWith(".xml");
		}
		
		public String getDescription() {
			return "*.html, *.htm, *.css, *.xml";
		}
	};
		
	//设置"*"过滤器
	public static FileFilter fileFilter_all = new FileFilter() {
		
		public boolean accept(File file) {
			return true;
		}
		
		public String getDescription() {
			return "All File";
		}
	};
	
}
