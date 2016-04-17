package ming.cloudmusic.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "DbMusic")
public class DbMusic implements Parcelable {

    /**
     * path 列
     */
    public static final String COLUMN_PATH = "path";

    /**
     * playSequence 列
     */
    public static final String COLUMN_PLAY_SEQUENCE = "playSequence";

    /**
     * histroySequence 列
     */
    public static final String COLUMN_HISTORY_SEQUENCE = "histroySequence";

    /**
     * 播放顺序默认值
     */
    public static final int DEFAULT_PLAY_SEQUENCE = -1;

    /**
     * 历史顺序默认值
     */
    public static final int DEFAULT_HISTORY_SEQUENCE = -1;


    /**
     * music id
     */
    @Column(name = "id", isId = true)
    private long id;


    /**
     * music title
     */
    @Column(name = "title")
    private String title;


    /**
     * music name
     */
    @Column(name = "name")
    private String name;


    /**
     * musci local path
     */
    @Column(name = "path")
    private String path;


    /**
     * music local filename
     */
    @Column(name = "fileNmae")
    private String fileNmae;


    /**
     * 发行公司
     */
    @Column(name = "artlist")
    private String artlist;


    /**
     * 专辑
     */
    @Column(name = "album")
    private String album;


    /**
     * 歌曲时长
     */
    @Column(name = "duration")
    private int duration;


    /**
     * 播放顺序
     */
    @Column(name = "playSequence")
    private int playSequence;


    /**
     * 历史顺序
     */
    @Column(name = "histroySequence")
    private int histroySequence;


    /**
     * 现在播放的时间点
     */
    @Column(name = "playedTime")
    private long playedTime;


    /**
     * 是否是本地音乐
     */
    @Column(name = "isLocalMusic")
    private boolean isLocalMusic;

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

    public String getFileNmae() {
        return fileNmae;
    }

    public void setFileNmae(String fileNmae) {
        this.fileNmae = fileNmae;
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

    public int getPlaySequence() {
        return playSequence;
    }

    public void setPlaySequence(int playSequence) {
        this.playSequence = playSequence;
    }

    public int getHistroySequence() {
        return histroySequence;
    }

    public void setHistroySequence(int histroySequence) {
        this.histroySequence = histroySequence;
    }

    public long getPlayedTime() {
        return playedTime;
    }

    public void setPlayedTime(long playedTime) {
        this.playedTime = playedTime;
    }

    public boolean isLocalMusic() {
        return isLocalMusic;
    }

    public void setLocalMusic(boolean localMusic) {
        isLocalMusic = localMusic;
    }

    @Override
    public String toString() {
        return "DbMusic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", fileNmae='" + fileNmae + '\'' +
                ", artlist='" + artlist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", playSequence=" + playSequence +
                ", histroySequence=" + histroySequence +
                ", playedTime=" + playedTime +
                ", isLocalMusic=" + isLocalMusic +
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
        dest.writeString(this.fileNmae);
        dest.writeString(this.artlist);
        dest.writeString(this.album);
        dest.writeInt(this.duration);
        dest.writeInt(this.playSequence);
        dest.writeInt(this.histroySequence);
        dest.writeLong(this.playedTime);
        dest.writeByte(isLocalMusic ? (byte) 1 : (byte) 0);
    }

    public DbMusic() {
    }

    protected DbMusic(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.fileNmae = in.readString();
        this.artlist = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
        this.playSequence = in.readInt();
        this.histroySequence = in.readInt();
        this.playedTime = in.readLong();
        this.isLocalMusic = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DbMusic> CREATOR = new Parcelable.Creator<DbMusic>() {
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
