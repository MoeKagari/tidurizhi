package tdrz.update.dto.memory;

import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.word.ResourceDto;
import tool.function.FunctionUtils;

/**
 * 资源记录
 * @author MoeKagari
 */
public class ResourceRecordDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final String event;
	private final long time;
	private final int[] material;

	public ResourceRecordDto(String description, long time, ResourceDto material) {
		this.event = description;
		this.time = time;
		this.material = FunctionUtils.arrayCopy(material.getResource());
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public String getEvent() {
		return this.event;
	}
}
