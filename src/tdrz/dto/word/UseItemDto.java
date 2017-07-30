package tdrz.dto.word;

import javax.json.JsonObject;

import tdrz.dto.AbstractWord;
import tdrz.dto.translator.MasterDataTranslator;
import tdrz.dto.word.MasterDataDto.MasterUserItemDto;

/**
 * 道具栏内的道具,除了几个特殊(比如损管,女神,etc)
 * @author MoeKagari
 */
public class UseItemDto extends AbstractWord {
	private int id;
	private int count;
	private MasterUserItemDto masterData;

	public UseItemDto(JsonObject json) {
		this.id = json.getInt("api_id");
		this.count = json.getInt("api_count");
		this.masterData = MasterDataTranslator.getMasterUserItemDto(this.id);
	}

	public MasterUserItemDto getMasterData() {
		if (this.masterData == null) {
			this.masterData = MasterDataTranslator.getMasterUserItemDto(this.id);
		}
		return this.masterData;
	}

	public int getId() {
		return this.id;
	}

	public int getCount() {
		return this.count;
	}
}
