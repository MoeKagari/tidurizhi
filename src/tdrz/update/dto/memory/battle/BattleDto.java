package tdrz.update.dto.memory.battle;

import tdrz.core.logic.TimeString;
import tdrz.update.dto.AbstractMemory;

/**
 * 出击之后到回港之前所有dto的超类
 * @author MoeKagari
 */
public abstract class BattleDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;

	public static String getSearch(int id) {
		switch (id) {
			case 1:
				return "成功(无损)";
			case 2://有未归还
				return "成功(有损)";
			case 3://全部未归还
				return "成功(全损)";
			case 4:
				return "失败(有)";
			case 5://没有舰载机
				return "成功(无)";
			case 6://没有舰载机
				return "失败(无)";
			default:
				return Integer.toString(id);
		}
	}

	public static String getHangxiang(int id) {
		switch (id) {
			case 1:
				return "同航战";
			case 2:
				return "反航战";
			case 3:
				return "T有利";
			case 4:
				return "T不利";
			default:
				return Integer.toString(id);
		}
	}

	public static String getZhenxin(int id) {
		switch (id) {
			case 1:
				return "单纵阵";
			case 2:
				return "复纵阵";
			case 3:
				return "轮型阵";
			case 4:
				return "梯形阵";
			case 5:
				return "单横阵";
			case 11:
				return "第一警戒航行序列";
			case 12:
				return "第二警戒航行序列";
			case 13:
				return "第三警戒航行序列";
			case 14:
				return "第四警戒航行序列";
			default:
				return Integer.toString(id);
		}
	}

	public static String getSeiku(int id) {
		switch (id) {
			case 0:
				return "制空均衡";
			case 1:
				return "制空确保";
			case 2:
				return "制空优势";
			case 3:
				return "制空劣势";
			case 4:
				return "制空丧失";
			default:
				return Integer.toString(id);
		}
	}

	public static String getNextPointType(int nextEventId, int nextEventKind) {
		switch (nextEventId) {
			case 2:
				switch (nextEventKind) {
					case 0:
						return "资源";
				}
				break;
			case 3:
				switch (nextEventKind) {
					case 0:
						return "渦潮";
				}
				break;
			case 4:
				switch (nextEventKind) {
					case 1:
						return "通常战斗";
					case 2:
						return "夜战";
					case 4:
						return "航空战";
					case 5:
						return "通常战斗(联合舰队)";
					case 6:
						return "空袭战";
				}
				break;
			case 5:
				switch (nextEventKind) {
					case 1:
						return "BOSS";
					case 5:
						return "BOSS(联合舰队)";
				}
				break;
			case 6:
				switch (nextEventKind) {
					case 0:
						return "気のせいだった";
					case 1:
						return "敵影を見ず";
					case 2:
						return "能动分歧";
					case 3:
						return "穏やかな海です";
					case 4:
						return "穏やかな海峡です";
					case 5:
						return "警戒が必要です";
					case 6:
						return "静かな海です";
				}
				break;
			case 7:
				switch (nextEventKind) {
					case 0:
						return "航空侦察";
				}
				break;
			case 8:
				switch (nextEventKind) {
					case 0:
						return "船团护卫成功";
				}
				break;
			case 9:
				switch (nextEventKind) {
					case 0:
						return "扬陆地点";
				}
				break;
		}

		return nextEventId + "-" + nextEventKind;
	};

	public static String getRank(String rank) {
		switch (rank) {
			case "S":
				return "S胜利";
			case "A":
				return "A胜利";
			case "B":
				return "B战术的胜利";
			case "C":
				return "C战术的败北";
			case "D":
				return "D败北";
			case "E":
				return "E败北";
			default:
				return rank;
		}
	}

	public static String getSupportType(int type) {
		switch (type) {
			case 1:
				return "航空支援";
			case 2:
				return "炮击支援";
			case 3:
				return "雷击支援";
			default:
				return "新支援类型:" + type;
		}
	}

	/*-----------------------------------------------------------------------------*/
	private final long time = TimeString.getCurrentTime();

	@Override
	public long getTime() {
		return this.time;
	}

	/** 演习? */
	public boolean isPractice() {
		return false;
	}

	/** 起点? */
	public boolean isStart() {
		return false;
	}
}
