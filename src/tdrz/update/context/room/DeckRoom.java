package tdrz.update.context.room;

import java.util.Arrays;
import java.util.stream.IntStream;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.commons.lang3.ArrayUtils;

import tdrz.core.translator.DeckDtoTranslator;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.ApiData;
import tdrz.update.dto.word.DeckDto;

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
				.mapToInt(ship -> (ship != null && ship.isNeedForPLUpdate()) ? ship.getCond() : -1)//
				.toArray();
	}
	/*---------------------------------------------------------------------------------------------------------*/

	public void doDeck(ApiData data, JsonValue api_data) {
		((JsonArray) api_data).getValuesAs(JsonObject.class).stream().forEach(json -> {
			if (json.getInt("api_id") == this.id) {
				this.deck = new DeckDto(json, data.getTime());
			}
		});
	}

	public void doChange(ApiData data, JsonValue api_data) {
		if (this.deck == null) return;
		if (Integer.parseInt(data.getField("api_id")) != this.id) return;

		int index = Integer.parseInt(data.getField("api_ship_idx"));//变更位置,0开始
		int shipId = Integer.parseInt(data.getField("api_ship_id"));

		if (index == -1) {
			//除旗舰其余全解除
			this.deck.setShips(new int[] { this.deck.getShips()[0], -1, -1, -1, -1, -1 });
		} else {
			if (shipId == -1) {
				//解除某一艘船
				this.deck.setShips(ArrayUtils.addAll(//
						IntStream.range(0, this.deck.getShips().length).filter(i -> i != index).map(i -> this.deck.getShips()[i]).toArray()//
						, -1));
			} else {
				//替换为另外的ship
				for (int i = 0; i < GlobalContext.deckRooms.length; i++) {
					int shipIndex = DeckDtoTranslator.indexInDeck(GlobalContext.deckRooms[i].deck, shipId);
					if (shipIndex != -1) {
						//替换的ship在某deck中
						GlobalContext.deckRooms[i].deck.getShips()[shipIndex] = this.deck.getShips()[index];
						break;
					}
				}
				this.deck.getShips()[index] = shipId;
			}
		}

		if (index != -1 && DeckDtoTranslator.isAkashiFlagship(this.deck)) {
			//变更之后明石旗舰
			//并且不是[随伴舰一括解除](index == -1)
			GlobalContext.getAkashiTimer().resetAkashiFlagshipWhenChange(data.getTime());
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
			this.deck = new DeckDto(json, data.getTime());
		}
	}
}
