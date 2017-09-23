package tdrz.update.dto.memory;

import tdrz.update.dto.word.ItemDto;
import tool.function.FunctionUtils;

public class RemodleRecordDto extends AbstractRecord {
	private static final long serialVersionUID = 1L;
	private final long time;
	private final int slotId;
	private final boolean certain, success;
	private final Item item, newItem;

	public RemodleRecordDto(long time, int slotId, boolean certain, boolean success, ItemDto item, ItemDto newItem) {
		this.time = time;
		this.slotId = slotId;
		this.certain = certain;
		this.success = success;
		this.item = new Item(item);
		this.newItem = FunctionUtils.notNull(newItem, Item::new, null);
	}

	public int getSlotId() {
		return this.slotId;
	}

	public boolean isCertain() {
		return this.certain;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public boolean isUpdate() {
		return this.newItem != null && this.newItem.getSlotitemId() != this.item.getSlotitemId();
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public Item getItem() {
		return this.item;
	}

	public Item getNewItem() {
		return this.newItem;
	}
}
