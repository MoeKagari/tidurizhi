package tdrz.dto.translator;

import java.util.Map;
import java.util.function.Function;

import tdrz.dto.word.MasterDataDto;
import tdrz.dto.word.MasterDataDto.MasterMissionDto;
import tdrz.dto.word.MasterDataDto.MasterShipDto;
import tdrz.dto.word.MasterDataDto.MasterSlotitemDto;
import tdrz.dto.word.MasterDataDto.MasterUserItemDto;
import tdrz.update.GlobalContext;
import tool.FunctionUtils;

public class MasterDataTranslator {
	public static String getMissionName(int id) {
		return FunctionUtils.notNull(getMasterMissionDto(id), MasterMissionDto::getName, "");
	}

	public static String getShipName(int id) {
		return FunctionUtils.notNull(getMasterShipDto(id), MasterShipDto::getName, "");
	}

	public static String getSlotitemName(int id) {
		return FunctionUtils.notNull(getMasterSlotitemDto(id), MasterSlotitemDto::getName, "");
	}

	public static String getUseitemName(int id) {
		return FunctionUtils.notNull(getMasterUserItemDto(id), MasterUserItemDto::getName, "");
	}

	public static MasterShipDto getMasterShipDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterShipDataMap);
	}

	public static MasterSlotitemDto getMasterSlotitemDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterSlotitemDataMap);
	}

	public static MasterMissionDto getMasterMissionDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterMissionDataMap);
	}

	public static MasterUserItemDto getMasterUserItemDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterUserItemDtoMap);
	}

	private static <T> T getMasterData(int id, Function<MasterDataDto, Map<Integer, T>> fun) {
		return FunctionUtils.notNull(GlobalContext.getMasterData(), md -> fun.apply(md).get(id), null);
	}
}
