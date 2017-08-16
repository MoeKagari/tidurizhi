package tdrz.update.dto.word;

import java.util.Arrays;
import java.util.stream.IntStream;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.commons.lang3.ArrayUtils;

import tdrz.core.internal.TimerCounter;
import tdrz.core.translator.DeckDtoTranslator;
import tdrz.core.translator.MasterDataTranslator;
import tdrz.core.util.JsonUtils;
import tdrz.update.dto.AbstractWord;
import tool.FunctionUtils;

/**
 * 舰队编成
 * @author MoeKagari
 */
public class DeckDto extends AbstractWord {
	private String name;
	private int[] ships;
	private DeckMissionDto deckMission;
	private final long time;//此deck的刷新时间

	public DeckDto(JsonObject json, long time) {
		this.name = json.getString("api_name");
		this.ships = JsonUtils.getIntArray(json, "api_ship");
		this.deckMission = new DeckMissionDto(json.getJsonArray("api_mission"));

		this.time = time;
	}

	/*-------------------------------------------------------------------------------------------*/

	public void change(int index, int shipId) {
		if (index == -1) {
			//1.除旗舰其余全解除
			this.ships = new int[] { this.ships[0], -1, -1, -1, -1, -1 };
			return;
		}

		int[] shipsTemp = FunctionUtils.arrayCopy(this.ships);
		int shipIndex = DeckDtoTranslator.indexInDeck(this, shipId);
		if (shipIndex != -1) {
			//2.交换两艘船
			int temp = shipsTemp[index];
			shipsTemp[index] = shipsTemp[shipIndex];
			shipsTemp[shipIndex] = temp;
		} else {
			//3.替换某一艘船
			//或者
			//4.解除某一艘船(shipId=-1时)
			shipsTemp[index] = shipId;
		}
		this.setShips(shipsTemp);
	}

	public void remove(int shipId) {
		int shipIndex = DeckDtoTranslator.indexInDeck(this, shipId);
		if (shipIndex != -1) {
			int[] shipsTemp = FunctionUtils.arrayCopy(this.ships);
			shipsTemp[shipIndex] = -1;
			this.setShips(shipsTemp);
		}
	}

	private void setShips(int[] shipsTemp) {
		shipsTemp = IntStream.of(shipsTemp).filter(ship -> ship != -1).toArray();//非 -1 提到前面
		int[] empty = new int[this.ships.length - shipsTemp.length];
		Arrays.fill(empty, -1);//空缺 为 -1
		this.ships = ArrayUtils.addAll(shipsTemp, empty);
	}

	public void setDeckName(String name) {
		this.name = name;
	}

	/*-------------------------------------------------------------------------------------------*/

	public int[] getShips() {
		return this.ships;
	}

	public long getTime() {
		return this.time;
	}

	public String getName() {
		return this.name;
	}

	public DeckMissionDto getDeckMission() {
		return this.deckMission;
	}

	/*-------------------------------------------------------------------------------------------*/

	/** 舰队远征信息 */
	public class DeckMissionDto {
		private final int state;
		private final long time;
		private final String name;
		private final TimerCounter timerCounter;

		public DeckMissionDto(JsonArray json) {
			this.state = json.getJsonNumber(0).intValue();
			this.name = MasterDataTranslator.getMissionName(json.getJsonNumber(1).intValue());
			this.time = json.getJsonNumber(2).longValue();
			this.timerCounter = new TimerCounter(this.time, 60, true, 2 * 60);
		}

		/** 远征名 */
		public String getName() {
			return this.name;
		}

		/** 0=未出撃, 1=遠征中, 2=遠征帰投, 3=強制帰投中 */
		public int getState() {
			return this.state;
		}

		/** 归还时间 */
		public long getTime() {
			return this.time;
		}

		public TimerCounter getTimerCounter() {
			return this.timerCounter;
		}
	}
}
