package tdrz.update.dto.memory;

import tdrz.core.translator.ShipDtoTranslator;
import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

/**
 * 开发记录
 * @author MoeKagari
 */
public class CreateItemDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final boolean success;
	private final int[] material;
	private final int slotitemId;
	private final String secretaryShipType;
	private final String secretaryShip;
	private final int secretaryShipLevel;

	public CreateItemDto(long time, boolean success, int[] material, int slotitemId, ShipDto secretary) {
		this.time = time;
		this.success = success;
		this.material = FunctionUtils.arrayCopy(material);
		this.slotitemId = slotitemId;
		this.secretaryShipType = ShipDtoTranslator.getTypeString(secretary);
		this.secretaryShip = ShipDtoTranslator.getName(secretary);
		this.secretaryShipLevel = FunctionUtils.notNull(secretary, ShipDto::getLevel, -1);
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

	public String getSecretaryShip() {
		return this.secretaryShip;
	}

	public int getSecretaryShipLevel() {
		return this.secretaryShipLevel;
	}

	public String getSecretaryShipType() {
		return secretaryShipType;
	}
}
