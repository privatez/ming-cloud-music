package ming.cloudmusic.event;

/**
 * Created by Lhy on 2016/3/5.
 */
public class Event {

    public static class ServiceEvent {

        public static final String SERVICE_PLAY = "S_PLAY";

        public static final String SERVICE_PAUSE = "S_PAUSE";

        public static final String SERVICE_NEXT = "S_NEXT";

        public static final String SERVICE_PREVIOUS = "S_PREVIOUS";

        public static final String SERVICE_PLAY_MODE = "S_PLAY_MODE";

        public static final String SERVICE_BAR_CHANGE = "S_BAR_CHANGE";

        public static final String SERVICE_POST_PLAYINGMUSIC = "S_POST_PLAYINGMUSIC";

    }

    public static class KeyEvent {

        public static final String KEY_PLAY = "K_PLAY";

        public static final String KEY_PAUSE = "K_PAUSE";

        public static final String KEY_NEXT = "K_NEXT";

        public static final String KEY_PREVIOUS = "K_PREVIOUS";

        public static final String KEY_PLAY_MODE = "K_PLAY_MODE";

        public static final String KEY_BAR_CHANGE = "K_BAR_CHANGE";

        public static final String KEY_GET_PLAYINGMUSIC = "KEY_GET_PLAYINGMUSIC";

    }

    public static class Extra {

        public static final String EXTRA_PLAY_MODE = "EXTRA_PLAY_MODE";

        public static final String EXTRA_BAR_CHANGE = "EXTRA_BAR_CHANGE";

        public static final String EXTRA_PLAYING_POSITION = "EXTRA_PLAYING_POSITION";

        public static final String EXTRA_PLAYING_TITLE = "EXTRA_PLAYING_TITLE";

        public static final String EXTRA_PLAYING_ART = "EXTRA_PLAYING_ART";

        public static final String EXTRA_PLAYING_DURATION = "EXTRA_PLAYING_DURATION";

    }


    /**
     * 正在播放
     */
    public static final String EVENT_MSG_IS_PLAY = "EVENT_MSG_IS_PLAY";
    public static final String EVENT_MSG_IS_PLAY_DATA = "EVENT_MSG_IS_PLAY_DATA";
    public static final String EVENT_MSG_IS_PLAY_TITLEDATA = "EVENT_MSG_IS_PLAY_TITLEDATA";
    public static final String EVENT_MSG_IS_PLAY_ARTDATA = "EVENT_MSG_IS_PLAY_ARTDATA";
    public static final String EVENT_MSG_IS_PLAY_DURATIONDATA = "EVENT_MSG_IS_PLAY_DURATIONDATA";


    /**
     * 播放按钮的行为
     */
    public static final String EVENT_MSG_PLAY_BN = "EVENT_PLAY_BN";
    public static final String EVENT_MSG_MSG_PAUSE_BN = "EVENT_MSG_MSG_PAUSE_BN";

    /**
     * 拖动进度条行为
     */
    public static final String EVENT_MSG_SEEKBAR = "EVENT_MSG_SEEKBAR";
    public static final String EVENT_MSG_SEEKBAR_DATA = "EVENT_MSG_SEEKBAR_DATA";

    /**
     * 进度条改变
     */
    public static final String EVENT_MSG_SEEKBAR_CHANGE = "EVENT_MSG_SEEKBAR_CHANGE";
    public static final String EVENT_MSG_SEEKBAR_CHANGE_DATA = "EVENT_MSG_SEEKBAR_CHANGE_DATA";

    /**
     * 播放模式
     */
    public static final String EVENT_MSG_PLAY_MODE = "EVENT_MSG_PLAY_MODE";

    /**
     * 更新播放模式按钮
     */
    public static final String EVENT_MSG_PLAY_MODE_BN = "EVENT_MSG_PLAY_MODE_BN";
    public static final String EVENT_MSG_PLAY_MODE_DATA = "EVENT_MSG_PLAY_MODE_DATA";

    /**
     * 第一次加载界面时请求获取当前播放歌曲
     */
    public static final String EVENT_MSG_GETMUSICFLAG = "EVENT_MSG_GETMUSICFLAG";
    /**
     * 第一次加载界面时发送当前播放歌曲
     */
    public static final String EVENT_MSG_SENDMUSICFLAG = "EVENT_MSG_SENDMUSICFLAG";
    public static final String EVENT_MSG_SENDMUSICFLAG_DATA = "EVENT_MSG_SENDMUSICFLAG_DATA";

    /**
     * 判断播放状态数据
     */
    public static final String EVENT_MSG_SENDMUSICFLAG_BN = "EVENT_MSG_SENDMUSICFLAG_BN";

    /**
     * 发送ITEM需要播放的歌曲
     */
    public static final String EVENT_MSG_CLICKPLAY = "EVENT_MSG_CLICKPLAY";
    public static final String EVENT_MSG_CLICKPLAY_DATA = "EVENT_MSG_CLICKPLAY_DATA";

    /**
     * 发送当前计时器时间
     */
    public static final String EVENT_MSG_SENDTIMEDATA = "EVENT_MSG_SENDTIMEDATA";
    public static final String EVENT_MSG_SENDTIMEDATA_DATA = "EVENT_MSG_SENDTIMEDATA_DATA";

    /**
     * 发送下载信息
     */
    public static final String EVENT_MSG_ISDLDING = "EVENT_MSG_ISDLDING1";
    /**
     * 下载的百分比
     */
    public static final String EVENT_MSG_ISDLDING_PRO = "EVENT_MSG_ISDLDING_PRO";
    /**
     * 当前下载下标
     */
    public static final String EVENT_MSG_ISDLDING_INDEX = "EVENT_MSG_ISDLDING_INDEX";

    public static final String EVENT_MSG_ISDLDING_SONGNAME = "EVENT_MSG_ISDLDING_SONGNAME";

    public static final String EVENT_MSG_ISDLDING_FINSH = "EVENT_MSG_ISDLDING_FINSH";
}
