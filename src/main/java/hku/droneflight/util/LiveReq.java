package hku.droneflight.util;

/**
 * 直播请求类
 */

public class LiveReq extends RequestMsg{

    public String name;
    public String description;
    public String streamUrl;
    public String resultUrl;

    public LiveReq(String name, String description, String streamUrl, String resultUrl) {
        this.name = name;
        this.description = description;
        this.streamUrl = streamUrl;
        this.resultUrl = resultUrl;
    }
}
