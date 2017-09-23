package tdrz.update.dto.memory;

import tdrz.update.dto.word.ItemDto;

/**
 * 废弃装备
 * 
 * @author MoeKagari
 */
public class DestroyItemDto extends AbstractRecord {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final String event;
	private final Item item;
	private final int group;

	public DestroyItemDto(long time, String event, ItemDto item, int group) {
		this.time = time;
		this.event = event;
		this.item = new Item(item);
		this.group = group;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public String getEvent() {
		return this.event;
	}

	public int getGroup() {
		return this.group;
	}

	public Item getItem() {
		return this.item;
	}
}
