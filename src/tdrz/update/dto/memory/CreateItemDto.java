package tdrz.update.dto.memory;

import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

/**
 * 开发记录
 * 
 * @author MoeKagari
 */
public class CreateItemDto extends AbstractRecord {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final boolean success;
	private final int[] material;
	private final int slotitemId;
	private final Ship secretaryShip;

	public CreateItemDto(long time, boolean success, int[] material, int slotitemId, ShipDto secretary) {
		this.time = time;
		this.success = success;
		this.material = FunctionUtils.arrayCopy(material);
		this.slotitemId = slotitemId;
		this.secretaryShip = new Ship(secretary);
	}

	public Ship getSecretaryShip() {
		return this.secretaryShip;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public int getSlotitemId() {
		return this.slotitemId;
	}
}
