package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFO;
import static weloveclouds.commons.serialization.models.XMLTokens.READ_RANGES;
import static weloveclouds.commons.serialization.models.XMLTokens.WRITE_RANGE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link String} to a {@link RingMetadataPart}.
 * 
 * @author Benedek, Hunton
 */
public class RingMetadataPartDeserializer implements IDeserializer<RingMetadataPart, String> {

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private IDeserializer<Set<HashRange>, String> hashRangesDeserializer =
            new HashRangesSetDeserializer();
    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public RingMetadataPart deserialize(String from) throws DeserializationException {
        RingMetadataPart deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                ServerConnectionInfo connectionInfo = deserializeConnectionInfo(from);
                Set<HashRange> readRanges = deserializeReadRanges(from);
                HashRange writeRange = deserializeWriteRange(from);

                if (connectionInfo == null && (readRanges == null || readRanges.isEmpty())
                        && writeRange == null) {
                    return deserialized;
                }

                deserialized = new RingMetadataPart.Builder().connectionInfo(connectionInfo)
                        .readRanges(readRanges).writeRange(writeRange).build();
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

    private ServerConnectionInfo deserializeConnectionInfo(String from)
            throws DeserializationException {
        Matcher connectionInfoMatcher = getRegexFromToken(CONNECTION_INFO).matcher(from);
        if (connectionInfoMatcher.find()) {
            return connectionInfoDeserializer.deserialize(connectionInfoMatcher.group(XML_NODE));
        }
        return null;
    }

    private Set<HashRange> deserializeReadRanges(String from) throws DeserializationException {
        HashSet<HashRange> defaultReturnValue = new HashSet<>();

        Matcher readRangesMatcher = getRegexFromToken(READ_RANGES).matcher(from);
        if (readRangesMatcher.find()) {
            Set<HashRange> deserialized =
                    hashRangesDeserializer.deserialize(readRangesMatcher.group(XML_NODE));
            return deserialized == null ? defaultReturnValue : deserialized;
        }

        return defaultReturnValue;
    }

    private HashRange deserializeWriteRange(String from) throws DeserializationException {
        Matcher writeRangeMatcher = getRegexFromToken(WRITE_RANGE).matcher(from);
        if (writeRangeMatcher.find()) {
            return hashRangeDeserializer.deserialize(writeRangeMatcher.group(XML_NODE));
        }
        return null;
    }

}
