package weloveclouds.communication.utils.detector;

import weloveclouds.commons.serialization.models.XMLTokens;

public class KVMessageFrameDetector extends AbstractMessageFrameDetector {

    public KVMessageFrameDetector() {
        super(XMLTokens.KVMESSAGE);
    }

}
