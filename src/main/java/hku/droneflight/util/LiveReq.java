package hku.droneflight.util;

import hku.droneflight.entity.User;

/**
 * 直播请求类
 */

public class LiveReq extends RequestMsg{

    public String name;
    public String description;
    public String streamUrl;
    public String resultUrl;
    public User user;

    public LiveReq(String name, String description, String streamUrl, String resultUrl,User user) {
        this.name = name;
        this.description = description;
        this.streamUrl = streamUrl;
        this.resultUrl = resultUrl;
        this.user = user;
    }
}
