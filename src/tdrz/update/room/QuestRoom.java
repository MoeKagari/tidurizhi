package tdrz.update.room;

import java.util.Collections;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import tdrz.dto.word.QuestDto;
import tdrz.update.GlobalContext;
import tdrz.update.data.ApiData;

public class QuestRoom {
	private int count;//总任务数
	private int pageCount;//总页数
	private int execCount;//正在执行的任务数

	public void doQuestList(ApiData data, JsonValue api_data) {
		JsonObject json = (JsonObject) api_data;

		this.count = json.getInt("api_count");
		this.pageCount = json.getInt("api_page_count");
		this.execCount = json.getInt("api_exec_count");

		JsonValue value = json.get("api_list");
		int currentPage = json.getInt("api_disp_page");
		int tab_id = Integer.parseInt(data.getField("api_tab_id"));
		//进行差分处理
		//未完善 TODO
		if (value instanceof JsonArray) {
			JsonArray array = (JsonArray) value;
			for (int no = 0; no < array.size(); no++) {
				JsonValue questValue = array.get(no);
				if (questValue instanceof JsonObject) {
					QuestDto quest = new QuestDto(currentPage, no, (JsonObject) questValue);
					//移除quest_no相同或者(page和no)相同的任务
					GlobalContext.getQuestlist().removeIf(ele -> (ele.getInformation().getNo() == quest.getInformation().getNo() || (ele.getPage() == quest.getPage() && ele.getNo() == quest.getNo())));
					GlobalContext.getQuestlist().add(quest);
				} else if (questValue instanceof JsonNumber) {
					//类别最后一页空位为-1
					//清除之后的任务
					//TODO
				}
			}
		} else if (value instanceof JsonNumber) {
			//空页为-1,清除当前类别的所有任务
			if (tab_id == 0) {//所有任务
				GlobalContext.getQuestlist().clear();
			} else if (tab_id == 9) {//进行中得任务
				GlobalContext.getQuestlist().removeIf(quest -> quest.getInformation().getState() == 2);
			} else {
				GlobalContext.getQuestlist().removeIf(quest -> quest.getInformation().getType() == tab_id);
			}
		} else if (value == JsonValue.NULL) {
			//最后页只有一个任务,完成任务后,重新获取任务列表时,此项为null
			//此时清除存储的当前页以及以后页的任务
			GlobalContext.getQuestlist().removeIf(quest -> quest.getPage() >= currentPage);
		} else {
			throw new RuntimeException("有未完成的case\n" + api_data);
		}

		//按照no排序
		Collections.sort(GlobalContext.getQuestlist(), (a, b) -> Integer.compare(a.getInformation().getNo(), b.getInformation().getNo()));
	}

	/*---------------------------------------------------------------------------------------------------------------------------*/

	public int getCount() {
		return this.count;
	}

	public int getExecCount() {
		return this.execCount;
	}

	public int getPageCount() {
		return this.pageCount;
	}
}
