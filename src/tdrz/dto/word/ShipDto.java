package tdrz.dto.word;

import javax.json.JsonObject;

import tdrz.dto.AbstractWord;
import tdrz.dto.translator.MasterDataTranslator;
import tdrz.dto.word.MasterDataDto.MasterShipDto;
import tdrz.utils.JsonUtils;

/**
 * 玩家所持舰娘
 * @author MoeKagari
 */
public class ShipDto extends AbstractWord {
	/**	 更新(PLTIME)时是否需要此ShipDto	 */
	private boolean needForPLUpdate = true;

	private int shipId;
	private int id;
	private int level;

	private int[] onSlot;
	private int[] slots;//长度5,最后位不明作用
	private int slotex;

	private boolean locked;
	private int cond;
	private int nowHp;
	private int maxHp;

	private int fuel;
	private int bull;

	private int soku;
	private long ndockTime;
	private int[] ndockCost;
	private int[] exp;
	private int[] luck;
	private int[] sakuteki;
	private int[] taisen;
	private int[] kaihi;
	private int[] soukou;
	private int[] taiku;
	private int[] raisou;
	private int[] karyoku;
	private int[] kyouka;

	private MasterShipDto msd;

	public ShipDto(JsonObject json) {
		this.id = json.getInt("api_id");
		this.level = json.getInt("api_lv");
		this.onSlot = JsonUtils.getIntArray(json, "api_onslot");
		this.slots = JsonUtils.getIntArray(json, "api_slot");
		this.slotex = json.getInt("api_slot_ex");
		this.locked = json.getInt("api_locked") == 1;
		this.cond = json.getInt("api_cond");
		this.nowHp = json.getInt("api_nowhp");
		this.maxHp = json.getInt("api_maxhp");
		this.fuel = json.getInt("api_fuel");
		this.bull = json.getInt("api_bull");
		this.soku = json.getInt("api_soku");
		this.ndockTime = json.getJsonNumber("api_ndock_time").longValue();
		this.ndockCost = JsonUtils.getIntArray(json, "api_ndock_item");
		this.exp = JsonUtils.getIntArray(json, "api_exp");
		this.shipId = json.getInt("api_ship_id");

		this.luck = JsonUtils.getIntArray(json, "api_lucky");
		this.sakuteki = JsonUtils.getIntArray(json, "api_sakuteki");
		this.taisen = JsonUtils.getIntArray(json, "api_taisen");
		this.kaihi = JsonUtils.getIntArray(json, "api_kaihi");
		this.soukou = JsonUtils.getIntArray(json, "api_soukou");
		this.taiku = JsonUtils.getIntArray(json, "api_taiku");
		this.raisou = JsonUtils.getIntArray(json, "api_raisou");
		this.karyoku = JsonUtils.getIntArray(json, "api_karyoku");
		this.kyouka = JsonUtils.getIntArray(json, "api_kyouka");

		this.msd = MasterDataTranslator.getMasterShipDto(this.shipId);
	}

	public MasterShipDto getMasterData() {
		if (this.msd == null) {
			this.msd = MasterDataTranslator.getMasterShipDto(this.shipId);
		}
		return this.msd;
	}

	/** 加入镇守府时的编号 */
	public int getId() {
		return this.id;
	}

	public int getShipId() {
		return this.shipId;
	}

	public int getLevel() {
		return this.level;
	}

	public int getCurrentExp() {
		return this.exp[0];
	}

	public int getNextExp() {
		return this.exp[1];
	}

	public double getCurrentExpPercent() {
		return this.exp[2];
	}

	public int getNowHp() {
		return this.nowHp;
	}

	public int getMaxHp() {
		return this.maxHp;
	}

	public int[] getSlots() {
		return this.slots;
	}

	public int getSlotex() {
		return this.slotex;
	}

	public int[] getOnSlot() {
		return this.onSlot;
	}

	public int getFuel() {
		return this.fuel;
	}

	public int getBull() {
		return this.bull;
	}

	public long getNdockTime() {
		return this.ndockTime;
	}

	public int[] getNdockCost() {
		return this.ndockCost;
	}

	public int getCond() {
		return this.cond;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public int getSoku() {
		return this.soku;
	}

	/** 火力,雷装,对空,装甲,运 */
	public int[] getKyouka() {
		return this.kyouka;
	}

	public int[] getKaryoku() {
		return this.karyoku;
	}

	public int[] getRaisou() {
		return this.raisou;
	}

	public int[] getTaiku() {
		return this.taiku;
	}

	public int[] getSoukou() {
		return this.soukou;
	}

	public int[] getKaihi() {
		return this.kaihi;
	}

	public int[] getTaisen() {
		return this.taisen;
	}

	public int[] getSakuteki() {
		return this.sakuteki;
	}

	public int[] getLuck() {
		return this.luck;
	}

	/*----------------------------------------------------------------------------------------------------------------------*/

	public boolean isNeedForPLUpdate() {
		return this.needForPLUpdate;
	}

	public void nyukyoEnd() {
		this.nowHp = this.maxHp;
		this.cond = Integer.max(this.cond, 40);
		this.ndockCost = new int[2];
		this.ndockTime = 0;
		this.needForPLUpdate = false;//入渠完毕,非自然恢复,更新(PLTIME)是,不需要此ship
	}

	public void updateWhenCharge(JsonObject info) {
		this.fuel = info.getInt("api_fuel");
		this.bull = info.getInt("api_bull");
		this.onSlot = JsonUtils.getIntArray(info, "api_onslot");
	}

	/** 入渠消耗,长度8 */
	public int[] getNyukyoCost() {
		return new int[] { this.ndockCost[0], 0, this.ndockCost[1], 0, 0, 0, 0, 0 };
	}

	public void setLocked(boolean b) {
		this.locked = b;
	}

	/** 开放ex装备槽 */
	public void openSlotex() {
		this.slotex = -1;//原先为0=未开放
	}

	/** 交换装备 */
	public void slotExchange(int[] newSlots) {
		this.slots = newSlots;
	}
}
