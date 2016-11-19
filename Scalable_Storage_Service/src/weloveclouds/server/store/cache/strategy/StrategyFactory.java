package weloveclouds.server.store.cache.strategy;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

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
                LOGGER.error(join(" ", "Unrecognized displacement startegy:",
                        displacemenetStrategyName));
                break;
        }

        return displacementStrategy;
    }

}
