package edu.nd.dronology.services.supervisor;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class LogCleaner {

	/**
	 * 30 days = 720 hours
	 */
	private static final int DELTE_TRESHOLD = 720;
	private static final ILogger LOGGER = LoggerProvider.getLogger(LogCleaner.class);

	public static void run() {
		try {

			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			System.out.println("Using logger config:" + ctx.getConfiguration().getConfigurationSource().getLocation());

			Map<String, Appender> appenders = ctx.getRootLogger().getAppenders();
			for (Appender app : appenders.values()) {
				if (app instanceof RollingFileAppender) {
					checkPath(((RollingFileAppender) app).getFileName().substring(0,
							((RollingFileAppender) app).getFileName().lastIndexOf("/")));
					return;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void checkPath(String substring) {
		
		File logFolder = new File(substring);
		File[] logFiles = logFolder.listFiles();
		LOGGER.info("Cleaning up log file directory: '" + logFolder.getAbsolutePath() + "'");
		for (File f : logFiles) {
			try {
				double lastModifified = (System.currentTimeMillis() - f.lastModified()) / 1000 / 60 / 60;

				if (lastModifified > DELTE_TRESHOLD && FilenameUtils.getExtension(f.getName()).equals("log")) {
					f.delete();
					LOGGER.info("Deleting log file older than " + (DELTE_TRESHOLD / 24) + " days:" + f.getAbsolutePath());
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

}
