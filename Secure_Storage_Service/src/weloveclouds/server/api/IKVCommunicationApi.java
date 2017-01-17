package weloveclouds.server.api;

import weloveclouds.communication.api.ICommunicationApi;

/**
 * Merges the capabilities of the IKVServerApi and the ICommunicationApi so the client is able to
 * handle the higher-level KV commands, along with the lower level byte send and receive commands.
 * (Interface extension design pattern)
 * 
 * @author Benedek
 */
public interface IKVCommunicationApi extends IKVServerApi, ICommunicationApi {

}
