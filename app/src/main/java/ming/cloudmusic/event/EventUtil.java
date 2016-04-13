package ming.cloudmusic.event;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;

/**
 * Created by Lhy on 2016/3/5.
 */
public class EventUtil {

    public static final String KEY = "KEY";

    public static final String SER = "SER";

    private KeyEvent mKeyEvent;

    private ServiceEvent mSerEvent;

    private EventPool mEventPool;

    private static EventUtil mEventUtil;

    private EventUtil() {
        mEventPool = EventPool.getDefaultPool();
    }

    public static EventUtil getDefault() {
        if (mEventUtil == null) {
            synchronized (EventUtil.class) {
                if (mEventUtil == null) {
                    mEventUtil = new EventUtil();
                }
            }
        }

        return mEventUtil;
    }

    /**
     * 发送Event
     *
     * @param msg
     * @param eventType   Event种类
     */
    public void postEventMsg(String msg, String eventType) {
        postEventMsgHasExtra(msg, null, eventType);
    }

    public void postEventMsgHasExtra(String msg, Map extras, String eventType) {
        switch (eventType) {
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
