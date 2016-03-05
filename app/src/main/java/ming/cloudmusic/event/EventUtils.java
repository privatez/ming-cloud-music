package ming.cloudmusic.event;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import ming.cloudmusic.event.model.MusicEvent;

/**
 * Created by Lhy on 2016/3/5.
 */
public class EventUtils {

    private MusicEvent mEvent;

    private static EventUtils eventUtils;

    private EventUtils (){
        mEvent = new MusicEvent();
    }

    public static EventUtils getDefault() {
        if(eventUtils == null) {
            synchronized (EventUtils.class){
                if(eventUtils == null){
                    eventUtils = new EventUtils();
                }
            }
        }

        return eventUtils;
    }

    public void postEventMsg(String msg) {
        postEventMsgHasExtra(msg,null);
    }

    public void postEventMsgHasExtra(String msg,HashMap extras) {
        if(mEvent!=null) {
            mEvent.setMsg(msg);
            if(extras!=null) {
                mEvent.getExtras().putAll(extras);
            }
            EventBus.getDefault().post(mEvent);
        }
    }

}
