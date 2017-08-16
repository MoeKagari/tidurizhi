package tdrz.update.dto.word;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import tdrz.core.util.JsonUtils;
import tdrz.update.dto.AbstractWord;

public class MasterDataDto extends AbstractWord {
	private final JsonObject json;
	private final Map<Integer, MasterShipDto> masterShipDataMap = new HashMap<>();
	private final Map<Integer, MasterSlotitemDto> masterSlotitemDataMap = new HashMap<>();
	private final Map<Integer, MasterMissionDto> masterMissionDataMap = new HashMap<>();
	private final Map<Integer, MasterUserItemDto> masterUserItemDtoMap = new HashMap<>();

	public MasterDataDto(JsonObject json) {
		this.json = json;
		json.getJsonArray("api_mst_ship").getValuesAs(JsonObject.class).stream().map(MasterShipDto::new).forEach(md -> this.masterShipDataMap.put(md.getId(), md));
		json.getJsonArray("api_mst_slotitem").getValuesAs(JsonObject.class).stream().map(MasterSlotitemDto::new).forEach(md -> this.masterSlotitemDataMap.put(md.getId(), md));
		json.getJsonArray("api_mst_mission").getValuesAs(JsonObject.class).stream().map(MasterMissionDto::new).forEach(md -> this.masterMissionDataMap.put(md.getId(), md));
		json.getJsonArray("api_mst_useitem").getValuesAs(JsonObject.class).stream().map(MasterUserItemDto::new).forEach(md -> this.masterUserItemDtoMap.put(md.getId(), md));
	}

	public JsonObject getJson() {
		return this.json;
	}

	public Map<Integer, MasterShipDto> getMasterShipDataMap() {
		return this.masterShipDataMap;
	}

	public Map<Integer, MasterSlotitemDto> getMasterSlotitemDataMap() {
		return this.masterSlotitemDataMap;
	}

	public Map<Integer, MasterMissionDto> getMasterMissionDataMap() {
		return this.masterMissionDataMap;
	}

	public Map<Integer, MasterUserItemDto> getMasterUserItemDtoMap() {
		return this.masterUserItemDtoMap;
	}

	public class MasterShipDto {
		private final JsonObject json;

		public MasterShipDto(JsonObject json) {
			this.json = json;
		}

		public int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}

		public int getGaizhaoLv() {
			return this.json.getInt("api_afterlv");
		}

		public int getGaizhaoAfterId() {
			return Integer.parseInt(this.json.getString("api_aftershipid"));
		}

		public int[] getTaik() {
			return JsonUtils.getIntArray(this.json, "api_taik");
		}

		public int[] getSouk() {
			return JsonUtils.getIntArray(this.json, "api_souk");
		}

		public int[] getHoug() {
			return JsonUtils.getIntArray(this.json, "api_houg");
		}

		public int[] getRaig() {
			return JsonUtils.getIntArray(this.json, "api_raig");
		}

		public int[] getTyku() {
			return JsonUtils.getIntArray(this.json, "api_tyku");
		}

		public int[] getLuck() {
			return JsonUtils.getIntArray(this.json, "api_luck");
		}

		public int[] getMaxeq() {
			return JsonUtils.getIntArray(this.json, "api_maxeq");
		}

		public int getFuelMax() {
			return this.json.getInt("api_fuel_max");
		}

		public int getBullMax() {
			return this.json.getInt("api_bull_max");
		}

		public int[] getOnslotMax() {
			return JsonUtils.getIntArray(this.json, "api_maxeq");
		}

		public int getType() {
			return this.json.getInt("api_stype");
		}

		public boolean isEnemy() {
			return this.json.size() <= 6;
		}
	}

	public class MasterSlotitemDto {
		private final JsonObject json;

		public MasterSlotitemDto(JsonObject json) {
			this.json = json;
		}

		public int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}

		/** 对潜 */
		public int getTaisen() {
			return this.json.getInt("api_tais");
		}

		/** 对空 */
		public int getTyku() {
			return this.json.getInt("api_tyku");
		}

		/** 索敌 */
		public int getSaku() {
			return this.json.getInt("api_saku");
		}

		/** 五个值 */
		public int[] getType() {
			return JsonUtils.getIntArray(this.json, "api_type");
		}
	}

	public class MasterMissionDto {
		private final JsonObject json;

		public MasterMissionDto(JsonObject json) {
			this.json = json;
		}

		public int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}
	}

	public class MasterUserItemDto {
		private final JsonObject json;

		public MasterUserItemDto(JsonObject json) {
			this.json = json;
		}

		private int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}

		/** 长度为2 */
		public String[] getDescription() {
			return JsonUtils.getStringArray(this.json, "api_description");
		}
	}
}
