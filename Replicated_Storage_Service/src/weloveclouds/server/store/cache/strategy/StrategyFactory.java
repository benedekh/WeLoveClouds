package weloveclouds.server.store.cache.strategy;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * To create a displacement strategy based on its name.
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
                LOGGER.error(StringUtils.join(" ", "Unrecognized displacement startegy:",
                        displacementStrategyName));
                break;
        }

        return displacementStrategy;
    }

}
