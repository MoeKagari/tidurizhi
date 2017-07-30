package tdrz.dto.memory;

import tdrz.dto.AbstractMemory;
import tdrz.dto.translator.ShipDtoTranslator;
import tdrz.dto.word.ShipDto;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateshipDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final String flagship;
	private final int flagshipLevel;
	private final int[] mm;
	private final long time;
	private int shipId;
	private int emptyCount = -1;

	public CreateshipDto(ShipDto secretary, int[] mm, long time) {
		this.flagship = ShipDtoTranslator.getName(secretary);
		this.flagshipLevel = secretary.getLevel();
		this.mm = mm;
		this.time = time;
	}

	public String getFlagship() {
		return this.flagship;
	}

	public int getFlagshipLevel() {
		return this.flagshipLevel;
	}

	public void setShipId(int shipId) {
		this.shipId = shipId;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public int getShipId() {
		return this.shipId;
	}

	public boolean largeflag() {
		return this.mm[0] >= 1000;
	}

	public boolean highspeed() {
		return this.mm[4] != 0;
	}

	public int zhicai() {
		return this.mm[6];
	}

	public int[] cost() {
		return new int[] { this.mm[0], this.mm[1], this.mm[2], this.mm[3] };
	}

	public int getEmptyCount() {
		return this.emptyCount;
	}

	public void setEmptyCount(int emptyCount) {
		this.emptyCount = emptyCount;
	}
}
