package weloveclouds.server.parsers;

import weloveclouds.server.models.ParsedMessage;

/**
 * Created by Benoit on 2016-10-30.
 */
public interface IMessageParser {
    ParsedMessage parse(byte[] rawMessage);
}
