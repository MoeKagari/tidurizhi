package tdrz.update.dto.word;

import javax.json.JsonArray;
import javax.json.JsonObject;

import tdrz.core.internal.TimerCounter;
import tdrz.core.translator.MasterDataTranslator;
import tdrz.core.util.JsonUtils;
import tdrz.update.dto.AbstractWord;

/**
 * 舰队编成
 * 
 * @author MoeKagari
 */
public class DeckDto extends AbstractWord {
	private final long time;//此deck的刷新时间
	private String name;
	private int[] ships;
	private final DeckMissionDto deckMission;

	public DeckDto(JsonObject json, long time) {
		this.time = time;
		this.name = json.getString("api_name");
		this.ships = JsonUtils.getIntArray(json, "api_ship");
		this.deckMission = new DeckMissionDto(json.getJsonArray("api_mission"));
	}

	/*-------------------------------------------------------------------------------------------*/

	public void setShips(int[] ships) {
		this.ships = ships;
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
