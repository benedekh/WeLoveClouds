package weloveclouds.ecs.utils;

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

import org.apache.log4j.Logger;

import weloveclouds.cli.utils.UserInputReader;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.exceptions.InvalidConfigurationException;
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
            LOGGER.error(ex.getMessage());
        }
        return storageNodes;
    }

    private StorageNode parseLine(String line) throws InvalidConfigurationException {
        StorageNode storageNode = null;
        Pattern lineRegex = Pattern.compile("(?<name>\\w+) ?" + "(?<ip>[0-9.]+) ?" + "" +
                "(?<port>[0-9]+)$");
        ServerConnectionInfo storageNodeNetworkInfos;
        try {
            Matcher matcher = lineRegex.matcher(line);
            if (matcher.find()) {
                if (matcher.group("name") != null && matcher.group("ip") != null && matcher.group("port") != null) {
                    storageNodeNetworkInfos = new ServerConnectionInfo.Builder()
                            .ipAddress(matcher.group("ip"))
                            .port(Integer.parseInt(matcher.group("port")))
                            .build();
                    storageNode = new StorageNode(matcher.group("name"), storageNodeNetworkInfos);
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
