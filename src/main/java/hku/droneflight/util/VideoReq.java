package hku.droneflight.util;

/**
 *
 */

public class VideoReq extends RequestMsg{
    public Integer id;
    public Integer uid;
    public String name;
    public String description;

    public VideoReq(Integer id, Integer uid, String name, String description) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.description = description;
    }
}
