package tdrz.core.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tdrz.core.logic.TimeString;

public class ShipGroup extends ArrayList<Integer> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(ShipGroup.class);
	private static final File file = new File(AppConstants.SHIP_GROUP_FILEPATH);
	private static final List<ShipGroup> SHIPGROUPS = new ArrayList<>();

	public static List<ShipGroup> get() {
		return SHIPGROUPS;
	}

	public static void load() {
		try (XMLDecoder de = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)))) {
			Object obj = de.readObject();
			if (obj instanceof List) {
				((List<?>) obj).forEach(value -> {
					if (value instanceof ShipGroup) {
						SHIPGROUPS.add((ShipGroup) value);
					}
				});
			}
		} catch (FileNotFoundException e) {

		}
	}

	public static void store() {
		try (XMLEncoder en = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)))) {
			en.writeObject(SHIPGROUPS);
		} catch (FileNotFoundException e) {
			LOG.warn("app配置保存失败", e);
		}
	}

	private String name;

	public ShipGroup() {
		this(String.valueOf(TimeString.getCurrentTime()));
	}

	public ShipGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
