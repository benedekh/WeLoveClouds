package weloveclouds.communication.utils.detector;

import weloveclouds.commons.serialization.models.XMLTokens;

public class KVTransactionMessageFrameDetector extends AbstractMessageFrameDetector {

    public KVTransactionMessageFrameDetector() {
        super(XMLTokens.KVTRANSACTION_MESSAGE);
    }

}
