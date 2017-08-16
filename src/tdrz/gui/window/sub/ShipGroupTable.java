package tdrz.gui.window.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tdrz.core.config.AppConstants;
import tdrz.core.config.ShipGroup;
import tdrz.core.translator.ItemDtoTranslator;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.core.util.SwtUtils;
import tdrz.gui.window.WindowResource;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.ShipDto;
import tool.FunctionUtils;

public class ShipGroupTable extends AbstractTable<ShipDto> {
	private final Button addButton, removeButton, editButton;
	private final org.eclipse.swt.widgets.List groupList;

	private String selected = null;
	private final AddGroupWindow addGroupWindow = new AddGroupWindow();
	private final EditGroupWindow editGroupWindow = new EditGroupWindow();

	public ShipGroupTable(ApplicationMain main, String title) {
		super(main, title);

		Composite leftContentComposite = new Composite(this.leftComposite, SWT.NONE);
		leftContentComposite.setLayoutData(SwtUtils.makeGridData(GridData.FILL_BOTH, 200));
		leftContentComposite.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0, 0, 0, 0, 4));
		{
			this.addButton = new Button(leftContentComposite, SWT.PUSH);
			this.addButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			this.addButton.setText("新建");
			this.addButton.addSelectionListener(new ControlSelectionListener(ev -> {
				this.addGroupWindow.displayWindow();
			}));

			this.removeButton = new Button(leftContentComposite, SWT.PUSH);
			this.removeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			this.removeButton.setText("删除");
			this.removeButton.addSelectionListener(new ControlSelectionListener(ev -> {
				Optional.ofNullable(this.selected).ifPresent(selectedName -> {
					this.groupList.remove(selectedName);
					ShipGroup.get().removeIf(sg -> sg.getName().equals(selectedName));
					this.selected = null;
					this.getUpdateTableListener().widgetSelected(ev);
				});
			}));

			this.editButton = new Button(leftContentComposite, SWT.PUSH);
			this.editButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			this.editButton.setText("编辑");
			this.editButton.addSelectionListener(new ControlSelectionListener(ev -> {
				this.editGroupWindow.displayWindow();
			}));

			this.groupList = new org.eclipse.swt.widgets.List(leftContentComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			this.groupList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
			this.groupList.setFont(new Font(WindowResource.DISPLAY, this.groupList.getFont().toString(), 18, SWT.NORMAL));
			this.groupList.setItems(ShipGroup.get().stream().map(ShipGroup::getName).toArray(String[]::new));
			this.groupList.addSelectionListener(new ControlSelectionListener(ev -> {
				int selectionIndex = this.groupList.getSelectionIndex();
				if (selectionIndex != -1) {
					String selectedName = this.groupList.getItem(selectionIndex);
					if (FunctionUtils.isFalse(StringUtils.equals(this.selected, selectedName))) {
						this.selected = selectedName;
						this.getUpdateTableListener().widgetSelected(ev);
					}
				}
			}));
		}
	}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(1055, 504));
	}

	@Override
	protected boolean haveLeftComposite() {
		return true;
	}

	@Override
	public boolean defaultVisible() {
		return true;
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("ID", true, ShipDto::getId));
		tcms.add(new TableColumnManager("舰娘", ShipDtoTranslator::getName));
		tcms.add(new TableColumnManager("舰种", ShipDtoTranslator::getTypeString));
		tcms.add(new TableColumnManager("等级", true, ShipDto::getLevel));
		tcms.add(new TableColumnManager("所处", rd -> FunctionUtils.ifFunction(ShipDtoTranslator.whichDeck(rd), wd -> wd != -1, wd -> AppConstants.DEFAULT_FLEET_NAME[wd], "")));
		tcms.add(new TableColumnManager("Cond", true, ShipDto::getCond));
		tcms.add(new TableColumnManager("现有经验", true, ShipDto::getCurrentExp));
		tcms.add(new TableColumnManager("升级所需", true, ShipDto::getNextExp));
		tcms.add(new TableColumnManager("入渠中", rd -> ShipDtoTranslator.isInNyukyo(rd) ? "是" : ""));
		for (int i = 0; i < 5; i++) {
			final int index = i;
			tcms.add(new TableColumnManager("装备" + (index + 1), rd -> {
				ItemDto item = GlobalContext.getItem(index == 4 ? rd.getSlotex() : rd.getSlots()[index]);
				return FunctionUtils.notNull(item, ItemDtoTranslator::getNameWithLevel, "");
			}));
		}
		tcms.add(new TableColumnManager("出击海域", rd -> FunctionUtils.ifFunction(rd.getSallyArea(), sa -> sa != 0, String::valueOf, "")));
	}

	@Override
	protected void updateData(List<ShipDto> datas) {
		Optional.ofNullable(this.selected).ifPresent(selectedName -> {
			ShipGroup.get().stream().filter(sg -> sg.getName().equals(selectedName)).findFirst().ifPresent(sg -> {
				sg.stream().map(GlobalContext::getShip).filter(FunctionUtils::isNotNull).forEach(datas::add);
			});
		});
	}

	private Point getCenterLocation(Point windowSize) {
		Point size = this.getWindowConfig().getSize();
		Point location = this.getWindowConfig().getLocation();
		return new Point(location.x + (size.x - windowSize.x) / 2, location.y + (size.y - windowSize.y) / 2);
	}

	private abstract class AbstractOperationWindow {
		protected final Shell operationShell;
		public final Composite operationComposite;

		public AbstractOperationWindow(String title) {
			this.operationShell = new Shell(ShipGroupTable.this.shell, SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
			this.operationShell.setText(title);
			this.operationShell.setSize(this.defaultSize());
			this.operationShell.setImage(WindowResource.LOGO);
			this.operationShell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			this.operationShell.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.operationShell.addListener(SWT.Close, ev -> {
				ev.doit = false;
				this.hiddenWindow();
			});

			this.operationComposite = new Composite(this.operationShell, SWT.NONE);
			this.operationComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.operationComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		public void displayWindow() {
			this.operationShell.setLocation(ShipGroupTable.this.getCenterLocation(this.operationShell.getSize()));
			this.operationShell.setVisible(true);
		}

		public void hiddenWindow() {
			this.operationShell.setVisible(false);
		}

		public abstract Point defaultSize();
	}

	private class AddGroupWindow extends AbstractOperationWindow {
		private final Text nameText;
		private final Label tipLabel;

		public AddGroupWindow() {
			super("新建分组");

			new Label(this.operationComposite, SWT.LEFT).setText("请输入组名 : ");

			this.nameText = new Text(this.operationComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.CENTER);
			this.nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			this.nameText.addModifyListener(ev -> {
				String name = this.nameText.getText();
				if (ShipGroup.get().stream().map(ShipGroup::getName).anyMatch(obj -> StringUtils.equals(obj, name))) {
					this.tipLabel.setText("已存在的组名");
				} else {
					this.tipLabel.setText("");
				}
			});

			this.tipLabel = new Label(this.operationComposite, SWT.LEFT);
			this.tipLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

			Button doAddGroup = new Button(this.operationComposite, SWT.PUSH);
			doAddGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
			doAddGroup.setText("新建");
			doAddGroup.addSelectionListener(new ControlSelectionListener(ev -> {
				this.hiddenWindow();
				Optional.ofNullable(this.nameText.getText()).filter(StringUtils::isNotEmpty).ifPresent(name -> {
					if (ShipGroup.get().stream().map(ShipGroup::getName).noneMatch(obj -> StringUtils.equals(obj, name))) {
						ShipGroup.get().add(new ShipGroup(name));
						ShipGroupTable.this.groupList.add(name);
					}
				});
			}));
		}

		@Override
		public void displayWindow() {
			this.nameText.setText("");
			this.tipLabel.setText("");
			super.displayWindow();
		}

		@Override
		public Point defaultSize() {
			return SwtUtils.DPIAwareSize(new Point(250, 122));
		}
	}

	private class EditGroupWindow extends AbstractOperationWindow {
		private final Button addShipButton, removeShipButton;
		private final org.eclipse.swt.widgets.List shipList;
		private final Map<String, Integer> shipDataMap = new HashMap<>();

		public EditGroupWindow() {
			super("");

			this.shipList = new org.eclipse.swt.widgets.List(this.operationComposite, SWT.MULTI | SWT.V_SCROLL);
			this.shipList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

			this.addShipButton = new Button(this.operationComposite, SWT.PUSH);
			this.addShipButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			this.addShipButton.setText("添加");
			this.addShipButton.addSelectionListener(new ControlSelectionListener(ev -> {
				this.editGroup(ev, true);
			}));

			this.removeShipButton = new Button(this.operationComposite, SWT.PUSH);
			this.removeShipButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			this.removeShipButton.setText("删除");
			this.removeShipButton.addSelectionListener(new ControlSelectionListener(ev -> {
				this.editGroup(ev, false);
			}));
		}

		private void editGroup(SelectionEvent ev, boolean add) {
			ShipGroup.get().stream().filter(sg -> StringUtils.equals(ShipGroupTable.this.selected, sg.getName())).findFirst().ifPresent(shipGroup -> {
				Predicate<Integer> filter;
				Consumer<Integer> con;
				if (add) {
					filter = id -> FunctionUtils.isFalse(shipGroup.contains(id));
					con = shipGroup::add;
				} else {
					filter = id -> FunctionUtils.isTrue(shipGroup.contains(id));
					con = shipGroup::remove;
				}
				Integer[] selection = Arrays.stream(this.shipList.getSelection()).map(this.shipDataMap::get).filter(filter).toArray(Integer[]::new);
				if (selection.length != 0) {
					FunctionUtils.forEach(selection, con);
					ShipGroupTable.this.getUpdateTableListener().widgetSelected(ev);
				}
			});
		}

		@Override
		public void displayWindow() {
			Optional.ofNullable(ShipGroupTable.this.selected).ifPresent(selectedName -> {
				this.operationShell.setText(selectedName);

				List<ShipDto> shipData = new ArrayList<>(GlobalContext.getShipMap().values());
				shipData.sort(Comparator.comparingInt(ShipDto::getLevel).reversed());
				this.shipDataMap.clear();

				Function<ShipDto, String> keyFunction = ship -> String.format("(Lv.%d)%s(%d)", ship.getLevel(), ShipDtoTranslator.getName(ship), ship.getId());
				FunctionUtils.toMapForEach(shipData, keyFunction, ShipDto::getId, this.shipDataMap::put);
				this.shipList.setItems(shipData.stream().map(keyFunction).toArray(String[]::new));

				super.displayWindow();
			});
		}

		@Override
		public Point defaultSize() {
			return SwtUtils.DPIAwareSize(new Point(200, 250));
		}
	}
}
