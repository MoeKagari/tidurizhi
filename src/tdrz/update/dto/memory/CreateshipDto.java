package tdrz.update.dto.memory;

import tdrz.core.translator.ShipDtoTranslator;
import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.word.ShipDto;
import tool.FunctionUtils;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateshipDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final int[] mm;
	private final long time;
	private final boolean largeflag;
	private final boolean highspeed;
	private int shipId;
	private int emptyCount = -1;
	private final String secretaryShipType;
	private final String secretaryShip;
	private final int secretaryShipLevel;

	public CreateshipDto(ShipDto secretary, int[] mm, long time, boolean largeflag, boolean highspeed) {
		this.mm = mm;
		this.time = time;
		this.largeflag = largeflag;
		this.highspeed = highspeed;
		this.secretaryShipType = ShipDtoTranslator.getTypeString(secretary);
		this.secretaryShip = ShipDtoTranslator.getName(secretary);
		this.secretaryShipLevel = FunctionUtils.notNull(secretary, ShipDto::getLevel, -1);
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
		return this.largeflag;
	}

	public boolean highspeed() {
		return this.highspeed;
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

	public String getSecretaryShipType() {
		return this.secretaryShipType;
	}

	public String getSecretaryShip() {
		return this.secretaryShip;
	}

	public int getSecretaryShipLevel() {
		return this.secretaryShipLevel;
	}
}
