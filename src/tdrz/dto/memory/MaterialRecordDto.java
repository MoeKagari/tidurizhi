package tdrz.dto.memory;

import tdrz.dto.AbstractMemory;
import tdrz.dto.word.MaterialDto;
import tool.FunctionUtils;

/**
 * 资源记录
 * @author MoeKagari
 */
public class MaterialRecordDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final String description;
	private final long time;
	private final int[] material;

	public MaterialRecordDto(String description, long time, MaterialDto material) {
		this.description = description;
		this.time = time;
		this.material = FunctionUtils.arrayCopy(material.getMaterial());
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public String getDescription() {
		return this.description;
	}
}
