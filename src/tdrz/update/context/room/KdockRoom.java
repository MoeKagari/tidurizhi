package tdrz.update.context.room;

import java.util.Optional;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.CreateShipDto;
import tdrz.update.dto.word.KdockDto;
import tool.function.FunctionUtils;

public class KdockRoom {
	private final int id;
	private KdockDto kdock = null;
	private CreateShipDto createshipDto;//当前的建造信息,用于createship-kdock-material的api链

	public KdockRoom(int id) {
		this.id = id;
	}

	public KdockDto getKdock() {
		return this.kdock;
	}

	public void doKdock(ApiData data, JsonValue api_data) {
		((JsonArray) api_data).getValuesAs(JsonObject.class).stream().forEach(json -> {
			if (json.getInt("api_id") == this.id) {
				this.kdock = new KdockDto(json);
			}
		});

		if (this.kdock != null && this.createshipDto != null) {//记录
			this.createshipDto.setShipId(this.kdock.getShipId());
			GlobalContext.getMemorylist().add(this.createshipDto);
		}

		this.createshipDto = null;
	}

	public void doCreateship(ApiData data, JsonValue api_data) {
		if (Integer.parseInt(data.getField("api_kdock_id")) != this.id) return;

		boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
		boolean large_flag = Integer.parseInt(data.getField("api_large_flag")) == 1;
		int[] mm = {//
				Integer.parseInt(data.getField("api_item1")),//
				Integer.parseInt(data.getField("api_item2")),//
				Integer.parseInt(data.getField("api_item3")),//
				Integer.parseInt(data.getField("api_item4")),//
				highspeed ? (large_flag ? 10 : 1) : 0,//高速建造材
				0, Integer.parseInt(data.getField("api_item5")), 0 //
		};

		//虽然后接material,但是为了记录,所以setResource
		GlobalContext.getCurrentMaterial().setMaterial(highspeed ? "高速建造" : "建造", data.getTime(), mm, false);
		if (GlobalContext.getSecretaryShip() != null) {
			this.createshipDto = new CreateShipDto(GlobalContext.getSecretaryShip(), mm, data.getTime(), large_flag, highspeed);
		}
		//后接kdock,material
	}

	public void doSpeedchange(ApiData data, JsonValue api_data) {
		if (this.kdock == null) return;
		if (Integer.parseInt(data.getField("api_kdock_id")) != this.id) return;

		boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
		boolean large_flag = this.kdock.largeFlag();
		int[] mm = { 0, 0, 0, 0,//
				highspeed ? (large_flag ? 10 : 1) : 0,//高速建造材
				0, 0, 0 };

		GlobalContext.getCurrentMaterial().setMaterial("高速建造(单独)", data.getTime(), mm, false);
	}

	public void doGetShip(ApiData data, JsonValue api_data) {
		if (Integer.parseInt(data.getField("api_kdock_id")) != this.id) return;

		JsonObject json = (JsonObject) api_data;

		//加入新船到
		GlobalContext.addNewShip(json.getJsonObject("api_ship"));
		//加入新船的装备,有可能为JsonValue.NULL
		Optional.ofNullable(json.get("api_slotitem")).filter(value -> value instanceof JsonArray).ifPresent(value -> {
			((JsonArray) value).forEach(GlobalContext::addNewItem);
		});
		//刷新kdock
		FunctionUtils.forEach(GlobalContext.kdockRooms, FunctionUtils.getConsumer(KdockRoom::doKdock, data, json.get("api_kdock")));
	}
}
