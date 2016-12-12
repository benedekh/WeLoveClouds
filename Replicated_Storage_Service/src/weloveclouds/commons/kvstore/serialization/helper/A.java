package weloveclouds.commons.kvstore.serialization.helper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import org.junit.Test;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtil;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.commons.serialization.utils.XMLPatternUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

public class A {

    @Test
    public void test() throws DeserializationException, IOException {
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange writeRange = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("a")).build();
        Set<HashRange> readRanges = new HashSet<>(Arrays.asList(range1, writeRange));
        RingMetadataPart metadataPart1 =
                new RingMetadataPart.Builder()
                        .connectionInfo(new ServerConnectionInfo.Builder().ipAddress("localhost")
                                .port(8080).build())
                        .readRanges(readRanges).writeRange(writeRange).build();

        RingMetadataPart metadataPart2 = new RingMetadataPart.Builder().connectionInfo(
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8082).build())
                .readRanges(readRanges).build();

        RingMetadata s =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));

        RingMetadataSerializer a = new RingMetadataSerializer();
        String serialized = a.serialize(s).toString();

        Matcher matcher = XMLPatternUtils.getRegexFromToken("RING_METADATA").matcher(serialized);
        matcher.find();
        String group = matcher.group(XMLPatternUtils.XML_NODE);

        RingMetadataDeserializer b = new RingMetadataDeserializer();
        RingMetadata c = b.deserialize(group);
        System.out.println(c);
        System.out.println(s.equals(c));

    }

}
