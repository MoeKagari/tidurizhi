package tdrz.core.translator;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import tdrz.core.config.AppConstants;
import tdrz.core.util.ToolUtils;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.DeckDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

public class DeckDtoTranslator {
	private static Stream<ShipDto> getShipStream(DeckDto deck) {
		return Arrays.stream(deck.getShips()).mapToObj(GlobalContext::getShip).filter(FunctionUtils::isNotNull);
	}

	/**
	 * 1,2,3,4
	 */
	public static String[] getShipNames(int id) {
		return getShipNames(GlobalContext.deckRooms[id - 1].getDeck());
	}

	public static String[] getShipNames(DeckDto deck) {
		if (deck != null) {
			return ToolUtils.toStringArray(deck.getShips(), id -> ShipDtoTranslator.getName(GlobalContext.getShip(id)));
		} else {
			return AppConstants.EMPTY_NAMES;
		}
	}

	public static int getZhikong(DeckDto deck) {
		if (deck == null) return 0;
		return getShipStream(deck).mapToInt(ShipDtoTranslator::getZhikong).sum();
	}

	public static int getSuodi(DeckDto deck) {
		if (deck == null) return 0;
		return getShipStream(deck).mapToInt(ShipDtoTranslator::getSuodi).sum();
	}

	public static int getTotalLv(DeckDto deck) {
		if (deck == null) return 0;
		return getShipStream(deck).mapToInt(ShipDto::getLevel).sum();
	}

	public static boolean highspeed(DeckDto deck) {
		if (deck == null) return true;
		return getShipStream(deck).allMatch(ShipDtoTranslator::highspeed);
	}

	public static boolean isShipInDeck(int deck, ShipDto ship) {
		return indexInDeck(GlobalContext.deckRooms[deck].getDeck(), ship.getId()) != -1;
	}

	public static boolean isShipInDeck(DeckDto deck, int id) {
		return indexInDeck(deck, id) != -1;
	}

	public static int indexInDeck(DeckDto deck, int id) {
		if (deck != null) {
			int[] ships = deck.getShips();
			for (int index = 0; index < ships.length; index++) {
				int ship = ships[index];
				if (ship != -1 && ship == id) {
					return index;
				}
			}
		}
		return -1;
	}

	public static boolean isAkashiFlagship(DeckDto deck) {
		if (deck == null) return false;
		return ShipDtoTranslator.isAkashi(GlobalContext.getShip(deck.getShips()[0]));
	}

	/** 泊地修理到点时,是否应该提醒 */
	public static boolean shouldNotifyAkashiTimer(DeckDto deck) {
		if (deck == null) return false;
		//远征中
		if (isInMission(deck)) return false;

		ShipDto flagship = GlobalContext.getShip(deck.getShips()[0]);
		if (flagship == null) return false;
		if (ShipDtoTranslator.isAkashi(flagship) == false) return false;
		//入渠中,中破大破,不能修理
		Predicate<ShipDto> cannot = ship -> ShipDtoTranslator.isInNyukyo(ship) || ShipDtoTranslator.terribleState(ship);
		//明石不能修理自己时,同时不能修理其它舰娘
		if (cannot.test(flagship)) return false;

		//修理数(2+修理设施)
		int count = 2 + Arrays.stream(flagship.getSlots()).filter(ItemDtoTranslator::isRepairItem).map(i -> 1).sum();
		//没有入渠,擦伤小破,可以修理		
		Predicate<ShipDto> can = ship -> !ShipDtoTranslator.isInNyukyo(ship) && ShipDtoTranslator.healthyState(ship);
		return Arrays.stream(deck.getShips()).limit(count).mapToObj(GlobalContext::getShip).filter(FunctionUtils::isNotNull).anyMatch(can);
	}

	public static boolean isInMission(DeckDto deck) {
		if (deck == null) return false;
		return deck.getDeckMission().getState() != 0;
	}

	public static boolean hasDapo(DeckDto deck) {
		if (deck == null) return false;
		return getShipStream(deck).anyMatch(ShipDtoTranslator::dapo);
	}
}
