package tdrz.core.internal;

public class TimerCounter {
	private final long endTime;
	private final long advance;
	private final long interval;
	private final boolean notifyZero;
	private boolean haveUpdatedAdvance = false;
	private boolean haveUpdated = false;
	private boolean haveUpdatedAgain = false;

	/**
	 * @param endTime 到点时间戳(毫秒)
	 * @param advance 提前多少时间提醒(秒),不大于0为不提醒
	 * @param notifyZero 到endTime时是否提醒
	 * @param interval endTime之后再次提醒间隔(秒),不大于0为不提醒
	 */
	public TimerCounter(long endTime, long advance, boolean notifyZero, long interval) {
		this.endTime = endTime;
		this.advance = advance;
		this.notifyZero = notifyZero;
		this.interval = interval;
	}

	public boolean needNotify(long currentTime) {
		long space = (this.endTime - currentTime) / 1000;
		if (this.advance > 0 && (space == this.advance - 1 || space == this.advance || space == this.advance + 1)) {
			if (this.haveUpdatedAdvance == false) {
				return this.haveUpdatedAdvance = true;
			}
		} else if (this.notifyZero && (space == -1 || space == 0 || space == 1)) {
			if (this.haveUpdated == false) {
				return this.haveUpdated = true;
			}
		} else if (this.interval > 0 && space < 0) {
			long spaceInterval = (-1 * space) % this.interval;
			if (spaceInterval == 0 || spaceInterval == 1 || spaceInterval == this.interval - 1) {
				if (this.haveUpdatedAgain == false) {
					return this.haveUpdatedAgain = true;
				}
			} else {
				this.haveUpdatedAgain = false;
			}
		}
		return false;
	}

	public boolean needNotify2(long currentTime) {
		long space = (this.endTime - currentTime) / 1000;
		if (this.advance > 0 && space == this.advance) {
			return true;
		} else if (this.notifyZero && space == 0) {
			return true;
		} else if (this.interval > 0 && space < 0 && (-1 * space) % this.interval == 0) {
			return true;
		}
		return false;
	}
}
