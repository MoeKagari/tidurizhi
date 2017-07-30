package tdrz.dto.word;

import javax.json.JsonObject;

import tdrz.dto.AbstractWord;
import tdrz.dto.translator.MasterDataTranslator;

/**
 * 演习对象
 * @author MoeKagari
 */
public class PracticeEnemyDto extends AbstractWord {
	private final PracticeEnemyShip[] ships;

	public PracticeEnemyDto(JsonObject json) {
		this.ships = json.getJsonObject("api_deck").getJsonArray("api_ships").getValuesAs(JsonObject.class).stream().map(PracticeEnemyShip::new).toArray(PracticeEnemyShip[]::new);
	}

	public PracticeEnemyShip[] getShips() {
		return this.ships;
	}

	public class PracticeEnemyShip {
		private final int id;
		private final int lv;
		private final String name;

		public PracticeEnemyShip(JsonObject json) {
			this.id = json.getInt("api_id");
			this.lv = this.exist() ? json.getInt("api_level") : -1;
			this.name = this.exist() ? MasterDataTranslator.getShipName(json.getInt("api_ship_id")) : null;
		}

		public boolean exist() {
			return this.id != -1;
		}

		public int getLv() {
			return this.lv;
		}

		public String getName() {
			return this.name;
		}
	}
}
