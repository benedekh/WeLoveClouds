package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KV_ENTRY;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.utils.FileUtility;

/**
 * A deserializer which converts a {@link MovableStorageUnit} to a {@link String}.
 * 
 * @author Benedek
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

                if (deserializedEntries.isEmpty()) {
                    throw new DeserializationException(CustomStringJoiner.join("",
                            "Unable to extract storage unit entries from:", from));
                }

                deserialized =
                        new MovableStorageUnit(deserializedEntries, FileUtility.createDummyPath());
            } catch (Exception ex) {
                new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
