package testing.weloveclouds.commons.serialization;

import org.junit.Before;

import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2016-12-18.
 */
public class StorageNodeSerializationDeserializationTest {
    ISerializer<AbstractXMLNode, StorageNode> storageNodeSerializer;

    @Before
    public void setUp(){

    }
}
