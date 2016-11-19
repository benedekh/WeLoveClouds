package weloveclouds.server.store.cache.strategy;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * To ccreate a displacement startegy based on its name.
 * 
 * @author Benedek
 */
public class StrategyFactory {

    private static final Logger LOGGER = Logger.getLogger(StrategyFactory.class);

    /**
     * Creates a {@link DisplacementStrategy} from the respective name.
     * 
     * @return the strategy or null if its name wasn't matched
     */
    public static DisplacementStrategy createDisplacementStrategy(String displacementStrategyName) {
        DisplacementStrategy displacementStrategy = null;

        switch (displacementStrategyName) {
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
                LOGGER.error(CustomStringJoiner.join(" ", "Unrecognized displacement startegy:",
                        displacementStrategyName));
                break;
        }

        return displacementStrategy;
    }

}
