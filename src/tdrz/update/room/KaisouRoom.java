package tdrz.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.word.ShipDto;
import tdrz.logic.TimeString;
import tdrz.update.GlobalContext;
import tdrz.update.data.ApiData;
import tdrz.utils.JsonUtils;
import tool.FunctionUtils;

public class KaisouRoom {
	public void doPowerup(ApiData data, JsonValue api_data) {
		String[] ids = data.getField("api_id_items").trim().split(",");
		JsonObject json = (JsonObject) api_data;
		long time = TimeString.getCurrentTime();
//		boolean success = json.getInt("api_powerup_flag") == 1;
//		ShipDto oldship = GlobalContext.getShip(Integer.parseInt(data.getField("api_id")));

		FunctionUtils.forEach(ids, id -> GlobalContext.destroyShip(time, "近代化改修", Integer.parseInt(id)));
		GlobalContext.addNewShip(json.getJsonObject("api_ship"));
		FunctionUtils.forEach(GlobalContext.deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, json.get("api_deck")));//更新deck
	}

	public void doSlotItemLock(ApiData data, JsonValue api_data) {
		int id = Integer.parseInt(data.getField("api_slotitem_id"));
		boolean lock = ((JsonObject) api_data).getInt("api_locked") == 1;
		FunctionUtils.notNull(GlobalContext.getItem(id), item -> item.slotItemLock(lock));
	}

	public void doShip3(ApiData data, JsonValue api_data) {
		JsonObject json = (JsonObject) api_data;
		json.getJsonArray("api_ship_data").forEach(GlobalContext::addNewShip);
		FunctionUtils.forEach(GlobalContext.deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, json.get("api_deck_data")));
	}

	public void doOpenSlotex(ApiData data, JsonValue api_data) {
		GlobalContext.updateShip(Integer.parseInt(data.getField("api_id")), ShipDto::openSlotex);
	}

	public void doSlotExchange(ApiData data, JsonValue api_data) {
		int id = Integer.parseInt(data.getField("api_id"));
		int[] newSlots = JsonUtils.getIntArray((JsonObject) api_data, "api_slot");
		GlobalContext.updateShip(id, ship -> ship.slotExchange(newSlots));
	}

	public void doSlotDeprive(ApiData data, JsonValue api_data) {
		JsonObject json = ((JsonObject) api_data).getJsonObject("api_ship_data");

		GlobalContext.addNewShip(json.getJsonObject("api_set_ship"));
		GlobalContext.addNewShip(json.getJsonObject("api_unset_ship"));
	}
}
