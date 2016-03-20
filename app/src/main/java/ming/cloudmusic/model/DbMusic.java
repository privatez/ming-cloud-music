package ming.cloudmusic.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "DbMusic")
public class DbMusic implements Parcelable {

    public static final int ISPLAYING = 1;

    public static final int ISHISTORY = 31;

    public static final int DEFAULT_VALUE = 0;

    @Column(name = "id", isId = true)
    private long id;


    @Column(name = "title")
    private String title;


    @Column(name = "name")
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

    @Column(name = "isPlaying")
    private int isPlaying;

    @Column(name = "isHistroy")
    private int isHistroy;

    @Column(name = "playedTime")
    private long playedTime;

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

    public int getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(int isPlaying) {
        this.isPlaying = isPlaying;
    }

    public int getIsHistroy() {
        return isHistroy;
    }

    public void setIsHistroy(int isHistroy) {
        this.isHistroy = isHistroy;
    }

    public long getPlayedTime() {
        return playedTime;
    }

    public void setPlayedTime(long playedTime) {
        this.playedTime = playedTime;
    }

    @Override
    public String toString() {
        return "DbMusic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", shortPath='" + shortPath + '\'' +
                ", artlist='" + artlist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", isPlaying=" + isPlaying +
                ", isHistroy=" + isHistroy +
                ", playedTime=" + playedTime +
                '}';
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
        dest.writeInt(this.isPlaying);
        dest.writeInt(this.isHistroy);
        dest.writeLong(this.playedTime);
    }

    public DbMusic() {
    }

    protected DbMusic(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.shortPath = in.readString();
        this.artlist = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
        this.isPlaying = in.readInt();
        this.isHistroy = in.readInt();
        this.playedTime = in.readLong();
    }

    public static final Creator<DbMusic> CREATOR = new Creator<DbMusic>() {
        @Override
        public DbMusic createFromParcel(Parcel source) {
            return new DbMusic(source);
        }

        @Override
        public DbMusic[] newArray(int size) {
            return new DbMusic[size];
        }
    };
}
