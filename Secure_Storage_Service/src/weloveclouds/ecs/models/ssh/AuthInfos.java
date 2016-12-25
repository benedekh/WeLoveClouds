package weloveclouds.ecs.models.ssh;

import weloveclouds.ecs.exceptions.authentication.InvalidAuthenticationInfosException;

import static weloveclouds.ecs.models.ssh.AuthenticationMethod.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class AuthInfos {
    private String username;
    private String password;
    private String privateKey;

    public AuthInfos(AuthInfosBuilder authInfosBuilder) {
        this.username = authInfosBuilder.username;
        this.password = authInfosBuilder.password;
        this.privateKey = authInfosBuilder.privateKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return password == null || password == "" ? PRIVATE_KEY : PASSWORD;
    }

    public static class AuthInfosBuilder {
        private String username;
        private String password;
        private String privateKey;

        public AuthInfosBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthInfosBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AuthInfosBuilder privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public AuthInfos build() throws InvalidAuthenticationInfosException {
            if (!isNullOrEmpty(username) && (!isNullOrEmpty(password) || !isNullOrEmpty(privateKey))) {
                return new AuthInfos(this);
            } else {
                throw new InvalidAuthenticationInfosException();
            }
        }

        private boolean isNullOrEmpty(String value) {
            return value == null || value.isEmpty();
        }
    }
}
