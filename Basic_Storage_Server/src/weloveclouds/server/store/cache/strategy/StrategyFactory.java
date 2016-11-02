package weloveclouds.server.store.cache.strategy;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

public class StrategyFactory {

    private static Logger LOGGER = Logger.getLogger(StrategyFactory.class);

    public static DisplacementStrategy createDisplacementStrategy(
            String displacemenetStrategyName) {
        DisplacementStrategy displacementStrategy = null;

        switch (displacemenetStrategyName) {
            case "FIFO":
                displacementStrategy = new FIFOStrategy();
                break;
            case "LRU":
                displacementStrategy = new LRUStrategy();
                break;
            case "LFU":
                displacementStrategy = new LFUStrategy();
                break;
            default:
                logError(join(" ", "Unrecognized displacement startegy:",
                        displacemenetStrategyName));
                break;
        }

        return displacementStrategy;
    }

    private static void logError(Object error) {
        synchronized (LOGGER) {
            LOGGER.error(error);
        }
    }

}
