package hku.droneflight.util;

/**
 *
 */

public class StringReq extends RequestMsg{
    public String videoId;

    public StringReq(String string) {
        this.videoId = string;
    }

    public StringReq() {

    }
}
