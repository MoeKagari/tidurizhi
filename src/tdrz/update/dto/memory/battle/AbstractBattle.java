package tdrz.update.dto.memory.battle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.json.JsonObject;

import tdrz.core.config.AppConstants;
import tdrz.core.translator.DeckDtoTranslator;
import tdrz.core.translator.MasterDataTranslator;
import tdrz.core.util.JsonUtils;
import tdrz.core.util.ToolUtils;
import tool.function.FunctionUtils;

public abstract class AbstractBattle extends BattleDto {
	private static final long serialVersionUID = 1L;
	private BattleDeck fDeck = null;
	private BattleDeck fDeckCombine = null;
	private BattleDeck eDeck = null;
	private BattleDeck eDeckCombine = null;

	private final int[] formation;
	private final int[] search;

	public AbstractBattle(long time, JsonObject json) {
		super(time);
		//BattleDeck的初始化
		{
			int[] nowhps = JsonUtils.getIntArray(json, "api_nowhps");
			int[] maxhps = JsonUtils.getIntArray(json, "api_maxhps");
			this.fDeck = new BattleDeck(false, false, Arrays.copyOfRange(nowhps, 1, 7), Arrays.copyOfRange(maxhps, 1, 7));
			this.eDeck = new BattleDeck(false, true, Arrays.copyOfRange(nowhps, 7, 13), Arrays.copyOfRange(maxhps, 7, 13));
		}
		if (json.containsKey("api_nowhps_combined") && json.containsKey("api_maxhps_combined")) {
			int[] nowhps_combined = JsonUtils.getIntArray(json, "api_nowhps_combined");
			int[] maxhps_combined = JsonUtils.getIntArray(json, "api_maxhps_combined");
			int len = nowhps_combined.length;
			if (len <= 1 + 6) {
				this.fDeckCombine = new BattleDeck(true, false, Arrays.copyOfRange(nowhps_combined, 1, 7), Arrays.copyOfRange(maxhps_combined, 1, 7));
			} else {
				this.fDeckCombine = new BattleDeck(true, false, Arrays.copyOfRange(nowhps_combined, 1, 7), Arrays.copyOfRange(maxhps_combined, 1, 7));
				this.eDeckCombine = new BattleDeck(true, true, Arrays.copyOfRange(nowhps_combined, 7, 13), Arrays.copyOfRange(maxhps_combined, 7, 13));
			}
		}

		//BattleDeck的舰娘名
		if (existBattleDeck(this.fDeckCombine)) {//我方为联合舰队
			this.fDeck.setNames(DeckDtoTranslator.getShipNames(1));
			this.fDeckCombine.setNames(DeckDtoTranslator.getShipNames(2));
		} else {
			String[] names;
			if (json.containsKey("api_deck_id")) {
				names = DeckDtoTranslator.getShipNames(JsonUtils.dissociateInt(json.get("api_deck_id")));
			} else if (json.containsKey("api_dock_id")) {
				names = DeckDtoTranslator.getShipNames(JsonUtils.dissociateInt(json.get("api_dock_id")));
			} else {
				names = AppConstants.EMPTY_NAMES;
			}
			this.fDeck.setNames(names);
		}
		{//敌方
			int[] ids = JsonUtils.dissociateIntArray(json, "api_ship_ke");
			this.eDeck.setNames(ToolUtils.toStringArray(Arrays.copyOfRange(ids, 1, 7), MasterDataTranslator::getShipName));
		}
		if (json.containsKey("api_ship_ke_combined")) {
			int[] ids = JsonUtils.dissociateIntArray(json, "api_ship_ke_combined");
			this.eDeckCombine.setNames(ToolUtils.toStringArray(Arrays.copyOfRange(ids, 1, 7), MasterDataTranslator::getShipName));
		}

		//索敌
		this.search = JsonUtils.dissociateIntArray(json, "api_search");
		//阵型和航向,[自-阵型,敌-阵型,航向]
		this.formation = JsonUtils.dissociateIntArray(json, "api_formation");

		//退避
		if (json.containsKey("api_escape_idx")) {
			FunctionUtils.forEachInt(JsonUtils.getIntArray(json, "api_escape_idx"), index -> this.fDeck.escapes.add(index - 1));
		}
		if (json.containsKey("api_escape_idx_combined")) {
			FunctionUtils.forEachInt(JsonUtils.getIntArray(json, "api_escape_idx_combined"), index -> this.fDeckCombine.escapes.add(index - 1));
		}
	}

	public boolean isMidnight() {
		return false;
	}

	public String[] getZhenxin() {
		return this.formation == null ? null
				: new String[] {//
						BattleDto.getZhenxin(this.formation[0]),//
						BattleDto.getZhenxin(this.formation[1])//
				};
	}

	public String getHangxiang() {
		return this.formation == null ? null : BattleDto.getHangxiang(this.formation[2]);
	}

	public String[] getSearch() {
		return this.search == null ? null
				: new String[] {//
						BattleDto.getSearch(this.search[0]),//
						BattleDto.getSearch(this.search[1])//
				};
	}

	public BattleDeck getfDeck() {
		return this.fDeck;
	}

	public BattleDeck getfDeckCombine() {
		return this.fDeckCombine;
	}

	public BattleDeck geteDeck() {
		return this.eDeck;
	}

	public BattleDeck geteDeckCombine() {
		return this.eDeckCombine;
	}

	public abstract BattleDeckAttackDamage getfDeckAttackDamage();

	public abstract BattleDeckAttackDamage getfDeckCombineAttackDamage();

	public abstract BattleDeckAttackDamage geteDeckAttackDamage();

	public abstract BattleDeckAttackDamage geteDeckCombineAttackDamage();

	/*--------------------------------------------------------------------------------------------------------------------*/

	public static boolean existBattleDeck(BattleDeck deck) {
		return FunctionUtils.notNull(deck, BattleDeck::exist, false);
	}

	/**
	 * 战斗时的舰队的信息
	 * 
	 * @author MoeKagari
	 */
	public static class BattleDeck implements Serializable {
		private static final long serialVersionUID = 1L;
		public final ArrayList<Integer> escapes = new ArrayList<>();
		public final boolean isCombine;
		public final boolean isEnemy;
		public final int[] nowhps;
		public final int[] maxhps;
		public String[] names = AppConstants.EMPTY_NAMES;

		public BattleDeck(boolean isCombine, boolean isEnemy, int[] nowhps, int[] maxhps) {
			this.isCombine = isCombine;
			this.isEnemy = isEnemy;
			this.nowhps = nowhps;
			this.maxhps = maxhps;
		}

		public boolean exist() {
			return Arrays.stream(this.nowhps).anyMatch(nowhp -> nowhp != -1);
		}

		public int getDeckLength() {
			int count = 0;
			for (int i = 0; i < 6; i++) {
				if (this.maxhps[i] != -1) {
					count++;
				}
			}
			return count;
		}

		public void setNames(String[] names) {
			this.names = names;
		}
	}

	public static abstract class BattleStage implements Serializable {
		private static final long serialVersionUID = 1L;
		public final BattleDeckAttackDamage fAttackDamage = new BattleDeckAttackDamage();
		public final BattleDeckAttackDamage eAttackDamage = new BattleDeckAttackDamage();
		public final BattleDeckAttackDamage fAttackDamageco = new BattleDeckAttackDamage();
		public final BattleDeckAttackDamage eAttackDamageco = new BattleDeckAttackDamage();
		public final ArrayList<BattleOneAttack> battleAttacks = new ArrayList<>();

		public void accept(BattleOneAttackSimulator boas) {
			this.fAttackDamage.getDamage(boas.fdmg);
			this.fAttackDamage.setAttack(boas.fatt);
			this.eAttackDamage.getDamage(boas.edmg);
			this.eAttackDamage.setAttack(boas.eatt);
			this.fAttackDamageco.getDamage(boas.fdmgco);
			this.fAttackDamageco.setAttack(boas.fattco);
			this.eAttackDamageco.getDamage(boas.edmgco);
			this.eAttackDamageco.setAttack(boas.eattco);
		}

		public abstract String getStageName();
	}

	/**
	 * 昼战开幕反潜,三次炮击战,夜战
	 * 
	 * @author MoeKagari
	 */
	public static class BattleOneAttack implements Serializable {
		private static final long serialVersionUID = 1L;
		/** 敌联合舰队时存在(因为有混战) */
		public final Boolean enemyAttack;
		public final int attackIndex;//攻击方位置(1-12),enemyAttack所代表的两只舰队,非联合舰队时,自方舰队在前,联合舰队时,第一舰队在前
		public final int[] defenseIndexs;//attackIndex的对方
		public final int[] dmgs;//此次造成的伤害,与defenseIndexs长度相同
		public final int attackType;//攻击类型,昼夜战不同
		public final boolean isMidnight;

		/**
		 * 夜战用
		 */
		public BattleOneAttack(int attackIndex, int[] defenseIndexs, int[] dmgs, int attackType) {
			this(null, true, attackIndex, defenseIndexs, dmgs, attackType);
		}

		/**
		 * 昼战用
		 */
		public BattleOneAttack(Boolean enemyAttack, boolean isMidnight, int attackIndex, int[] defenseIndexs, int[] dmgs, int attackType) {
			this.enemyAttack = enemyAttack;
			this.attackIndex = attackIndex;
			this.defenseIndexs = defenseIndexs;
			this.dmgs = dmgs;
			this.attackType = attackType;
			this.isMidnight = isMidnight;
		}
	}

	/**
	 * 接收{@link BattleOneAttack}进行模拟
	 * 
	 * @author MoeKagari
	 */
	public static class BattleOneAttackSimulator implements Serializable {
		private static final long serialVersionUID = 1L;
		public final int[] fdmg = new int[6];
		public final int[] fatt = new int[6];
		public final int[] edmg = new int[6];
		public final int[] eatt = new int[6];
		public final int[] fdmgco = new int[6];
		public final int[] fattco = new int[6];
		public final int[] edmgco = new int[6];
		public final int[] eattco = new int[6];

		/**
		 * @param fcombine
		 *                自方参战deck是否是联合舰队
		 */
		public void accept(BattleOneAttack boa, Boolean fcombine) {
			Boolean enemyAttack = boa.enemyAttack;
			int attackIndex = boa.attackIndex;
			int[] defenseIndexs = boa.defenseIndexs;
			int[] damages = boa.dmgs;

			int[][] atter = null, dmger = null;
			if (enemyAttack == null && fcombine != null) {//敌方非联合舰队
				atter = new int[][] { fcombine == Boolean.TRUE ? this.fattco : this.fatt, this.eatt };
				dmger = new int[][] { fcombine == Boolean.TRUE ? this.fdmgco : this.fdmg, this.edmg };
			} else if (enemyAttack == Boolean.FALSE) {//敌联合舰队,我方攻击
				atter = new int[][] { this.fatt, this.fattco };
				dmger = new int[][] { this.edmg, this.edmgco };
			} else if (enemyAttack == Boolean.TRUE) {//敌联合舰队,敌方攻击
				atter = new int[][] { this.eatt, this.eattco };
				dmger = new int[][] { this.fdmg, this.fdmgco };
			} else {
				System.out.println("enemyAttack == null && fcombine == null");
			}

			for (int i = 0; i < damages.length; i++) {
				if (defenseIndexs[i] == -1) continue;//三炮CI -> [index,-1,-1]

				if (atter != null) atter[(attackIndex - 1) / 6][(attackIndex - 1) % 6] += damages[i];
				if (dmger != null) dmger[(defenseIndexs[i] - 1) / 6][(defenseIndexs[i] - 1) % 6] += damages[i];
			}
		}
	}

	/**
	 * BattleDeck 的attack和damage<br>
	 * 供每个 BattleStage 用
	 * 
	 * @author MoeKagari
	 */
	public static class BattleDeckAttackDamage implements Serializable {
		private static final long serialVersionUID = 1L;
		public final int[] dmgs = new int[6];
		public final int[] attack = new int[6];

		/**
		 * 受到伤害
		 */
		public void getDamage(int[] gd) {
			for (int i = 0; i < 6; i++) {
				this.dmgs[i] += gd[i];
			}
		}

		/**
		 * 攻击输出
		 */
		public void setAttack(int[] sa) {
			for (int i = 0; i < 6; i++) {
				this.attack[i] += sa[i];
			}
		}

		public BattleDeckAttackDamage add(BattleDeckAttackDamage next) {
			for (int i = 0; i < 6; i++) {
				this.dmgs[i] = this.dmgs[i] + next.dmgs[i];
				this.attack[i] = this.attack[i] + next.attack[i];
			}
			return this;
		}
	}

}
