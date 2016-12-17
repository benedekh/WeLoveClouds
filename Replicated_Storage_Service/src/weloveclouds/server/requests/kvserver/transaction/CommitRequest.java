package weloveclouds.server.requests.kvserver.transaction;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;

public class CommitRequest extends AbstractRequest<CommitRequest.Builder> {

    protected CommitRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        // TODO Auto-generated method stub
        return null;
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public AbstractRequest<Builder> build() {
            return new CommitRequest(this);
        }
    }
}
