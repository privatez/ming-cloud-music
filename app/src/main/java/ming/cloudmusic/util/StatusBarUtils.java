package ming.cloudmusic.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ming.cloudmusic.R;

/**
 * Created by lihaiye on 16/5/14.
 */
public class StatusBarUtils {

    /**
     * The statusbar default background color
     */
    private static final int BACKGROUND_COLOR_DEFAULT = R.color.red;

    /**
     * The statusbar default background color
     */
    private static final int BACKGROUND_DRAWABLE_DEFAULT = R.drawable.actionbar_bg;

    /**
     * 设置状态栏颜色为默认颜色
     *
     * @param activity 需要设置的activity
     * @return 设置状态栏颜色是否成功
     */
    public static boolean setStatusBarDefaultBackgroundColor(Activity activity) {
        setStatusBarBackgroundResourcesColor(activity, BACKGROUND_COLOR_DEFAULT);
        return setStatusBarTextColorDrak(activity);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param resColor 状态栏颜色值
     */
    public static void setStatusBarBackgroundResourcesColor(Activity activity, int resColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, resColor);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param resColor 状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int resColor) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(activity.getResources().getColor(resColor));
        return statusView;
    }

    /**
     * 设置状态栏颜色为默认颜色
     *
     * @param activity 需要设置的activity
     * @return 设置状态栏颜色是否成功
     */
    public static boolean setStatusBarDefaultBackgroundDrawable(Activity activity) {
        setStatusBarBackgroundDrawale(activity, activity.getResources().getDrawable(BACKGROUND_DRAWABLE_DEFAULT));
        return setStatusBarTextColorDrak(activity);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param drawable 状态栏图片
     */
    public static void setStatusBarBackgroundDrawale(Activity activity, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, drawable);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param drawable 状态栏图片
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, Drawable drawable) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackground(drawable);
        return statusView;
    }

    public static void setPadding(Context context, View view) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        view.setPadding(0, statusBarHeight, 0, 0);
    }

    /**
     * 用于获取状态栏的高度。 使用Resource对象获取（推荐这种方式）
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 使状态栏透明
     * <p>
     * 适用于根布局是图片作为背景的界面,此时需要图片填充到状态栏
     * 重写父类setStatusBarColor()方法并删除super,再调用此方法
     *
     * @param activity 需要设置的activity
     */
    public static void setStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 修改状态栏字体颜色为白色
     *
     * @param activity 需要设置的activity
     * @return 设置状态栏颜色是否成功
     */
    public static boolean setStatusBarTextColorWhite(Activity activity) {
        return setStatusBarDarkMode(activity, false);
    }

    /**
     * 修改状态栏字体颜色为灰色
     *
     * @param activity 需要设置的activity
     * @return 设置状态栏颜色是否成功
     */
    public static boolean setStatusBarTextColorDrak(Activity activity) {
        return setStatusBarDarkMode(activity, true);
    }

    /**
     * 修改状态栏字体颜色
     *
     * @param activity 需要设置的activity
     * @param darkmode true（灰色）/false（白色）
     * @return 设置状态栏颜色是否成功
     */
    private static boolean setStatusBarDarkMode(Activity activity, boolean darkmode) {
        return (setMeiZuStatusBarDarkMode(activity.getWindow(), darkmode) || setMiuiStatusBarDarkMode(activity, darkmode));
    }

    /**
     * 针对Miui系统修改状态栏字体颜色
     *
     * @param activity 需要设置的activity
     * @param darkmode true（灰色）/false（白色）
     * @return 设置状态栏颜色是否成功
     */
    private static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 针对魅族系统修改状态栏字体颜色
     *
     * @param window   需要设置的window
     * @param darkmode true（灰色）/false（白色）
     * @return 设置状态栏颜色是否成功
     */
    private static boolean setMeiZuStatusBarDarkMode(Window window, boolean darkmode) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (darkmode) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
