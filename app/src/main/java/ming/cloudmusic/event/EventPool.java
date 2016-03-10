package ming.cloudmusic.event;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;

/**
 * Created by lihaiye on 16/3/9.
 * <p>
 * Event池
 */
public class EventPool {

    /**
     * 默认最小Event数
     */
    private static final int DEFAULT_MIX = 3;

    /**
     * 默认最大Event数
     */
    private static final int DEFAULT_MAX = 5;

    /**
     * 当前最大Event数
     */
    private int mMaxEvents;

    private int mKeyEventsTag = -1;

    private int mServiceEventsTag = -1;

    private List<KeyEvent> mKeyEvents;
    private List<ServiceEvent> mServiceEvents;

    private static EventPool sEventPool;

    private EventPool() {
        mMaxEvents = DEFAULT_MAX;
        mKeyEvents = new ArrayList<>(mMaxEvents);
        mServiceEvents = new ArrayList<>(mMaxEvents);
        addKeyEvents(0, mMaxEvents);
        addServiceEvents(0, mMaxEvents);
    }


    /**
     * 获得单例EventPool
     *
     * @return
     */
    public static EventPool getDefaultPool() {

        if (sEventPool == null) {
            synchronized (EventPool.class) {
                if (sEventPool == null)
                    sEventPool = new EventPool();
            }
        }

        return sEventPool;
    }



    /**
     * 设置Event池最大容量
     *
     * @param maxSize
     */

    public void setMaxEvents(int maxSize) {

        if (maxSize <= DEFAULT_MIX || maxSize == mMaxEvents) {
            return;
        }

        if (maxSize > mMaxEvents) {
            addKeyEvents(mMaxEvents, maxSize - mMaxEvents);
            addServiceEvents(mMaxEvents, maxSize - mMaxEvents);
        } else if (maxSize < mMaxEvents) {
            removeKeyEvents(mMaxEvents - maxSize);
            removeServiceEvents(mMaxEvents - maxSize);
        }

        mMaxEvents = maxSize;

    }


    /**
     * 向Event池添加KeyEvent
     *
     * @param location
     * @param size
     */
    private void addKeyEvents(int location, int size) {

        for (int i = location; i < location + size; i++) {
            KeyEvent event = new KeyEvent();
            mKeyEvents.add(location, event);
        }
    }


    /**
     * 向Event池添加ServiceEvent
     *
     * @param location
     * @param size
     */
    private void addServiceEvents(int location, int size) {
        for (int i = location; i < location + size; i++) {
            ServiceEvent event = new ServiceEvent();
            mServiceEvents.add(location, event);
        }
    }


    /**
     * 移除Event池中不需要的KeyEvent
     *
     * @param size
     */
    private void removeKeyEvents(int size) {
        for (int i = 1; i == size; i++) {
            mKeyEvents.remove(mKeyEvents.size() - i);
        }
    }


    /**
     * 移除Event池中不需要的ServiceEvent
     *
     * @param size
     */
    private void removeServiceEvents(int size) {
        for (int i = 1; i == size; i++) {
            mServiceEvents.remove(mServiceEvents.size() - i);
        }
    }


    /**
     * 获取可用的KeyEvent
     *
     * @return
     */
    public KeyEvent getKeyEvent() {
        mKeyEventsTag++;

        if (mKeyEventsTag >= mMaxEvents) {
            mKeyEventsTag = 0;
        }

        return mKeyEvents.get(mKeyEventsTag);
    }


    /**
     * 获取可用的ServiceEvent
     *
     * @return
     */
    public ServiceEvent getServiceEvent() {
        mServiceEventsTag++;

        if (mServiceEventsTag >= mMaxEvents) {
            mServiceEventsTag = 0;
        }

        //LogUtils.log("mServiceEventsTag" + mServiceEventsTag + ".....");
        return mServiceEvents.get(mServiceEventsTag);
    }

}
