package ming.cloudmusic.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lihaiye on 16/3/24.
 */
public class AppUpdate extends BmobObject {

    private int versionCode;
    private String versionName;
    private String changeLog;
    private String url;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "AppUpdate{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", changeLog='" + changeLog + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
