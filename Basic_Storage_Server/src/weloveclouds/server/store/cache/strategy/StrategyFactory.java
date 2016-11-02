package weloveclouds.server.store.cache.strategy;

public class StrategyFactory {

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
        }

        return displacementStrategy;
    }

}
