package ming.cloudmusic.util;

public interface Constant {

	/**
	 * 显示时间对话框需要的数据
	 */
	public static final String[] TIMESTRING = { "不开启", "10分钟后", "20分钟后", "30分钟后", "45分钟后",
		"60分钟后", "90分钟后" };
	
	/**
	 * 计时器需要的时间-毫秒
	 */
	public static final int[] TIME = { 0, 600000, 1200000, 1800000, 2700000, 3600000,
		5400000 };

	/**
	 * 网络接口需要的KEY
	 */
	public static final String APIKEY = "23b2578fa5f44ee9b82d03f8e7258716";

	/**
	 * 接口网站
	 */
	public static final String HTTPURL = "http://apis.baidu.com/geekery/music/query";

}
