package tdrz.update.context.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.ApiData;
import tdrz.update.dto.word.NdockDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

public class NdockRoom {
	private final int id;
	private NdockDto ndock = null;

	public NdockRoom(int id) {
		this.id = id;
	}

	public NdockDto getNdock() {
		return this.ndock;
	}

	public void doNdock(ApiData data, JsonValue api_data) {
		((JsonArray) api_data).getValuesAs(JsonObject.class).stream().forEach(json -> {
			if (json.getInt("api_id") == this.id) {
				this.ndock = new NdockDto(json);
			}
		});
	}

	public void doNyukyoStart(ApiData data, JsonValue api_data) {
		if (Integer.parseInt(data.getField("api_ndock_id")) != this.id) return;

		ShipDto ship = GlobalContext.getShip(Integer.parseInt(data.getField("api_ship_id")));
		if (ship != null) {
			GlobalContext.getCurrentMaterial().setMaterial("入渠", data.getTime(), ship.getNyukyoCost(), false);
		}

		boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
		if (highspeed) {
			//使用高速修复,后无ndock
			FunctionUtils.notNull(ship, ShipDto::nyukyoEnd);
			GlobalContext.getCurrentMaterial().setMaterial("高速修复", data.getTime(), new int[] { 0, 0, 0, 0, 0, 1, 0, 0 }, false);
			this.ndock = null;
		} else {
			//不使用高速修复,后接ndock ,无需处理
		}
	}

	public void doNyukyoSpeedChange(ApiData data, JsonValue api_data) {
		if (Integer.parseInt(data.getField("api_ndock_id")) != this.id) return;

		if (this.ndock != null) {
			GlobalContext.updateShip(this.ndock.getShipId(), ShipDto::nyukyoEnd);
			this.ndock = null;
		}
		GlobalContext.getCurrentMaterial().setMaterial("高速修复(单独)", data.getTime(), new int[] { 0, 0, 0, 0, 0, 1, 0, 0 }, false);
	}
}
