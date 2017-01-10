package weloveclouds.commons.monitoring.models;

import java.util.List;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a metric for statistical logging.
 * 
 * @author Benoit
 */
public class Metric {
    private static final String METRIC_PART_DELIMITER = ".";
    private Service service;
    private String name;

    protected Metric(Builder builder) {
        this.service = builder.service;
        this.name = builder.name;
    }

    public String toString() {
        return StringUtils.join(METRIC_PART_DELIMITER, service, name);
    }

    /**
     * Builder pattern for creating a {@link Metric} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private Service service;
        private String name;

        public Builder service(Service service) {
            this.service = service;
            return this;
        }

        public Builder name(List<String> nameParts) {
            this.name = StringUtils.join(METRIC_PART_DELIMITER, nameParts);
            return this;
        }

        public Metric build() {
            return new Metric(this);
        }
    }
}
