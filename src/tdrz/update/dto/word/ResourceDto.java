package tdrz.update.dto.word;

import tdrz.update.dto.AbstractWord;
import tool.FunctionUtils;

/**
 * 八种资源
 * @author MoeKagari
 */
public class ResourceDto extends AbstractWord {
	private static final String[] TEXT = { "油", "弹", "钢", "铝", "高修建造材", "高速修复材", "开发资材", "螺丝" };
	private final int[] resource;

	public ResourceDto(int[] resource) {
		if (resource == null) {
			throw new RuntimeException("参数不能为null");
		}
		if (resource.length != 8) {
			throw new RuntimeException("参数长度应==8");
		}
		this.resource = FunctionUtils.arrayCopy(resource);
	}

	public int[] getResource() {
		return FunctionUtils.arrayCopy(this.resource);
	}

	public static String[] getResourceText() {
		return FunctionUtils.arrayCopy(TEXT);
	}

	public int[] getResourceForApplicationMain() {
		return new int[] { this.resource[0], this.resource[2], this.resource[5], this.resource[6], this.resource[1], this.resource[3], this.resource[7], this.resource[4] };
	}

	public static String[] getResourceTextForApplicationMain() {
		return new String[] { TEXT[0], TEXT[2], TEXT[5], TEXT[6], TEXT[1], TEXT[3], TEXT[7], TEXT[4] };
	}
}
