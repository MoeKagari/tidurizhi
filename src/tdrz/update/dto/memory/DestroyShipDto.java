package tdrz.update.dto.memory;

import tdrz.update.dto.word.ShipDto;

/**
 * 解体舰娘
 * 
 * @author MoeKagari
 */
public class DestroyShipDto extends AbstractRecord {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final String event;
	private final Ship ship;

	public DestroyShipDto(long time, String event, ShipDto ship) {
		this.time = time;
		this.event = event;
		this.ship = new Ship(ship);
	}

	public Ship getShip() {
		return this.ship;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public String getEvent() {
		return this.event;
	}
}
