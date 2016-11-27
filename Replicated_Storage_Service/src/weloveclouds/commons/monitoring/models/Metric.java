package weloveclouds.commons.monitoring.models;

import java.util.List;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Created by Benoit on 2016-11-27.
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
        return CustomStringJoiner.join(METRIC_PART_DELIMITER, service.toString(), name);
    }

    public static class Builder {
        private Service service;
        private String name;

        public Builder service(Service service) {
            this.service = service;
            return this;
        }

        public Builder name(List<String> nameParts) {
            this.name = CustomStringJoiner.join(METRIC_PART_DELIMITER, nameParts);
            return this;
        }

        public Metric build() {
            return new Metric(this);
        }
    }
}
