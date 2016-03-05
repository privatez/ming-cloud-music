package ming.cloudmusic.event.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lhy on 2016/3/5.
 */
public class MusicEvent {

    private String msg;
    private Map extras;

    public MusicEvent() {
        extras = new HashMap<>();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map getExtras() {
        return extras;
    }

    public void setExtras(Map extras) {
        this.extras = extras;
    }
}
