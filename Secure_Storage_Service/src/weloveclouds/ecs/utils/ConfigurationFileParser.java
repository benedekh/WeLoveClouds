package weloveclouds.ecs.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.exceptions.configuration.InvalidConfigurationException;
import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2016-11-21.
 */
public class ConfigurationFileParser implements IParser<List<StorageNode>, File> {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationFileParser.class);

    @Override
    public List<StorageNode> parse(File configFile) {
        List<StorageNode> storageNodes = new ArrayList<>();
        String line;
        try (InputStream fileInputStream = new FileInputStream(configFile);
             InputStreamReader isr = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            while ((line = bufferedReader.readLine()) != null) {
                storageNodes.add(parseLine(line));
            }
        } catch (InvalidConfigurationException | IOException ex) {
            LOGGER.warn(ex.getMessage());
        }
        return storageNodes;
    }

    private StorageNode parseLine(String line) throws InvalidConfigurationException {
        StorageNode storageNode = null;
        Pattern lineRegex = Pattern.compile("(?<name>\\w+) ?" + "(?<ip>[0-9.]+) ?" + "" +
                "(?<kvServerPort>[0-9]+) ?" + "(?<ecsChannelPort>[0-9]+) ?" + "" +
                "(?<kvChannelPort>[0-9]+)$");
        ServerConnectionInfo serverConnectionInfo;
        ServerConnectionInfo ecsChannelConnectionInfo;
        ServerConnectionInfo kvChannelConnectionInfo;
        try {
            Matcher matcher = lineRegex.matcher(line);
            if (matcher.find()) {
                if (matcher.group("name") != null && matcher.group("ip") != null && matcher.group
                        ("kvServerPort") != null && matcher.group("ecsChannelPort") != null
                        && matcher.group("kvChannelPort") != null) {
                    serverConnectionInfo = new ServerConnectionInfo.Builder()
                            .ipAddress(matcher.group("ip"))
                            .port(Integer.parseInt(matcher.group("kvServerPort")))
                            .build();
                    ecsChannelConnectionInfo = new ServerConnectionInfo.Builder()
                            .ipAddress(matcher.group("ip"))
                            .port(Integer.parseInt(matcher.group("ecsChannelPort")))
                            .build();
                    kvChannelConnectionInfo = new ServerConnectionInfo.Builder()
                            .ipAddress(matcher.group("ip"))
                            .port(Integer.parseInt(matcher.group("kvChannelPort")))
                            .build();
                    storageNode = new StorageNode.Builder()
                            .name(matcher.group("name"))
                            .serverConnectionInfo(serverConnectionInfo)
                            .ecsChannelConnectionInfo(ecsChannelConnectionInfo)
                            .kvChannelConnectionInfo(kvChannelConnectionInfo)
                            .build();
                }
            } else {
                throw new InvalidConfigurationException("Unable to parse the provided configuration " +
                        "file");
            }
        } catch (UnknownHostException | NumberFormatException ex) {
            throw new InvalidConfigurationException("Unable to parse the provided configuration " +
                    "file", ex);
        }
        return storageNode;
    }
}
