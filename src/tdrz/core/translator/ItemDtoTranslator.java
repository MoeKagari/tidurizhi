package tdrz.core.translator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import tdrz.core.translator.ItemDtoTranslator.ItemDataMap.ItemData;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.MasterDataDto.MasterSlotitemDto;
import tool.FunctionUtils;

public class ItemDtoTranslator {
	public static String getName(ItemDto item) {
		if (item == null) return "";
		return FunctionUtils.notNull(item.getMasterData(), MasterSlotitemDto::getName, "");
	}

	public static int[] getType(ItemDto item) {
		if (item == null) return null;
		return FunctionUtils.notNull(item.getMasterData(), MasterSlotitemDto::getType, null);
	}

	public static String getTypeString(ItemDto item) {
		if (item == null) return "";
		return FunctionUtils.notNull(item.getMasterData(), md -> Arrays.toString(md.getType()), "");
	}

	public static String getNameWithLevel(ItemDto item) {
		if (item == null) return "";
		int star = item.getLevel();
		int alv = item.getAlv();
		return String.format("%s%s%s", getName(item), alv > 0 ? (" 熟" + alv) : "", star > 0 ? (" ★" + star) : "");
	}

	public static char getOneWordName(ItemDto item) {
		ItemData itemData = ItemDataMap.get(item.getSlotitemId());
		return itemData == null ? ' ' : itemData.getOneWordName();
	}

	public static int getTaisen(int id) {
		return getTaisen(GlobalContext.getItem(id));
	}

	public static int getTaisen(ItemDto item) {
		return Optional.ofNullable(item).map(ItemDto::getMasterData).map(MasterSlotitemDto::getTaisen).orElse(0);
	}

	public static int getSuodi(int id) {
		return getSuodi(GlobalContext.getItem(id));
	}

	public static int getSuodi(ItemDto item) {
		int suodi = 0;
		if (item == null) return suodi;

		//TODO

		return suodi;
	}

	public static int getZhikong(int id, int count) {
		return getZhikong(GlobalContext.getItem(id), count);
	}

	public static int getZhikong(ItemDto item, int count) {
		int zhikong = 0;
		if (item == null) return zhikong;

		MasterSlotitemDto msd = item.getMasterData();
		if (msd != null) {
			int[] type = msd.getType();

			//自带对空(含改修)
			int lv = item.getLevel();
			if (type[0] == 3 && type[1] == 5 && type[2] == 6 && type[3] == 6) {//舰战
				zhikong += Math.floor((msd.getTyku() + 0.2 * lv) * Math.sqrt(count));
			}
			if (type[0] == 3 && type[1] == 5 && type[2] == 7 && type[3] == 7) {//舰爆
				if (type[4] == 12) {//爆战
					zhikong += Math.floor((msd.getTyku() + 0.25 * lv) * Math.sqrt(count));
				} else {
					zhikong += Math.floor(msd.getTyku() * Math.sqrt(count));
				}
			}
			if ((type[0] == 3 && type[1] == 5 && type[2] == 8 && type[3] == 8) ||//舰攻
					(type[0] == 5 && type[1] == 36 && type[2] == 45 && type[3] == 43) ||//水战
					(type[0] == 5 && type[1] == 43 && type[2] == 11 && type[3] == 10) ||//水爆
					(type[0] == 3 && type[1] == 40) //喷气机
			) {
				zhikong += Math.floor(msd.getTyku() * Math.sqrt(count));
			}

			//熟练度制空
			int alv = item.getAlv();
			if (alv >= 0) {
				if ((type[0] == 3 && type[1] == 5 && type[2] == 6 && type[3] == 6) ||//舰战
						(type[0] == 5 && type[1] == 36 && type[2] == 45 && type[3] == 43) //水战
				) {
					zhikong += new int[] { 0, 1, 3, 7, 11, 16, 17, 25 }[alv];
				}
				if (type[0] == 5 && type[1] == 43 && type[2] == 11 && type[3] == 10) {//水爆
					zhikong += new int[] { 0, 1, 2, 3, 3, 5, 6, 9 }[alv];
				}
				if ((type[0] == 3 && type[1] == 5 && type[2] == 7 && type[3] == 7) ||//舰爆
						(type[0] == 3 && type[1] == 5 && type[2] == 8 && type[3] == 8) ||//舰攻
						(type[0] == 3 && type[1] == 40) //喷气机
				) {
					if (alv == 7) {//alv<7时,不知
						zhikong += 3;
					}
				}
			}
		}

		return zhikong;
	}

	public static boolean isRepairItem(int id) {
		return isRepairItem(GlobalContext.getItem(id));
	}

	public static boolean isRepairItem(ItemDto item) {
		if (item == null) return false;
		return item.getSlotitemId() == 86;
	}

	public static class ItemDataMap {
		private static final Map<Integer, ItemData> ITEMDATA = new HashMap<>();

		static {
			ITEMDATA.put(1, new ItemData(1, "12cm単装砲", '小'));
			ITEMDATA.put(2, new ItemData(2, "12.7cm連装砲", '小'));
			ITEMDATA.put(3, new ItemData(3, "10cm連装高角砲", '小'));
			ITEMDATA.put(4, new ItemData(4, "14cm単装砲", '中'));
			ITEMDATA.put(5, new ItemData(5, "15.5cm三連装砲", '中'));
			ITEMDATA.put(6, new ItemData(6, "20.3cm連装砲", '中'));
			ITEMDATA.put(7, new ItemData(7, "35.6cm連装砲", '大'));
			ITEMDATA.put(8, new ItemData(8, "41cm連装砲", '大'));
			ITEMDATA.put(9, new ItemData(9, "46cm三連装砲", '大'));
			ITEMDATA.put(10, new ItemData(10, "12.7cm連装高角砲", '副'));
			ITEMDATA.put(11, new ItemData(11, "15.2cm単装砲", '副'));
			ITEMDATA.put(12, new ItemData(12, "15.5cm三連装副砲", '副'));
			ITEMDATA.put(13, new ItemData(13, "61cm三連装魚雷", '雷'));
			ITEMDATA.put(14, new ItemData(14, "61cm四連装魚雷", '雷'));
			ITEMDATA.put(15, new ItemData(15, "61cm四連装(酸素)魚雷", '雷'));
			ITEMDATA.put(16, new ItemData(16, "九七式艦攻", '攻'));
			ITEMDATA.put(17, new ItemData(17, "天山", '攻'));
			ITEMDATA.put(18, new ItemData(18, "流星", '攻'));
			ITEMDATA.put(19, new ItemData(19, "九六式艦戦", '战'));
			ITEMDATA.put(20, new ItemData(20, "零式艦戦21型", '战'));
			ITEMDATA.put(21, new ItemData(21, "零式艦戦52型", '战'));
			ITEMDATA.put(22, new ItemData(22, "烈風", '战'));
			ITEMDATA.put(23, new ItemData(23, "九九式艦爆", '爆'));
			ITEMDATA.put(24, new ItemData(24, "彗星", '爆'));
			ITEMDATA.put(25, new ItemData(25, "零式水上偵察機", '侦'));
			ITEMDATA.put(26, new ItemData(26, "瑞雲", '爆'));
			ITEMDATA.put(27, new ItemData(27, "13号対空電探", '探'));
			ITEMDATA.put(28, new ItemData(28, "22号対水上電探", '探'));
			ITEMDATA.put(29, new ItemData(29, "33号対水上電探", '探'));
			ITEMDATA.put(30, new ItemData(30, "21号対空電探", '探'));
			ITEMDATA.put(31, new ItemData(31, "32号対水上電探", '探'));
			ITEMDATA.put(32, new ItemData(32, "14号対空電探", '探'));
			ITEMDATA.put(33, new ItemData(33, "改良型艦本式タービン", '缶'));
			ITEMDATA.put(34, new ItemData(34, "強化型艦本式缶", '缶'));
			ITEMDATA.put(35, new ItemData(35, "三式弾", '三'));
			ITEMDATA.put(36, new ItemData(36, "九一式徹甲弾", '彻'));
			ITEMDATA.put(37, new ItemData(37, "7.7mm機銃", '铳'));
			ITEMDATA.put(38, new ItemData(38, "12.7mm単装機銃", '铳'));
			ITEMDATA.put(39, new ItemData(39, "25mm連装機銃", '铳'));
			ITEMDATA.put(40, new ItemData(40, "25mm三連装機銃", '铳'));
			ITEMDATA.put(41, new ItemData(41, "甲標的", '甲'));
			ITEMDATA.put(42, new ItemData(42, "応急修理要員", '要'));
			ITEMDATA.put(43, new ItemData(43, "応急修理女神", '女'));
			ITEMDATA.put(44, new ItemData(44, "九四式爆雷投射機", '投'));
			ITEMDATA.put(45, new ItemData(45, "三式爆雷投射機", '投'));
			ITEMDATA.put(46, new ItemData(46, "九三式水中聴音機", '听'));
			ITEMDATA.put(47, new ItemData(47, "三式水中探信儀", '听'));
			ITEMDATA.put(48, new ItemData(48, "12.7cm単装高角砲", '副'));
			ITEMDATA.put(49, new ItemData(49, "25mm単装機銃", '铳'));
			ITEMDATA.put(50, new ItemData(50, "20.3cm(3号)連装砲", '中'));
			ITEMDATA.put(51, new ItemData(51, "12cm30連装噴進砲", '铳'));
			ITEMDATA.put(52, new ItemData(52, "流星改", '攻'));
			ITEMDATA.put(53, new ItemData(53, "烈風改", '战'));
			ITEMDATA.put(54, new ItemData(54, "彩雲", '彩'));
			ITEMDATA.put(55, new ItemData(55, "紫電改二", '战'));
			ITEMDATA.put(56, new ItemData(56, "震電改", '战'));
			ITEMDATA.put(57, new ItemData(57, "彗星一二型甲", '爆'));
			ITEMDATA.put(58, new ItemData(58, "61cm五連装(酸素)魚雷", '雷'));
			ITEMDATA.put(59, new ItemData(59, "零式水上観測機", '侦'));
			ITEMDATA.put(60, new ItemData(60, "零式艦戦62型(爆戦)", '爆'));
			ITEMDATA.put(61, new ItemData(61, "二式艦上偵察機", '它'));
			ITEMDATA.put(62, new ItemData(62, "試製晴嵐", '爆'));
			ITEMDATA.put(63, new ItemData(63, "12.7cm連装砲B型改二", '小'));
			ITEMDATA.put(64, new ItemData(64, "Ju87C改", '爆'));
			ITEMDATA.put(65, new ItemData(65, "15.2cm連装砲", '副'));
			ITEMDATA.put(66, new ItemData(66, "8cm高角砲", '副'));
			ITEMDATA.put(67, new ItemData(67, "53cm艦首(酸素)魚雷", '雷'));
			ITEMDATA.put(68, new ItemData(68, "大発動艇", '发'));
			ITEMDATA.put(69, new ItemData(69, "カ号観測機", 'カ'));
			ITEMDATA.put(70, new ItemData(70, "三式指揮連絡機(対潜)", 'カ'));
			ITEMDATA.put(71, new ItemData(71, "10cm連装高角砲(砲架)", '副'));
			ITEMDATA.put(72, new ItemData(72, "増設バルジ(中型艦)", '板'));
			ITEMDATA.put(73, new ItemData(73, "増設バルジ(大型艦)", '板'));
			ITEMDATA.put(74, new ItemData(74, "探照灯", '灯'));
			ITEMDATA.put(75, new ItemData(75, "ドラム缶(輸送用)", '桶'));
			ITEMDATA.put(76, new ItemData(76, "38cm連装砲", '大'));
			ITEMDATA.put(77, new ItemData(77, "15cm連装副砲", '副'));
			ITEMDATA.put(78, new ItemData(78, "12.7cm単装砲", '副'));
			ITEMDATA.put(79, new ItemData(79, "瑞雲(六三四空)", '爆'));
			ITEMDATA.put(80, new ItemData(80, "瑞雲12型", '爆'));
			ITEMDATA.put(81, new ItemData(81, "瑞雲12型(六三四空)", '爆'));
			ITEMDATA.put(82, new ItemData(82, "九七式艦攻(九三一空)", '攻'));
			ITEMDATA.put(83, new ItemData(83, "天山(九三一空)", '攻'));
			ITEMDATA.put(84, new ItemData(84, "2cm 四連装FlaK 38", '铳'));
			ITEMDATA.put(85, new ItemData(85, "3.7cm FlaK M42", '铳'));
			ITEMDATA.put(86, new ItemData(86, "艦艇修理施設", '修'));
			ITEMDATA.put(87, new ItemData(87, "新型高温高圧缶", '缶'));
			ITEMDATA.put(88, new ItemData(88, "22号対水上電探改四", '探'));
			ITEMDATA.put(89, new ItemData(89, "21号対空電探改", '探'));
			ITEMDATA.put(90, new ItemData(90, "20.3cm(2号)連装砲", '中'));
			ITEMDATA.put(91, new ItemData(91, "12.7cm連装高角砲(後期型)", '小'));
			ITEMDATA.put(92, new ItemData(92, "毘式40mm連装機銃", '铳'));
			ITEMDATA.put(93, new ItemData(93, "九七式艦攻(友永隊)", '攻'));
			ITEMDATA.put(94, new ItemData(94, "天山一二型(友永隊)", '攻'));
			ITEMDATA.put(95, new ItemData(95, "潜水艦53cm艦首魚雷(8門)", '雷'));
			ITEMDATA.put(96, new ItemData(96, "零式艦戦21型(熟練)", '战'));
			ITEMDATA.put(97, new ItemData(97, "九九式艦爆(熟練)", '爆'));
			ITEMDATA.put(98, new ItemData(98, "九七式艦攻(熟練)", '攻'));
			ITEMDATA.put(99, new ItemData(99, "九九式艦爆(江草隊)", '爆'));
			ITEMDATA.put(100, new ItemData(100, "彗星(江草隊)", '爆'));
			ITEMDATA.put(101, new ItemData(101, "照明弾", '照'));
			ITEMDATA.put(102, new ItemData(102, "九八式水上偵察機(夜偵)", '夜'));
			ITEMDATA.put(103, new ItemData(103, "試製35.6cm三連装砲", '大'));
			ITEMDATA.put(104, new ItemData(104, "35.6cm連装砲(ダズル迷彩)", '大'));
			ITEMDATA.put(105, new ItemData(105, "試製41cm三連装砲", '大'));
			ITEMDATA.put(106, new ItemData(106, "13号対空電探改", '探'));
			ITEMDATA.put(107, new ItemData(107, "艦隊司令部施設", '司'));
			ITEMDATA.put(108, new ItemData(108, "熟練艦載機整備員", '扳'));
			ITEMDATA.put(109, new ItemData(109, "零戦52型丙(六〇一空)", '战'));
			ITEMDATA.put(110, new ItemData(110, "烈風(六〇一空)", '战'));
			ITEMDATA.put(111, new ItemData(111, "彗星(六〇一空)", '爆'));
			ITEMDATA.put(112, new ItemData(112, "天山(六〇一空)", '攻'));
			ITEMDATA.put(113, new ItemData(113, "流星(六〇一空)", '攻'));
			ITEMDATA.put(114, new ItemData(114, "38cm連装砲改", '大'));
			ITEMDATA.put(115, new ItemData(115, "Ar196改", '侦'));
			ITEMDATA.put(116, new ItemData(116, "一式徹甲弾", '彻'));
			ITEMDATA.put(117, new ItemData(117, "試製46cm連装砲", '大'));
			ITEMDATA.put(118, new ItemData(118, "紫雲", '侦'));
			ITEMDATA.put(119, new ItemData(119, "14cm連装砲", '中'));
			ITEMDATA.put(120, new ItemData(120, "91式高射装置", '射'));
			ITEMDATA.put(121, new ItemData(121, "94式高射装置", '射'));
			ITEMDATA.put(122, new ItemData(122, "10cm連装高角砲+高射装置", '小'));
			ITEMDATA.put(123, new ItemData(123, "SKC34 20.3cm連装砲", '中'));
			ITEMDATA.put(124, new ItemData(124, "FuMO25 レーダー", '探'));
			ITEMDATA.put(125, new ItemData(125, "61cm三連装(酸素)魚雷", '雷'));
			ITEMDATA.put(126, new ItemData(126, "WG42 (Wurfgerät 42)", 'W'));
			ITEMDATA.put(127, new ItemData(127, "試製FaT仕様九五式酸素魚雷改", '雷'));
			ITEMDATA.put(128, new ItemData(128, "試製51cm連装砲", '大'));
			ITEMDATA.put(129, new ItemData(129, "熟練見張員", '张'));
			ITEMDATA.put(130, new ItemData(130, "12.7cm高角砲+高射装置", '副'));
			ITEMDATA.put(131, new ItemData(131, "25mm三連装機銃 集中配備", '铳'));
			ITEMDATA.put(132, new ItemData(132, "零式水中聴音機", '听'));
			ITEMDATA.put(133, new ItemData(133, "381mm/50 三連装砲", '大'));
			ITEMDATA.put(134, new ItemData(134, "OTO 152mm三連装速射砲", '副'));
			ITEMDATA.put(135, new ItemData(135, "90mm単装高角砲", '副'));
			ITEMDATA.put(136, new ItemData(136, "プリエーゼ式水中防御隔壁", '板'));
			ITEMDATA.put(137, new ItemData(137, "381mm/50 三連装砲改", '大'));
			ITEMDATA.put(138, new ItemData(138, "二式大艇", '艇'));
			ITEMDATA.put(139, new ItemData(139, "15.2cm連装砲改", '中'));
			ITEMDATA.put(140, new ItemData(140, "96式150cm探照灯", '灯'));
			ITEMDATA.put(141, new ItemData(141, "32号対水上電探改", '探'));
			ITEMDATA.put(142, new ItemData(142, "15m二重測距儀+21号電探改二", '探'));
			ITEMDATA.put(143, new ItemData(143, "九七式艦攻(村田隊)", '攻'));
			ITEMDATA.put(144, new ItemData(144, "天山一二型(村田隊)", '攻'));
			ITEMDATA.put(145, new ItemData(145, "戦闘糧食", '粮'));
			ITEMDATA.put(146, new ItemData(146, "洋上補給", '补'));
			ITEMDATA.put(147, new ItemData(147, "120mm連装砲", '小'));
			ITEMDATA.put(148, new ItemData(148, "試製南山", '爆'));
			ITEMDATA.put(149, new ItemData(149, "四式水中聴音機", '听'));
			ITEMDATA.put(150, new ItemData(150, "秋刀魚の缶詰", '它'));
			ITEMDATA.put(151, new ItemData(151, "試製景雲(艦偵型)", '它'));
			ITEMDATA.put(152, new ItemData(152, "零式艦戦52型(熟練)", '战'));
			ITEMDATA.put(153, new ItemData(153, "零戦52型丙(付岩井小隊)", '战'));
			ITEMDATA.put(154, new ItemData(154, "零戦62型(爆戦/岩井隊)", '爆'));
			ITEMDATA.put(155, new ItemData(155, "零戦21型(付岩本小隊)", '战'));
			ITEMDATA.put(156, new ItemData(156, "零戦52型甲(付岩本小隊)", '战'));
			ITEMDATA.put(157, new ItemData(157, "零式艦戦53型(岩本隊)", '战'));
			ITEMDATA.put(158, new ItemData(158, "Bf109T改", '战'));
			ITEMDATA.put(159, new ItemData(159, "Fw190T改", '战'));
			ITEMDATA.put(160, new ItemData(160, "10.5cm連装砲", '副'));
			ITEMDATA.put(161, new ItemData(161, "16inch三連装砲 Mk.7", '大'));
			ITEMDATA.put(162, new ItemData(162, "203mm/53 連装砲", '中'));
			ITEMDATA.put(163, new ItemData(163, "Ro.43水偵", '侦'));
			ITEMDATA.put(164, new ItemData(164, "Ro.44水上戦闘機", '战'));
			ITEMDATA.put(165, new ItemData(165, "二式水戦改", '战'));
			ITEMDATA.put(166, new ItemData(166, "大発動艇(八九式中戦車&陸戦隊)", '车'));
			ITEMDATA.put(167, new ItemData(167, "特二式内火艇", '内'));
			ITEMDATA.put(168, new ItemData(168, "九六式陸攻", '陆'));
			ITEMDATA.put(169, new ItemData(169, "一式陸攻", '陆'));
			ITEMDATA.put(170, new ItemData(170, "一式陸攻(野中隊)", '陆'));
			ITEMDATA.put(171, new ItemData(171, "OS2U", '侦'));
			ITEMDATA.put(172, new ItemData(172, "5inch連装砲 Mk.28 mod.2", '大'));
			ITEMDATA.put(173, new ItemData(173, "Bofors 40mm四連装機関砲", '铳'));
			ITEMDATA.put(174, new ItemData(174, "53cm連装魚雷", '雷'));
			ITEMDATA.put(175, new ItemData(175, "雷電", '陆'));
			ITEMDATA.put(176, new ItemData(176, "三式戦 飛燕", '陆'));
			ITEMDATA.put(177, new ItemData(177, "三式戦 飛燕(飛行第244戦隊)", '陆'));
			ITEMDATA.put(178, new ItemData(178, "PBY-5A Catalina", '艇'));
			ITEMDATA.put(179, new ItemData(179, "試製61cm六連装(酸素)魚雷", '雷'));
			ITEMDATA.put(180, new ItemData(180, "一式陸攻 二二型甲", '陆'));
			ITEMDATA.put(181, new ItemData(181, "零式艦戦32型", '战'));
			ITEMDATA.put(182, new ItemData(182, "零式艦戦32型(熟練)", '战'));
			ITEMDATA.put(183, new ItemData(183, "16inch三連装砲 Mk.7+GFCS", '大'));
			ITEMDATA.put(184, new ItemData(184, "Re.2001 OR改", '战'));
			ITEMDATA.put(185, new ItemData(185, "三式戦 飛燕一型丁", '陆'));
			ITEMDATA.put(186, new ItemData(186, "一式陸攻 三四型", '陆'));
			ITEMDATA.put(187, new ItemData(187, "銀河", '陆'));
			ITEMDATA.put(188, new ItemData(188, "Re.2001 G改", '攻'));
			ITEMDATA.put(189, new ItemData(189, "Re.2005 改", '战'));
			ITEMDATA.put(190, new ItemData(190, "38.1cm Mk.I連装砲", '大'));
			ITEMDATA.put(191, new ItemData(191, "QF 2ポンド8連装ポンポン砲", '铳'));
			ITEMDATA.put(192, new ItemData(192, "38.1cm Mk.I/N連装砲改", '大'));
			ITEMDATA.put(193, new ItemData(193, "特大発動艇", '发'));
			ITEMDATA.put(194, new ItemData(194, "Laté 298B", '爆'));
			ITEMDATA.put(195, new ItemData(195, "SBD", '爆'));
			ITEMDATA.put(196, new ItemData(196, "TBD", '攻'));
			ITEMDATA.put(197, new ItemData(197, "F4F-3", '战'));
			ITEMDATA.put(198, new ItemData(198, "F4F-4", '战'));
			ITEMDATA.put(199, new ItemData(199, "噴式景雲改", '喷'));
			ITEMDATA.put(200, new ItemData(200, "橘花改", '喷'));
			ITEMDATA.put(204, new ItemData(204, "艦本新設計 増設バルジ(大型艦)", '板'));
			ITEMDATA.put(205, new ItemData(205, "F6F-3", '战'));

			//start 2017-6-4 2:29:22
			ITEMDATA.put(203, new ItemData(203, "艦本新設計 増設バルジ(中型艦)", '板'));
			ITEMDATA.put(207, new ItemData(207, "瑞雲(六三一空)", '爆'));
			ITEMDATA.put(208, new ItemData(208, "晴嵐(六三一空)", '爆'));
			ITEMDATA.put(209, new ItemData(209, "彩雲(輸送用分解済)", '它'));
			ITEMDATA.put(210, new ItemData(210, "潜水艦搭載電探&水防式望遠鏡", '它'));
			ITEMDATA.put(211, new ItemData(211, "潜水艦搭載電探&逆探(E27)", '它'));
			ITEMDATA.put(212, new ItemData(212, "彩雲(東カロリン空)", '彩'));
			ITEMDATA.put(213, new ItemData(213, "後期型艦首魚雷(6門)", '雷'));
			ITEMDATA.put(214, new ItemData(214, "熟練聴音員+後期型艦首魚雷(6門)", '雷'));
			ITEMDATA.put(215, new ItemData(215, "Ro.44水上戦闘機bis", '战'));
			ITEMDATA.put(216, new ItemData(216, "二式水戦改(熟練)", '战'));
			ITEMDATA.put(217, new ItemData(217, "強風改", '战'));
			ITEMDATA.put(219, new ItemData(219, "零式艦戦63型(爆戦)", '爆'));
			ITEMDATA.put(220, new ItemData(220, "8cm高角砲改+増設機銃", '副'));
			ITEMDATA.put(221, new ItemData(221, "一式戦 隼II型", '陆'));
			ITEMDATA.put(222, new ItemData(222, "一式戦 隼III型甲", '陆'));
			ITEMDATA.put(223, new ItemData(223, "一式戦 隼III型甲(54戦隊)", '陆'));
			ITEMDATA.put(224, new ItemData(224, "爆装一式戦 隼III型改(55戦隊)", '陆'));
			ITEMDATA.put(225, new ItemData(225, "一式戦 隼II型(64戦隊)", '陆'));
			ITEMDATA.put(226, new ItemData(226, "九五式爆雷", '投'));
			ITEMDATA.put(227, new ItemData(227, "二式爆雷", '投'));
			ITEMDATA.put(228, new ItemData(228, "九六式艦戦改", '战'));
			ITEMDATA.put(229, new ItemData(229, "12.7cm単装高角砲(後期型)", '副'));
			ITEMDATA.put(230, new ItemData(230, "特大発動艇+戦車第11連隊", '发'));
			ITEMDATA.put(231, new ItemData(231, "30.5cm三連装砲", '大'));
			ITEMDATA.put(232, new ItemData(232, "30.5cm三連装砲改", '大'));
			ITEMDATA.put(236, new ItemData(236, "41cm三連装砲改", '大'));

			ITEMDATA.put(573, new ItemData(573, "深海潜水下駄履き", ' '));
			ITEMDATA.put(574, new ItemData(574, "深海攻撃哨戒鷹", ' '));
			ITEMDATA.put(575, new ItemData(575, "深海攻撃哨戒鷹改", ' '));
			ITEMDATA.put(576, new ItemData(576, "深海12inch三連装砲", ' '));
			//end

			//start 2017-7-24 3:50:57
			ITEMDATA.put(206, new ItemData(206, "F6F-5", '战'));
			ITEMDATA.put(233, new ItemData(233, "F4U-1D", '战'));
			ITEMDATA.put(234, new ItemData(234, "15.5cm三連装副砲改", '副'));
			ITEMDATA.put(235, new ItemData(235, "15.5cm三連装砲改", '中'));
			ITEMDATA.put(237, new ItemData(237, "瑞雲(六三四空/熟練)", '爆'));
			ITEMDATA.put(238, new ItemData(238, "零式水上偵察機11型乙", '侦'));
			ITEMDATA.put(241, new ItemData(241, "戦闘糧食(特別なおにぎり)", '它'));
			//end

			ITEMDATA.put(501, new ItemData(501, "5inch単装砲", ' '));
			ITEMDATA.put(502, new ItemData(502, "5inch連装砲", ' '));
			ITEMDATA.put(503, new ItemData(503, "3inch単装高角砲", ' '));
			ITEMDATA.put(504, new ItemData(504, "5inch単装高射砲", ' '));
			ITEMDATA.put(505, new ItemData(505, "8inch三連装砲", ' '));
			ITEMDATA.put(506, new ItemData(506, "6inch連装速射砲", ' '));
			ITEMDATA.put(507, new ItemData(507, "14inch連装砲", ' '));
			ITEMDATA.put(508, new ItemData(508, "16inch連装砲", ' '));
			ITEMDATA.put(509, new ItemData(509, "16inch三連装砲", ' '));
			ITEMDATA.put(510, new ItemData(510, "5inch単装高射砲", ' '));
			ITEMDATA.put(511, new ItemData(511, "6inch単装砲", ' '));
			ITEMDATA.put(512, new ItemData(512, "12.5inch連装副砲", ' '));
			ITEMDATA.put(513, new ItemData(513, "21inch魚雷前期型", ' '));
			ITEMDATA.put(514, new ItemData(514, "21inch魚雷後期型", ' '));
			ITEMDATA.put(515, new ItemData(515, "高速深海魚雷", ' '));
			ITEMDATA.put(516, new ItemData(516, "深海棲艦攻", ' '));
			ITEMDATA.put(517, new ItemData(517, "深海棲艦攻 Mark.II", ' '));
			ITEMDATA.put(518, new ItemData(518, "深海棲艦攻 Mark.III", ' '));
			ITEMDATA.put(519, new ItemData(519, "深海棲艦戦", ' '));
			ITEMDATA.put(520, new ItemData(520, "深海棲艦戦 Mark.II", ' '));
			ITEMDATA.put(521, new ItemData(521, "深海棲艦戦 Mark.III", ' '));
			ITEMDATA.put(522, new ItemData(522, "飛び魚艦戦", ' '));
			ITEMDATA.put(523, new ItemData(523, "深海棲艦爆", ' '));
			ITEMDATA.put(524, new ItemData(524, "深海棲艦爆 Mark.II", ' '));
			ITEMDATA.put(525, new ItemData(525, "深海棲艦偵察機", ' '));
			ITEMDATA.put(526, new ItemData(526, "飛び魚偵察機", ' '));
			ITEMDATA.put(527, new ItemData(527, "対空レーダ― Mark.I", ' '));
			ITEMDATA.put(528, new ItemData(528, "水上レーダ― Mark.I", ' '));
			ITEMDATA.put(529, new ItemData(529, "水上レーダ― Mark.II", ' '));
			ITEMDATA.put(530, new ItemData(530, "対空レーダ― Mark.II", ' '));
			ITEMDATA.put(531, new ItemData(531, "深海水上レーダー", ' '));
			ITEMDATA.put(532, new ItemData(532, "深海対空レーダ―", ' '));
			ITEMDATA.put(533, new ItemData(533, "改良型深海タービン", ' '));
			ITEMDATA.put(534, new ItemData(534, "強化型深海缶", ' '));
			ITEMDATA.put(535, new ItemData(535, "対空散弾", ' '));
			ITEMDATA.put(536, new ItemData(536, "劣化徹甲弾", ' '));
			ITEMDATA.put(537, new ItemData(537, "12.7mm機銃", ' '));
			ITEMDATA.put(538, new ItemData(538, "20mm機銃", ' '));
			ITEMDATA.put(539, new ItemData(539, "40mm二連装機関砲", ' '));
			ITEMDATA.put(540, new ItemData(540, "40mm四連装機関砲", ' '));
			ITEMDATA.put(541, new ItemData(541, "深海烏賊魚雷", ' '));
			ITEMDATA.put(542, new ItemData(542, "深海爆雷投射機", ' '));
			ITEMDATA.put(543, new ItemData(543, "深海ソナー", ' '));
			ITEMDATA.put(544, new ItemData(544, "深海爆雷投射機 Mk.II", ' '));
			ITEMDATA.put(545, new ItemData(545, "深海ソナー Mk.II", ' '));
			ITEMDATA.put(546, new ItemData(546, "飛び魚艦爆", ' '));
			ITEMDATA.put(547, new ItemData(547, "深海猫艦戦", ' '));
			ITEMDATA.put(548, new ItemData(548, "深海地獄艦爆", ' '));
			ITEMDATA.put(549, new ItemData(549, "深海復讐艦攻", ' '));
			ITEMDATA.put(550, new ItemData(550, "5inch連装両用莢砲", ' '));
			ITEMDATA.put(551, new ItemData(551, "20inch連装砲", ' '));
			ITEMDATA.put(552, new ItemData(552, "15inch要塞砲", ' '));
			ITEMDATA.put(553, new ItemData(553, "4inch連装両用砲+CIC", ' '));
			ITEMDATA.put(554, new ItemData(554, "深海水上攻撃機", ' '));
			ITEMDATA.put(555, new ItemData(555, "深海水上攻撃機改", ' '));
			ITEMDATA.put(556, new ItemData(556, "深海猫艦戦改", ' '));
			ITEMDATA.put(557, new ItemData(557, "深海地獄艦爆改", ' '));
			ITEMDATA.put(558, new ItemData(558, "深海復讐艦攻改", ' '));
			ITEMDATA.put(559, new ItemData(559, "深海FCS+CIC", ' '));
			ITEMDATA.put(560, new ItemData(560, "深海探照灯", ' '));
			ITEMDATA.put(561, new ItemData(561, "深海解放陸爆", ' '));
			ITEMDATA.put(562, new ItemData(562, "深海解放陸爆Ace", ' '));
			ITEMDATA.put(563, new ItemData(563, "8inch長射程連装砲", ' '));
			ITEMDATA.put(564, new ItemData(564, "深海水上偵察観測機", ' '));
			ITEMDATA.put(565, new ItemData(565, "5inch沿岸設置砲", ' '));
			ITEMDATA.put(566, new ItemData(566, "深海猫艦戦(爆装)", ' '));
			ITEMDATA.put(567, new ItemData(567, "沿岸設置レーダー", ' '));
			ITEMDATA.put(568, new ItemData(568, "16inch三連装砲", ' '));
			ITEMDATA.put(569, new ItemData(569, "深海偵察飛行艇", ' '));
			ITEMDATA.put(570, new ItemData(570, "高速深海魚雷 mod.2", ' '));
			ITEMDATA.put(571, new ItemData(571, "深海水母小鬼機", ' '));
			ITEMDATA.put(572, new ItemData(572, "深海熊猫艦戦", ' '));
		}

		public static void main(String[] args) {
			GlobalContext.load();
			FunctionUtils.notNull(GlobalContext.getMasterData(), md -> {
				Map<Integer, MasterSlotitemDto> map = md.getMasterSlotitemDataMap();
				map.forEach((id, msd) -> {
					if (ITEMDATA.containsKey(id) == false) {
						System.out.println(String.format("ITEMDATA.put(%d, new ItemData(%d, \"%s\", ' '));", id, id, msd.getName()));
					}
				});
			});
		}

		public static ItemData get(int id) {
			return ITEMDATA.get(id);
		}

		/**
		 * 装备信息
		 * @author MoeKagari
		 */
		public static class ItemData {
			private final int id;//id
			private final String name;//装备名
			private final char oneWordName;//装备简称

			public ItemData(int id, String name, char oneWordName) {
				this.id = id;
				this.name = name;
				this.oneWordName = oneWordName;
			}

			public int getId() {
				return this.id;
			}

			public String getName() {
				return this.name;
			}

			public char getOneWordName() {
				return this.oneWordName;
			}
		}
	}
}
