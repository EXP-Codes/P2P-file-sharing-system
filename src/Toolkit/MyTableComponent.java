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
 * �����������
 * ���ڴ������������������Ч��
 */
public class MyTableComponent extends JTable implements MouseMotionListener, MouseListener {

	//���ڱ�ʶ���ࣨ�����󣩵�Ψһ�ԣ���ϵͳ�Զ�����
	private static final long serialVersionUID = 6246394655030720271L;

	//������
	public int TableLength;		
	
	//�����µı���Ԫ����Ⱦ��
	public NewCellRenderer newCellRenderer;
	
	//��ǰ������ڱ�����
	public static int currentMouseRow = -1;
		
	public MyTableComponent(Vector<Vector<String>> TableData, Vector<String> TableColName) {
		//���ø��෽����ͨ������TableColName������TableData�����JTable
		super(new DefaultTableModel(TableData, TableColName));
		
		//����һ��ֻ��ѡ���һ��
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		TableLength = TableData.size();	//��ȡ�����ȣ����������
		setRenderer();					//���ñ���Ⱦ�������ñ����ԣ�
		
		this.addMouseListener(this);		//���������������¡��ͷš�������������뿪��
		this.addMouseMotionListener(this);	//������궯���������ƶ����϶���
	}
	
	//���õ�Ԫ�����ݾ�����ʾ(��д�ķ������÷����Զ�����)
	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		//��ȡ��ǰ���ĵ�Ԫ���ȱʡ��Ⱦ��
		DefaultTableCellRenderer CellRenderer = (DefaultTableCellRenderer) super.getDefaultRenderer(columnClass);
		
		//���õ�Ԫ�����ݵ�ˮƽ���뷽ʽΪ"����"
		CellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		return CellRenderer;
	}
	
	//�����񲻿ɱ༭(��д�ķ������÷����Զ�����)
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	//���ñ���Ⱦ�������ñ����ԣ�
	private void setRenderer() {
		//��ȡ��ǰ���ı�ͷ��ȱʡ��Ⱦ����������������
		DefaultTableCellRenderer HeaderRenderer = (DefaultTableCellRenderer) this.getTableHeader().getDefaultRenderer();
		
		//���ñ�ͷ���ݵ�ˮƽ���뷽ʽΪ"����"
		HeaderRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		
		//ͨ�������µĵ�Ԫ����Ⱦ��NewCellRenderer�Ĳ������ԣ��滻Ĭ�ϵĵ�Ԫ����Ⱦ��DefaultRenderer�Ĳ�������
		//�˷���Ϊ�����ñ�����ʾЧ��
		newCellRenderer = new NewCellRenderer();
		this.setDefaultRenderer(Object.class, newCellRenderer);
		
		//���ݵ�Ԫ��������ʱ�������п�
		newCellRenderer.adjustTableColumnWidths(this);
		return;
	}
	
	/*
	 * ��Ƕ�࣬���õ�Ԫ������Ⱦ��������
	 */
	public class NewCellRenderer extends DefaultTableCellRenderer {
		
		//���ڱ�ʶ���ࣨ�����󣩵�Ψһ�ԣ���ϵͳ�Զ�����
		private static final long serialVersionUID = 2448719575083152260L;

		//�����޸ĺ�ı���Ԫ����Ⱦ��
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			//���ñ���ż�еı���ɫ
			if (row % 2 == 0) {
				this.setBackground(new Color(236, 246, 248));	//�Ұ�ɫ
			}
			else {
				this.setBackground(new Color(255, 255, 255));	//��ɫ
			}
			
			//�������������(������)����ɫ
			if (row == currentMouseRow) {
				this.setBackground(new Color(154, 221, 151));	//ǳ��ɫ
			}
			
			//�̳�Label��ķ���, ����table�ĵ�Ԫ����뷽ʽ
			this.setHorizontalAlignment((int) Component.CENTER_ALIGNMENT); 		// ˮƽ����
			this.setHorizontalTextPosition((int) Component.CENTER_ALIGNMENT); 	// ��ֱ����

			table.getTableHeader().setBackground(new Color(206, 231, 255));	//���ñ�ͷ�ı���ɫ:	����ɫ
			table.setSelectionBackground(new Color(233, 251, 4));			//����ѡ���еı���ɫ:	�ʻ�
			table.setSelectionForeground(new Color(0, 0, 255));				//����ѡ���е�ǰ��ɫ:	����
			return super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
		}
		
		//�����п�
		public void adjustTableColumnWidths(JTable table) {
			JTableHeader header = table.getTableHeader(); 		//��ȡ��ͷ
			int rowCount = table.getRowCount(); 				//��ȡ��������
			TableColumnModel tcm = table.getColumnModel(); 		//��ȡ������ģ��
			
			for (int col = 0; col < tcm.getColumnCount(); col++) {	//ѭ������ÿһ��
				TableColumn column = tcm.getColumn(col);			//��ȡ��col���ж���
				
				// �ñ�ͷ����Ⱦ�������col�б�ͷ�Ŀ��
				int Colwidth = (int) header.getDefaultRenderer().getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();

				for (int row = 0; row < rowCount; row++) {			//ѭ�������i�е�ÿһ�У��õ�Ԫ����Ⱦ�������col�е�row�еĵ�Ԫ�񳤶�
					int CellWidth = (int) table.getCellRenderer(row, col).getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
					Colwidth = Math.max(Colwidth, CellWidth); 		//ȡ���Ŀ����Ϊ�п�
				}
				//���ϵ�Ԫ��֮���ˮƽ��ࣨȱʡΪ1���أ�
				Colwidth += table.getIntercellSpacing().width;

				//���õ�col�е���ѡ���
				column.setPreferredWidth(Colwidth);
			}
			//�����������õĿ�����²��ָ�����
			table.doLayout();
			return;
		}
	}

	//��꣨���º��϶��¼�
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//��꣨û�а��£��ƶ��¼�
	@Override
	public void mouseMoved(MouseEvent e) {
		Point mouseLocation = e.getPoint();					//��ȡ��굱ǰ����
		currentMouseRow = this.rowAtPoint(mouseLocation);	//������������ȡ��굱ǰ������
		
		//������ڱ�����ƶ�ʱ�������������е���ɫ
		for(int r = 0; r < TableLength; r++) {
			if(r == currentMouseRow){
				MyTableComponent.this.setBackground(Color.green);
				break;
			}
		}
		//��Ԫ���ػ棬Ŀ���Ǽ���getTableCellRendererComponent����
		this.repaint();
	}
	
	//������¼�
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//�����루�����¼�
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//����뿪�������¼�
	@Override
	public void mouseExited(MouseEvent arg0) {
		//��������뿪���󣬻�ԭ������ԭ������ɫ
		currentMouseRow = -1;	//����������Ϊ-1
		this.repaint();			//��Ԫ���ػ�
	}

	//��갴�£����ţ��¼�
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	//����ͷ��¼�
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
