package app_kvEcs;

import weloveclouds.ecs.configuration.providers.AuthConfigurationProvider;

public class ECSClient {
    public static void main(String[] args) throws Exception{
        AuthConfigurationProvider auth = AuthConfigurationProvider.getInstance();
        System.out.println(auth.getPassword());
    }
}