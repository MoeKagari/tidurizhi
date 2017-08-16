package tdrz.update.dto.word;

import javax.json.JsonObject;

import tdrz.update.dto.AbstractWord;

/**
 * 提督的一些游戏数据
 * @author MoeKagari
 */
public class BasicDto extends AbstractWord {
	private final JsonObject json;

	public BasicDto(JsonObject json) {
		this.json = json;
	}

	public String getUserName() {
		return this.json.getString("api_nickname");
	}

	public int getUserLevel() {
		return this.json.getInt("api_level");
	}

	public int getMaxChara() {
		return this.json.getInt("api_max_chara");
	}

	public int getMaxSlotItem() {
		return this.json.getInt("api_max_slotitem");
	}

	public int getDeckLength() {
		return this.json.getInt("api_count_deck");
	}

	public int getKdockLength() {
		return this.json.getInt("api_count_kdock");
	}

	public int getNdockLength() {
		return this.json.getInt("api_count_ndock");
	}
}
