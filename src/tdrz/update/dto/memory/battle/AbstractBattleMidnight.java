package tdrz.update.dto.memory.battle;

import java.util.function.BiFunction;

import javax.json.JsonArray;
import javax.json.JsonObject;

import tdrz.core.util.JsonUtils;
import tdrz.update.context.data.ApiData;

public abstract class AbstractBattleMidnight extends AbstractBattle {
	private static final long serialVersionUID = 1L;
	private final int[] touchPlane;
	private final int[] flare;
	private int[] activeDeck = { 1, 1 };
	public final BattleMidnightStage battleMidnightStage;

	public AbstractBattleMidnight(ApiData data, JsonObject json) {
		super(data.getTime(), json);

		this.flare = JsonUtils.dissociateIntArray(json, "api_flare_pos");
		this.touchPlane = JsonUtils.dissociateIntArray(json, "api_touch_plane");
		this.battleMidnightStage = new BattleMidnightStage(json.getJsonObject("api_hougeki"));
		if (json.containsKey("api_active_deck")) {
			this.activeDeck = JsonUtils.dissociateIntArray(json, "api_active_deck");
		} else {
			if (existBattleDeck(this.getfDeckCombine())) {
				this.activeDeck = new int[] { 2, 1 };
			}
		}
	}

	@Override
	public boolean isMidnight() {
		return true;
	}

	/**
	 * 开幕夜战
	 */
	public boolean isMidnightOnly() {
		return false;
	}

	public boolean[] getTouchPlane() {
		return this.touchPlane == null ? null : new boolean[] { this.touchPlane[0] > 0, this.touchPlane[1] > 0 };
	}

	public boolean[] getFlare() {
		return this.flare == null ? null : new boolean[] { this.flare[0] > 0, this.flare[1] > 0 };
	}

	@Override
	public BattleDeckAttackDamage getfDeckAttackDamage() {
		return this.activeDeck[0] == 1 ? this.battleMidnightStage.fAttackDamage : null;
	}

	@Override
	public BattleDeckAttackDamage getfDeckCombineAttackDamage() {
		return this.activeDeck[0] == 1 ? null : this.battleMidnightStage.fAttackDamage;
	}

	@Override
	public BattleDeckAttackDamage geteDeckAttackDamage() {
		return this.activeDeck[1] == 1 ? this.battleMidnightStage.eAttackDamage : null;
	}

	@Override
	public BattleDeckAttackDamage geteDeckCombineAttackDamage() {
		return this.activeDeck[1] == 1 ? null : this.battleMidnightStage.eAttackDamage;
	}

	public BattleDeck[] getActiveDeck() {
		BiFunction<Integer, BattleDeck[], BattleDeck> get = (index, bds) -> index == 1 ? bds[0] : bds[1];
		return new BattleDeck[] {//
				get.apply(this.activeDeck[0], new BattleDeck[] { this.getfDeck(), this.getfDeckCombine() }),//
				get.apply(this.activeDeck[1], new BattleDeck[] { this.geteDeck(), this.geteDeckCombine() })//
		};
	}

	public class BattleMidnightStage extends BattleStage {
		private static final long serialVersionUID = 1L;

		public BattleMidnightStage(JsonObject json) {
			JsonArray at_list = json.getJsonArray("api_at_list");
			JsonArray df_list = json.getJsonArray("api_df_list");
			JsonArray damage = json.getJsonArray("api_damage");
			JsonArray sp_list = json.getJsonArray("api_sp_list");
			for (int x = 1; x < at_list.size(); x++) {
				int at_index = at_list.getInt(x);
				int[] df_index = JsonUtils.getIntArray(df_list.getJsonArray(x));
				int[] da = JsonUtils.getIntArray(damage.getJsonArray(x));
				int sp = sp_list.getInt(x);
				this.battleAttacks.add(new BattleOneAttack(at_index, df_index, da, sp));
			}

			BattleOneAttackSimulator boas = new BattleOneAttackSimulator();
			this.battleAttacks.forEach(boa -> boas.accept(boa, Boolean.FALSE));
			this.accept(boas);
		}

		@Override
		public String getStageName() {
			return AbstractBattleMidnight.this.isMidnightOnly() ? "开幕" : "" + "夜战";
		}
	}
}
