package ming.cloudmusic.event.model;

import java.util.HashMap;

/**
 * Created by Lhy on 2016/3/5.
 */
public class MusicEvent {

    private String msg;
    private HashMap extras;

    public MusicEvent() {
        extras = new HashMap<>();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HashMap getExtras() {
        return extras;
    }

    public void setExtras(HashMap extras) {
        this.extras = extras;
    }
}
