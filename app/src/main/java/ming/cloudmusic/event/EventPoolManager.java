package ming.cloudmusic.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ming.cloudmusic.event.model.BaseEvent;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.util.LogUtils;

/**
 * Created by Lhy on 2016/4/24.
 */
public class EventPoolManager {

    /**
     * 默认最小Event数
     */
    private static final int DEFAULT_MIX = 3;

    /**
     * 默认最大Event数
     */
    private static final int DEFAULT_MAX = 5;

    private List<Class<?>> mEventClasses;

    private Map<String, EventPool> mEventPoolMap;

    private static EventPoolManager sEventPoolManager;

    private EventPoolManager() {
        mEventClasses = new ArrayList<>();
        //TODO 临时方案-手动添加类名
        mEventClasses.add(KeyEvent.class);
        mEventClasses.add(ServiceEvent.class);
        mEventPoolMap = new HashMap<>();
        initPool();
    }

    /**
     * 获得单例EventPool
     *
     * @return
     */
    public static EventPoolManager getDefaultPool() {

        if (sEventPoolManager == null) {
            synchronized (EventPoolManager.class) {
                if (sEventPoolManager == null)
                    sEventPoolManager = new EventPoolManager();
            }
        }

        return sEventPoolManager;
    }

    private void initPool() {
        for (Class c : mEventClasses) {
            EventPool eventPool = new EventPool();
            for (int i = 0; i < DEFAULT_MAX; i++) {
                try {
                    BaseEvent newClass = (BaseEvent) c.newInstance();
                    eventPool.mEvents.add(newClass);
                } catch (InstantiationException e) {
                    LogUtils.log("initPool InstantiationException" + e.getMessage());
                } catch (IllegalAccessException e) {
                    LogUtils.log("initPool IllegalAccessException" + e.getMessage());
                }
            }
            LogUtils.log(c.getSimpleName() + "....." + eventPool.toString());
            mEventPoolMap.put(getKey(c), eventPool);
        }
    }

    /**
     * 通过类名设置pool大小
     *
     * @param c
     * @param maxSize
     */
    protected void setMaxEventSize(Class c, int maxSize) {
        EventPool eventPool = mEventPoolMap.get(getKey(c));

        if (maxSize <= DEFAULT_MIX || maxSize == eventPool.mMaxEventSize) {
            return;
        }

        if (maxSize > eventPool.mMaxEventSize) {
            addEvents(eventPool.mEvents, eventPool.mMaxEventSize, maxSize - eventPool.mMaxEventSize);
        } else if (maxSize < eventPool.mMaxEventSize) {
            removeEvents(eventPool.mEvents, eventPool.mMaxEventSize - maxSize);
        }

        eventPool.mMaxEventSize = maxSize;

    }

    private void addEvents(List<BaseEvent> events, int location, int size) {

        for (int i = location; i < location + size; i++) {
            BaseEvent event = new BaseEvent();
            events.add(location, event);
        }
    }

    private void removeEvents(List<BaseEvent> events, int size) {
        for (int i = 1; i == size; i++) {
            events.remove(events.size() - i);
        }
    }

    private String getKey(Class c) {
        return c.getSimpleName();
    }

    /**
     * 得到一个BaseEvent
     *
     * @param c
     * @return
     */
    public BaseEvent getEvent(Class c) {
        EventPool eventPool = mEventPoolMap.get(c.getSimpleName());
        eventPool.mCurrentPosition++;

        if (eventPool.mCurrentPosition >= eventPool.mMaxEventSize) {
            eventPool.mCurrentPosition = 0;
        }

        //LogUtils.log("getEvent: " + mEventPoolMap.get(c.getSimpleName()).mEvents.get(eventPool.mCurrentPosition).toString());

        return mEventPoolMap.get(getKey(c)).mEvents.get(eventPool.mCurrentPosition);
    }

    class EventPool {
        public int mCurrentPosition;
        public int mMaxEventSize;
        public List<BaseEvent> mEvents;

        public EventPool() {
            mCurrentPosition = -1;
            mMaxEventSize = DEFAULT_MAX;
            mEvents = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "OldEventPool{" +
                    "mCurrentPosition=" + mCurrentPosition +
                    ", mMaxEventSize=" + mMaxEventSize +
                    ", mEvents=" + mEvents +
                    '}';
        }
    }

}
