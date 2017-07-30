package tdrz.dto.memory.battle;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.AbstractMemory;
import tdrz.update.data.ApiData;

public abstract class AbstractInfoBattleResult extends AbstractInfoBattle {
	private static final long serialVersionUID = 1L;

	private final String rank;

	private final String questName;
	private final String deckName;
	private final BattleResult_GetShip getShip;

	private final int mvp;
	private final int mvpCombined;

	public AbstractInfoBattleResult(ApiData data, JsonObject json) {
		this.rank = json.getString("api_win_rank");
		this.questName = json.getString("api_quest_name", null);
		this.deckName = json.getJsonObject("api_enemy_info").getString("api_deck_name");
		this.getShip = json.containsKey("api_get_ship") ? (new BattleResult_GetShip(json.getJsonObject("api_get_ship"))) : null;
		this.mvp = json.getInt("api_mvp");

		//随伴舰队MVP,-1表示无随伴舰队
		//有可能无此key(mcv==null),或为JsonValue.NULL
		JsonValue mcv = json.get("api_mvp_combined");
		if (mcv instanceof JsonNumber) {
			this.mvpCombined = ((JsonNumber) mcv).intValue();
		} else {
			this.mvpCombined = -1;
		}
	}

	public int getMvp() {
		return this.mvp;
	}

	public int getMvpCombined() {
		return this.mvpCombined;
	}

	public String getRank() {
		return BattleDto.getRank(this.rank);
	}

	public String getQuestName() {
		return this.questName;
	}

	public String getDeckName() {
		return this.deckName;
	}

	public BattleResult_GetShip getNewShip() {
		return this.getShip;
	}

	public class BattleResult_GetShip extends AbstractMemory {
		private static final long serialVersionUID = 1L;
		private final int id;
		private final String type;
		private final String name;

		public BattleResult_GetShip(JsonObject json) {
			this.id = json.getInt("api_ship_id");
			this.type = json.getString("api_ship_type");
			this.name = json.getString("api_ship_name");
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getType() {
			return this.type;
		}
	}

}
