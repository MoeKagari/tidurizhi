package tdrz.dto.memory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.AbstractMemory;
import tdrz.utils.JsonUtils;

/**
 * 远征结果
 * @author MoeKagari
 *
 */
public class MissionResultDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final int deckId;
	private final int state;//0=失败,1=成功,2=大成功
	private final String area;
	private final String name;
	private final int[] material;
	private final MissionResultItem[] items = { null, null };
	private final long time;

	public MissionResultDto(int deckId, JsonObject json, long time) {
		this.deckId = deckId;
		this.time = time;
		this.state = json.getInt("api_clear_result");
		this.area = json.getString("api_maparea_name");
		this.name = json.getString("api_quest_name");

		JsonValue value = json.get("api_get_material");
		if (value instanceof JsonArray) {
			this.material = JsonUtils.getIntArray((JsonArray) value);
		} else {
			this.material = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		}

		int[] flags = JsonUtils.getIntArray(json, "api_useitem_flag");
		if (flags[0] != 0) {
			this.items[0] = new MissionResultItem(json.getJsonObject("api_get_item1"), flags[0]);
		}
		if (flags[1] != 0) {
			this.items[1] = new MissionResultItem(json.getJsonObject("api_get_item2"), flags[1]);
		}
	}

	public int getDeckId() {
		return this.deckId;
	}

	public int getState() {
		return this.state;
	}

	public String getStateString() {
		switch (this.state) {
			case 0:
				return "失败";
			case 1:
				return "成功";
			case 2:
				return "大成功";
			default:
				return Integer.toString(this.state);
		}
	}

	public String getArea() {
		return this.area;
	}

	public String getName() {
		return this.name;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public MissionResultItem[] getItems() {
		return this.items;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public class MissionResultItem extends AbstractMemory {
		private static final long serialVersionUID = 1L;
		private final int flag;
		private final int id;
		private final String name;
		private final int count;

		public MissionResultItem(JsonObject json, int flag) {
			this.flag = flag;
			this.id = json.getInt("api_useitem_id");
			this.name = json.getString("api_useitem_name", null);
			this.count = json.getInt("api_useitem_count");
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			switch (this.flag) {
				case 0:
					return "";
				case 1:
					return "高速修复材";
				case 2:
					return "高速建造材";
				case 3:
					return "开发资材";
				case 4:
					return this.name;
			}
			return this.flag + ":" + this.id + ":" + this.name;
		}

		public int getCount() {
			return this.count;
		}
	}
}
