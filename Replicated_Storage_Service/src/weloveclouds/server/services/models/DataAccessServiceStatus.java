package weloveclouds.server.services.models;

import weloveclouds.server.services.DataAccessService;

/**
 * Represents the recent status of the {@link DataAccessService}.
 * 
 * @author Benedek
 */
public enum DataAccessServiceStatus {

    STARTED, STOPPED, WRITELOCK_ACTIVE, WRITELOCK_INACTIVE;

}
