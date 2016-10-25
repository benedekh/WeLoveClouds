package weloveclouds.client.utils;

import java.util.List;

public abstract class StringJoiner {
  
  public static String join(String delimiter, List<String> fragments){
    StringBuffer buffer = new StringBuffer();
    for(String fragment: fragments){
      buffer.append(fragment);
      buffer.append(delimiter);
    }
    buffer.setLength(buffer.length()-delimiter.length());
    return buffer.toString();
  }

}
