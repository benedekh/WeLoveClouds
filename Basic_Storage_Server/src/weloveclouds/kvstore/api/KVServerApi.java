package weloveclouds.kvstore.api;

import weloveclouds.communication.services.CommunicationService;
import weloveclouds.kvstore.IKVMessage;

public class KVServerApi implements IKVServerApi {

    private CommunicationService communicationService;

    public KVServerApi(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @Override
    public void connect() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    synchronized public IKVMessage put(String key, String value) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    synchronized public IKVMessage get(String key) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
