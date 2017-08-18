package tdrz.update.context.room;

import java.util.Comparator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.ApiData;
import tdrz.update.dto.word.BasicDto;
import tool.function.FunctionUtils;

public class MainRoom {
	/** 最后一次返回母港的时间 */
	private long lastUpdateTime = -1;

	public void doPort(ApiData data, JsonValue api_data) {
		JsonObject json = (JsonObject) api_data;
		this.doBasic(data, json.get("api_basic"));
		this.doMaterial(data, json.get("api_material"));
		GlobalContext.getAkashiTimer().resetWhenPort(data.getTime());
		if (json.containsKey("api_combined_flag")) {
			GlobalContext.setCombined(json.getInt("api_combined_flag") > 0);
		}

		int[] oldconds = GlobalContext.deckRooms[0].getConds();//第一舰队的疲劳(旧)
		long oldtime = this.lastUpdateTime;
		{
			this.lastUpdateTime = data.getTime();
			GlobalContext.getShipMap().clear();
			json.getJsonArray("api_ship").forEach(GlobalContext::addNewShip);
			FunctionUtils.forEach(GlobalContext.ndockRooms, FunctionUtils.getConsumer(NdockRoom::doNdock, data, json.get("api_ndock")));
			FunctionUtils.forEach(GlobalContext.deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, json.get("api_deck_port")));
		}
		int[] newconds = GlobalContext.deckRooms[0].getConds();//第一舰队的疲劳(新)
		long newtime = this.lastUpdateTime;
		GlobalContext.updatePLTIME(oldtime, oldconds, newtime, newconds);
	}

	public void doRequireInfo(ApiData data, JsonValue api_data) {
		JsonObject json = (JsonObject) api_data;
		this.doSlotItem(data, json.get("api_slot_item"));
		this.doUseitem(data, json.get("api_useitem"));
		FunctionUtils.forEach(GlobalContext.kdockRooms, FunctionUtils.getConsumer(KdockRoom::doKdock, data, json.get("api_kdock")));
		//TODO  api_unsetslot
	}

	public void doMaterial(ApiData data, JsonValue api_data) {
		int[] mm = ((JsonArray) api_data).getValuesAs(JsonObject.class).stream()//
				.sorted(Comparator.comparingInt(json -> json.getInt("api_id")))// 1到8
				.mapToInt(json -> json.getInt("api_value"))//
				.toArray();
		GlobalContext.getCurrentMaterial().setMaterial(null, data.getTime(), mm);
	}

	public void doBasic(ApiData data, JsonValue api_data) {
		GlobalContext.setBasicInformation(new BasicDto((JsonObject) api_data));
	}

	public void doSlotItem(ApiData data, JsonValue api_data) {
		GlobalContext.getItemMap().clear();
		((JsonArray) api_data).forEach(GlobalContext::addNewItem);
	}

	public void doUseitem(ApiData data, JsonValue api_data) {
		GlobalContext.getUseitemMap().clear();
		((JsonArray) api_data).forEach(GlobalContext::addNewUseItem);
	}
}
