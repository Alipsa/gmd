package se.alipsa.groovy.gmd;

import com.openhtmltopdf.util.Diagnostic;
import com.openhtmltopdf.util.XRLog;
import com.openhtmltopdf.util.XRLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;

/**
 * TODO: contribute this class to the openhtmltopdf project
 * https://github.com/danfickle/openhtmltopdf
 */
public class Log4jXRLogger implements XRLogger {

  private static final String DEFAULT_LOGGER_NAME = "org.openhtmltopdf.other";

  private static final Map<String, String> LOGGER_NAME_MAP;

  static {
    LOGGER_NAME_MAP = new HashMap<>();

    LOGGER_NAME_MAP.put(XRLog.CONFIG, "com.openhtmltopdf.config");
    LOGGER_NAME_MAP.put(XRLog.EXCEPTION, "com.openhtmltopdf.exception");
    LOGGER_NAME_MAP.put(XRLog.GENERAL, "com.openhtmltopdf.general");
    LOGGER_NAME_MAP.put(XRLog.INIT, "com.openhtmltopdf.init");
    LOGGER_NAME_MAP.put(XRLog.JUNIT, "com.openhtmltopdf.junit");
    LOGGER_NAME_MAP.put(XRLog.LOAD, "com.openhtmltopdf.load");
    LOGGER_NAME_MAP.put(XRLog.MATCH, "com.openhtmltopdf.match");
    LOGGER_NAME_MAP.put(XRLog.CASCADE, "com.openhtmltopdf.cascade");
    LOGGER_NAME_MAP.put(XRLog.XML_ENTITIES, "com.openhtmltopdf.load.xmlentities");
    LOGGER_NAME_MAP.put(XRLog.CSS_PARSE, "com.openhtmltopdf.cssparse");
    LOGGER_NAME_MAP.put(XRLog.LAYOUT, "com.openhtmltopdf.layout");
    LOGGER_NAME_MAP.put(XRLog.RENDER, "com.openhtmltopdf.render");
  }

  @Override
  public void log(String where, Level level, String msg) {
    LogManager.getLogger(getLoggerName(where)).log(toLog4JLevel(level), msg);
  }

  @Override
  public void log(String where, Level level, String msg, Throwable th) {
    LogManager.getLogger(getLoggerName(where)).log(toLog4JLevel(level), msg, th);
  }

  @Override
  public void setLevel(String logger, Level level) {
    throw new UnsupportedOperationException("log4j should be not be configured here");
  }

  @Override
  public boolean isLogLevelEnabled(Diagnostic diagnostic) {
    var lvl = toLog4JLevel(diagnostic.getLevel());
    var logger = LogManager.getLogger(getLoggerName(diagnostic.getLogMessageId().getWhere()));
    return logger.isEnabled(lvl);
  }

  @Override
  public void log(Diagnostic diagnostic) {
    var logger = LogManager.getLogger(getLoggerName(diagnostic.getLogMessageId().getWhere()));
    var level = toLog4JLevel(diagnostic.getLevel());
    String msg = diagnostic.getLogMessageId().getMessageFormat();
    logger.log(level, msg, diagnostic.getArgs());
  }

  private org.apache.logging.log4j.Level toLog4JLevel(Level level) {
    if (level == Level.SEVERE) {
      return org.apache.logging.log4j.Level.ERROR;
    } else if (level == Level.WARNING) {
      return org.apache.logging.log4j.Level.WARN;
    } else if (level == Level.INFO) {
      return org.apache.logging.log4j.Level.INFO;
    } else if (level == Level.CONFIG) {
      return org.apache.logging.log4j.Level.INFO;
    } else if (level == Level.FINE) {
      return org.apache.logging.log4j.Level.DEBUG;
    } else if (level == Level.FINER || level == Level.FINEST) {
      return org.apache.logging.log4j.Level.TRACE;
    } else {
      return org.apache.logging.log4j.Level.INFO;
    }
  }

  private String getLoggerName(String xrLoggerName) {
    return LOGGER_NAME_MAP.getOrDefault(xrLoggerName, DEFAULT_LOGGER_NAME);
  }
}
