package tdrz.core.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tdrz.core.config.AppConstants;
import tool.function.FunctionUtils;

public class ApplicationLock {
	private static final File LOCK_FILE = new File(AppConstants.LOCKFILEPATH);
	private static final Logger LOG = LogManager.getLogger(ApplicationLock.class);

	private FileOutputStream fos;
	private FileChannel fch;
	private FileLock flock;
	private boolean isError;

	public ApplicationLock() {
		try {
			FunctionUtils.ifNotConsumer(LOCK_FILE.getParentFile(), p -> p.exists(), File::mkdirs);
			this.fos = new FileOutputStream(LOCK_FILE);
			this.fch = this.fos.getChannel();
			this.flock = this.fch.tryLock();
			this.isError = false;
		} catch (IOException e) {
			this.isError = true;
			LOG.warn("锁文件时发生错误", e);
		}
	}

	public boolean isLocked() {
		return this.flock != null;
	}

	public boolean isError() {
		return this.isError;
	}

	public void release() {
		try {
			if (this.flock != null) this.flock.release();
			if (this.fch != null) this.fch.close();
			if (this.fos != null) this.fos.close();
			LOCK_FILE.delete();
		} catch (IOException e) {
			LOG.warn("释放锁文件发生错误", e);
		}
	}
}
