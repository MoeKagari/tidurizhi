package tdrz.update.dto.memory;

import java.io.Serializable;

import tdrz.core.translator.ItemDtoTranslator;
import tdrz.core.translator.MasterDataTranslator;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.ShipDto;

public abstract class AbstractRecord extends AbstractMemory {
	private static final long serialVersionUID = 1L;

	public class Ship implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int id, shipId, level;
		private final String name;

		public Ship(ShipDto ship) {
			this.id = ship.getId();
			this.shipId = ship.getShipId();
			this.name = ShipDtoTranslator.getName(ship);
			this.level = ship.getLevel();
		}

		public int getShipId() {
			return this.shipId;
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

		public String getTypeString() {
			return ShipDtoTranslator.getTypeString(MasterDataTranslator.getMasterShipDto(this.shipId));
		}
	}

	public class Item implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int id, slotitemId, level, alv;
		private final String name;

		public Item(ItemDto item) {
			this.id = item.getId();
			this.slotitemId = item.getSlotitemId();
			this.level = item.getLevel();
			this.alv = item.getAlv();
			this.name = ItemDtoTranslator.getName(item);
		}

		public int getId() {
			return this.id;
		}

		public int getSlotitemId() {
			return this.slotitemId;
		}

		public int getLevel() {
			return this.level;
		}

		public int getAlv() {
			return this.alv;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String toString() {
			return String.format("%s%s%s", this.name, this.alv > 0 ? (" 熟" + this.alv) : "", this.level > 0 ? (" ★" + this.level) : "");
		}
	}
}
