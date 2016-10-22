package weloveclouds.communication.models;

import java.nio.charset.Charset;

/**
 * Created by Benoit on 2016-10-21.
 */
public class Response {
    private byte[] content;

    public Response(){}

    public Response(byte[] content){
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String toString(){
        return new String(content, Charset.forName("UTF-8"));
    }

    public Response withContent(byte[] content){
        setContent(content);
        return this;
    }
}

