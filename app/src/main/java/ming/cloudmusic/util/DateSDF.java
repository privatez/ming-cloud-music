package ming.cloudmusic.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateSDF {
    public static Object getDefaultSDF(Object object) {
        return getSDF(object, "mm:ss");
    }

    public static Object getSDF(Object object, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        object = sdf.format(object);
        return object;
    }
}
