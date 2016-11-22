package weloveclouds.evaluation.preparation.util;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class KeyCreatorUtility {

    public static String createBaseKeyNameForTargetFilePath(Path prefixPath, Path targetFilePath) {
        String prefix = prefixPath.toFile().getAbsolutePath();
        String pathWhosePrefixIsToBeRemoved = targetFilePath.toFile().getAbsolutePath().toString();
        StringBuffer sb = new StringBuffer(pathWhosePrefixIsToBeRemoved);

        int end = pathWhosePrefixIsToBeRemoved.lastIndexOf(prefix) + prefix.length();
        sb.replace(pathWhosePrefixIsToBeRemoved.lastIndexOf(prefix), end, "");

        return sb.toString();
    }

    public static String generate20BytesKey() {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(UUID.randomUUID().getMostSignificantBits());
        bb.putLong(UUID.randomUUID().getMostSignificantBits());
        String result22BytesInASCII =
                StringUtils.newStringUtf8(Base64.encodeBase64(bb.array(), false)).replace("=", "");
        String result20BytesInASCII =
                result22BytesInASCII.substring(0, result22BytesInASCII.length() - 2);
        return result20BytesInASCII;
    }

}
