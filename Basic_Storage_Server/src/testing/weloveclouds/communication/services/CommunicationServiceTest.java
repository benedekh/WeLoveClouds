package testing.weloveclouds.communication.services;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.exceptions.AlreadyConnectedException;
import weloveclouds.communication.exceptions.AlreadyDisconnectedException;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit
 */
@RunWith(MockitoJUnitRunner.class)
public class CommunicationServiceTest {
    private static final String VALID_SERVER_IP_ADDRESS = "131.159.52.2";
    private static final String INVALID_SERVER_IP_ADDRESS = "127.0.0.1";
    private static final int VALID_SERVER_PORT = 50000;
    private static final boolean CONNECTED_FLAG = true;
    private CommunicationService communicationService;
    private ServerConnectionInfo validServerConnectionInfos;
    private ServerConnectionInfo invalidServerConnectionInfos;
    private Socket socketFromInvalidServerInfos;

    @Mock
    Socket socketMock;
    @Mock
    SocketFactory socketFactoryMock;

    @Before
    public void setUp() throws Exception {
        communicationService = new CommunicationService(socketFactoryMock);

        validServerConnectionInfos = new ServerConnectionInfo.ServerConnectionInfoBuilder()
                .ipAddress(InetAddress.getByName(VALID_SERVER_IP_ADDRESS)).port(VALID_SERVER_PORT).build();
        invalidServerConnectionInfos = new ServerConnectionInfo.ServerConnectionInfoBuilder()
                .ipAddress(InetAddress.getByName(INVALID_SERVER_IP_ADDRESS)).port(VALID_SERVER_PORT)
                .build();

        when(socketFactoryMock.createTcpSocketFromInfo(validServerConnectionInfos))
                .thenReturn(new Socket(validServerConnectionInfos.getIpAddress(),
                        validServerConnectionInfos.getPort()));

        when(socketFactoryMock.createTcpSocketFromInfo(invalidServerConnectionInfos))
                .thenReturn(socketFromInvalidServerInfos);
    }

    @Test
    public void shouldConnectToRemoteServerUsingValidServerInfos() throws Exception {
        assertFalse(communicationService.isConnected());
        verifyConnectionTo(validServerConnectionInfos);
    }

    @Test(expected = IOException.class)
    public void shouldThrowWhenConnectingToRemoteServerUsingInvalidServerInfos() throws Exception {
        socketFromInvalidServerInfos = new Socket(invalidServerConnectionInfos.getIpAddress(),
                invalidServerConnectionInfos.getPort());

        assertFalse(communicationService.isConnected());

        try {
            communicationService.connectTo(invalidServerConnectionInfos);
        } finally {
            assertFalse(communicationService.isConnected());
        }
    }

    @Test
    public void shouldCloseTheConnectionOnDisconnect() throws Exception {
        assertFalse(communicationService.isConnected());
        verifyConnectionTo(validServerConnectionInfos);
        verifyDisconnection();
    }

    @Test(expected = AlreadyConnectedException.class)
    public void shouldThrowWhenTryingToConnectToServerButIsAlreadyConnected() throws Exception {
        assertFalse(communicationService.isConnected());
        verifyConnectionTo(validServerConnectionInfos);
        verifyConnectionTo(validServerConnectionInfos);
    }

    @Test(expected = ClientNotConnectedException.class)
    public void shouldThrowWhenTryingToSendWithoutBeingConnectedToServer() throws Exception {
        String message = "Should not be send";
        communicationService.send(message.getBytes());
    }

    @Test(expected = ClientNotConnectedException.class)
    public void shouldThrowWhenTryingToReceiveWithoutBeingConnectedToServer() throws Exception {
        communicationService.receive();
    }

    @Test
    public void shouldReadTheReceivedDataOnReception() throws Exception {
        final String RECEIVED_MESSAGE = "RECEIVED DATA";
        InputStream mockedSocketInputStream = new ByteArrayInputStream(RECEIVED_MESSAGE.getBytes());
        when(socketFactoryMock.createTcpSocketFromInfo(any(ServerConnectionInfo.class)))
                .thenReturn(socketMock);
        when(socketMock.getInputStream()).thenReturn(mockedSocketInputStream);
        when(socketMock.isConnected()).thenReturn(CONNECTED_FLAG);

        communicationService.connectTo(validServerConnectionInfos);

        assertThat(new String(communicationService.receive())).isEqualTo(RECEIVED_MESSAGE);
    }

    @Test(expected = AlreadyDisconnectedException.class)
    public void shouldThrowWhenDisconnectingWithoutBeingConnected() throws Exception {
        assertThat(communicationService.isConnected()).isFalse();
        verifyDisconnection();
    }

    @Test
    public void shouldBeAbleToConnectAndDisconnectMultipleTimes() throws Exception {
        when(socketFactoryMock.createTcpSocketFromInfo(validServerConnectionInfos))
                .thenReturn(new Socket(validServerConnectionInfos.getIpAddress(),
                        validServerConnectionInfos.getPort()))
                .thenReturn(new Socket(validServerConnectionInfos.getIpAddress(),
                        validServerConnectionInfos.getPort()));
        final int NUMBER_OF_CONNECTION_TESTS = 2;

        for (int i = 0; i < NUMBER_OF_CONNECTION_TESTS; i++) {
            verifyConnectionTo(validServerConnectionInfos);
            verifyDisconnection();
        }
    }

    private void verifyConnectionTo(ServerConnectionInfo remoteServer) throws Exception {
        communicationService.connectTo(remoteServer);
        assertThat(communicationService.isConnected()).isTrue();
    }

    private void verifyDisconnection() throws Exception {
        communicationService.disconnect();
        assertThat(communicationService.isConnected()).isFalse();
    }
}
