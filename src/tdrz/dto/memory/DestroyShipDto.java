package tdrz.dto.memory;

import tdrz.dto.AbstractMemory;
import tdrz.dto.translator.ShipDtoTranslator;
import tdrz.dto.word.ShipDto;

/**
 * 解体舰娘
 * @author MoeKagari
 */
public class DestroyShipDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final String event;
	private final int id;
	private final String name;
	private final int level;

	public DestroyShipDto(long time, String event, ShipDto ship) {
		this.time = time;
		this.event = event;
		this.id = ship.getId();
		this.name = ShipDtoTranslator.getName(ship);
		this.level = ship.getLevel();
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public String getEvent() {
		return this.event;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getLevel() {
		return this.level;
	}
}
