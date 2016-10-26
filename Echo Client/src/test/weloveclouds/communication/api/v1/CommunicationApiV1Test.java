package test.weloveclouds.communication.api.v1;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit
 */
public class CommunicationApiV1Test {
    private ICommunicationApi communicationApi;

    @Mock
    CommunicationService communicationServiceMock;

    @Before
    public void setUp(){
        communicationApi = new CommunicationApiV1(communicationServiceMock);
    }

    @Test
    public void apiDisconnectShouldCallTheCommunicationServiceMethod() throws Exception{
        communicationApi.disconnect();
        verify(communicationServiceMock, times(1)).disconnect();
    }
}
