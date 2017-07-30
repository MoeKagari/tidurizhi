package tdrz.dto.word;

import javax.json.JsonObject;

import tdrz.dto.AbstractWord;
import tdrz.internal.TimerCounter;

/**
 * 渠位
 * @author MoeKagari
 */
public class NdockDto extends AbstractWord {
	private final int state;
	private final int shipId;
	private final long time;
	private final TimerCounter timerCounter;

	public NdockDto(JsonObject json) {
		this.state = json.getInt("api_state");
		this.shipId = json.getInt("api_ship_id");
		this.time = json.getJsonNumber("api_complete_time").longValue();
		this.timerCounter = new TimerCounter(this.time, 60, false, -1);
	}

	public int getState() {
		return this.state;
	}

	public int getShipId() {
		return this.shipId;
	}

	public long getTime() {
		return this.time;
	}

	public TimerCounter getTimerCounter() {
		return this.timerCounter;
	}
}
