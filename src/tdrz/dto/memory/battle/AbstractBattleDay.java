package tdrz.dto.memory.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.memory.battle.daymidnight.BattleDayDto;
import tdrz.dto.memory.battle.daymidnight.CombinebattleDayDto;
import tdrz.dto.memory.battle.daymidnight.CombinebattleDayWaterDto;
import tdrz.dto.memory.battle.practice.PracticeBattleDayDto;
import tdrz.update.data.ApiData;
import tdrz.utils.JsonUtils;
import tdrz.utils.ToolUtils;

public abstract class AbstractBattleDay extends AbstractBattle {
	private static final long serialVersionUID = 1L;
	public final ArrayList<BattleDayStage> battleDayStage = new ArrayList<>();

	public AbstractBattleDay(ApiData data, JsonObject json) {
		super(json);

		if (json.containsKey("api_air_base_injection")) {
			this.battleDayStage.add(new AirbaseInjection(json.getJsonObject("api_air_base_injection")));
		}
		if (json.containsKey("api_injection_kouku")) {
			this.battleDayStage.add(new InjectionKouko(json.getJsonObject("api_injection_kouku")));
		}
		if (json.containsKey("api_air_base_attack")) {
			JsonArray air_base_attack = json.getJsonArray("api_air_base_attack");
			for (int index = 0; index < air_base_attack.size(); index++) {
				this.battleDayStage.add(new AirbaseAttack(index, air_base_attack.getJsonObject(index)));
			}
		}
		if (json.containsKey("api_stage_flag")) {
			this.battleDayStage.add(new Kouko(1, JsonUtils.getIntArray(json, "api_stage_flag"), json.getJsonObject("api_kouku")));
		}
		if (json.containsKey("api_stage_flag2")) {
			this.battleDayStage.add(new Kouko(2, JsonUtils.getIntArray(json, "api_stage_flag2"), json.getJsonObject("api_kouku2")));
		}
		if (json.containsKey("api_support_flag") && json.getInt("api_support_flag") != 0) {
			this.battleDayStage.add(new SupportAttack(json.getInt("api_support_flag"), json.getJsonObject("api_support_info")));
		}
		if (json.containsKey("api_opening_taisen_flag") && json.getInt("api_opening_taisen_flag") == 1) {
			this.battleDayStage.add(new OpeningTaisen(json.getJsonObject("api_opening_taisen")));
		}
		if (json.containsKey("api_opening_flag") && json.getInt("api_opening_flag") == 1) {
			this.battleDayStage.add(new OpeningAttack(json.getJsonObject("api_opening_atack")));
		}
		if (json.containsKey("api_hourai_flag") && this.getRaigekiIndex() != -1) {
			int[] hourai_flags = JsonUtils.getIntArray(json, "api_hourai_flag");
			int raigekiIndex = this.getRaigekiIndex();
			int count = 0;
			for (int index = 0; index < hourai_flags.length; index++) {
				if (index + 1 == raigekiIndex) {
					if (hourai_flags[index] == 1) this.battleDayStage.add(new Raigeki(json.getJsonObject("api_raigeki")));
				} else {
					count++;
					if (hourai_flags[index] == 1) this.battleDayStage.add(new Hougeki(count, json.getJsonObject("api_hougeki" + count)));
				}
			}
		}
	}

	@Override
	public BattleDeckAttackDamage getfDeckAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.fAttackDamage);
	}

	@Override
	public BattleDeckAttackDamage getfDeckCombineAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.fAttackDamageco);
	}

	@Override
	public BattleDeckAttackDamage geteDeckAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.eAttackDamage);
	}

	@Override
	public BattleDeckAttackDamage geteDeckCombineAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.eAttackDamageco);
	}

	private BattleDeckAttackDamage getBattleDeckAttackDamage(Function<BattleDayStage, BattleDeckAttackDamage> mapper) {
		return this.battleDayStage.stream().map(mapper).reduce(new BattleDeckAttackDamage(), BattleDeckAttackDamage::add);
	}

	/**
	 * 雷击战的index,1,2,3,4<br>
	 * 插入三次炮击战哪个位置<br>
	 * 默认-1为无三次炮击战以及雷击战
	 */
	protected int getRaigekiIndex() {
		return -1;
	}

	/*---------------------------------昼战的各个战斗阶段--------------------------------------------*/

	public abstract class BattleDayStage extends BattleStage {
		private static final long serialVersionUID = 1L;
	}

	public class AirbaseInjection extends BattleDayStage {
		private static final long serialVersionUID = 1L;

		public AirbaseInjection(JsonObject json) {}

		@Override
		public String getStageName() {
			return "基地航空队-喷气机";
		}
	}

	public class InjectionKouko extends BattleDayStage {
		private static final long serialVersionUID = 1L;
		private final int[][] planeLostStage1 = new int[][] { null, null };
		private final int[][] planeLostStage2 = new int[][] { null, null };

		public InjectionKouko(JsonObject json) {
			JsonObject stage1 = json.getJsonObject("api_stage1");
			this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
			this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };

			JsonObject stage2 = json.getJsonObject("api_stage2");
			this.planeLostStage2[0] = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
			this.planeLostStage2[1] = new int[] { stage2.getInt("api_e_count"), stage2.getInt("api_e_lostcount") };

			int[] fdmg = new int[6];
			int[] edmg = new int[6];
			int[] fdmgco = new int[6];
			int[] edmgco = new int[6];
			{
				JsonValue api_stage3 = json.get("api_stage3");
				if (api_stage3 instanceof JsonObject) {//舰载机被打光时，为JsonValue.NULL
					JsonObject stage3 = (JsonObject) api_stage3;
					int[] fdam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3, "api_fdam"));
					int[] edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3, "api_edam"));
					for (int i = 1; i <= 6; i++) {
						fdmg[i - 1] += fdam[i];
						edmg[i - 1] += edam[i];
					}
				}
			}
			if (json.containsKey("api_stage3_combined")) {
				JsonValue api_stage3_combined = json.get("api_stage3_combined");
				if (api_stage3_combined instanceof JsonObject) {//舰载机被打光时，为JsonValue.NULL
					JsonObject stage3_combined = (JsonObject) api_stage3_combined;
					if (stage3_combined.containsKey("api_fdam")) {
						int[] fdam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3_combined, "api_fdam"));
						for (int i = 1; i <= 6; i++) {
							fdmgco[i - 1] += fdam[i];
						}
					}
					if (stage3_combined.containsKey("api_edam")) {
						int[] edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3_combined, "api_edam"));
						for (int i = 1; i <= 6; i++) {
							edmgco[i - 1] += edam[i];
						}
					}
				}
			}
			this.fAttackDamage.getDamage(fdmg);
			this.eAttackDamage.getDamage(edmg);
			this.fAttackDamageco.getDamage(fdmgco);
			this.eAttackDamageco.getDamage(edmgco);
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[][] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		@Override
		public String getStageName() {
			return "航空战-喷气机";
		}
	}

	public class AirbaseAttack extends BattleDayStage {
		private static final long serialVersionUID = 1L;
		private final int index;
		private final int[][] planeLostStage1 = new int[][] { null, null };
		private final int[][] planeLostStage2 = new int[][] { null, null };

		public AirbaseAttack(int index, JsonObject json) {
			this.index = index;

			int[] stages = JsonUtils.getIntArray(json, "api_stage_flag");
			if (stages[0] == 1) {
				JsonObject stage1 = json.getJsonObject("api_stage1");
				this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
				this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };
			}
			if (stages[1] == 1) {
				JsonObject stage2 = json.getJsonObject("api_stage2");
				this.planeLostStage2[0] = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
				this.planeLostStage2[1] = new int[] { stage2.getInt("api_e_count"), stage2.getInt("api_e_lostcount") };
			}

			int[] edmg = new int[6];
			int[] edmgco = new int[6];
			if (stages[2] == 1) {
				JsonObject stage3 = json.getJsonObject("api_stage3");
				int[] edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3, "api_edam"));
				for (int i = 1; i <= 6; i++) {
					edmg[i - 1] += edam[i];
				}
			}
			if (stages[2] == 1 && json.containsKey("api_stage3_combined")) {
				JsonObject stage3_combined = json.getJsonObject("api_stage3_combined");
				int[] edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3_combined, "api_edam"));
				for (int i = 1; i <= 6; i++) {
					edmgco[i - 1] += edam[i];
				}
			}
			this.eAttackDamage.getDamage(edmg);
			this.eAttackDamageco.getDamage(edmgco);
		}

		public int getIndex() {
			return this.index;
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[][] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		@Override
		public String getStageName() {
			return "第" + new String[] { "一", "二", "三", "四", "五", "六", "七", "八" }[this.index] + "轮基地航空队";
		}
	}

	public class Kouko extends BattleDayStage {
		private static final long serialVersionUID = 1L;
		private final int index;
		private boolean[] stages = null;
		private Integer seiku = null;
		private int[] touch = null;
		private int[][] planeLostStage1 = new int[][] { null, null };
		private int[][] planeLostStage2 = new int[][] { null, null };
		private int[] duikongci = null;//[index,kind]

		public Kouko(int index, int[] flags, JsonObject json) {
			this.index = index;
			this.stages = new boolean[] { flags[0] == 1, flags[1] == 1, flags[2] == 1 };
			if (this.stages[0]) {
				JsonObject stage1 = json.getJsonObject("api_stage1");
				this.seiku = stage1.getInt("api_disp_seiku");
				this.touch = JsonUtils.getIntArray(stage1, "api_touch_plane");
				this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
				this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };
			}
			if (this.stages[1]) {
				JsonObject stage2 = json.getJsonObject("api_stage2");
				this.planeLostStage2[0] = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
				this.planeLostStage2[1] = new int[] { stage2.getInt("api_e_count"), stage2.getInt("api_e_lostcount") };
				//对空CI信息,在此
				if (stage2.containsKey("api_air_fire")) {
					JsonObject air_fire = stage2.getJsonObject("api_air_fire");
					this.duikongci = new int[] { air_fire.getInt("api_idx"), air_fire.getInt("api_kind") };
				}
			}

			int[] fdmg = new int[6];
			int[] edmg = new int[6];
			int[] fdmgco = new int[6];
			int[] edmgco = new int[6];
			if (this.stages[2]) {
				JsonObject stage3 = json.getJsonObject("api_stage3");
				int[] fdam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3, "api_fdam"));
				int[] edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3, "api_edam"));
				for (int i = 1; i <= 6; i++) {
					fdmg[i - 1] += fdam[i];
					edmg[i - 1] += edam[i];
				}
			}
			if (this.stages[2] && json.containsKey("api_stage3_combined")) {
				JsonObject stage3_combined = json.getJsonObject("api_stage3_combined");
				if (stage3_combined.containsKey("api_fdam")) {
					int[] fdam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3_combined, "api_fdam"));
					for (int i = 1; i <= 6; i++) {
						fdmgco[i - 1] += fdam[i];
					}
				}
				if (stage3_combined.containsKey("api_edam")) {
					int[] edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3_combined, "api_edam"));
					for (int i = 1; i <= 6; i++) {
						edmgco[i - 1] += edam[i];
					}
				}
			}
			this.fAttackDamage.getDamage(fdmg);
			this.eAttackDamage.getDamage(edmg);
			this.fAttackDamageco.getDamage(fdmgco);
			this.eAttackDamageco.getDamage(edmgco);
		}

		public boolean[] getStages() {
			return this.stages;
		}

		public String getSeiku() {
			return this.seiku == null ? null : BattleDto.getSeiku(this.seiku);
		}

		public boolean[] getTouchPlane() {
			if (this.touch == null) return null;
			return new boolean[] { this.touch[0] > 0, this.touch[1] > 0 };
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[][] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		public int[] getDuikongci() {
			return this.duikongci;
		}

		@Override
		public String getStageName() {
			return (this.index != 1 ? String.format("第%s轮", new String[] { "零", "一", "二", "三", "四" }[this.index]) : "") + "航空战";
		}
	}

	public class SupportAttack extends BattleDayStage {
		private static final long serialVersionUID = 1L;
		private final int type;

		public SupportAttack(int type, JsonObject json) {
			this.type = type;

			int[] damage = null;
			if (type == 1) {//航空支援
				JsonObject airattack = json.getJsonObject("api_support_airatack");
				int[] flags = JsonUtils.getIntArray(airattack, "api_stage_flag");
				if (flags[2] == 1) {
					JsonObject stage3 = airattack.getJsonObject("api_stage3");
					damage = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(stage3, "api_edam"));
				}
			} else if (type == 2 || type == 3) {//炮击支援或雷击支援
				JsonObject hourai = json.getJsonObject("api_support_hourai");
				damage = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(hourai, "api_damage"));
			}
			if (damage != null) {
				switch (damage.length) {
					case 1 + 12:
						this.eAttackDamageco.getDamage(Arrays.copyOfRange(damage, 7, 13));
					case 1 + 6:
						this.eAttackDamage.getDamage(Arrays.copyOfRange(damage, 1, 7));
				}
			}
		}

		@Override
		public String getStageName() {
			return BattleDto.getSupportType(this.type);
		}
	}

	public class OpeningTaisen extends BattleDayStage {
		private static final long serialVersionUID = 1L;
		/**
		 *  0,开幕对潜<br>
		 *  1,第一轮炮击战<br>
		 *  2,第二轮炮击战<br>
		 *  3,第三轮炮击战
		 */
		private final int index;

		/**开幕反潜用*/
		public OpeningTaisen(JsonObject json) {
			this(0, json);
		}

		/**炮击战用*/
		public OpeningTaisen(int index, JsonObject json) {
			this.index = index;
			/** 敌联合舰队时存在(因为有混战)  */
			JsonArray at_eflag = json.containsKey("api_at_eflag") ? json.getJsonArray("api_at_eflag") : null;
			JsonArray at_list = json.getJsonArray("api_at_list");
			JsonArray at_type = json.getJsonArray("api_at_type");
			JsonArray df_list = json.getJsonArray("api_df_list");
			JsonArray damage = json.getJsonArray("api_damage");
			for (int x = 1; x < at_list.size(); x++) {
				Boolean enemyAttack = at_eflag == null ? null : (at_eflag.getInt(x) == 1 ? Boolean.TRUE : Boolean.FALSE);
				int attackIndex = at_list.getInt(x);
				int[] defenseIndexs = JsonUtils.getIntArray(df_list.getJsonArray(x));
				int[] dmgs = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(damage.getJsonArray(x)));
				int type = at_type.getInt(x);
				this.battleAttacks.add(new BattleOneAttack(enemyAttack, false, attackIndex, defenseIndexs, dmgs, type));
			}

			BattleOneAttackSimulator boas = new BattleOneAttackSimulator();
			this.battleAttacks.forEach(boa -> boas.accept(boa, this.getSimulatorObject(boa.enemyAttack)));
			this.accept(boas);
		}

		public Boolean getSimulatorObject(Boolean enemyAttack) {
			if (enemyAttack == null) {//敌方非联合舰队
				if (AbstractBattleDay.this instanceof BattleDayDto || AbstractBattleDay.this instanceof PracticeBattleDayDto) {//6v6
					return Boolean.FALSE;
				} else if (this.getIndex() == 0) {//12v6,开幕对潜
					return Boolean.TRUE;
				} else if (AbstractBattleDay.this instanceof CombinebattleDayWaterDto) {
					switch (this.getIndex()) {
						case 1:
						case 2:
							return Boolean.FALSE;
						case 3:
							return Boolean.TRUE;
					}
				} else if (AbstractBattleDay.this instanceof CombinebattleDayDto) {
					switch (this.getIndex()) {
						case 1:
							return Boolean.TRUE;
						case 2:
						case 3:
							return Boolean.FALSE;
					}
				}
			}
			return null;//敌联合舰队时,在BattleOneAttackSimulator内部判断
		}

		public int getIndex() {
			return this.index;
		}

		@Override
		public String getStageName() {
			return "先制反潜";
		}
	}

	public class OpeningAttack extends BattleDayStage {
		private static final long serialVersionUID = 1L;
		public int[] frai;
		public int[] erai;
		public int[] fdam;
		public int[] edam;
		public int[] fydam;
		public int[] eydam;

		public OpeningAttack(JsonObject json) {
			//-1开头,长度1+(6or12)
			this.frai = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(json, "api_frai"));//目标
			this.erai = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(json, "api_erai"));
			this.fdam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(json, "api_fdam"));//受到的伤害
			this.edam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(json, "api_edam"));
			this.fydam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(json, "api_fydam"));//攻击
			this.eydam = ToolUtils.doubleToIntegerFloor(JsonUtils.getDoubleArray(json, "api_eydam"));

			switch (this.fdam.length) {//自方受伤
				case 1 + 12:
					this.fAttackDamage.getDamage(Arrays.copyOfRange(this.fdam, 1, 7));
					this.fAttackDamageco.getDamage(Arrays.copyOfRange(this.fdam, 7, 13));
					break;
				case 1 + 6:
					if (existBattleDeck(AbstractBattleDay.this.getfDeckCombine())) {//自方联合舰队,2队受到雷击
						this.fAttackDamageco.getDamage(Arrays.copyOfRange(this.fdam, 1, 7));
					} else {//自方非联合舰队
						this.fAttackDamage.getDamage(Arrays.copyOfRange(this.fdam, 1, 7));
					}
					break;
			}
			switch (this.edam.length) {//敌方受伤
				case 1 + 12:
					this.eAttackDamage.getDamage(Arrays.copyOfRange(this.edam, 1, 7));
					this.eAttackDamageco.getDamage(Arrays.copyOfRange(this.edam, 7, 13));
					break;
				case 1 + 6:
					if (existBattleDeck(AbstractBattleDay.this.geteDeckCombine())) {
						this.eAttackDamageco.getDamage(Arrays.copyOfRange(this.edam, 1, 7));
					} else {//敌方非联合舰队
						this.eAttackDamage.getDamage(Arrays.copyOfRange(this.edam, 1, 7));
					}
					break;
			}
			switch (this.fydam.length) {//自方攻击
				case 1 + 12:
					this.fAttackDamage.setAttack(Arrays.copyOfRange(this.fydam, 1, 7));
					this.fAttackDamageco.setAttack(Arrays.copyOfRange(this.fydam, 7, 13));
					break;
				case 1 + 6:
					if (existBattleDeck(AbstractBattleDay.this.getfDeckCombine())) {//自方联合舰队
						this.fAttackDamageco.setAttack(Arrays.copyOfRange(this.fydam, 1, 7));
					} else {//自方非联合舰队
						this.fAttackDamage.setAttack(Arrays.copyOfRange(this.fydam, 1, 7));
					}
					break;
			}
			switch (this.eydam.length) {//敌方攻击
				case 1 + 12:
					this.eAttackDamage.setAttack(Arrays.copyOfRange(this.eydam, 1, 7));
					this.eAttackDamageco.setAttack(Arrays.copyOfRange(this.eydam, 7, 13));
					break;
				case 1 + 6:
					if (existBattleDeck(AbstractBattleDay.this.geteDeckCombine())) {
						this.eAttackDamageco.setAttack(Arrays.copyOfRange(this.eydam, 1, 7));
					} else {//敌方非联合舰队
						this.eAttackDamage.setAttack(Arrays.copyOfRange(this.eydam, 1, 7));
					}
					break;
			}
		}

		@Override
		public String getStageName() {
			return "开幕雷击";
		}
	}

	public class Hougeki extends OpeningTaisen {
		private static final long serialVersionUID = 1L;

		public Hougeki(int index, JsonObject json) {
			super(index, json);
		}

		@Override
		public String getStageName() {
			return "第" + new String[] { "零", "一", "二", "三", "四" }[this.getIndex()] + "轮炮击战";
		}
	}

	public class Raigeki extends OpeningAttack {
		private static final long serialVersionUID = 1L;

		public Raigeki(JsonObject json) {
			super(json);
		}

		@Override
		public String getStageName() {
			return "雷击战";
		}
	}
}
