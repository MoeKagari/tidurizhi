package tdrz.gui.window.sub.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tdrz.core.config.AppConstants;
import tdrz.core.logic.TimeString;
import tdrz.core.translator.DeckDtoTranslator;
import tdrz.core.translator.ItemDtoTranslator;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.core.util.SwtUtils;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.MasterDataDto.MasterShipDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

/**
 * 所有舰娘
 * @author MoeKagari
 */
public abstract class ShipListTable extends AbstractTable<ShipDto> {
	private Button noLimitDeckButton;
	private List<Button> deckButtons;
	private Composite fleetFilterComposite;

	private Button noLimitTypeButton;
	private List<Button> typeButtons;
	private Composite typeFilterComposite;

	private List<Button> infoButtons;
	private Composite infoFilterComposite;

	public ShipListTable(ApplicationMain main, String title) {
		super(main, title);
	}

	@Override
	public boolean haveTopComposite() {
		return true;
	}

	@Override
	public String getWindowConfigKey() {
		return ShipListTable.class.getName() + "-" + this.getMode();
	}

	@Override
	protected void updateData(List<ShipDto> datas) {
		datas.addAll(GlobalContext.getShipMap().values());
		Collections.sort(datas, Comparator.comparingInt(ShipDto::getId));
	}

	public enum ShipListTableMode {
		INFORMATION,
		PARAMENTER,
		ALL
	}

	protected abstract ShipListTableMode getMode();

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("ID", true, ShipDto::getId));
		tcms.add(new TableColumnManager("舰娘", ShipDtoTranslator::getName));
		tcms.add(new TableColumnManager("舰种", ShipDtoTranslator::getTypeString));
		tcms.add(new TableColumnManager("等级", true, ShipDto::getLevel));
		tcms.add(new TableColumnManager("所处", ShipDtoTranslator::whichDeckString));

		switch (this.getMode()) {
			case INFORMATION:
				this.initTCMS_information(tcms);
				break;
			case PARAMENTER:
				this.initTCMS_paramenter(tcms);
				break;
			case ALL:
				this.initTCMS_information(tcms);
				this.initTCMS_paramenter(tcms);
				break;
		}

		tcms.add(new TableColumnManager("炮击战", true, ShipDtoTranslator::getPowerHougeki));
		tcms.add(new TableColumnManager("雷击战", true, ShipDtoTranslator::getPowerRageki));
		tcms.add(new TableColumnManager("夜战", true, ShipDtoTranslator::getPowerMidnight));
		IntStream.range(0, 5).mapToObj(index -> new TableColumnManager(String.format("装备%d", index + 1), rd -> {
			ItemDto item = GlobalContext.getItem(index == 4 ? rd.getSlotex() : rd.getSlots()[index]);
			return FunctionUtils.notNull(item, ItemDtoTranslator::getNameWithLevel, "");
		})).forEach(tcms::add);
		tcms.add(new TableColumnManager("出击海域", rd -> FunctionUtils.ifFunction(rd.getSallyArea(), sa -> sa != 0, String::valueOf, "")));
	}

	private void initTCMS_information(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("补给", rd -> ShipDtoTranslator.needHokyo(rd) ? "需要" : ""));
		tcms.add(new TableColumnManager("Cond", true, ShipDto::getCond));
		tcms.add(new TableColumnManager("现有经验", true, ShipDto::getCurrentExp));
		tcms.add(new TableColumnManager("升级所需", true, ShipDto::getNextExp));
		tcms.add(new TableColumnManager("现在耐久", true, ShipDto::getNowHp));
		tcms.add(new TableColumnManager("最大耐久", true, ShipDto::getMaxHp));
		tcms.add(new TableColumnManager("速力", rd -> ShipDtoTranslator.getSokuString(rd, false)));
		tcms.add(new TableColumnManager("增设", rd -> rd.getSlotex() != 0 ? "有" : ""));
		tcms.add(new TableColumnManager("Lock", rd -> rd.isLocked() ? "" : "无"));
		tcms.add(new TableColumnManager("远征中", rd -> ShipDtoTranslator.isInMission(rd) ? "是" : ""));
		tcms.add(new TableColumnManager("入渠中", rd -> ShipDtoTranslator.isInNyukyo(rd) ? "是" : ""));
		tcms.add(new TableColumnManager("油耗", true, rd -> FunctionUtils.notNull(rd.getMasterData(), MasterShipDto::getFuelMax, "")));
		tcms.add(new TableColumnManager("弹耗", true, rd -> FunctionUtils.notNull(rd.getMasterData(), MasterShipDto::getBullMax, "")));
		tcms.add(new TableColumnManager("消耗", true, rd -> FunctionUtils.notNull(rd.getMasterData(), msd -> msd.getFuelMax() + msd.getBullMax(), "")));
		{
			TableColumnManager tcm = new TableColumnManager("状态", rd -> ShipDtoTranslator.getStateString(rd, false));
			tcm.setComparator(Comparator.comparingDouble(ShipDtoTranslator::getHPPercent));
			tcms.add(tcm);
		}
		{
			TableColumnManager tcm = new TableColumnManager("修理时间", rd -> TimeString.toDateRestString(rd.getNdockTime() / 1000, ""));
			tcm.setComparator(Comparator.comparingLong(ShipDto::getNdockTime));
			tcms.add(tcm);
		}
		{
			TableColumnManager tcm = new TableColumnManager("修理花费", rd -> ShipDtoTranslator.perfectState(rd) ? "" : Arrays.toString(rd.getNdockCost()));
			tcm.setComparator(Comparator.comparingInt(rd -> rd.getNdockCost()[0]));
			tcms.add(tcm);
		}
	}

	private void initTCMS_paramenter(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("火力", true, rd -> rd.getKaryoku()[0]));
		tcms.add(new TableColumnManager("雷装", true, rd -> rd.getRaisou()[0]));
		tcms.add(new TableColumnManager("对空", true, rd -> rd.getTaiku()[0]));
		tcms.add(new TableColumnManager("装甲", true, rd -> rd.getSoukou()[0]));
		tcms.add(new TableColumnManager("回避", true, rd -> rd.getKaihi()[0]));
		tcms.add(new TableColumnManager("对潜", true, rd -> rd.getTaisen()[0]));
		tcms.add(new TableColumnManager("索敌", true, rd -> rd.getSakuteki()[0]));
		tcms.add(new TableColumnManager("运", true, rd -> rd.getLuck()[0]));
	}

	@Override
	protected Predicate<ShipDto> initFilter() {
		Composite filterComposite = new Composite(this.topComposite, SWT.NONE);
		filterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filterComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0, 2, 2, 4, 0));
		{
			this.fleetFilterComposite = new Composite(filterComposite, SWT.NONE);
			this.fleetFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.fleetFilterComposite.setLayout(SwtUtils.makeGridLayout(20, 0, 0, 0, 0));
		}
		{
			this.typeFilterComposite = new Composite(filterComposite, SWT.NONE);
			this.typeFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.typeFilterComposite.setLayout(SwtUtils.makeGridLayout(20, 0, 0, 0, 0));
		}
		{
			this.infoFilterComposite = new Composite(filterComposite, SWT.NONE);
			this.infoFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.infoFilterComposite.setLayout(SwtUtils.makeGridLayout(20, 0, 0, 0, 0));
		}

		return this.buildFilter().negate();
	}

	private Predicate<ShipDto> buildFilter() {
		{
			this.noLimitDeckButton = new Button(this.fleetFilterComposite, SWT.RADIO);
			this.noLimitDeckButton.setText("无限制");
			this.noLimitDeckButton.setSelection(true);
			this.noLimitDeckButton.addSelectionListener(new ControlSelectionListener(ev -> {
				this.deckButtons.forEach(button -> button.setSelection(false));
			}).andThen(this.getUpdateTableListener()));
		}
		this.deckButtons = new ArrayList<>();
		Predicate<ShipDto> deckFilter = Stream.of(//
				ship -> this.noLimitDeckButton.getSelection(),//
				this.newDeckFilter("无所属舰队", IntStream::noneMatch, 0, 1, 2, 3),//
				this.newDeckFilter(AppConstants.DEFAULT_FLEET_NAME[0], IntStream::allMatch, 0),//
				this.newDeckFilter(AppConstants.DEFAULT_FLEET_NAME[1], IntStream::allMatch, 1),//
				this.newDeckFilter(AppConstants.DEFAULT_FLEET_NAME[2], IntStream::allMatch, 2),//
				this.newDeckFilter(AppConstants.DEFAULT_FLEET_NAME[3], IntStream::allMatch, 3)//
		).reduce(Predicate::or).get();

		{
			this.noLimitTypeButton = new Button(this.typeFilterComposite, SWT.RADIO);
			this.noLimitTypeButton.setText("全舰");
			this.noLimitTypeButton.setSelection(true);
			this.noLimitTypeButton.addSelectionListener(new ControlSelectionListener(ev -> {
				this.typeButtons.forEach(button -> button.setSelection(false));
			}).andThen(this.getUpdateTableListener()));
		}
		this.typeButtons = new ArrayList<>();
		Predicate<ShipDto> typeFilter = Stream.of(//
				ship -> this.noLimitTypeButton.getSelection(),//
				this.newTypeFilter("駆逐艦", 2),//
				this.newTypeFilter("軽巡洋艦", 3),//
				this.newTypeFilter("重雷装巡洋艦", 4),//
				this.newTypeFilter("重巡洋艦", 5),//
				this.newTypeFilter("航空巡洋艦", 6),//
				this.newTypeFilter("軽空母", 7),//
				this.newTypeFilter("正規空母", 11),//
				this.newTypeFilter("装甲空母", 18),//
				this.newTypeFilter("戦艦", 8, 9, 10, 12),//
				this.newTypeFilter("潜水艦", 13, 14),//
				this.newTypeFilter("水上機母艦", 16),//
				this.newTypeFilter("其它", 1, 17, 19, 20, 21, 22)//
		).reduce(Predicate::or).get();

		this.infoButtons = new ArrayList<>();
		Predicate<ShipDto> infoFilter = Stream.of(//
				this.newInfoFilter("没远征", ship -> FunctionUtils.isFalse(ShipDtoTranslator.isInMission(ship))),//
				this.newInfoFilter("非LV1", ship -> ship.getLevel() != 1),//
				this.newInfoFilter("婚舰", ship -> ship.getLevel() > 99),//
				this.newInfoFilter("Lock", ShipDto::isLocked),//
				this.newInfoFilter("有增设", ship -> ship.getSlotex() != 0),//
				this.newInfoFilter("有闪", ship -> ship.getCond() > 49),//
				this.newInfoFilter("需入渠", ship -> FunctionUtils.isFalse(ShipDtoTranslator.perfectState(ship))),//
				this.newInfoFilter("需补给", ShipDtoTranslator::needHokyo),//
				this.newInfoFilter("可装大发系", ShipDtoTranslator::canEquipDaihatsu),//
				this.newInfoFilter("可先制反潜", ShipDtoTranslator::canOpeningTaisen),//
				this.newInfoFilter("有贴条", ship -> ship.getSallyArea() != 0)//
		).reduce(Predicate::and).get();

		return Stream.of(deckFilter, typeFilter, infoFilter).reduce(Predicate::and).get();
	}

	private Predicate<ShipDto> newDeckFilter(String text, BiPredicate<IntStream, IntPredicate> bpre, int... deckNumbers) {
		Button button = new Button(this.fleetFilterComposite, SWT.CHECK);
		button.setText(text);
		button.addSelectionListener(new ControlSelectionListener(ev -> {
			this.noLimitDeckButton.setSelection(this.deckButtons.stream().noneMatch(Button::getSelection));
		}).andThen(this.getUpdateTableListener()));
		this.deckButtons.add(button);

		return this.buildButtonPredicate(button, ship -> {
			return bpre.test(IntStream.of(deckNumbers), deckNumber -> DeckDtoTranslator.isShipInDeck(deckNumber, ship));
		}, false);
	}

	private Predicate<ShipDto> newTypeFilter(String text, int... types) {
		Button button = new Button(this.typeFilterComposite, SWT.CHECK);
		button.setText(text);
		button.addSelectionListener(new ControlSelectionListener(ev -> {
			this.noLimitTypeButton.setSelection(this.typeButtons.stream().noneMatch(Button::getSelection));
		}).andThen(this.getUpdateTableListener()));
		this.typeButtons.add(button);

		return this.buildButtonPredicate(button, ship -> {
			MasterShipDto msd = ship.getMasterData();
			if (msd != null) {
				return IntStream.of(types).anyMatch(type -> type == msd.getType());
			}
			return false;
		}, false);
	}

	private Predicate<ShipDto> newInfoFilter(String text, Predicate<ShipDto> shipPredicater) {
		Button button = new Button(this.infoFilterComposite, SWT.CHECK);
		button.setText(text);
		button.addSelectionListener(this.getUpdateTableListener());
		this.infoButtons.add(button);

		return this.buildButtonPredicate(button, shipPredicater, true);
	}

	private Predicate<ShipDto> buildButtonPredicate(Button button, Predicate<ShipDto> valueWhenSelected, boolean defaultValue) {
		return ship -> {
			if (button.getSelection()) {
				return valueWhenSelected.test(ship);
			}
			return defaultValue;
		};
	}
}
