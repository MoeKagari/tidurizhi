package tdrz.update.room;

import java.util.Arrays;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.translator.DeckDtoTranslator;
import tdrz.dto.word.DeckDto;
import tdrz.update.GlobalContext;
import tdrz.update.data.ApiData;
import tool.FunctionUtils;

public class DeckRoom {
	private final int id;
	private DeckDto deck = null;

	public DeckRoom(int id) {
		this.id = id;
	}

	public DeckDto getDeck() {
		return this.deck;
	}

	public int[] getConds() {
		if (this.deck == null) return null;
		return Arrays.stream(this.deck.getShips()).mapToObj(GlobalContext::getShip)//
				.mapToInt(ship -> (ship != null && ship.isNeedForPLUpdate()) ? ship.getCond() : -1).toArray();
	}
	/*---------------------------------------------------------------------------------------------------------*/

	public void doDeck(ApiData data, JsonValue api_data) {
		((JsonArray) api_data).getValuesAs(JsonObject.class).stream().forEach(json -> {
			if (json.getInt("api_id") == this.id) {
				this.deck = new DeckDto(json);
			}
		});
	}

	public void doChange(ApiData data, JsonValue api_data) {
		if (this.deck == null) return;
		if (Integer.parseInt(data.getField("api_id")) != this.id) return;

		int index = Integer.parseInt(data.getField("api_ship_idx"));//变更位置,0开始
		int shipId = Integer.parseInt(data.getField("api_ship_id"));
		this.deck.change(index, shipId);

		if (DeckDtoTranslator.isAkashiFlagship(this.deck) && index != -1) {//变更之后明石旗舰,并且不是[随伴舰一括解除]
			FunctionUtils.ifRunnable(GlobalContext.getAkashiTimer(), FunctionUtils::isNull, GlobalContext::setAkashiTimer);
			GlobalContext.getAkashiTimer().resetAkashiFlagshipWhenChange();
		}
	}

	public void doUpdatedeckName(ApiData data, JsonValue api_data) {
		if (this.deck == null) return;
		if (Integer.parseInt(data.getField("api_id")) != this.id) return;

		this.deck.setDeckName(data.getField("api_name"));
	}

	public void doPresetSelect(ApiData data, JsonValue api_data) {
		JsonObject json = (JsonObject) api_data;
		if (json.getInt("api_id") == this.id) {
			this.deck = new DeckDto(json);
		}
	}

	public void doShipLock(ApiData data, JsonValue api_data) {
		int id = Integer.parseInt(data.getField("api_ship_id"));
		int lock_value = ((JsonObject) api_data).getInt("api_locked");
		GlobalContext.updateShip(id, ship -> ship.setLocked(lock_value == 1));
	}
}
