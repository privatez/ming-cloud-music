package ming.cloudmusic.util;

public class Constant {

    public static final String BMOB_ID = "4174572b5ca2f5ae15b7e2cbddf46ba8";

    /**
     * 时间String
     */
    public static final String[] TIMESTRING = {"������", "10���Ӻ�", "20���Ӻ�", "30���Ӻ�", "45���Ӻ�",
            "60���Ӻ�", "90���Ӻ�"};

    /**
     * 时间
     */
    public static final int[] TIME = {0, 600000, 1200000, 1800000, 2700000, 3600000,
            5400000};

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

        public static final String ISLOGGED = "logged";
        public static final String USEID = "userid";
        public static final String PLAYINT_MODE = "playing_mode";
        public static final String PLAYING_POSITION = "playing_position";
        public static final String PLAYING_DURATION = "playing_duration";
    }

}
