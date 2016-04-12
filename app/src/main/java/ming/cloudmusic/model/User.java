package ming.cloudmusic.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by lihaiye on 16/4/12.
 */
public class User extends BmobUser {
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
