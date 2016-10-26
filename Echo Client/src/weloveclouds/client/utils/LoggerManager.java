package weloveclouds.client.utils;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogManager {

  private static final LogManager instance = new LogManager();

  private Set<Logger> managedLoggers;

  private LogManager() {
    this.managedLoggers = new HashSet<>();
  }

  public static LogManager getInstance() {
    return instance;
  }

  public void setLogLevel(Level level) {
    for (Logger logger : managedLoggers) {
      logger.setLevel(level);
    }
  }

  public Logger createLogger(Class clazz) {
    Logger logger = Logger.getLogger(clazz);
    managedLoggers.add(logger);
    return logger;
  }

}
