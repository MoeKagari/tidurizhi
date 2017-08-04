package tdrz.gui.window.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tdrz.gui.window.WindowBase;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContext;
import tdrz.update.data.DataType;
import tdrz.utils.ToolUtils;
import tool.FunctionUtils;

public abstract class AbstractTable<T> extends WindowBase {
	private Table table;
	private final List<T> datas = new ArrayList<>();
	private final List<TableColumnManager> tcms = new ArrayList<>();
	private final List<SortedTableColumn> sortColumns = new ArrayList<>();//多级排序顺序
	private final Predicate<T> filter;
	private final ControlSelectionListener updateTableListener = new ControlSelectionListener(ev -> this.updateWindowRedraw(this::updateTable));

	public AbstractTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.filter = this.initFilter();
		this.initTCMS(this.tcms);
		if (FunctionUtils.isFalse(this.isTableSortable())) {
			this.tcms.forEach(tcm -> tcm.sortable = false);
		}

		this.initTable();
		this.initMenuBar();
	}

	private void initTable() {
		this.table = new Table(this.getCenterComposite(), SWT.MULTI | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		if (this.haveNo()) {//行头
			TableColumnManager tcm = new TableColumnManager("No.", true, null);
			tcm.sortable = false;//行头不可排序
			this.tcms.add(0, tcm);
		}

		for (int index = 0; index < this.tcms.size(); index++) {
			new SortedTableColumn(index, this.tcms.get(index));
		}
	}

	private void initMenuBar() {
		MenuItem cmdMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
		cmdMenuItem.setText("操作");
		Menu cmdMenu = new Menu(cmdMenuItem);
		cmdMenuItem.setMenu(cmdMenu);
		{
			MenuItem update = new MenuItem(cmdMenu, SWT.PUSH);
			update.setText("刷新");
			update.addSelectionListener(this.updateTableListener);

			MenuItem clearSort = new MenuItem(cmdMenu, SWT.PUSH);
			clearSort.setText("默认顺序");
			clearSort.addSelectionListener(new ControlSelectionListener(ev -> this.updateWindowRedraw(this::clearSort)));

			MenuItem autoWidth = new MenuItem(cmdMenu, SWT.PUSH);
			autoWidth.setText("自适应列宽");
			autoWidth.addSelectionListener(new ControlSelectionListener(ev -> this.updateWindowRedraw(this::autoWidth)));
		}

//		MenuItem otherMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
//		otherMenuItem.setText("其它");
//		Menu otherMenu = new Menu(otherMenuItem);
//		otherMenuItem.setMenu(otherMenu);
//		{
//			MenuItem topMostMenuItem = new MenuItem(otherMenu, SWT.CHECK);
//			topMostMenuItem.setText("总在前");
//			topMostMenuItem.addSelectionListener(new ControlSelectionListener(ev -> this.setTopMost(topMostMenuItem.getSelection())));
//		}
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	/** 清除排序 */
	private void clearSort() {
		if (this.isTableSortable()) {
			this.sortColumns.clear();
			this.table.setSortColumn(null);
			this.updateTable();
		}
	}

	/** 自适应列宽 */
	private void autoWidth() {
		FunctionUtils.forEach(this.table.getColumns(), TableColumn::pack);
	}

	private void updateTable() {
		int top = this.table.getTopIndex();
		FunctionUtils.forEach(this.table.getItems(), TableItem::dispose);

		//更新数据
		this.updateData(this.datas);
		this.datas.removeIf(this.filter);
		//多级排序	Comparator<T> originalOrder = (a, b) -> 0;
		this.datas.sort(this.sortColumns.stream().map(stc -> (Comparator<T>) stc).reduce((a, b) -> 0, Comparator::thenComparing));
		for (int row = 0; row < this.datas.size(); row++) {
			T data = this.datas.get(row);
			DataTableItem tableItem = new DataTableItem(data);
			for (int col = 0; col < this.tcms.size(); col++) {
				TableColumnManager tcm = this.tcms.get(col);
				tableItem.setText(col, tcm.getValue(row + 1, data));
			}
		}
		this.datas.clear();

		if (this.table.getData("packed") == null) {//只自动pack一次
			this.autoWidth();
			this.table.setData("packed", "");
		}
		this.table.setTopIndex(top);
	}

	@Override
	public void update(DataType type) {
		if (this.needUpdate(type)) {
			this.updateWindowRedraw(this::updateTable);
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	public final ControlSelectionListener getUpdateTableListener() {
		return this.updateTableListener;
	}

	@Override
	protected boolean canMaxSize() {
		return true;
	}

	/** table是否启用排序,默认true,也可单独对TableColumnManager设置,但此方法false时,会覆盖所有的TableColumnManager为false */
	protected boolean isTableSortable() {
		return true;
	}

	/** true时候remove某条data */
	protected Predicate<T> initFilter() {
		return data -> false;
	}

	/** 是否有数字行头,默认true */
	protected boolean haveNo() {
		return true;
	}

	/** 是否,隐藏之后清空,显示之前更新,默认true */
	protected boolean disposeAndUpdate() {
		return true;
	}

	@Override
	public void displayWindow() {
		FunctionUtils.ifRunnable(this.disposeAndUpdate(), this::updateTable);//显示之前更新
		super.displayWindow();
	}

	@Override
	public void hiddenWindow() {
		super.hiddenWindow();
		FunctionUtils.ifRunnable(this.disposeAndUpdate(), this.table::clearAll);// 隐藏之后清空
	}

	/** 添加列 */
	protected abstract void initTCMS(List<TableColumnManager> tcms);

	/** 添加行 */
	protected abstract void updateData(List<T> datas);

	/** {@link GlobalContext}接收到类型为type的数据,更新了全局数据后,是否自动更新此table,默认false */
	protected boolean needUpdate(DataType type) {
		return false;
	}

	private class DataTableItem extends TableItem {
		public final T data;

		public DataTableItem(T data) {
			super(AbstractTable.this.table, SWT.NONE);
			this.data = data;
		}

		public DataTableItem(T data, int index) {
			super(AbstractTable.this.table, SWT.NONE, index);
			this.data = data;
		}

		@Override
		protected void checkSubclass() {}
	}

	/**
	 * table列的属性
	 * @author MoeKagari
	 */
	protected class TableColumnManager {
		private final boolean isInteger;
		private final String name;
		private final Function<T, Object> value;
		private Comparator<T> comparator = null;
		private boolean sortable = true;

		public TableColumnManager(String name, boolean isInteger, Function<T, Object> value) {
			this.name = name;
			this.isInteger = isInteger;
			this.value = value;
		}

		public TableColumnManager(String name, Function<T, Object> value) {
			this(name, false, value);
		}

		private String getValue(int index, T t) {// null 为 行号列
			return this.value == null ? Integer.toString(index) : this.value.apply(t).toString();
		}

		public void setSortable(boolean sortable) {
			this.sortable = sortable;
		}

		public void setComparator(Comparator<T> comparator) {
			this.comparator = comparator;
		}
	}

	/**
	 * table列的排列功能
	 * @author MoeKagari
	 */
	private class SortedTableColumn extends SelectionAdapter implements Comparator<T> {
		private boolean direction = true;//是否从小到大排序,否则从大到小排序
		private final int index;
		private final TableColumnManager tcm;
		private final TableColumn tableColumn;

		public SortedTableColumn(int index, TableColumnManager tcm) {
			this.index = index;
			this.tcm = tcm;

			this.tableColumn = new TableColumn(AbstractTable.this.table, SWT.LEFT);
			this.tableColumn.setText(tcm.name);
			this.tableColumn.setWidth(40);
			if (tcm.sortable) {
				this.tableColumn.addSelectionListener(this);
			}
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			AbstractTable.this.sortColumns.remove(this);
			AbstractTable.this.sortColumns.add(this);

			if (AbstractTable.this.table.getSortColumn() == this.tableColumn) {
				this.direction = !this.direction;//如果不是改变排序列,则改变排序方向
			}

			AbstractTable.this.updateWindowRedraw(this::sortTable);
			AbstractTable.this.table.setSortColumn(this.tableColumn);
			AbstractTable.this.table.setSortDirection(this.direction ? SWT.DOWN : SWT.UP);
		}

		/* 排序table */
		@SuppressWarnings("unchecked")
		private void sortTable() {
			TableItem[] tableItems = AbstractTable.this.table.getItems();
			Arrays.sort(tableItems, (a, b) -> this.compare((DataTableItem) a, (DataTableItem) b));
			for (TableItem tableItem : tableItems) {
				T data = ((DataTableItem) tableItem).data;
				String[] values = ToolUtils.toStringArray(AbstractTable.this.tcms.size(), tableItem::getText);

				tableItem.dispose();
				new DataTableItem(data).setText(values);
			}
			if (AbstractTable.this.haveNo()) {//刷新行号
				FunctionUtils.forEach(AbstractTable.this.table.getItems(), (tableItem, index) -> tableItem.setText(0, String.valueOf(index + 1)));
			}
		}

		private int getSign() {
			return this.direction ? -1 : 1;
		}

		private int compare(DataTableItem item1, DataTableItem item2) {
			if (this.tcm.comparator != null) {
				return this.getSign() * this.tcm.comparator.compare(item1.data, item2.data);
			} else {
				return this.compare(item1.getText(this.index), item2.getText(this.index));
			}
		}

		@Override
		public int compare(T data1, T data2) {
			if (this.tcm.comparator != null) {
				return this.getSign() * this.tcm.comparator.compare(data1, data2);
			} else {
				return this.compare(this.tcm.value.apply(data1).toString(), this.tcm.value.apply(data2).toString());
			}
		}

		private int compare(String value1, String value2) {
			if (this.tcm.isInteger) {
				int a = StringUtils.isBlank(value1) ? 0 : Integer.parseInt(value1);
				int b = StringUtils.isBlank(value2) ? 0 : Integer.parseInt(value2);
				return this.getSign() * Integer.compare(a, b);
			} else {
				return this.getSign() * value1.compareTo(value2);
			}
		}
	}
}
