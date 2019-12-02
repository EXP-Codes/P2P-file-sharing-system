package Toolkit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/*
 * 表单组件配置器
 * 用于创建表单组件，并配置其效果
 */
public class MyTableComponent extends JTable implements MouseMotionListener, MouseListener {

	//用于标识本类（本对象）的唯一性，由系统自动生成
	private static final long serialVersionUID = 6246394655030720271L;

	//表单长度
	public int TableLength;		
	
	//配置新的表单单元格渲染器
	public NewCellRenderer newCellRenderer;
	
	//当前鼠标所在表单的行
	public static int currentMouseRow = -1;
		
	public MyTableComponent(Vector<Vector<String>> TableData, Vector<String> TableColName) {
		//调用父类方法，通过列名TableColName和数据TableData构造表单JTable
		super(new DefaultTableModel(TableData, TableColName));
		
		//设置一次只能选择表单一行
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		TableLength = TableData.size();	//获取表单长度（表格行数）
		setRenderer();					//配置表单渲染器（设置表单属性）
		
		this.addMouseListener(this);		//设置鼠标监听（按下、释放、单击、进入或离开）
		this.addMouseMotionListener(this);	//设置鼠标动作监听（移动、拖动）
	}
	
	//设置单元格内容居中显示(重写的方法，该方法自动调用)
	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		//获取当前表格的单元格的缺省渲染器
		DefaultTableCellRenderer CellRenderer = (DefaultTableCellRenderer) super.getDefaultRenderer(columnClass);
		
		//设置单元格内容的水平对齐方式为"居中"
		CellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		return CellRenderer;
	}
	
	//定义表格不可编辑(重写的方法，该方法自动调用)
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	//配置表单渲染器（设置表单属性）
	private void setRenderer() {
		//获取当前表格的表头的缺省渲染器（属性配置器）
		DefaultTableCellRenderer HeaderRenderer = (DefaultTableCellRenderer) this.getTableHeader().getDefaultRenderer();
		
		//设置表头内容的水平对齐方式为"居中"
		HeaderRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		
		//通过设置新的单元格渲染器NewCellRenderer的部分属性，替换默认的单元格渲染器DefaultRenderer的部分属性
		//此方法为了设置表单的显示效果
		newCellRenderer = new NewCellRenderer();
		this.setDefaultRenderer(Object.class, newCellRenderer);
		
		//根据单元格内容随时调整表单列宽
		newCellRenderer.adjustTableColumnWidths(this);
		return;
	}
	
	/*
	 * 内嵌类，设置单元格新渲染器的属性
	 */
	public class NewCellRenderer extends DefaultTableCellRenderer {
		
		//用于标识本类（本对象）的唯一性，由系统自动生成
		private static final long serialVersionUID = 2448719575083152260L;

		//返回修改后的表单单元格渲染器
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			//设置表单奇偶行的背景色
			if (row % 2 == 0) {
				this.setBackground(new Color(236, 246, 248));	//灰白色
			}
			else {
				this.setBackground(new Color(255, 255, 255));	//白色
			}
			
			//设置鼠标所在行(悬浮行)的颜色
			if (row == currentMouseRow) {
				this.setBackground(new Color(154, 221, 151));	//浅绿色
			}
			
			//继承Label类的方法, 设置table的单元格对齐方式
			this.setHorizontalAlignment((int) Component.CENTER_ALIGNMENT); 		// 水平居中
			this.setHorizontalTextPosition((int) Component.CENTER_ALIGNMENT); 	// 垂直居中

			table.getTableHeader().setBackground(new Color(206, 231, 255));	//设置表头的背景色:	天蓝色
			table.setSelectionBackground(new Color(233, 251, 4));			//设置选中行的背景色:	鲜黄
			table.setSelectionForeground(new Color(0, 0, 255));				//设置选中行的前景色:	深蓝
			return super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
		}
		
		//调整列宽
		public void adjustTableColumnWidths(JTable table) {
			JTableHeader header = table.getTableHeader(); 		//获取表头
			int rowCount = table.getRowCount(); 				//获取表格的行数
			TableColumnModel tcm = table.getColumnModel(); 		//获取表格的列模型
			
			for (int col = 0; col < tcm.getColumnCount(); col++) {	//循环处理每一列
				TableColumn column = tcm.getColumn(col);			//获取第col个列对象
				
				// 用表头的渲染器计算第col列表头的宽度
				int Colwidth = (int) header.getDefaultRenderer().getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();

				for (int row = 0; row < rowCount; row++) {			//循环处理第i列的每一行，用单元格渲染器计算第col列第row行的单元格长度
					int CellWidth = (int) table.getCellRenderer(row, col).getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
					Colwidth = Math.max(Colwidth, CellWidth); 		//取最大的宽度作为列宽
				}
				//加上单元格之间的水平间距（缺省为1像素）
				Colwidth += table.getIntercellSpacing().width;

				//设置第col列的首选宽度
				column.setPreferredWidth(Colwidth);
			}
			//按照上述设置的宽度重新布局各个列
			table.doLayout();
			return;
		}
	}

	//鼠标（按下后）拖动事件
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//鼠标（没有按下）移动事件
	@Override
	public void mouseMoved(MouseEvent e) {
		Point mouseLocation = e.getPoint();					//获取鼠标当前坐标
		currentMouseRow = this.rowAtPoint(mouseLocation);	//根据鼠标坐标获取鼠标当前所在行
		
		//当鼠标在表格上移动时，设置其所在行的颜色
		for(int r = 0; r < TableLength; r++) {
			if(r == currentMouseRow){
				MyTableComponent.this.setBackground(Color.green);
				break;
			}
		}
		//单元格重绘，目的是激活getTableCellRendererComponent方法
		this.repaint();
	}
	
	//鼠标点击事件
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//鼠标进入（表单）事件
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//鼠标离开（表单）事件
	@Override
	public void mouseExited(MouseEvent arg0) {
		//设置鼠标离开表格后，还原悬浮行原本的颜色
		currentMouseRow = -1;	//设置悬浮行为-1
		this.repaint();			//单元格重绘
	}

	//鼠标按下（不放）事件
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	//鼠标释放事件
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
