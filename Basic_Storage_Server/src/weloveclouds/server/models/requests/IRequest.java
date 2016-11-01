package weloveclouds.server.models.requests;

import weloveclouds.server.models.responses.IResponse;

/**
 * Created by Benoit on 2016-10-31.
 */
public interface IRequest {
    IResponse execute();
}
