package tdrz.update.dto.word;

import javax.json.JsonObject;

import tdrz.update.dto.AbstractWord;

/**
 * 建造位
 * 
 * @author MoeKagari
 */
public class KdockDto extends AbstractWord {
	private final int shipId;
	private final int state;
	private final long time;
	private final boolean largeFlag;

	public KdockDto(JsonObject json) {
		this.shipId = json.getInt("api_created_ship_id");
		this.state = json.getInt("api_state");
		this.time = json.getJsonNumber("api_complete_time").longValue();
		this.largeFlag = json.getInt("api_item1") >= 1000;
	}

	public int getShipId() {
		return this.shipId;
	}

	public long getTime() {
		return this.time;
	}

	/** -1=未开启,0=未使用,3=建造完成,2=建造中 */
	public int getState() {
		return this.state;
	}

	public boolean largeFlag() {
		return this.largeFlag;
	}
}
