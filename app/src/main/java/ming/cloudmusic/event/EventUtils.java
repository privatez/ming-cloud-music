package ming.cloudmusic.event;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;

/**
 * Created by Lhy on 2016/3/5.
 */
public class EventUtils {

    public static final String KEY = "KEY";

    public static final String SER = "SER";

    private KeyEvent mKeyEvent;

    private ServiceEvent mSerEvent;

    private EventPool mEventPool;

    private static EventUtils eventUtils;

    private EventUtils() {
        mEventPool = EventPool.getDefaultPool();
    }

    public static EventUtils getDefault() {
        if (eventUtils == null) {
            synchronized (EventUtils.class) {
                if (eventUtils == null) {
                    eventUtils = new EventUtils();
                }
            }
        }

        return eventUtils;
    }

    public void postEventMsg(String msg, String type) {
        postEventMsgHasExtra(msg, null, type);
    }

    public void postEventMsgHasExtra(String msg, HashMap extras, String type) {
        switch (type) {
            case SER:
                mSerEvent = mEventPool.getServiceEvent();
                if (mSerEvent != null) {
                    mSerEvent.setMsg(msg);
                    if (extras != null) {
                        mSerEvent.getExtras().putAll(extras);
                    }
                    EventBus.getDefault().post(mSerEvent);
                }
                break;
            case KEY:
                mKeyEvent = mEventPool.getKeyEvent();
                if (mKeyEvent != null) {
                    mKeyEvent.setMsg(msg);
                    if (extras != null) {
                        mKeyEvent.getExtras().putAll(extras);
                    }
                    EventBus.getDefault().post(mKeyEvent);
                }
                break;
        }
    }


}
