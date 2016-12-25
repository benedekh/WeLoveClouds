package weloveclouds.server.services.datastore.models;

import weloveclouds.server.services.datastore.DataAccessService;

/**
 * Represents the recent status of the {@link DataAccessService}.
 * 
 * @author Benedek
 */
public enum DataAccessServiceStatus {

    STARTED, STOPPED, WRITELOCK_ACTIVE, WRITELOCK_INACTIVE;

}
