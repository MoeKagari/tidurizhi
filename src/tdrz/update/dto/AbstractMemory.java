package tdrz.update.dto;

import java.io.Serializable;

public abstract class AbstractMemory extends AbstractData implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 此对象事件发生的时间 */
	public abstract long getTime();
}
