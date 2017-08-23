package tdrz.gui.composite;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tdrz.core.config.AppConstants;
import tdrz.core.logic.HPMessage;
import tdrz.core.translator.DeckDtoTranslator;
import tdrz.core.translator.ItemDtoTranslator;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.core.util.SwtUtils;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.GlobalContextUpdater;
import tdrz.update.context.data.ApiDataListener;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.word.DeckDto;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

public class FleetWindow implements ApiDataListener {
	private final int id;//1,2,3,4
	private final Composite composite;

	/** 舰队名 */
	private Label fleetNameLabel;
	/** 制空 */
	private Label zhikongLabel;
	/** 索敌 */
	private Label suodiLabel;
	/** 总等级 */
	private Label totallvLabel;
	/** 舰队速度 */
	private Label sokuLabel;

	private final ShipComposite[] shipComposites;

	public FleetWindow(int id, Composite composite) {
		this.id = id;

		this.composite = composite;
		this.composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.fleetNameLabel = new Label(this.composite, SWT.CENTER);
		this.fleetNameLabel.setText(AppConstants.DEFAULT_FLEET_NAME[this.id - 1]);
		this.fleetNameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		SwtUtils.insertHSeparator(this.composite);
		this.initInfoComposite();
		SwtUtils.insertHSeparator(this.composite);
		this.initInfoComposite2();
		SwtUtils.insertHSeparator(this.composite);

		this.shipComposites = IntStream.range(0, 6)//
				.mapToObj(index -> new ShipComposite(composite))//
				.toArray(ShipComposite[]::new);

		GlobalContextUpdater.addListener(this);
	}

	private void initInfoComposite() {
		Composite infoComposite = new Composite(this.composite, SWT.NONE);
		infoComposite.setLayout(SwtUtils.makeGridLayout(9, 0, 0, 0, 0, -3, -2));
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			new Label(infoComposite, SWT.NONE).setText("制空:");

			this.zhikongLabel = new Label(infoComposite, SWT.LEFT);
			this.zhikongLabel.setText("0000");

			SwtUtils.insertBlank(infoComposite, 5);
			new Label(infoComposite, SWT.NONE).setText("索敌:");

			this.suodiLabel = new Label(infoComposite, SWT.LEFT);
			this.suodiLabel.setText("000");

			SwtUtils.insertBlank(infoComposite, 5);
			new Label(infoComposite, SWT.NONE).setText("总等级:");

			this.totallvLabel = new Label(infoComposite, SWT.LEFT);
			this.totallvLabel.setText("000");

			SwtUtils.insertBlank(infoComposite);
		}
	}

	private void initInfoComposite2() {
		Composite infoComposite2 = new Composite(this.composite, SWT.NONE);
		infoComposite2.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0, -3, -2));
		infoComposite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			this.sokuLabel = new Label(infoComposite2, SWT.NONE);
			this.sokuLabel.setText("高速");

			SwtUtils.insertBlank(infoComposite2);
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		DeckDto deck = GlobalContext.deckRooms[this.id - 1].getDeck();

		switch (type) {
			default:
				break;

			case DECK_UPDATEDECKNAME:
				if (deck != null) {
					SwtUtils.setText(this.fleetNameLabel, deck.getName());
				}
				break;

			case PORT:
			case DECK_CHANGE:
			case DECK_PRESET_SELECT:
			case KAISOU_POWERUP:
			case KAISOU_SLOTSET:
			case KAISOU_UNSETSLOT_ALL:
			case KAISOU_SLOTSET_EX:
			case KAISOU_SLOT_EXCHANGE:
			case KAISOU_SLOT_DEPRIVE:
			case KAISOU_REMODELING:
			case KAISOU_MARRIAGE:
			case KAISOU_SHIP3:
			case SHIP2:
			case BASIC:
			case DECK:
			case NDOCK:
			case SLOT_ITEM:
			case NDOCK_NYUKYO_START:
			case NDOCK_NYUKYO_SPEEDCHANGE:
			case DESTROYSHIP:
			case CHARGE:
			case REQUIRE_INFO:
			case BATTLE_SHIPDECK:
				this.composite.setRedraw(false);
				FunctionUtils.notNull(deck, this::updateDeck);
				this.composite.setRedraw(true);
				break;
		}
	}

	private void updateDeck(DeckDto deck) {
		//舰队名
		SwtUtils.setText(this.fleetNameLabel, deck.getName());
		//制空,索敌,总等级,舰队速度
		SwtUtils.setText(this.zhikongLabel, String.valueOf(DeckDtoTranslator.getZhikong(deck)));
		SwtUtils.setText(this.suodiLabel, String.valueOf(DeckDtoTranslator.getSuodi(deck)));
		SwtUtils.setText(this.totallvLabel, String.valueOf(DeckDtoTranslator.getTotalLv(deck)));
		SwtUtils.setText(this.sokuLabel, DeckDtoTranslator.highspeed(deck) ? "高速" : "低速");
		//ship 状态
		ShipDto[] ships = IntStream.of(deck.getShips()).mapToObj(GlobalContext::getShip).toArray(ShipDto[]::new);
		FunctionUtils.forEach(this.shipComposites, ships, ShipComposite::update);
	}

	private class ShipComposite extends Composite {
		private final Label iconLabel, nameLabel, lvLabel, hpLabel, hpmsgLabel, condLabel;
		private final Label[] equipLabels;

		public ShipComposite(Composite fleetComposite) {
			super(fleetComposite, SWT.NONE);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.iconLabel = new Label(this, SWT.CENTER);
			this.iconLabel.setText("!");
			this.iconLabel.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2), 16));

			Composite upsideBase = new Composite(this, SWT.NONE);
			upsideBase.setLayout(SwtUtils.makeGridLayout(5, 0, 0, 0, 0, 1, -1));
			upsideBase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.nameLabel = new Label(upsideBase, SWT.LEFT);
				this.nameLabel.setText("名");
				GridData nameLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
				nameLabelGridData.minimumWidth = 56;
				this.nameLabel.setLayoutData(nameLabelGridData);

				SwtUtils.insertBlank(upsideBase, 5);

				this.hpLabel = new Label(upsideBase, SWT.RIGHT);
				this.hpLabel.setText("000/000");

				SwtUtils.insertBlank(upsideBase, 5);

				this.hpmsgLabel = new Label(upsideBase, SWT.RIGHT);
				this.hpmsgLabel.setText("健在");
			}

			Composite downsideBase = new Composite(this, SWT.NONE);
			downsideBase.setLayout(SwtUtils.makeGridLayout(10, 0, 0, 0, 0, -2, -2));
			downsideBase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.lvLabel = new Label(downsideBase, SWT.LEFT);
				this.lvLabel.setText("Lv.100");

				SwtUtils.insertBlank(downsideBase, 5);

				this.equipLabels = IntStream.range(0, 5).mapToObj(index -> {
					Label equipLabel = new Label(downsideBase, SWT.CENTER);
					equipLabel.setText("装");
					return equipLabel;
				}).toArray(Label[]::new);

				SwtUtils.insertBlank(downsideBase, 5);
				SwtUtils.insertBlank(downsideBase);

				this.condLabel = new Label(downsideBase, SWT.RIGHT);
				this.condLabel.setText("100");
			}
		}

		private void update(ShipDto ship) {
			if (ship == null) {
				this.clear();
				return;
			}

			SwtUtils.setText(this.iconLabel, (ShipDtoTranslator.terribleState(ship) || ShipDtoTranslator.needHokyo(ship)) ? "!" : "");
			SwtUtils.setText(this.nameLabel, ShipDtoTranslator.getName(ship));
			SwtUtils.setToolTipText(this.nameLabel, ShipDtoTranslator.getDetail(ship));
			SwtUtils.setText(this.hpLabel, String.format("%d/%d", ship.getNowHp(), ship.getMaxHp()));
			SwtUtils.setText(this.hpmsgLabel, ShipDtoTranslator.getStateString(ship, true));
			this.hpmsgLabel.setBackground(ShipDtoTranslator.dapo(ship) ? HPMessage.getColor(HPMessage.getString(0.1)) : null);
			SwtUtils.setText(this.lvLabel, String.format("Lv.%d", ship.getLevel()));
			SwtUtils.setText(this.condLabel, String.valueOf(ship.getCond()));

			//五个装备
			ItemDto[] slots = IntStream.concat(IntStream.of(ship.getSlots()).limit(4), IntStream.of(ship.getSlotex())).mapToObj(GlobalContext::getItem).toArray(ItemDto[]::new);
			Character[] equipTexts = Stream.of(slots).map(slot -> slot == null ? null : Character.valueOf(ItemDtoTranslator.getOneWordName(slot))).toArray(Character[]::new);
			String tooltip = Stream.of(slots).filter(FunctionUtils::isNotNull).map(ItemDtoTranslator::getNameWithLevel).reduce((a, b) -> String.join("\n", a, b)).orElse("");
			FunctionUtils.forEach(this.equipLabels, equipTexts, (label, text) -> {
				SwtUtils.setText(label, text != null ? text.toString() : "");
				SwtUtils.setToolTipText(label, text != null ? tooltip : "");
			});
		}

		private void clear() {
			SwtUtils.setText(this.iconLabel, "");

			SwtUtils.setText(this.nameLabel, "");
			SwtUtils.setToolTipText(this.nameLabel, "");
			SwtUtils.setText(this.hpLabel, "");
			SwtUtils.setText(this.hpmsgLabel, "");
			this.hpmsgLabel.setBackground(null);
			SwtUtils.setText(this.lvLabel, "");
			SwtUtils.setText(this.condLabel, "");

			FunctionUtils.forEach(this.equipLabels, FunctionUtils.getConsumer(SwtUtils::setText, ""));
			FunctionUtils.forEach(this.equipLabels, FunctionUtils.getConsumer(SwtUtils::setToolTipText, ""));
		}
	}
}
