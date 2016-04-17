package ming.cloudmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by lihaiye on 16/3/24.
 */
public class CustomUtils {

    /**
     * get version name
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * get version code
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 检查是否存在相应的Intent
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    /**
     * 开启文件管理器
     *
     * @param activity
     * @param requestCode
     */
    public static void startFileManager(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (CustomUtils.isIntentAvailable(activity, intent)) {
            activity.startActivityForResult(Intent.createChooser(intent, "请选择文件!"), requestCode);
        } else {
            ToastUtils.showShort(activity, "请安装文件管理器");
        }
    }

    /**
     * 创建弹框
     * 宽度为match_parent，停留在底部
     *
     * @param context
     * @param layout
     * @param animation
     * @param showSoftInput
     * @param gravity
     * @return
     */
    public static AlertDialog createDialog(Context context, int layout, int animation, boolean showSoftInput, int gravity) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        if (showSoftInput) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = gravity;

        window.setAttributes(lp);
        window.setWindowAnimations(animation);

        return dialog;
    }

    /**
     * 创建弹框
     * 宽度为match_parent，停留在中心
     *
     * @param context
     * @param layout
     * @return
     */
    public static AlertDialog createCenterDialog(Context context, int layout) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);

        return dialog;
    }

    /**
     * 退出登录
     *
     * @param context
     */
    public static void logout(Context context) {
        SharedPrefsUtil sharedPrefs = new SharedPrefsUtil(context.getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);
        ToastUtils.showShort(context, "退出登录成功");
        sharedPrefs.clearAll();
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_LOGOUT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
