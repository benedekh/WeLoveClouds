package app_kvEcs;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.metadata.StorageNode;
import weloveclouds.ecs.models.ssh.AuthInfos;
import weloveclouds.ecs.models.ssh.LaunchJar;
import weloveclouds.ecs.services.JshSecureShellService;
import weloveclouds.hashing.models.HashRange;

public class ECSClient {
    public static void main(String[] args) throws Exception{
        JshSecureShellService secureShellService = new JshSecureShellService();
        AuthInfos authInfos = new AuthInfos.AuthInfosBuilder().username("dev").password
                ("dev123*").build();
        ServerConnectionInfo connectionInfo = new ServerConnectionInfo
                .ServerConnectionInfoBuilder().ipAddress("192.168.229.128").port(22).build();
        StorageNode host = new StorageNode("id", connectionInfo, new HashRange(null, null));

        secureShellService.execute(new LaunchJar("").on(host).withAuthInfos(authInfos));
    }
}