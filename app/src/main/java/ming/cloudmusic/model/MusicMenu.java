package ming.cloudmusic.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by lihaiye on 16/4/28.
 */
public class MusicMenu extends BmobObject {

    private String userId;
    private String menuId;
    private String title;
    private List<DbMusic> dbMusics;

    public MusicMenu() {
        dbMusics = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DbMusic> getDbMusics() {
        return dbMusics;
    }

    public void setDbMusics(List<DbMusic> dbMusics) {
        this.dbMusics = dbMusics;
    }

    @Override
    public String toString() {
        return "MusicMenu{" +
                "userId='" + userId + '\'' +
                ", menuId='" + menuId + '\'' +
                ", title='" + title + '\'' +
                ", dbMusics=" + dbMusics +
                '}';
    }

}
