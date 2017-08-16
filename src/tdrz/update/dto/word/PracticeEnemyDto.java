package tdrz.update.dto.word;

import javax.json.JsonObject;

import tdrz.core.translator.MasterDataTranslator;
import tdrz.update.dto.AbstractWord;

/**
 * 演习对象
 * @author MoeKagari
 */
public class PracticeEnemyDto extends AbstractWord {
	private final PracticeEnemyShip[] ships;

	public PracticeEnemyDto(JsonObject json) {
		this.ships = json.getJsonObject("api_deck").getJsonArray("api_ships").getValuesAs(JsonObject.class).stream().map(jo -> jo.size() == 1 ? null : new PracticeEnemyShip(jo)).toArray(PracticeEnemyShip[]::new);
	}

	public PracticeEnemyShip[] getShips() {
		return this.ships;
	}

	public class PracticeEnemyShip {
		//private final int id;//-1为不存在ship,下列两个值也不存在,其它时候无需要的意义
		private final int lv;
		private final String name;

		public PracticeEnemyShip(JsonObject json) {
			//this.id = json.getInt("api_id");
			this.lv = json.getInt("api_level");
			this.name = MasterDataTranslator.getShipName(json.getInt("api_ship_id"));
		}

//		public boolean exist() {
//			return this.id != -1;
//		}

		public int getLv() {
			return this.lv;
		}

		public String getName() {
			return this.name;
		}
	}
}
