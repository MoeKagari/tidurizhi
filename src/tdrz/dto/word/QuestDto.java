package tdrz.dto.word;

import javax.json.JsonObject;

import tdrz.dto.AbstractWord;
import tdrz.utils.JsonUtils;

/**
 * 任务
 * @author MoeKagari
 */
public class QuestDto extends AbstractWord {
	private final int page;
	private final int no;
	private final QuestInformationDto information;

	public QuestDto(int page, int no, JsonObject json) {
		this.page = page;
		this.no = no;
		this.information = new QuestInformationDto(json);
	}

	public int getPage() {
		return this.page;
	}

	public int getNo() {
		return this.no;
	}

	public QuestInformationDto getInformation() {
		return this.information;
	}

	public class QuestInformationDto {
		private final JsonObject json;

		public QuestInformationDto(JsonObject json) {
			this.json = json;
		}

		public int getNo() {
			return this.json.getInt("api_no");
		}

		public int getType() {
			return this.json.getInt("api_type");
		}

		public String getTypeString() {
			int type = this.getType();
			switch (type) {
				case 1:
					return "日常";
				case 2:
					return "周常";
				case 3:
					return "月常";
				case 4:
					return "单次";
				case 5:
					return "其它";
				default:
					return Integer.toString(type);
			}
		}

		public int getCategory() {
			return this.json.getInt("api_category");
		}

		public String getCategoryString() {
			int category = this.getCategory();
			switch (category) {
				case 1:
					return "编成";
				case 2:
					return "出击";
				case 3:
					return "演习";
				case 4:
					return "远征";
				case 5:
					return "补给/入渠";
				case 6:
					return "工厂";
				case 7:
					return "改装";
				case 8:
					return "出击(新)";
				case 9:
					return "其它";
				default:
					return Integer.toString(category);
			}
		}

		public int getState() {
			return this.json.getInt("api_state");
		}

		public String getStateString() {
			int state = this.getState();
			switch (state) {
				case 1:
					return "";
				case 2:
					return "进行";
				case 3:
					return "达成";
				default:
					return Integer.toString(state);
			}
		}

		public String getTitle() {
			return this.json.getString("api_title");
		}

		public String getDetail() {
			return this.json.getString("api_detail");
		}

		public int[] getMaterial() {
			return JsonUtils.getIntArray(this.json, "api_get_material");
		}

		public String getProcess() {
			switch (this.json.getInt("api_progress_flag")) {
				case 1:
					return "50%";
				case 2:
					return "80%";
				default:
					return "";
			}
		}
	}
}
