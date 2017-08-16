package tdrz.update.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tdrz.core.config.AppConstants;
import tdrz.core.internal.TrayMessageBox;
import tdrz.core.logic.TimeString;
import tdrz.core.translator.DeckDtoTranslator;
import tdrz.core.util.JsonUtils;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.context.data.ApiData;
import tdrz.update.context.room.DeckRoom;
import tdrz.update.context.room.KdockRoom;
import tdrz.update.context.room.MainRoom;
import tdrz.update.context.room.NdockRoom;
import tdrz.update.context.room.QuestRoom;
import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.memory.CreateItemDto;
import tdrz.update.dto.memory.DestroyItemDto;
import tdrz.update.dto.memory.DestroyShipDto;
import tdrz.update.dto.memory.MissionResultDto;
import tdrz.update.dto.memory.ResourceRecordDto;
import tdrz.update.dto.memory.battle.BattleDto;
import tdrz.update.dto.memory.battle.day.BattleDayDto;
import tdrz.update.dto.memory.battle.day.CombineBattleEachDayDto;
import tdrz.update.dto.memory.battle.day.CombineBattleEachDayWaterDto;
import tdrz.update.dto.memory.battle.day.CombinebattleDayDto;
import tdrz.update.dto.memory.battle.day.CombinebattleDayWaterDto;
import tdrz.update.dto.memory.battle.day.CombinebattleECDayDto;
import tdrz.update.dto.memory.battle.day.airbattle.BattleAirbattleDto;
import tdrz.update.dto.memory.battle.day.airbattle.BattleAirbattleLDDto;
import tdrz.update.dto.memory.battle.day.airbattle.CombineBattleAirbattleDto;
import tdrz.update.dto.memory.battle.day.airbattle.CombineBattleAirbattleLDDto;
import tdrz.update.dto.memory.battle.info.InfoBattleGobackPortDto;
import tdrz.update.dto.memory.battle.info.InfoBattleNextDto;
import tdrz.update.dto.memory.battle.info.InfoBattleResultDto;
import tdrz.update.dto.memory.battle.info.InfoBattleShipdeckDto;
import tdrz.update.dto.memory.battle.info.InfoBattleStartAirBaseDto;
import tdrz.update.dto.memory.battle.info.InfoBattleStartDto;
import tdrz.update.dto.memory.battle.info.InfoCombinebattleResultDto;
import tdrz.update.dto.memory.battle.midnight.BattleMidnightDto;
import tdrz.update.dto.memory.battle.midnight.CombinebattleECMidnightDto;
import tdrz.update.dto.memory.battle.midnight.CombinebattleMidnightDto;
import tdrz.update.dto.memory.battle.midnight.sp.BattleMidnightSPDto;
import tdrz.update.dto.memory.battle.midnight.sp.CombinebattleMidnightSPDto;
import tdrz.update.dto.memory.battle.practice.PracticeBattleDayDto;
import tdrz.update.dto.memory.battle.practice.PracticeBattleMidnightDto;
import tdrz.update.dto.memory.battle.practice.PracticeBattleResultDto;
import tdrz.update.dto.word.AirbaseDto;
import tdrz.update.dto.word.BasicDto;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.MapinfoDto;
import tdrz.update.dto.word.MasterDataDto;
import tdrz.update.dto.word.PracticeEnemyDto;
import tdrz.update.dto.word.PresetDeckDto;
import tdrz.update.dto.word.QuestDto;
import tdrz.update.dto.word.ResourceDto;
import tdrz.update.dto.word.ShipDto;
import tdrz.update.dto.word.UseItemDto;
import tool.FunctionUtils;

public class GlobalContext {
	private static final Logger LOG = LogManager.getLogger(GlobalContext.class);

	public static void load() {
		try {
			InputStream is;
			File file = AppConstants.MASTERDATA_FILE;
			if (file.exists() && file.isFile()) {
				is = new FileInputStream(file);
			} else {//读取程序内置的备份
				is = GlobalContext.class.getResourceAsStream(AppConstants.MASTERDATAFILE_BACKUP);
			}
			JsonObject json = Json.createReader(new InputStreamReader(is, Charset.forName("utf-8"))).readObject();
			masterData = new MasterDataDto(json);
			is.close();
		} catch (Exception e) {
			LOG.warn("MasterData读取失败", e);
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AppConstants.MEMORY_FILE))) {
			((List<?>) ois.readObject()).forEach(ele -> {
				if (ele instanceof AbstractMemory) {
					memoryList.memorys.add((AbstractMemory) ele);
				}
			});
		} catch (Exception e) {
			LOG.warn("memory读取失败", e);
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AppConstants.ITEM_FILE))) {
			((List<?>) ois.readObject()).forEach(ele -> {
				if (ele instanceof ItemDto) {
					ItemDto item = (ItemDto) ele;
					itemMap.put(item.getId(), item);
				}
			});
		} catch (Exception e) {
			LOG.warn("item读取失败", e);
		}
	}

	public static void store() {
		try {
			if (masterData != null) {
				FileUtils.write(AppConstants.MASTERDATA_FILE, masterData.getJson().toString(), Charset.forName("utf-8"));
			}
		} catch (Exception e) {
			LOG.warn("MasterData保存失败", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConstants.MEMORY_FILE))) {
			oos.writeObject(memoryList.memorys);
		} catch (Exception e) {
			LOG.warn("memory保存失败", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConstants.ITEM_FILE))) {
			oos.writeObject(new ArrayList<>(itemMap.values()));
		} catch (Exception e) {
			LOG.warn("item保存失败", e);
		}
	}

	public final static DeckRoom[] deckRooms = { new DeckRoom(1), new DeckRoom(2), new DeckRoom(3), new DeckRoom(4) };
	public final static NdockRoom[] ndockRooms = { new NdockRoom(1), new NdockRoom(2), new NdockRoom(3), new NdockRoom(4) };
	public final static KdockRoom[] kdockRooms = { new KdockRoom(1), new KdockRoom(2), new KdockRoom(3), new KdockRoom(4) };
	public final static MainRoom mainRoom = new MainRoom();
	public final static QuestRoom questRoom = new QuestRoom();

	/** 服务器ip */
	private static String serverName = null;
	/** 司令部等级 提督名字 最大保有舰娘数 最大保有装备数 */
	private static BasicDto basicInformation = null;
	/** 是否结成联合舰队 */
	private static boolean combined = false;
	/** 通过返回母港时第一舰队无疲劳变化来更新此值 */
	private static PLTime PLTIME = null;
	/** 泊地修理 */
	private static FleetAkashiTimer akashiTimer = new FleetAkashiTimer();

	/** 路基详情 */
	private static AirbaseDto airbase = null;
	/** 地图详情 */
	private static MapinfoDto mapinfo = null;
	/** 当前资源 */
	private static CurrentMaterial currentMaterial = new CurrentMaterial();
	/** 演习对手 */
	private static PracticeEnemyDto practiceEnemy = null;
	/** master data */
	private static MasterDataDto masterData = null;

	/** 所有记录({@link AbstractMemory}的子类) */
	private final static MemoryList memoryList = new MemoryList();

	/** 所有任务 */
	private final static List<QuestDto> questList = new ArrayList<>();
	/** 所有装备 */
	private final static Map<Integer, ItemDto> itemMap = new HashMap<>();
	/** 所有舰娘 */
	private final static Map<Integer, ShipDto> shipMap = new HashMap<>();
	/** 所有useitem */
	private final static Map<Integer, UseItemDto> useItemMap = new HashMap<>();

	/** 编成记录(游戏中的) */
	private final static PresetDeckList presetDeckList = new PresetDeckList();

	/*----------------------------------------------静态方法------------------------------------------------------------------*/

	public static void updatePLTIME(long oldtime, int[] oldconds, long newtime, int[] newconds) {
		if (oldtime <= 0 || newtime <= 0) return;
		if (PLTIME != null && PLTIME.getRange() < 2 * 1000) return;

		if (PLTime.need(oldtime, oldconds, newtime, newconds)) {
			if (PLTIME == null) {
				PLTIME = new PLTime(newtime - 3 * 60 * 1000, oldtime);
			} else {
				PLTIME.update(oldtime, oldconds, newtime, newconds);
			}
		}
	}

	public static void destroyShip(long time, String event, int id) {
		Optional.ofNullable(getShip(id)).ifPresent(ship -> {
			final int count = Arrays.stream(ship.getSlots()).filter(slot -> slot > 0).map(i -> 1).sum();
			FunctionUtils.forEachInt(ship.getSlots(), item_id -> destroyItem(time, event, item_id, count));
			destroyItem(time, event, ship.getSlotex(), -1);

			memoryList.add(new DestroyShipDto(time, event, ship));
			FunctionUtils.forEach(deckRooms, dr -> Optional.ofNullable(dr.getDeck()).ifPresent(deck -> deck.remove(id)));
			shipMap.remove(ship.getId());
		});
	}

	public static void destroyItem(long time, String event, int id, int group) {
		Optional.ofNullable(getItem(id)).ifPresent(item -> {
			memoryList.add(new DestroyItemDto(time, event, item, group));
			itemMap.remove(item.getId());
		});
	}

	public static ShipDto getShip(int id) {
		return shipMap.get(id);
	}

	public static ShipDto addNewShip(JsonValue value) {
		ShipDto ship = null;
		if (value instanceof JsonObject) {
			ship = new ShipDto((JsonObject) value);
			shipMap.put(ship.getId(), ship);
		}
		return ship;
	}

	public static ItemDto getItem(int id) {
		return itemMap.get(id);
	}

	public static ItemDto addNewItem(JsonValue value) {
		ItemDto item = null;
		if (value instanceof JsonObject) {
			item = new ItemDto((JsonObject) value);
			itemMap.put(item.getId(), item);
		}
		return item;
	}

	public static UseItemDto getUseItem(int id) {
		return useItemMap.get(id);
	}

	public static UseItemDto addNewUseItem(JsonValue value) {
		UseItemDto useItem = null;
		if (value instanceof JsonObject) {
			useItem = new UseItemDto((JsonObject) value);
			useItemMap.put(useItem.getId(), useItem);
		}
		return useItem;
	}

	public static void updateShip(int id, Consumer<ShipDto> handler) {
		Optional.ofNullable(getShip(id)).ifPresent(handler);
	}

	public static ShipDto getSecretaryShip() {
		return Optional.ofNullable(deckRooms[0].getDeck()).map(deck -> getShip(deck.getShips()[0])).orElse(null);
	}

	/*----------------------------------------------getter,setter------------------------------------------------------------------*/

	public static String getServerName() {
		return serverName;
	}

	public static boolean isCombined() {
		return combined;
	}

	public static void setCombined(boolean combined) {
		GlobalContext.combined = combined;
	}

	public static Map<Integer, UseItemDto> getUseitemMap() {
		return useItemMap;
	}

	public static Map<Integer, ItemDto> getItemMap() {
		return itemMap;
	}

	public static Map<Integer, ShipDto> getShipMap() {
		return shipMap;
	}

	public static List<QuestDto> getQuestlist() {
		return questList;
	}

	public static PLTime getPLTIME() {
		return PLTIME;
	}

	public static FleetAkashiTimer getAkashiTimer() {
		return akashiTimer;
	}

	public static MemoryList getMemorylist() {
		return memoryList;
	}

	public static PresetDeckList getPresetdecklist() {
		return presetDeckList;
	}

	public static CurrentMaterial getCurrentMaterial() {
		return currentMaterial;
	}

	public static BasicDto getBasicInformation() {
		return basicInformation;
	}

	public static void setBasicInformation(BasicDto basicInformation) {
		GlobalContext.basicInformation = basicInformation;
	}

	public static PracticeEnemyDto getPracticeEnemy() {
		return practiceEnemy;
	}

	public static void setPracticeEnemy(PracticeEnemyDto practiceEnemyDto) {
		GlobalContext.practiceEnemy = practiceEnemyDto;
	}

	public static MasterDataDto getMasterData() {
		return masterData;
	}

	public static void setMasterData(MasterDataDto masterData) {
		GlobalContext.masterData = masterData;
	}

	public static MapinfoDto getMapinfo() {
		return mapinfo;
	}

	public static void setMapinfo(MapinfoDto mapinfo) {
		GlobalContext.mapinfo = mapinfo;
	}

	public static AirbaseDto getAirbase() {
		return airbase;
	}

	public static void setAirbase(AirbaseDto airbase) {
		GlobalContext.airbase = airbase;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static class CurrentMaterial {
		private ResourceDto material = null;

		public ResourceDto getMaterial() {
			return this.material;
		}

		/** 更新资源 */
		public void setMaterial(String event, long time, int[] mm) {
			this.material = new ResourceDto(mm);
			if (event != null) {//PORT时为null
				memoryList.add(new ResourceRecordDto(event, time, this.material));
			}
		}

		/** 更新主要资源(前4) */
		public void setMainMaterial(String event, long time, int[] mm) {
			if (this.material != null) {
				this.setMaterial(event, time, ArrayUtils.addAll(mm, Arrays.copyOfRange(this.material.getResource(), 4, 8)));
			}
		}

		/** 增加资源 */
		public void setMaterial(String event, long time, int[] mm, boolean isIncrease) {
			if (this.material != null) {
				int[] resource = new int[mm.length];
				for (int i = 0; i < mm.length; i++) {
					resource[i] = this.material.getResource()[i] + (isIncrease ? 1 : -1) * mm[i];
				}
				this.setMaterial(event, time, resource);
			}
		}

		/** 增加主要资源(前4) */
		public void setMainMaterial(String event, long time, int[] mm, boolean isIncrease) {
			this.setMaterial(event, time, ArrayUtils.addAll(mm, 0, 0, 0, 0), isIncrease);
		}
	}

	public static class MemoryList {
		public final ArrayList<AbstractMemory> memorys = new ArrayList<>();

		public void add(AbstractMemory memory) {
			if (memory instanceof BattleDto) {
				this.lastBattle = (BattleDto) memory;
			}
			this.memorys.add(memory);
		}

		//start battle
		private BattleDto lastBattle = null;

		public BattleDto getLastBattle() {
			BattleDto result = this.lastBattle;
			this.lastBattle = null;
			return result;
		}
		//end
	}

	public static class PLTime {
		private long floor, ceil;
		private TreeSet<long[]> notuse = new TreeSet<>((a, b) -> Long.compare(a[0], b[0]));

		public PLTime(long floor, long ceil) {
			this.floor = floor;
			this.ceil = ceil;
		}

		public long getTime() {
			return (this.floor + this.ceil) / 2;
		}

		public long getRange() {
			return (this.ceil - this.floor) / 2;
		}

		public void update(long oldtime, int[] oldconds, long newtime, int[] newconds) {
			long time1 = oldtime;
			long time2 = newtime;

			//循环到预测时间段
			while (time1 >= this.ceil && time2 >= this.ceil) {
				time1 -= 3 * 60 * 1000;
				time2 -= 3 * 60 * 1000;
			}

			if (time2 <= this.floor) {
				//如果不在预测时间段内,不需要
			} else {
				//有交集
				this.notuse.add(new long[] { time1, time2 });
				this.update();
			}
		}

		private void update() {
			TreeSet<long[]> temps = new TreeSet<>((a, b) -> Long.compare(a[0], b[0]));
			temps.addAll(this.notuse);
			this.notuse.clear();

			//整合
			long[] time = null;
			for (long[] temp : temps) {
				if (time == null) time = Arrays.copyOf(temp, temp.length);
				if (Arrays.equals(time, temp)) continue;

				if (time[1] >= temp[0]) {
					time[1] = temp[1];
				} else {
					this.update(time[0], time[1]);
					time = null;
				}
			}

			if (time != null) {
				this.update(time[0], time[1]);
			}
		}

		private void update(long time1, long time2) {
			if (time1 >= this.floor && time2 <= this.ceil) {
				if (time1 == this.floor && time2 < this.ceil) {
					this.floor = time2;
				} else if (time1 > this.floor && time2 == this.ceil) {
					this.ceil = time1;
				} else {//时间段是预测时间段的子集,需收集之后整合					
					this.notuse.add(new long[] { time1, time2 });
				}
			} else {//只有交集				
				if (time1 >= this.floor) {
					this.ceil = time1;
				} else if (time2 <= this.ceil) {
					this.floor = time2;
				}
			}
		}

		public static boolean need(long oldtime, int[] oldconds, long newtime, int[] newconds) {
			if (newtime - oldtime > 60 * 1000) {
				//两次刷新时间大于一分钟,则忽略此次刷新母港
				//尽管理论上可以最大三分钟,但是根据游戏情况,缩小范围显得更优
				return false;
			}

			if (oldconds != null && newconds != null) {
				Predicate<int[]> need = conds -> Arrays.stream(oldconds).anyMatch(i -> (i >= 0) && (i < 49));
				if (need.test(oldconds) && need.test(newconds) && Arrays.equals(oldconds, newconds)) {
					//有舰娘的疲劳处于[0,49),并且两个time没有发生疲劳变化
					return true;
				}
			}

			return false;
		}
	}

	public static class FleetAkashiTimer {
		private final static int RESET_LIMIT = 20 * 60;
		private long time = -1;

		public void update(ApplicationMain main, TrayMessageBox box, long currentTime) {
			if (this.time == -1) return;
			long rest = (currentTime - this.time) / 1000;
			main.getAkashiTimerComposite().timeLabel.setText(TimeString.toDateRestString(rest));
			if (rest == RESET_LIMIT) {
				if (Arrays.stream(deckRooms).map(DeckRoom::getDeck).anyMatch(DeckDtoTranslator::shouldNotifyAkashiTimer)) {
					box.add("泊地修理", "泊地修理已20分钟");
				}
			}
		}

		public void resetWhenPort(long currentTime) {
			if (this.time == -1) return;
			if ((currentTime - this.time) / 1000 >= RESET_LIMIT) {
				this.time = currentTime;
			}
		}

		public void resetAkashiFlagshipWhenChange(long currentTime) {
			this.time = currentTime;
		}
	}

	public static class PresetDeckList {
		private int max = -1;
		private PresetDeckDto[] decks = null;

		public PresetDeckDto add(PresetDeckDto deck) {
			if (this.decks != null) {
				this.decks[deck.getNo() - 1] = deck;
			}
			return deck;
		}

		public void remove(int no) {
			if (this.decks != null) {
				this.decks[no - 1] = null;
			}
		}

		public void init(JsonObject json) {
			if (this.decks != null) {
				Arrays.fill(this.decks, null);
				json.forEach((no, value) -> this.add(new PresetDeckDto(Integer.parseInt(no), (JsonObject) value)));
			}
		}

		public int getMax() {
			return this.max;
		}

		public void setMax(int newMax) {
			if (this.max != newMax) {
				if (this.decks == null) {
					this.decks = new PresetDeckDto[newMax];
				} else {
					this.decks = Arrays.copyOfRange(this.decks, 0, newMax);
				}
				this.max = newMax;
			}
		}
	}

	public static void updateContext(ApiData data) {
		serverName = data.getServerName();
		final long time = data.getTime();
		JsonValue api_data = data.getJsonObject().get("api_data");

		switch (data.getType()) {
			case CHARGE: {
				JsonObject json = (JsonObject) api_data;
				json.getJsonArray("api_ship").getValuesAs(JsonObject.class).forEach(info -> {
					GlobalContext.updateShip(info.getInt("api_id"), ship -> ship.updateWhenCharge(info));
				});
				currentMaterial.setMainMaterial("补给", data.getTime(), JsonUtils.getIntArray(json.getJsonArray("api_material")));
			}
				break;
			case CREATEITEM: {
				JsonObject json = (JsonObject) api_data;

				boolean success = json.getInt("api_create_flag") == 1;
				int slotitemId = success ? GlobalContext.addNewItem(json.getJsonObject("api_slot_item")).getSlotitemId() : -1;

				int[] mm = { //
						Integer.parseInt(data.getField("api_item1")),//
						Integer.parseInt(data.getField("api_item2")),//
						Integer.parseInt(data.getField("api_item3")),//
						Integer.parseInt(data.getField("api_item4")),//
						0, 0, success ? 1 : 0, 0//
				};

				currentMaterial.setMaterial("开发", time, mm, false);
				memoryList.add(new CreateItemDto(time, success, mm, slotitemId, GlobalContext.getSecretaryShip()));
			}
				break;
			case DESTROYSHIP: {
				GlobalContext.destroyShip(time, "工厂解体", Integer.parseInt(data.getField("api_ship_id")));
				currentMaterial.setMainMaterial("解体", time, JsonUtils.getIntArray((JsonObject) api_data, "api_material"));
			}
				break;
			case DESTROYITEM: {
				String[] ids = data.getField("api_slotitem_ids").trim().split(",");
				FunctionUtils.forEach(ids, id -> GlobalContext.destroyItem(time, "工厂废弃", Integer.valueOf(id), ids.length));
				currentMaterial.setMainMaterial("废弃", time, JsonUtils.getIntArray((JsonObject) api_data, "api_get_material"), true);
			}
				break;

			case KDOCK:
				FunctionUtils.forEach(kdockRooms, FunctionUtils.getConsumer(KdockRoom::doKdock, data, api_data));
				break;
			case KDOCK_CREATESHIP:
				FunctionUtils.forEach(kdockRooms, FunctionUtils.getConsumer(KdockRoom::doCreateship, data, api_data));
				break;
			case KDOCK_SPEEDCHANGE:
				FunctionUtils.forEach(kdockRooms, FunctionUtils.getConsumer(KdockRoom::doSpeedchange, data, api_data));
				break;
			case KDOCK_GETSHIP:
				FunctionUtils.forEach(kdockRooms, FunctionUtils.getConsumer(KdockRoom::doGetShip, data, api_data));
				break;

			case NDOCK:
				FunctionUtils.forEach(ndockRooms, FunctionUtils.getConsumer(NdockRoom::doNdock, data, api_data));
				break;
			case NDOCK_NYUKYO_START:
				FunctionUtils.forEach(ndockRooms, FunctionUtils.getConsumer(NdockRoom::doNyukyoStart, data, api_data));
				break;
			case NDOCK_NYUKYO_SPEEDCHANGE:
				FunctionUtils.forEach(ndockRooms, FunctionUtils.getConsumer(NdockRoom::doNyukyoSpeedchange, data, api_data));
				break;

			case DECK:
				FunctionUtils.forEach(deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, api_data));
				break;
			case DECK_CHANGE:
				FunctionUtils.forEach(deckRooms, FunctionUtils.getConsumer(DeckRoom::doChange, data, api_data));
				break;
			case DECK_UPDATEDECKNAME:
				FunctionUtils.forEach(deckRooms, FunctionUtils.getConsumer(DeckRoom::doUpdatedeckName, data, api_data));
				break;
			case DECK_PRESET_SELECT:
				FunctionUtils.forEach(deckRooms, FunctionUtils.getConsumer(DeckRoom::doPresetSelect, data, api_data));
				break;
			case DECK_SHIP_LOCK: {
				int id = Integer.parseInt(data.getField("api_ship_id"));
				int lock_value = ((JsonObject) api_data).getInt("api_locked");
				GlobalContext.updateShip(id, ship -> ship.setLocked(lock_value == 1));
			}
				break;
			case DECK_PRESET_DECK:
			case DECK_PRESET_REGISTER:
			case DECK_PRESET_DELETE:
			case DECK_ITEMUSE_COND:
				break;

			case MISSION:
			case MISSION_START:
			case MISSION_RETURN:
				break;
			case MISSION_RESULT:
				memoryList.add(new MissionResultDto(Integer.parseInt(data.getField("api_deck_id")), (JsonObject) api_data, data.getTime()));
				break;

			case QUEST_START:
			case QUEST_STOP:
			case QUEST_CLEAR:
				break;
			case QUEST_LIST:
				questRoom.doQuestList(data, api_data);
				break;

			case REMODEL_SLOTLIST:
			case REMODEL_SLOTLIST_DETAIL:
				break;
			case REMODEL_SLOT: {
				JsonObject json = (JsonObject) api_data;

				int slotId = Integer.parseInt(data.getField("api_slot_id"));
				//boolean certain = Integer.parseInt(data.getField("api_certain_flag")) == 1;
				//boolean success = json.getInt("api_remodel_flag") == 1;
				//ItemDto item = GlobalContext.getItem(slotId);
				if (json.containsKey("api_after_slot")) {
					//TODO 未确定
					//更新装备时,两装备的ID不同
					ItemDto newItem = GlobalContext.addNewItem(json.getJsonObject("api_after_slot"));
					if (newItem.getId() != slotId) {
						GlobalContext.destroyItem(time, "改修更新", slotId, 1);
					}
				}
				if (json.containsKey("api_use_slot_id")) {
					int[] useSlotIds = JsonUtils.getIntArray(json, "api_use_slot_id");
					FunctionUtils.forEachInt(useSlotIds, id -> GlobalContext.destroyItem(time, "改修消耗", id, useSlotIds.length));
				}
				currentMaterial.setMaterial("改修", time, JsonUtils.getIntArray(json, "api_after_material"));
			}
				break;

			case KAISOU_POWERUP: {
				String[] ids = data.getField("api_id_items").trim().split(",");
				JsonObject json = (JsonObject) api_data;
//				boolean success = json.getInt("api_powerup_flag") == 1;
//				ShipDto oldship = GlobalContext.getShip(Integer.parseInt(data.getField("api_id")));

				FunctionUtils.forEach(ids, id -> GlobalContext.destroyShip(time, "近代化改修", Integer.parseInt(id)));
				GlobalContext.addNewShip(json.getJsonObject("api_ship"));
				FunctionUtils.forEach(GlobalContext.deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, json.get("api_deck")));//更新deck
			}
				break;
			case KAISOU_SLOTITEM_LOCK: {
				int id = Integer.parseInt(data.getField("api_slotitem_id"));
				int lock_value = ((JsonObject) api_data).getInt("api_locked");
				FunctionUtils.notNull(GlobalContext.getItem(id), item -> item.slotItemLock(lock_value == 1));
			}
				break;
			case KAISOU_SHIP3: {
				JsonObject json = (JsonObject) api_data;
				json.getJsonArray("api_ship_data").forEach(GlobalContext::addNewShip);
				FunctionUtils.forEach(GlobalContext.deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, json.get("api_deck_data")));
			}
				break;
			case KAISOU_OPEN_EXSLOT:
				GlobalContext.updateShip(Integer.parseInt(data.getField("api_id")), ShipDto::openSlotex);
				break;
			case KAISOU_SLOT_EXCHANGE: {
				int id = Integer.parseInt(data.getField("api_id"));
				int[] newSlots = JsonUtils.getIntArray((JsonObject) api_data, "api_slot");
				GlobalContext.updateShip(id, ship -> ship.slotExchange(newSlots));
			}
				break;
			case KAISOU_SLOT_DEPRIVE: {
				JsonObject json = ((JsonObject) api_data).getJsonObject("api_ship_data");
				GlobalContext.addNewShip(json.getJsonObject("api_set_ship"));
				GlobalContext.addNewShip(json.getJsonObject("api_unset_ship"));
			}
				break;
			case KAISOU_MARRIAGE:
				GlobalContext.addNewShip(api_data);
				break;
			case KAISOU_SLOTSET:
			case KAISOU_UNSETSLOT_ALL:
			case KAISOU_SLOTSET_EX:
			case KAISOU_REMODELING:
				break;

			case AIRBASE_CHANGENAME:
			case AIRBASE_SUPPLY:
			case AIRBASE_EXPAND:
			case AIRBASE_INFORMATION:
			case AIRBASE_SETPLANE:
				break;

			case SORTIE_CONDITIONS:
			case MXLTVKPYUKLH:
			case UPDATECOMMENT:
			case GET_INCENTIVE:
			case UNSETSLOT:
			case ITEMUSE:
			case PAYCHECK:
			case PAYITEM:
			case PAYITEM_USE:
			case RECORD:
			case PICTURE_BOOK:
			case MUSIC_LIST:
			case MUSIC_PLAY:
			case RADIO_PLAY:
			case SET_PORTBGM:
			case FURNITURE_BUY:
			case FURNITURE_CHANGE:
			case EVENTMAP_RANK_SELECT:
				break;

			case COMBINED:
				break;
			case USEITEM:
				mainRoom.doUseitem(data, api_data);
				break;
			case MASTERDATA:
				GlobalContext.setMasterData(new MasterDataDto((JsonObject) api_data));
				break;
			case REQUIRE_INFO:
				mainRoom.doRequireInfo(data, api_data);
				break;
			case MAPINFO:
				GlobalContext.setMapinfo(new MapinfoDto(((JsonObject) api_data).getJsonArray("api_map_info")));//地图详情
				GlobalContext.setAirbase(new AirbaseDto(((JsonObject) api_data).getJsonArray("api_air_base")));//路基详情
				break;
			case MATERIAL:
				mainRoom.doMaterial(data, api_data);
				break;
			case PORT:
				mainRoom.doPort(data, api_data);
				break;
			case BASIC:
				mainRoom.doBasic(data, api_data);
				break;
			case SLOT_ITEM:
				mainRoom.doSlotItem(data, api_data);
				break;
			case SHIP2: {
				shipMap.clear();
				data.getJsonObject().getJsonArray("api_data").forEach(GlobalContext::addNewShip);
				FunctionUtils.forEach(deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, data.getJsonObject().get("api_data_deck")));
			}
				break;

			case PRACTICE_LIST:
			case PRACTICE_CHANGE_MATCHING_KIND:
				break;
			case PRACTICE_ENEMYINFO:
				GlobalContext.setPracticeEnemy(new PracticeEnemyDto((JsonObject) api_data));
				break;

			/*------------------------------------------------------ 战斗部分 --------------------------------------------------------------------------------------------------------*/

			case BATTLE_PRACTICE_DAY:
				memoryList.add(new PracticeBattleDayDto(data, (JsonObject) api_data));
				break;
			case BATTLE_PRACTICE_MIDNIGHT:
				memoryList.add(new PracticeBattleMidnightDto(data, (JsonObject) api_data));
				break;
			case BATTLE_PRACTICE_RESULT:
				memoryList.add(new PracticeBattleResultDto(data, (JsonObject) api_data));
				break;

			case BATTLE_SHIPDECK: {
				JsonObject json = (JsonObject) api_data;
				json.getJsonArray("api_ship_data").forEach(GlobalContext::addNewShip);
				FunctionUtils.forEach(deckRooms, FunctionUtils.getConsumer(DeckRoom::doDeck, data, json.get("api_deck_data")));
			}
				memoryList.add(new InfoBattleShipdeckDto(data, (JsonObject) api_data));
				break;
			case BATTLE_GOBACK_PORT:
				memoryList.add(new InfoBattleGobackPortDto());
				break;
			case BATTLE_START_AIR_BASE:
				memoryList.add(new InfoBattleStartAirBaseDto(data, (JsonObject) api_data));
				break;

			case BATTLE_START:
				memoryList.add(new InfoBattleStartDto(combined, data, (JsonObject) api_data));
				break;
			case BATTLE_NEXT:
				memoryList.add(new InfoBattleNextDto(data, (JsonObject) api_data));
				break;

			case BATTLE_AIRBATTLE:
				memoryList.add(new BattleAirbattleDto(data, (JsonObject) api_data));
				break;
			case BATTLE_AIRBATTLE_LD:
				memoryList.add(new BattleAirbattleLDDto(data, (JsonObject) api_data));
				break;
			case BATTLE_DAY:
				memoryList.add(new BattleDayDto(data, (JsonObject) api_data));
				break;
			case BATTLE_MIDNIGHT:
				memoryList.add(new BattleMidnightDto(data, (JsonObject) api_data));
				break;
			case BATTLE_MIDNIGHT_SP:
				memoryList.add(new BattleMidnightSPDto(data, (JsonObject) api_data));
				break;
			case BATTLE_RESULT:
				memoryList.add(new InfoBattleResultDto(data, (JsonObject) api_data));
				break;

			case COMBINEBATTLE_AIRBATTLE:
				memoryList.add(new CombineBattleAirbattleDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_AIRBATTLE_LD:
				memoryList.add(new CombineBattleAirbattleLDDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_MIDNIGHT_SP:
				memoryList.add(new CombinebattleMidnightSPDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_RESULT:
				memoryList.add(new InfoCombinebattleResultDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_DAY:
				memoryList.add(new CombinebattleDayDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_DAY_WATER:
				memoryList.add(new CombinebattleDayWaterDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_MIDNIGHT:
				memoryList.add(new CombinebattleMidnightDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_EC_DAY:
				memoryList.add(new CombinebattleECDayDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_EACH_DAY:
				memoryList.add(new CombineBattleEachDayDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_EACH_DAY_WATER:
				memoryList.add(new CombineBattleEachDayWaterDto(data, (JsonObject) api_data));
				break;
			case COMBINEBATTLE_EC_MIDNIGHT:
				memoryList.add(new CombinebattleECMidnightDto(data, (JsonObject) api_data));
				break;
		}
	}
}
