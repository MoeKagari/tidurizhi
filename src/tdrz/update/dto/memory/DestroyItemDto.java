package tdrz.update.dto.memory;

import tdrz.core.translator.ItemDtoTranslator;
import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.word.ItemDto;

/**
 * 废弃装备
 * @author MoeKagari
 */
public class DestroyItemDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final String event;
	private final int id;
	private final String name;
	private final int lv;
	private final int alv;
	private final int group;

	public DestroyItemDto(long time, String event, ItemDto item, int group) {
		this.time = time;
		this.event = event;
		this.id = item.getId();
		this.name = ItemDtoTranslator.getName(item);
		this.lv = item.getLevel();
		this.alv = item.getAlv();
		this.group = group;
	}

	public int getLv() {
		return this.lv;
	}

	public int getAlv() {
		return this.alv;
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

	public int getGroup() {
		return this.group;
	}
}
