package client;


import weloveclouds.communication.api.v1.IKVServerApi;
import weloveclouds.kvstore.models.IKVMessage;

public class KVStore implements IKVServerApi {

	
	/**
	 * Initialize DataAccessService with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		
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
