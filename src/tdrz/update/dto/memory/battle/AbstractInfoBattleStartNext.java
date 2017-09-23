package tdrz.update.dto.memory.battle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.core.util.JsonUtils;
import tdrz.update.context.data.ApiData;

public abstract class AbstractInfoBattleStartNext extends AbstractInfoBattle {
	private static final long serialVersionUID = 1L;
	private final int mapareaId;
	private final int mapareaNo;
	private final int next;
	private final int nextEventId;
	private final int nextEventKind;
	private final int nextCount;

	private List<GetItem> items = null;
	private DestructionBattle destructionBattle = null;
	private int[] airsearch = null;//[侦察机种类,侦察结果]
	private int[] happening = null;//[涡潮种类,有无电探,掉落量]

	public AbstractInfoBattleStartNext(ApiData data, JsonObject json) {
		super(data.getTime());

		this.mapareaId = json.getInt("api_maparea_id");
		this.mapareaNo = json.getInt("api_mapinfo_no");
		this.next = json.getInt("api_no");
		this.nextEventId = json.getInt("api_event_id");
		this.nextEventKind = json.getInt("api_event_kind");
		this.nextCount = json.getInt("api_next");

		//获得的道具
		if (json.containsKey("api_itemget")) {
			JsonValue api_itemget = json.get("api_itemget");
			if (api_itemget instanceof JsonArray) {//资源点
				this.items = ((JsonArray) api_itemget).stream().map(GetItem::new).collect(Collectors.toList());
			} else if (api_itemget instanceof JsonObject) {//航空侦察点
				this.items = new ArrayList<>();
				this.items.add(new GetItem(api_itemget));
			}
		}

		//基地受损
		if (json.containsKey("api_destruction_battle")) {
			this.destructionBattle = new DestructionBattle(json.getJsonObject("api_destruction_battle"));
		}

		if (json.containsKey("api_airsearch")) {
			JsonObject api_airsearch = json.getJsonObject("api_airsearch");
			int kind = api_airsearch.getInt("api_plane_type");
			int result = api_airsearch.getInt("api_result");
			if (kind == 0 && result == 0) {
				//
			} else {
				this.airsearch = new int[] { kind, result };
			}
		}

		//涡潮
		if (json.containsKey("api_happening")) {
			JsonObject api_happening = json.getJsonObject("api_happening");
			this.happening = new int[] { api_happening.getInt("api_mst_id"), api_happening.getInt("api_dentan"), api_happening.getInt("api_count") };
		}

		/*  (1-6) 
		  "api_get_eo_rate": 75,
		  "api_itemget_eo_result": {
		  	"api_usemst": 5, 
		  	"api_id": 60, 
		  	"api_getcount": 1 
		  },
		  
		  "api_itemget_eo_comment": { 
		  	"api_usemst": 4, 
		  	"api_id": 1,
		  	"api_getcount": 1000 
		  }
		 */
	}

	public String[] getHappening() {
		IntFunction<String> getKind = kind -> {
			switch (kind) {
				case 1:
					return "油";
				case 2:
					return "弹";
			}
			return String.valueOf(kind);
		};
		return this.happening == null ? null : new String[] { getKind.apply(this.happening[0]), this.happening[1] == 1 ? "有" : "无", String.valueOf(this.happening[2]) };
	}

	public String[] getAirsearch() {
		IntFunction<String> getKind = kind -> {
			switch (kind) {
				case 0:
					return "无";
				case 1:
					return "大型飞行艇";
				case 2:
					return "水上侦察机";
			}
			return String.valueOf(kind);
		};
		IntFunction<String> getResult = result -> {
			switch (result) {
				case 0:
					return "失败";
				case 1:
					return "成功";
				case 2:
					return "大成功";
			}
			return String.valueOf(result);
		};
		return this.airsearch == null ? null : new String[] { getKind.apply(this.airsearch[0]), getResult.apply(this.airsearch[1]) };
	}

	public List<GetItem> getItems() {
		return this.items;
	}

	public DestructionBattle getDestructionBattle() {
		return this.destructionBattle;
	}

	public boolean isBoss() {
		return this.nextEventId == 5;
	}

	public boolean isGoal() {
		return this.nextCount == 0;
	}

	public int getNext() {
		return this.next;
	}

	public String getNextType() {
		return BattleDto.getNextPointType(this.nextEventId, this.nextEventKind);
	}

	public int getMapareaId() {
		return this.mapareaId;
	}

	public int getMapareaNo() {
		return this.mapareaNo;
	}

	public String getMapString() {
		return this.mapareaId + "-" + this.mapareaNo;
	}

	public class GetItem implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int id;
		private final int count;

		public GetItem(JsonValue value) {
			JsonObject json = (JsonObject) value;
			this.id = json.getInt("api_id");
			this.count = json.getInt("api_getcount");
		}

		@Override
		public String toString() {
			return this.getItemString() + "-" + this.count;
		}

		private String getItemString() {
			switch (this.id) {
				case 1:
					return "油";
				case 2:
					return "弹";
				case 3:
					return "钢";
				case 4:
					return "铝";
				case 5:
					return "高速建造材";
				case 6:
					return "高速修复材";
				case 7:
					return "开发资材";
				case 11:
					return "家具箱(中)";
				case 12:
					return "家具箱(大)";
				default:
					return String.valueOf(this.id);
			}
		}
	}

	public class DestructionBattle implements Serializable {
		private static final long serialVersionUID = 1L;
		public final int[] nowhps;
		public final int[] maxhps;
		public final int[] dmgs;

		private final int lostKind;
		private final Integer seiku;

		public DestructionBattle(JsonObject json) {
			this.nowhps = Arrays.copyOfRange(JsonUtils.getIntArray(json, "api_nowhps"), 1, 7);
			this.maxhps = Arrays.copyOfRange(JsonUtils.getIntArray(json, "api_maxhps"), 1, 7);
			this.lostKind = json.getInt("api_lost_kind");

			JsonObject attack = json.getJsonObject("api_air_base_attack");
			int[] flags = JsonUtils.getIntArray(attack, "api_stage_flag");
			if (flags[0] == 1) {
				this.seiku = attack.getJsonObject("api_stage1").getInt("api_disp_seiku");
			} else {
				this.seiku = null;
			}
			if (flags[2] == 1) {
				this.dmgs = Arrays.copyOfRange(JsonUtils.getIntArray(attack.getJsonObject("api_stage3"), "api_fdam"), 1, 7);
			} else {
				this.dmgs = new int[6];
			}
		}

		public int[] getBefore() {
			return this.nowhps;
		}

		public int[] getAfter() {
			int[] after = new int[6];
			for (int i = 0; i < 6; i++) {
				if (this.nowhps[i] != -1) {
					after[i] = this.nowhps[i] - this.dmgs[i];
				} else {
					after[i] = -1;
				}
			}
			return after;
		}

		public int getBaseNumber() {
			return Arrays.stream(this.maxhps).filter(i -> i != -1).map(i -> 1).sum();
		}

		public String getSeiku() {
			return this.seiku == null ? null : BattleDto.getSeiku(this.seiku);
		}

		public String getLostKind() {
			switch (this.lostKind) {
				case 1:
					return "空襲により備蓄資源に損害を受けました！";
				case 2:
					return "空襲により備蓄資源に損害を受け\n基地航空隊にも地上撃破の損害が発生しました！";
				case 3:
					return "空襲により基地航空隊に地上撃破の損害が発生しました！";
				case 4:
					return "空襲による基地の損害はありません。";
			}
			return "基地受损显示种类: " + this.lostKind;
		}
	}
}
