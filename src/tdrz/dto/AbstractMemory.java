package tdrz.dto;

import java.io.Serializable;

public abstract class AbstractMemory extends AbstractData implements Serializable {
	private static final long serialVersionUID = 1L;

	public long getTime() {
		return -1;
	}

	public boolean isBattle() {
		return false;
	}
}
