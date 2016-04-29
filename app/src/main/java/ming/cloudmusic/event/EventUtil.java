package ming.cloudmusic.event;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;

/**
 * Created by Lhy on 2016/3/5.
 */
public class EventUtil {

    private static final String EVENTTYPE_KEY = "KEY";
    private static final String EVENTTYPE_SER = "SER";

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

    public void postKeyEvent(String eventMsg) {
        postEventMsg(eventMsg, EVENTTYPE_KEY);
    }

    public void postKeyEventHasExtra(String eventMsg, Map extras) {
        postEventMsgHasExtra(eventMsg, extras, EVENTTYPE_KEY);
    }

    public void postSerEvent(String eventMsg) {
        postEventMsg(eventMsg, EVENTTYPE_SER);
    }

    public void postSerEventHasExtra(String eventMsg, Map extras) {
        postEventMsgHasExtra(eventMsg, extras, EVENTTYPE_SER);
    }

    private void postEventMsg(String eventMsg, String eventType) {
        postEventMsgHasExtra(eventMsg, null, eventType);
    }

    /**
     * 发送Event
     *
     * @param eventMsg
     * @param extras
     * @param eventType
     */
    private void postEventMsgHasExtra(String eventMsg, Map extras, String eventType) {
        switch (eventType) {
            case EVENTTYPE_SER:
                mSerEvent = mEventPool.getServiceEvent();
                if (mSerEvent != null) {
                    mSerEvent.setMsg(eventMsg);
                    if (extras != null) {
                        mSerEvent.getExtras().putAll(extras);
                    }
                    EventBus.getDefault().post(mSerEvent);
                }
                break;
            case EVENTTYPE_KEY:
                mKeyEvent = mEventPool.getKeyEvent();
                if (mKeyEvent != null) {
                    mKeyEvent.setMsg(eventMsg);
                    if (extras != null) {
                        mKeyEvent.getExtras().putAll(extras);
                    }
                    EventBus.getDefault().post(mKeyEvent);
                }
                break;
        }
    }


}
