package tdrz.update.dto.memory;

import tdrz.update.dto.word.ShipDto;

/**
 * 建造记录
 * 
 * @author MoeKagari
 */
public class CreateShipDto extends AbstractRecord {
	private static final long serialVersionUID = 1L;
	private final int[] mm;
	private final long time;
	private final boolean largeflag, highspeed;
	private final Ship secretaryShip;

	private int shipId, emptyCount = -1;

	public CreateShipDto(ShipDto secretary, int[] mm, long time, boolean largeflag, boolean highspeed) {
		this.mm = mm;
		this.time = time;
		this.largeflag = largeflag;
		this.highspeed = highspeed;
		this.secretaryShip = new Ship(secretary);
	}

	public Ship getSecretaryShip() {
		return this.secretaryShip;
	}

	@Override
	public long getTime() {
		return this.time;
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

	public void setShipId(int shipId) {
		this.shipId = shipId;
	}

	public int getShipId() {
		return this.shipId;
	}

	public int getEmptyCount() {
		return this.emptyCount;
	}

	public void setEmptyCount(int emptyCount) {
		this.emptyCount = emptyCount;
	}
}
