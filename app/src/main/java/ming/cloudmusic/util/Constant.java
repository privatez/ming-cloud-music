package ming.cloudmusic.util;

public class Constant {

    public static final String BMOB_ID = "4174572b5ca2f5ae15b7e2cbddf46ba8";

    public static final String ACTION_LOGOUT = "ming.cloudmusic.entarnce";

    /**
     * KEY
     */
    public static final String APIKEY = "23b2578fa5f44ee9b82d03f8e7258716";

    /**
     * URL
     */
    public static final String HTTPURL = "http://apis.baidu.com/geekery/music/query";

    public static class SharedPrefrence {
        public static final String SHARED_NAME = "cloud_music";

        public static final String USER_NAME = "username";
        public static final String USER_ID = "userid";
        public static final String ISADMIN = "admin";
        public static final String PLAYINT_MODE = "playing_mode";
        public static final String PLAYING_POSITION = "playing_position";
        public static final String PLAYING_DURATION = "playing_duration";
        public static final String PLAYING_ID = "playing_id";

        public static final String AS_VISITOR_LOGGED = "visitor_logged";
        public static final String AS_USER_LOGGED = "user_logged";
    }

    public static class PlayMode {
        public static final int SINGLE = 0;
        public static final int RAMDOM = 1;
        public static final int ALL = 2;
    }

    public enum TimingPlay {

        UNOPEN("不开启", 0, 0),
        TEN("10分钟后", 10 * 60000, 1),
        TWENTY("20分钟后", 20 * 60000, 2),
        THIRTY("30分钟后", 30 * 60000, 3),
        FORTY_FIVE("45分钟后", 45 * 60000, 4),
        SIXTY("60分钟后", 60 * 60000, 5),
        NINETY("90分钟后", 90 * 60000, 6);

        // 成员变量
        private String timeText;
        private long time;
        private int index;

        // 构造方法
        private TimingPlay(String timeText, long time, int index) {
            this.timeText = timeText;
            this.time = time;
            this.index = index;
        }

        public static String getTimeText(int index) {
            for (TimingPlay timingPlay : TimingPlay.values()) {
                if (timingPlay.getIndex() == index) {
                    return timingPlay.timeText;
                }
            }
            return null;
        }

        public static long getTime(int index) {
            for (TimingPlay timingPlay : TimingPlay.values()) {
                if (timingPlay.getIndex() == index) {
                    return timingPlay.time;
                }
            }
            return 0;
        }

        public int getIndex() {
            return index;
        }

        public static int getSize() {
            return 7;
        }
    }

}
