package weloveclouds.communication.utils.detector;

import weloveclouds.commons.serialization.models.XMLTokens;

public class KVAdminMessageFrameDetector extends AbstractMessageFrameDetector {

    public KVAdminMessageFrameDetector() {
        super(XMLTokens.KVADMIN_MESSAGE);
    }

}
