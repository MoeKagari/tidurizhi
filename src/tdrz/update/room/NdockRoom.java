package tdrz.update.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.word.NdockDto;
import tdrz.dto.word.ShipDto;
import tdrz.update.GlobalContext;
import tdrz.update.data.ApiData;

public class NdockRoom {
	private final int id;
	private NdockDto ndock;

	public NdockRoom(int id) {
		this.id = id;
	}

	public NdockDto getNdock() {
		return this.ndock;
	}
	/*---------------------------------------------------------------------------------------------------------*/

	public void doNdock(ApiData data, JsonValue api_data) {
		((JsonArray) api_data).getValuesAs(JsonObject.class).stream().forEach(json -> {
			if (json.getInt("api_id") == this.id) {
				this.ndock = new NdockDto(json);
			}
		});
	}

	public void doNyukyoStart(ApiData data, JsonValue api_data) {
		if (Integer.parseInt(data.getField("api_ndock_id")) != this.id) return;

		int shipId = Integer.parseInt(data.getField("api_ship_id"));
		boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
		ShipDto ship = GlobalContext.getShip(shipId);

		if (ship != null) {
			GlobalContext.reduceMaterial(ship.getNyukyoCost());
		}
		if (highspeed) {//使用高速修复,后无ndock
			GlobalContext.reduceMaterial(new int[] { 0, 0, 0, 0, 0, 1, 0, 0 });
			this.ndock = null;
		} else {
			//不使用高速修复,后接ndock ,无需处理
		}
	}

	public void doNyukyoSpeedchange(ApiData data, JsonValue api_data) {
		if (Integer.parseInt(data.getField("api_ndock_id")) != this.id) return;

		if (this.ndock != null) {
			GlobalContext.reduceMaterial(new int[] { 0, 0, 0, 0, 0, 1, 0, 0 });
			this.ndock = null;
		}
	}
}
