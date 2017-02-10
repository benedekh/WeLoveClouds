package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KV_ENTRY;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link String} to a {@link MovableStorageUnit}.
 * 
 * @author Benedek, Hunton
 */
public class MovableStorageUnitDeserializer implements IDeserializer<MovableStorageUnit, String> {

    private IDeserializer<KVEntry, String> kvEntryDeserializer = new KVEntryDeserializer();

    @Override
    public MovableStorageUnit deserialize(String from) throws DeserializationException {
        MovableStorageUnit deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                Map<String, String> deserializedEntries = new HashMap<>();
                Matcher entriesMatcher = getRegexFromToken(KV_ENTRY).matcher(from);
                while (entriesMatcher.find()) {
                    KVEntry deserializedEntry =
                            kvEntryDeserializer.deserialize(entriesMatcher.group(XML_NODE));
                    deserializedEntries.put(deserializedEntry.getKey(),
                            deserializedEntry.getValue());
                }
                deserialized =
                        new MovableStorageUnit(deserializedEntries, PathUtils.createDummyPath());
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
