package ming.cloudmusic.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalMusic implements Parcelable {
    /**
     * ¸èÇúID
     */
    private long id;
    /**
     * ¸èÇútitle
     */
    private String title;
    /**
     * ¸èÇúÃû
     */
    private String name;
    /**
     * ¸èÇú´æ·ÅÂ·¾¶
     */
    private String path;
    /**
     * ¸èÇúµÄÑÝ³ªÕß
     */
    private String artlist;
    /**
     * ¸èÇú×¨¼­Ãû
     */
    private String album;
    /**
     * ¸èÇúÊ±³¤
     */
    private int duration;

    @Override
    public String toString() {
        return "LocalMusic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", artlist='" + artlist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.artlist);
        dest.writeString(this.album);
        dest.writeInt(this.duration);
    }

    public LocalMusic() {
    }

    protected LocalMusic(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.artlist = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
    }

    public static final Parcelable.Creator<LocalMusic> CREATOR = new Parcelable.Creator<LocalMusic>() {
        public LocalMusic createFromParcel(Parcel source) {
            return new LocalMusic(source);
        }

        public LocalMusic[] newArray(int size) {
            return new LocalMusic[size];
        }
    };
}
