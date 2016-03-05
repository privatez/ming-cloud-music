package ming.cloudmusic.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateSDF {
	public static Object getSDF(Object object) {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);
		object = sdf.format(object);
		return object;
	}
}
