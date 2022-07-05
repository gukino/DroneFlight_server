package hku.droneflight.util;

import hku.droneflight.entity.User;

/**
 *
 */

public class Url {
    public String streamUrl;
    public String resultUrl;
    public User user;

    public Url(String streamUrl, String resultUrl) {
        this.streamUrl = streamUrl;
        this.resultUrl = resultUrl;
    }
}
