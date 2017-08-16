package tdrz.gui.window.sub;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sup.WindowBase;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tool.FunctionUtils;

public abstract class AbstractTableSuper<T> extends WindowBase {
	public AbstractTableSuper(ApplicationMain main, String title) {
		super(main, title);
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

	/**
	 * table列的属性
	 * @author MoeKagari
	 */
	protected class TableColumnManager {
		private final String name;
		private final Function<T, Object> value;
		private Comparator<T> comparator;
		private boolean sortable = true;

		public TableColumnManager(String name, Function<T, Object> value) {
			this(name, false, value);
		}

		public TableColumnManager(String name, boolean isInteger, Function<T, Object> value) {
			this.name = name;
			this.value = value;
			if (isInteger) {
				this.comparator = (a, b) -> {
					int i1 = FunctionUtils.ifFunction(this.getValue(a), StringUtils::isNotBlank, Integer::parseInt, 0);
					int i2 = FunctionUtils.ifFunction(this.getValue(b), StringUtils::isNotBlank, Integer::parseInt, 0);
					return Integer.compare(i1, i2);
				};
			} else {
				this.comparator = Comparator.comparing(this::getValue);
			}
		}

		public String getValue(T data) {
			return Optional.ofNullable(this.value).map(v -> v.apply(data)).map(Object::toString).orElse("");
		}

		public String getName() {
			return this.name;
		}

		public Comparator<T> getComparator() {
			return this.comparator;
		}

		public boolean isSortable() {
			return this.sortable;
		}

		public void setSortable(boolean sortable) {
			this.sortable = sortable;
		}

		public void setComparator(Comparator<T> comparator) {
			this.comparator = comparator;
		}
	}
}
