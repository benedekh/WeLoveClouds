package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerInitializationContextDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.ServerInitializationContextSerializer;
import weloveclouds.server.models.ServerInitializationContext;

public class ServerInitializationContextTest {

    private static final IDeserializer<ServerInitializationContext, String> contextDeserializer =
            new ServerInitializationContextDeserializer();
    private static final ISerializer<String, ServerInitializationContext> contextSerializer =
            new ServerInitializationContextSerializer();

    @Test
    public void testServerInitializationContextSerializationAndDeserialization()
            throws UnknownHostException, DeserializationException {
        ServerInitializationContext context = new ServerInitializationContext.Builder()
                .cacheSize(10).displacementStrategyName("LRU").build();

        String serializedContext = contextSerializer.serialize(context);
        ServerInitializationContext deserializedContext =
                contextDeserializer.deserialize(serializedContext);

        Assert.assertEquals(context.toString(), deserializedContext.toString());
        Assert.assertEquals(context, deserializedContext);
    }
}
