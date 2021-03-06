package ming.cloudmusic.event;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import ming.cloudmusic.event.model.DataEvent;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.util.LogUtils;

/**
 * Created by Lhy on 2016/3/5.
 */
public class EventUtil {

    private static final String EVENTTYPE_KEY = "KEY";
    private static final String EVENTTYPE_SER = "SER";
    private static final String EVENTTYPE_DATA = "DATA";

    private KeyEvent mKeyEvent;
    private ServiceEvent mSerEvent;
    private DataEvent mDataEvent;

    private EventBus mEventBus;
    private EventPoolManager mEventPoolManager;

    private static EventUtil mEventUtil;

    private EventUtil() {
        mEventBus = EventBus.getDefault();
        mEventPoolManager = EventPoolManager.getDefaultPool();
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

    public void postDataEvent(String eventMsg) {
        postEventMsg(eventMsg, EVENTTYPE_DATA);
    }

    public void postDataEventHasExtra(String eventMsg, Map extras) {
        postEventMsgHasExtra(eventMsg, extras, EVENTTYPE_DATA);
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
                mSerEvent = (ServiceEvent) mEventPoolManager.getEvent(ServiceEvent.class);
                if (mSerEvent != null) {
                    mSerEvent.setMsg(eventMsg);
                    if (extras != null) {
                        mSerEvent.getExtras().putAll(extras);
                    }
                    mEventBus.post(mSerEvent);
                }
                break;
            case EVENTTYPE_KEY:
                mKeyEvent = (KeyEvent) mEventPoolManager.getEvent(KeyEvent.class);
                if (mKeyEvent != null) {
                    mKeyEvent.setMsg(eventMsg);
                    if (extras != null) {
                        mKeyEvent.getExtras().putAll(extras);
                    }
                    mEventBus.post(mKeyEvent);
                }
                break;
            case EVENTTYPE_DATA:
                mDataEvent = (DataEvent) mEventPoolManager.getEvent(DataEvent.class);
                LogUtils.log("EVENTTYPE_DATA123" + eventMsg);
                if (mDataEvent != null) {
                    mDataEvent.setMsg(eventMsg);
                    if (extras != null) {
                        mDataEvent.getExtras().putAll(extras);
                    }
                    mEventBus.post(mDataEvent);
                }
                break;
        }
    }

}
