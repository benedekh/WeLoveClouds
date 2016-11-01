package weloveclouds.kvstore.communication.api;

import weloveclouds.communication.services.CommunicationService;
import weloveclouds.kvstore.IKVMessage;

public class KVCommunicationApi implements KVCommInterface {

    private CommunicationService communicationService;

    public KVCommunicationApi(CommunicationService communicationService) {
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
    public IKVMessage put(String key, String value) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IKVMessage get(String key) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
