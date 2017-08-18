package tdrz.gui.window.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tdrz.core.util.SwtUtils;
import tdrz.core.util.ToolUtils;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.context.data.DataType;
import tool.function.FunctionUtils;

/**
 * 所有table的超类
 * @author MoeKagari
 */
public abstract class AbstractTable<T> extends AbstractTableSuper<T> {
	private final Table table;
	private final Predicate<T> filter;
	private final List<TableColumnManager> tcms = new ArrayList<>();
	private final List<T> datas = new ArrayList<>();
	private final List<Comparator<T>> sortColumns = new ArrayList<>();//多级排序顺序
	private final int ROW_HEADER_INDEX = 0;

	public AbstractTable(ApplicationMain main, String title) {
		super(main, title);

		this.filter = this.initFilter();

		this.initTCMS(this.tcms);
		if (FunctionUtils.isFalse(this.isTableSortable())) {
			this.tcms.forEach(tcm -> tcm.setSortable(false));
		}
		if (this.haveRowHeader()) {//行头
			TableColumnManager tcm = new TableColumnManager("No.", true, null);
			tcm.setSortable(false);//行头不可排序
			this.tcms.add(this.ROW_HEADER_INDEX, tcm);
		}

		this.table = new Table(this.centerComposite, SWT.MULTI | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.tcms.forEach(SortedTableColumn::new);

		//对table的一些操作
		Menu operationMenu = SwtUtils.makeCasacdeMenu(this.menuBar, "操作");
		{
			SwtUtils.makeMenuItem(operationMenu, SWT.PUSH, "刷新", this.getUpdateTableListener());
			SwtUtils.makeMenuItem(operationMenu, SWT.PUSH, "默认顺序", ev -> this.updateWindowRedraw(this::clearSort));
			SwtUtils.makeMenuItem(operationMenu, SWT.PUSH, "自适应列宽", ev -> this.updateWindowRedraw(this::autoWidth));
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	/** 刷新行号 */
	private void refreshRowHeader() {
		if (this.haveRowHeader()) {
			FunctionUtils.forEachUseIndex(this.table.getItems(), (index, tableItem) -> tableItem.setText(this.ROW_HEADER_INDEX, String.valueOf(index + 1)));
		}
	}

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

	/** 更新table */
	private void updateTable() {
		int top = this.table.getTopIndex();
		this.table.removeAll();

		this.updateData(this.datas);//更新数据
		this.datas.removeIf(this.filter);
		this.sortColumns.stream().reduce(Comparator::thenComparing).ifPresent(this.datas::sort);//多级排序
		FunctionUtils.toMapForEach(this.datas, //
				FunctionUtils::returnSelf, DataTableItem::new, //
				(data, tableItem) -> {
					tableItem.setText(this.tcms.stream().map(tcm -> tcm.getValue(data)).toArray(String[]::new));
				}//
		);
		this.refreshRowHeader();
		this.datas.clear();

		if (this.table.getData("autoWidth") == null) {//只自动pack一次
			this.autoWidth();
			this.table.setData("autoWidth", "");
		}
		this.table.setTopIndex(top);
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	/** new一个 更新table 的listener */
	protected final ControlSelectionListener getUpdateTableListener() {
		return new ControlSelectionListener(ev -> this.updateWindowRedraw(this::updateTable));
	}

	@Override
	public void update(DataType type) {
		if (this.needUpdate(type)) {
			this.updateWindowRedraw(this::updateTable);
		}
	}

	@Override
	public final void displayWindow() {
		FunctionUtils.ifRunnable(this.disposeAndUpdate(), this::updateTable);//显示之前更新
		super.displayWindow();
	}

	@Override
	public final void hiddenWindow() {
		super.hiddenWindow();
		FunctionUtils.ifRunnable(this.disposeAndUpdate(), this.table::removeAll);// 隐藏之后清空
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
	 * table列的排列功能
	 * @author MoeKagari
	 */
	private class SortedTableColumn extends SelectionAdapter implements Comparator<T> {
		private boolean direction = true;//是否从小到大排序,否则从大到小排序
		private final TableColumnManager tcm;
		private final TableColumn tableColumn;

		public SortedTableColumn(TableColumnManager tcm) {
			this.tcm = tcm;

			this.tableColumn = new TableColumn(AbstractTable.this.table, SWT.LEFT);
			this.tableColumn.setText(tcm.getName());
			this.tableColumn.setWidth(40);
			if (tcm.isSortable()) {
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
			Arrays.sort(tableItems, (a, b) -> this.compare(((DataTableItem) a).data, ((DataTableItem) b).data));
			for (TableItem tableItem : tableItems) {
				T data = ((DataTableItem) tableItem).data;
				String[] values = ToolUtils.toStringArray(AbstractTable.this.tcms.size(), tableItem::getText);
				tableItem.dispose();
				new DataTableItem(data).setText(values);
			}
			AbstractTable.this.refreshRowHeader();
		}

		@Override
		public int compare(T data1, T data2) {
			return (this.direction ? -1 : 1) * this.tcm.getComparator().compare(data1, data2);
		}
	}
}
