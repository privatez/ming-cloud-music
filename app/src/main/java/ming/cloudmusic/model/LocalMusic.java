package ming.cloudmusic.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "LocalMusic")
public class LocalMusic implements Parcelable {

    @Column(name = "id",isId = true)
    private long id;


    @Column(name = "title")
    private String title;


    @Column(name = "title")
    private String name;


    @Column(name = "path")
    private String path;


    @Column(name = "shortPath")
    private String shortPath;


    @Column(name = "artlist")
    private String artlist;


    @Column(name = "album")
    private String album;


    @Column(name = "duration")
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

    public String getShortPath() {
        return shortPath;
    }

    public void setShortPath(String shortPath) {
        this.shortPath = shortPath;
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
        dest.writeString(this.shortPath);
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
        this.shortPath = in.readString();
        this.artlist = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
    }

    public static final Creator<LocalMusic> CREATOR = new Creator<LocalMusic>() {
        public LocalMusic createFromParcel(Parcel source) {
            return new LocalMusic(source);
        }

        public LocalMusic[] newArray(int size) {
            return new LocalMusic[size];
        }
    };
}
