package ming.cloudmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ming.cloudmusic.R;
import ming.cloudmusic.adapter.CommonAdapter;
import ming.cloudmusic.adapter.ViewHolder;
import ming.cloudmusic.model.DbMusic;

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
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
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
     * 创建底部弹框
     * 宽度为match_parent，停留在底部
     *
     * @param context
     * @param layout
     * @return
     */
    public static AlertDialog createButtomDialog(Context context, int layout, int animation, boolean showSoftInput) {

        return createDialog(context, layout, animation, showSoftInput, Gravity.BOTTOM);
    }

    public static AlertDialog createPlayingMusicListDialog(final Context context) {
        AlertDialog dialog = createButtomDialog(context, R.layout.dialog_playing_musiclist, 0, false);
        final MusicsManager musicsManager = MusicsManager.getInstance();
        final List<DbMusic> musicList = musicsManager.getPlayingMusics();
        final CommonAdapter<DbMusic> adapter;

        ListView listView = (ListView) dialog.findViewById(R.id.lv_playing_musiclist);
        final TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvClear = (TextView) dialog.findViewById(R.id.tv_clear);

        tvTitle.setText("播放列表（" + musicList.size() + "）");

        listView.setAdapter(adapter = new CommonAdapter<DbMusic>(context, musicList, R.layout.item_playing_musiclist_dialog) {
            @Override
            public void convert(ViewHolder holder, final DbMusic item) {
                TextView tvMusicName = holder.getView(R.id.tv_music_name);
                TextView tvMusicArt = holder.getView(R.id.tv_music_art);
                ImageView ivPlaying = holder.getView(R.id.iv_playing);
                ImageView ivDelete = holder.getView(R.id.iv_delete);

                tvMusicName.setText(item.getTitle() + " - ");
                tvMusicArt.setText(item.getArtlist());

                int color;
                if (MusicsManager.getInstance().isMusicPlaying(item.getId())) {
                    color = getResColor(context, R.color.cloudred);
                    ivPlaying.setVisibility(View.VISIBLE);
                } else {
                    color = getResColor(context, R.color.black);
                    ivPlaying.setVisibility(View.GONE);
                }
                tvMusicName.setTextColor(color);
                tvMusicArt.setTextColor(color);

                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        musicsManager.removePlayingMusicById(item.getId());
                        musicList.remove(item);
                        notifyDataSetChanged();
                        tvTitle.setText("播放列表（" + musicList.size() + "）");
                    }
                });
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicsManager.clearPlayingMusics();
                musicList.clear();
                adapter.notifyDataSetChanged();
                tvTitle.setText("播放列表（" + musicList.size() + "）");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicsManager.playMusicByPosition(musicList, position);
                adapter.notifyDataSetChanged();
            }
        });

        return dialog;
    }

    public static int getResColor(Context context, int color) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, color);
        } else {
            return context.getResources().getColor(color);
        }
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
