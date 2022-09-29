package qwertzite.guerrillacity.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.util.McUtil;

public class ModLog {
	private static final String SERVER_SIGN = "S";
	private static final String CLIENT_SIGN = "C";
	
	private static final Logger SERVER = logInit(SERVER_SIGN);
	private static final Logger CLIENT = logInit(CLIENT_SIGN);
	
	private static final Logger logInit(String sign) {
		String name = GuerrillaCityCore.LOG_BASE_NAME + ":" + sign;
		return LogManager.getFormatterLogger(name);
	}
	
	/**
	 * Returns a {@link Logger} for current side.
	 *
	 * @return a logger instance
	 */
	private static Logger getLogger() {
		return McUtil.isServer() ? SERVER : CLIENT;
	}
	
	public static void log(Level level, String message, Throwable throwable) {
		Logger logger = getLogger();
		logger.log(level, message, throwable);
	}
	
	public static void log(Level level, String message, Object...params) {
		Logger logger = getLogger();
		logger.log(level, message, params);
	}
	
	public static void info(String message, Object...params) {
		ModLog.log(Level.INFO, message, params);
	}
}
