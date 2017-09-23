package tdrz.gui.window.sub.table;

import java.util.ArrayList;
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
import tdrz.core.translator.DeckDtoTranslator;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.core.util.SwtUtils;
import tdrz.gui.other.ControlSelectionListener;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.MasterDataDto.MasterShipDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

/**
 * 所有舰娘
 * 
 * @author MoeKagari
 */
public abstract class ShipListTable extends ShipListAbstract {
	private Button noLimitDeckButton;
	private List<Button> deckButtons;
	private Composite fleetFilterComposite;

	private Button noLimitTypeButton;
	private List<Button> typeButtons;
	private Composite typeFilterComposite;

	private List<Button> infoButtons;
	private Composite infoFilterComposite;

	@Override
	public String defaultTitle() {
		return this.getMode().title;
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
		datas.sort(Comparator.comparingInt(ShipDto::getId));
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
