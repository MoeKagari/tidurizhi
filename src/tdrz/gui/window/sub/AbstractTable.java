package tdrz.gui.window.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
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
import tdrz.gui.window.listener.WindowConfigChangedAdapter;
import tdrz.gui.window.sup.WindowBase;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tool.function.FunctionUtils;

/**
 * 所有table的超类
 * @author MoeKagari
 */
public abstract class AbstractTable<T> extends WindowBase {
	private final Table table;
	private final Predicate<T> filter;
	private final List<TableColumnManager> tcms = new ArrayList<>();
	private final List<T> datas = new ArrayList<>();
	private final List<Comparator<T>> sortColumns = new ArrayList<>();//多级排序顺序
	private final int ROW_HEADER_INDEX = 0;

	public AbstractTable() {
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
		FunctionUtils.forEachUseIndex(this.tcms, SortedTableColumn::new);

		//对table的一些操作
		Menu operationMenu = SwtUtils.makeCasacdeMenu(this.menuBar, "操作");
		{
			SwtUtils.makeMenuItem(operationMenu, SWT.PUSH, "刷新", this.getUpdateTableListener());
			SwtUtils.makeMenuItem(operationMenu, SWT.PUSH, "默认顺序", ev -> this.updateWindowRedraw(this::clearSort));
			SwtUtils.makeMenuItem(operationMenu, SWT.PUSH, "自适应列宽", ev -> this.updateWindowRedraw(this::autoWidth));
		}

		this.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
			@Override
			public void displayBefore() {
				FunctionUtils.ifRunnable(AbstractTable.this.disposeAndUpdate(), AbstractTable.this::updateTable);//显示之前更新
			}

			@Override
			public void hiddenAfter() {
				FunctionUtils.ifRunnable(AbstractTable.this.disposeAndUpdate(), AbstractTable.this.table::removeAll);//隐藏之后清空
			}
		});
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
		if (this.datas.size() != 0) {
			this.datas.removeIf(this.filter);
			//排序a,b,c
			//定理 : aaaaaaa=a
			//命题 : a,b,c的混乱排序,最后一个a,b,c的顺序为abc(比如abbabbbcaacbccc)
			//由于[定理],这个混乱排序可以等于 a,b,c的混乱排序+abc 
			//问 : a,b,c的混乱排序+abc = abc
			//1.数据集对于结尾排序c来说无相等的数据,很容易理解命题成立
			//2.数据集对于结尾排序c来说有相等的数据,这些相等的数据聚集成块
			//(1)a,b的混乱排序+ab 是否等于 ab
			//结尾排序b对块的内部不会发生变化
			//所以命题变为 aaaaaaaa+ab 是否等于 ab
			//很显然相等
			//(2)a,b,c的混乱排序+abc 是否等于 abc
			//结尾排序c对块的内部不会发生变化
			//所以命题变为 a,b的混乱排序+abc 是否等于 abc
			//由于a,b的混乱排序+ab 等于 ab
			//所以命题成立
			//同理证明多个排序
			this.sortColumns.forEach(this.datas::sort);//多级排序
			FunctionUtils.toMapForEach(this.datas, //
					FunctionUtils::returnSelf, DataTableItem::new, //
					(data, tableItem) -> tableItem.setText(//
							this.tcms.stream().map(tcm -> tcm.getValue(data)).toArray(String[]::new)//
					)//
			);
			this.datas.clear();

			if (this.table.getData("autoWidth") == null) {//只自动 autoWidth 一次
				this.autoWidth();
				this.table.setData("autoWidth", "");
			}
		}
		this.table.setTopIndex(top);
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

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
	protected boolean haveRowHeader() {
		return true;
	}

	/** 是否,隐藏之后清空,显示之前更新,默认true */
	protected boolean disposeAndUpdate() {
		return true;
	}

	/** {@link GlobalContext}接收到类型为type的数据,更新了全局数据后,是否自动更新此table,默认false */
	protected boolean needUpdate(DataType type) {
		return false;
	}

	/** 添加列 */
	protected abstract void initTCMS(List<TableColumnManager> tcms);

	/** 添加行 */
	protected abstract void updateData(List<T> datas);

	/*------------------------------------------------------------------------------------------------------------------------*/

	protected class DataTableItem extends TableItem {
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
		private final String name;
		private final boolean isInteger;
		private final Function<T, Object> value;

		private boolean sortable = true;
		private Comparator<T> comparator;

		public TableColumnManager(String name, Function<T, Object> value) {
			this(name, false, value);
		}

		public TableColumnManager(String name, boolean isInteger, Function<T, Object> value) {
			this.name = name;
			this.isInteger = isInteger;
			this.value = value;
		}

		public String getValue(T data) {
			return Optional.ofNullable(this.value).map(v -> v.apply(data)).map(Object::toString).orElse("");
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
			Arrays.sort(tableItems, (a, b) -> {
				if (this.tcm.comparator != null) {
					return this.getSign() * this.tcm.comparator.compare(((DataTableItem) a).data, ((DataTableItem) b).data);
				} else {
					return this.compare(a.getText(this.index), b.getText(this.index));
				}
			});
			for (TableItem tableItem : tableItems) {
				String[] values = ToolUtils.toStringArray(AbstractTable.this.tcms.size(), tableItem::getText);
				tableItem.dispose();
				new DataTableItem(((DataTableItem) tableItem).data).setText(values);
			}
			AbstractTable.this.refreshRowHeader();
		}

		private int getSign() {
			return this.direction ? -1 : 1;
		}

		@Override
		public int compare(T data1, T data2) {
			if (this.tcm.comparator != null) {
				return this.getSign() * this.tcm.comparator.compare(data1, data2);
			} else {
				return this.compare(this.tcm.getValue(data1), this.tcm.getValue(data2));
			}
		}

		private int compare(String text1, String text2) {
			int result;
			if (this.tcm.isInteger) {
				int i1 = FunctionUtils.ifFunction(text1, StringUtils::isNotBlank, Integer::parseInt, 0);
				int i2 = FunctionUtils.ifFunction(text2, StringUtils::isNotBlank, Integer::parseInt, 0);
				result = Integer.compare(i1, i2);
			} else {
				result = text1.compareTo(text2);
			}
			return this.getSign() * result;
		}
	}
}
