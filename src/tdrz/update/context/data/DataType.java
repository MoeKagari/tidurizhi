package tdrz.update.context.data;

import java.util.HashMap;
import java.util.Map;

import tool.function.FunctionUtils;

public enum DataType {
	//start battle相关
	BATTLE_GOBACK_PORT("/kcsapi/api_req_combined_battle/goback_port", "退避"),
	BATTLE_START_AIR_BASE("/kcsapi/api_req_map/start_air_base", "设置陆航去处"),

	BATTLE_PRACTICE_DAY("/kcsapi/api_req_practice/battle", "演习昼战"),
	BATTLE_PRACTICE_MIDNIGHT("/kcsapi/api_req_practice/midnight_battle", "演习夜战"),
	BATTLE_PRACTICE_RESULT("/kcsapi/api_req_practice/battle_result", "演习结果"),

	BATTLE_START("/kcsapi/api_req_map/start", "舰队出击"),
	BATTLE_SHIPDECK("/kcsapi/api_get_member/ship_deck", "战斗结束后的deck刷新", "result 与 next 之间"),
	BATTLE_NEXT("/kcsapi/api_req_map/next", "战斗结束后的下一点"),

	BATTLE_AIRBATTLE("/kcsapi/api_req_sortie/airbattle", "空袭战", "航空战可对敌方造成伤害,可进入夜战"),
	BATTLE_AIRBATTLE_LD("/kcsapi/api_req_sortie/ld_airbattle", "长距离空袭战", "不可对敌方造成任何伤害(陆基例外),不可进入夜战"),
	BATTLE_DAY("/kcsapi/api_req_sortie/battle", "昼战"),
	BATTLE_MIDNIGHT("/kcsapi/api_req_battle_midnight/battle", "夜战"),
	BATTLE_MIDNIGHT_SP("/kcsapi/api_req_battle_midnight/sp_midnight", "开幕夜战"),
	BATTLE_RESULT("/kcsapi/api_req_sortie/battleresult", "战斗结果"),

	COMBINEBATTLE_AIRBATTLE("/kcsapi/api_req_combined_battle/airbattle", "联合舰队-空袭战"),
	COMBINEBATTLE_AIRBATTLE_LD("/kcsapi/api_req_combined_battle/ld_airbattle", "联合舰队-长距离空袭战"),
	COMBINEBATTLE_MIDNIGHT_SP("/kcsapi/api_req_combined_battle/sp_midnight", "联合舰队-开幕夜战"),
	COMBINEBATTLE_RESULT("/kcsapi/api_req_combined_battle/battleresult", "联合舰队-战斗结果"),
	COMBINEBATTLE_DAY("/kcsapi/api_req_combined_battle/battle", "昼战-12vs6-机动"),
	COMBINEBATTLE_DAY_WATER("/kcsapi/api_req_combined_battle/battle_water", "昼战-12vs6-水打"),
	COMBINEBATTLE_MIDNIGHT("/kcsapi/api_req_combined_battle/midnight_battle", "夜战-12vs6"),
	COMBINEBATTLE_EC_DAY("/kcsapi/api_req_combined_battle/ec_battle", "昼战-6vs12"),
	COMBINEBATTLE_EACH_DAY("/kcsapi/api_req_combined_battle/each_battle", "昼战-12vs12-机动"),
	COMBINEBATTLE_EACH_DAY_WATER("/kcsapi/api_req_combined_battle/each_battle_water", "昼战-12vs12-水打"),
	COMBINEBATTLE_EC_MIDNIGHT("/kcsapi/api_req_combined_battle/ec_midnight_battle", "夜战-6vs12-12vs12"),
	//end

	AIRBASE_INFORMATION("/kcsapi/api_get_member/base_air_corps", "基地航空队-信息"),
	AIRBASE_SETPLANE("/kcsapi/api_req_air_corps/set_plane", "基地航空队-设置飞机"),
	AIRBASE_CHANGENAME("/kcsapi/api_req_air_corps/change_name", "基地航空队-变更队名"),
	AIRBASE_EXPAND("/kcsapi/api_req_air_corps/expand_base", "基地航空队-扩张"),
	AIRBASE_SUPPLY("/kcsapi/api_req_air_corps/supply", "陆航补给"),

	SORTIE_CONDITIONS("/kcsapi/api_get_member/sortie_conditions", "活动海域的出击条件", "点击决定按钮与出现[选择出击舰队]界面之间,两次返回母港之间只会发起一次"),
	MXLTVKPYUKLH("/kcsapi/api_req_ranking/mxltvkpyuklh", "战果排行list"),
	UPDATECOMMENT("/kcsapi/api_req_member/updatecomment", "变更签名"),
	GET_INCENTIVE("/kcsapi/api_req_member/get_incentive", "道具奖励(启动时)(比如战果奖励)"),
	UNSETSLOT("/kcsapi/api_get_member/unsetslot", "未知-UNSETSLOT"),

	USEITEM("/kcsapi/api_get_member/useitem", "所有道具(普通道具栏)"),
	ITEMUSE("/kcsapi/api_req_member/itemuse", "使用道具(普通道具栏)"),
	PAYCHECK("/kcsapi/api_dmm_payment/paycheck", "购买道具是否成功"),
	PAYITEM("/kcsapi/api_get_member/payitem", "氪金道具(未取出)"),
	PAYITEM_USE("/kcsapi/api_req_member/payitemuse", "使用氪金道具(取出到普通道具栏???)"),
	RECORD("/kcsapi/api_get_member/record", "战绩"),
	PICTURE_BOOK("/kcsapi/api_get_member/picture_book", "图鉴"),
	MUSIC_LIST("/kcsapi/api_req_furniture/music_list", "点唱机list"),
	MUSIC_PLAY("/kcsapi/api_req_furniture/music_play", "点唱机play"),
	RADIO_PLAY("/kcsapi/api_req_furniture/radio_play", "母港bgm的??"),
	SET_PORTBGM("/kcsapi/api_req_furniture/set_portbgm", "母港bgm设定"),
	FURNITURE_BUY("/kcsapi/api_req_furniture/buy", "购买家具"),
	FURNITURE_CHANGE("/kcsapi/api_req_furniture/change", "更换家具"),
	EVENTMAP_RANK_SELECT("/kcsapi/api_req_map/select_eventmap_rank", "选择活动难度"),

	MASTERDATA("/kcsapi/api_start2", "游戏的masterdata"),
	REQUIRE_INFO("/kcsapi/api_get_member/require_info", "进入游戏时的一堆数据"),
	COMBINED("/kcsapi/api_req_hensei/combined", "联合舰队结成"),
	MAPINFO("/kcsapi/api_get_member/mapinfo", "地图与路基详情"),
	SHIP2("/kcsapi/api_get_member/ship2", "KAISOU_MARRIAGE 和 ITEMUSE_COND 之后刷新ship和deck"),

	MATERIAL("/kcsapi/api_get_member/material", "资源"),
	PORT("/kcsapi/api_port/port", "返回母港"),
	BASIC("/kcsapi/api_get_member/basic", "司令部数据集"),
	SLOT_ITEM("/kcsapi/api_get_member/slot_item", "所有装备"),

	CHARGE("/kcsapi/api_req_hokyu/charge", "补给"),
	CREATEITEM("/kcsapi/api_req_kousyou/createitem", "开发装备"),
	DESTROYSHIP("/kcsapi/api_req_kousyou/destroyship", " 解体"),
	DESTROYITEM("/kcsapi/api_req_kousyou/destroyitem2", "废弃"),

	DECK_PRESET_DECK("/kcsapi/api_get_member/preset_deck", "编成列表"),
	DECK_PRESET_REGISTER("/kcsapi/api_req_hensei/preset_register", "记录编成到编成列表"),
	DECK_PRESET_DELETE("/kcsapi/api_req_hensei/preset_delete", "删除编成记录"),
	DECK_PRESET_SELECT("/kcsapi/api_req_hensei/preset_select", "展开编成"),

	REMODEL_SLOTLIST("/kcsapi/api_req_kousyou/remodel_slotlist", "进入改修工厂"),
	REMODEL_SLOTLIST_DETAIL("/kcsapi/api_req_kousyou/remodel_slotlist_detail", "选择改修装备之后到执行界面"),
	REMODEL_SLOT("/kcsapi/api_req_kousyou/remodel_slot", "改修执行"),

	KAISOU_POWERUP("/kcsapi/api_req_kaisou/powerup", "近代化改修"),
	KAISOU_SLOTSET("/kcsapi/api_req_kaisou/slotset", "更换装备(有到无,无到有,有到有,三种)"),
	KAISOU_UNSETSLOT_ALL("/kcsapi/api_req_kaisou/unsetslot_all", "装备全解除"),
	KAISOU_OPEN_EXSLOT("/kcsapi/api_req_kaisou/open_exslot", "开启ex装备槽"),
	KAISOU_SLOTSET_EX("/kcsapi/api_req_kaisou/slotset_ex", "装备ex装备"),
	KAISOU_SLOT_EXCHANGE("/kcsapi/api_req_kaisou/slot_exchange_index", "交换装备位置"),
	KAISOU_SLOT_DEPRIVE("/kcsapi/api_req_kaisou/slot_deprive", "从另一位舰娘身上交换装备"),
	KAISOU_REMODELING("/kcsapi/api_req_kaisou/remodeling", "舰娘改造"),
	KAISOU_SLOTITEM_LOCK("/kcsapi/api_req_kaisou/lock", "装备lock"),
	KAISOU_MARRIAGE("/kcsapi/api_req_kaisou/marriage", "marriage"),
	KAISOU_SHIP3("/kcsapi/api_get_member/ship3", "执行一些kaisou_api之后刷新decks和一些ship"),

	QUEST_LIST("/kcsapi/api_get_member/questlist", "任务列表"),
	QUEST_CLEAR("/kcsapi/api_req_quest/clearitemget", "完成任务"),
	QUEST_START("/kcsapi/api_req_quest/start", "开始任务"),
	QUEST_STOP("/kcsapi/api_req_quest/stop", "中止任务"),

	PRACTICE_LIST("/kcsapi/api_get_member/practice", "演习列表"),
	PRACTICE_ENEMYINFO("/kcsapi/api_req_member/get_practice_enemyinfo", "演习对象信息"),
	PRACTICE_CHANGE_MATCHING_KIND("/kcsapi/api_req_practice/change_matching_kind", "更换演习群"),

	MISSION("/kcsapi/api_get_member/mission", "远征列表"),
	MISSION_START("/kcsapi/api_req_mission/start", "远征开始"),
	MISSION_RESULT("/kcsapi/api_req_mission/result", "远征结果"),
	MISSION_RETURN("/kcsapi/api_req_mission/return_instruction", "远征中止归还"),

	DECK("/kcsapi/api_get_member/deck", "当前的舰队编成"),
	DECK_SHIP_LOCK("/kcsapi/api_req_hensei/lock", "舰娘lock"),
	DECK_CHANGE("/kcsapi/api_req_hensei/change", "改变编成"),
	DECK_UPDATEDECKNAME("/kcsapi/api_req_member/updatedeckname", "变更编成名"),
	DECK_ITEMUSE_COND("/kcsapi/api_req_member/itemuse_cond", "給粮舰道具使用", "給粮舰道具使用"),

	NDOCK("/kcsapi/api_get_member/ndock", "当前的入渠列表"),
	NDOCK_NYUKYO_START("/kcsapi/api_req_nyukyo/start", "入渠开始"),
	NDOCK_NYUKYO_SPEEDCHANGE("/kcsapi/api_req_nyukyo/speedchange", "使用高速修复材"),

	KDOCK("/kcsapi/api_get_member/kdock", "当前的建造列表"),
	KDOCK_CREATESHIP("/kcsapi/api_req_kousyou/createship", "建造开始"),
	KDOCK_SPEEDCHANGE("/kcsapi/api_req_kousyou/createship_speedchange", "使用高速建造材"),
	KDOCK_GETSHIP("/kcsapi/api_req_kousyou/getship", "建造获得舰娘");

	/*--------------------------------------------------------------------------------------------------------------*/
	private static final Map<String, DataType> TYPEMAP = new HashMap<>();
	static {
		FunctionUtils.forEach(DataType.values(), type -> TYPEMAP.put(type.uri, type));
	}

	public static DataType getType(String uri) {
		return TYPEMAP.get(uri);
	}

	private final String uri;
	private final String detail;
	private final String tip;

	private DataType(String uri, String detail) {
		this(uri, detail, null);
	}

	private DataType(String uri, String detail, String tip) {
		this.uri = uri;
		this.detail = detail;
		this.tip = tip;
	}

	public String getDetail() {
		return this.detail;
	}

	public String getTip() {
		return this.tip == null ? this.uri : String.format("%s\n%s", this.tip, this.uri);
	}
}
