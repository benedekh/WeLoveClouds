package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNIT;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link String} to a {@link Set<MovableStorageUnit>}.
 * 
 * @author Benedek, Hunton
 */
public class MovableStorageUnitsSetDeserializer
        implements IDeserializer<Set<MovableStorageUnit>, String> {

    private IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();

    @Override
    public Set<MovableStorageUnit> deserialize(String from) throws DeserializationException {
        Set<MovableStorageUnit> deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                deserialized = new HashSet<>();
                Matcher storageUnitMatcher = getRegexFromToken(STORAGE_UNIT).matcher(from);
                while (storageUnitMatcher.find()) {
                    deserialized.add(storageUnitDeserializer
                            .deserialize(storageUnitMatcher.group(XML_NODE)));
                }
                if(deserialized.isEmpty()){
                    return null;
                }
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
