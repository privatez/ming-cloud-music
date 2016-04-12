package ming.cloudmusic.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lhy on 2016/4/12.
 */
public class CloudDbmusic extends BmobObject {

    private String title;
    private String name;
    private String artlist;
    private String album;
    private int duration;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtlist() {
        return artlist;
    }

    public void setArtlist(String artlist) {
        this.artlist = artlist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CloudDbmusic{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", artlist='" + artlist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", url='" + url + '\'' +
                '}';
    }
}
