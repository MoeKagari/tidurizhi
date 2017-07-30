package tdrz.dto.word;

import tdrz.dto.AbstractWord;

/**
 * 八种资源
 * @author MoeKagari
 */
public class MaterialDto extends AbstractWord {
	/**	 油弹钢铝,高修建造材,桶,开发资材,螺丝	 */
	private final int fuel, ammo, metal, bauxite, burner, bucket, research, screw;

	public MaterialDto(int[] material) {
		this.fuel = material[0];
		this.ammo = material[1];
		this.metal = material[2];
		this.bauxite = material[3];
		this.burner = material[4];
		this.bucket = material[5];
		this.research = material[6];
		this.screw = material[7];
	}

	public int[] getMaterialForWindow() {
		return new int[] { this.fuel, this.metal, this.bucket, this.research, this.ammo, this.bauxite, this.screw, this.burner };
	}

	public int[] getMaterial() {
		return new int[] { this.fuel, this.ammo, this.metal, this.bauxite, this.burner, this.bucket, this.research, this.screw };
	}

	public static String[] getMaterialStrings() {
		return new String[] { "油", "弹", "钢", "铝", "高修建造材", "高速修复材", "开发资材", "螺丝" };
	}
}
