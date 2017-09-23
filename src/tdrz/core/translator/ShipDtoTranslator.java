package tdrz.core.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import tdrz.core.config.AppConstants;
import tdrz.core.logic.HPMessage;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.room.DeckRoom;
import tdrz.update.context.room.NdockRoom;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.MasterDataDto.MasterShipDto;
import tdrz.update.dto.word.MasterDataDto.MasterSlotitemDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

/** 全部方法 ship==null 可 */
public class ShipDtoTranslator {
	public static double getHPPercent(ShipDto ship) {
		if (ship == null) return 1;
		return FunctionUtils.division(ship.getNowHp(), ship.getMaxHp());
	}

	public static String getTypeString(ShipDto ship) {
		if (ship == null) return "";
		return getTypeString(ship.getMasterData());
	}

	public static String getTypeString(MasterShipDto msdd) {
		if (msdd == null) return "";
		int type = msdd.getType();
		switch (type) {
			case 1:
				return "海防艦";
			case 2:
				return "駆逐艦";
			case 3:
				return "軽巡洋艦";
			case 4:
				return "重雷装巡洋艦";
			case 5:
				return "重巡洋艦";
			case 6:
				return "航空巡洋艦";
			case 7:
				return "軽空母";
			case 8:
				return "巡洋戦艦";
			case 9:
				return "戦艦";
			case 10:
				return "航空戦艦";
			case 11:
				return "正規空母";
			case 12:
				return "超弩級戦艦";
			case 13:
				return "潜水艦";
			case 14:
				return "潜水空母";
			case 15:
				return "補給艦";//敌方
			case 16:
				return "水上機母艦";
			case 17:
				return "揚陸艦";
			case 18:
				return "装甲空母";
			case 19:
				return "工作艦";
			case 20:
				return "潜水母艦";
			case 21:
				return "練習巡洋艦";
			case 22:
				return "補給艦";//自方
			default:
				return String.valueOf(type);
		}
	}

	public static String getName(ShipDto ship) {
		if (ship == null) return "";
		return FunctionUtils.notNull(ship.getMasterData(), MasterShipDto::getName, "");
	}

	public static String getDetail(ShipDto ship) {
		if (ship == null) return "";
		ArrayList<String> detail = new ArrayList<>();
		{
			detail.add(getName(ship));
			detail.add(String.format("经验: %d/%d", ship.getNextExp(), ship.getCurrentExp()));
			detail.add(String.format("速力: %s", getSokuString(ship, true)));

		}
		return StringUtils.join(detail, "\n");
	}

	public static String getSokuString(ShipDto ship, boolean showHighspeed) {
		if (ship == null) return "";
		int soku = ship.getSoku();
		switch (soku) {
			case 5:
				return "低速";
			case 10:
				return showHighspeed ? "高速" : "";
			case 15:
				return "高速+";
			case 20:
				return "最速";
			default:
				return Integer.toString(soku);
		}
	}

	public static boolean highspeed(ShipDto ship) {
		if (ship == null) return true;
		return ship.getSoku() != 5;
	}

	public static int getSuodi(ShipDto ship) {
		if (ship == null) return 0;
		int suodi = 0;
		for (int i = 0; i < 4; i++) {
			suodi += ItemDtoTranslator.getSuodi(ship.getSlots()[i]);
		}
		return suodi;
	}

	public static int getZhikong(ShipDto ship) {
		if (ship == null) return 0;
		int zhikong = 0;
		for (int i = 0; i < 4; i++) {
			zhikong += ItemDtoTranslator.getZhikong(ship.getSlots()[i], ship.getOnSlot()[i]);
		}
		return zhikong;
	}

	public static boolean isAkashi(ShipDto ship) {
		if (ship == null) return false;
		return ship.getShipId() == 182 || ship.getShipId() == 187;
	}

	public static boolean isInNyukyo(ShipDto ship) {
		if (ship == null) return false;
		return Arrays.stream(GlobalContext.ndockRooms).map(NdockRoom::getNdock).anyMatch(ndock -> //
		FunctionUtils.isNotNull(ndock) &&//
				ndock.getShipId() == ship.getId()//
		);
	}

	public static boolean isInMission(ShipDto ship) {
		if (ship == null) return false;
		return Arrays.stream(GlobalContext.deckRooms).map(DeckRoom::getDeck).anyMatch(deck ->//
		FunctionUtils.isNotNull(deck) &&//
				DeckDtoTranslator.isInMission(deck) &&//
				DeckDtoTranslator.isShipInDeck(deck, ship.getId()) //
		);
	}

	public static String getStateString(ShipDto ship, boolean showMax) {
		if (ship == null) return "";
		String text = HPMessage.getString(getHPPercent(ship));
		return FunctionUtils.isFalse(showMax) && StringUtils.equals(text, HPMessage.getString(1)) ? "" : text;
	}

	public static boolean needHokyo(ShipDto ship) {
		if (ship == null) return false;
		MasterShipDto msdd = ship.getMasterData();
		if (msdd == null) return false;
		return msdd.getFuelMax() != ship.getFuel() || //
				msdd.getBullMax() != ship.getBull() || //
				FunctionUtils.isFalse(Arrays.equals(msdd.getOnslotMax(), ship.getOnSlot()));
	}

	/** 完好 */
	public static boolean perfectState(ShipDto ship) {
		if (ship == null) return false;
		return ship.getNowHp() == ship.getMaxHp();
	}

	/** 擦伤小破 */
	public static boolean healthyState(ShipDto ship) {
		if (ship == null) return false;
		return terribleState(ship) ? false : getHPPercent(ship) < 1;
	}

	/** 中破大破 */
	public static boolean terribleState(ShipDto ship) {
		if (ship == null) return false;
		return getHPPercent(ship) <= 0.5;
	}

	public static boolean dapo(ShipDto ship) {
		if (ship == null) return false;
		return getHPPercent(ship) <= 0.25;
	}

	public static int whichDeck(ShipDto ship) {
		if (ship != null) {
			for (int i = 0; i < 4; i++) {
				if (DeckDtoTranslator.isShipInDeck(GlobalContext.deckRooms[i].getDeck(), ship.getId())) {
					return i;
				}
			}
		}
		return -1;
	}

	public static String whichDeckString(ShipDto ship) {
		return FunctionUtils.ifFunction(ShipDtoTranslator.whichDeck(ship), wd -> wd != -1, wd -> AppConstants.DEFAULT_FLEET_NAME[wd], "");
	}

	public static boolean canOpeningTaisen(ShipDto ship) {
		if (ship == null) return false;

		MasterShipDto msd = ship.getMasterData();
		if (msd == null) return false;
		//驱逐舰,轻巡洋舰,重雷装巡洋舰,练习巡洋舰
		if (IntStream.of(2, 3, 4, 21).noneMatch(type -> type == msd.getType())) return false;

		int taisen = ship.getTaisen()[0];
		for (int equip : ArrayUtils.addAll(ship.getSlots(), ship.getSlotex())) {
			MasterSlotitemDto md = FunctionUtils.notNull(GlobalContext.getItem(equip), ItemDto::getMasterData, null);
			taisen -= FunctionUtils.notNull(md, MasterSlotitemDto::getTaisen, 0);
		}
		return taisen >= 64;
	}

	public static boolean canEquipDaihatsu(ShipDto ship) {
		return true;//TODO
	}

	public static int getPowerHougeki(ShipDto ship) {
		return 0;//TODO
	}

	public static int getPowerRageki(ShipDto ship) {
		return 0;//TODO
	}

	public static int getPowerMidnight(ShipDto ship) {
		return 0;//TODO
	}
}
