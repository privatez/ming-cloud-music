package ming.cloudmusic.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.R;
import ming.cloudmusic.activity.AboutAppActivity;
import ming.cloudmusic.activity.EntranceActivity;
import ming.cloudmusic.adapter.CommonAdapter;
import ming.cloudmusic.adapter.ViewHolder;
import ming.cloudmusic.service.ExitService;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.DateSDF;
import ming.cloudmusic.util.SharedPrefsUtil;
import ming.cloudmusic.util.ToastUtils;


/**
 * Created by lihaiye on 16/3/10.
 */
public class MenuDrawerHelper implements View.OnClickListener {

    /**
     * 滑动触发菜单栏弹出屏占比
     */
    private static final float MENU_CAN_TOUCH = 0.15f;

    /**
     * 菜单栏弹出后屏占比
     */
    private static final float MENU_WINDOW_SCALE = 0.85f;

    private static final int ACTION_DEFAULT = 0;
    private static final int ACTION_LOGIN = 1;
    private static final int ACTION_SETTING = 2;
    private static final int ACTION_TIMINGPLAY = 3;
    private static final int ACTION_LOGOUT = 4;

    private TextView tvMenuTitle;
    private TextView tvMenuLogin;
    private TextView tvMenuTime;
    private LinearLayout llMenuLogout;

    private MenuDrawer mDrawer;

    private Activity mActivity;

    private SharedPrefsUtil mSharedPrefs;

    private MenuDrawer.OnInterceptMoveEventListener mListener;

    private List<String> mExitTime;
    private CommonAdapter<String> mExitAdapter;

    private int mAction;
    private int mCheckPosition;

    public MenuDrawerHelper(Activity activity, MenuDrawer.OnInterceptMoveEventListener listener) {
        mAction = ACTION_DEFAULT;
        mActivity = activity;
        mSharedPrefs = new SharedPrefsUtil(mActivity.getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);
        mListener = listener;
        initMenu();
        initExitAdapter();
    }

    private void initMenu() {

        //初始化 TYPE.OVERLAY 为覆盖模式
        mDrawer = MenuDrawer.attach(mActivity, MenuDrawer.Type.OVERLAY);

        //设置菜单栏的Layout
        mDrawer.setMenuView(R.layout.menu_main);
        //设置Activity的Layout
        mDrawer.setContentView(R.layout.activity_main);
        //设置滑动模式
        mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setDrawerIndicatorEnabled(true);
        //设置菜单宽度
        mDrawer.setMenuSize((int) (getWindowWidth(mActivity) * MENU_WINDOW_SCALE));
        //设置阴影宽度
        mDrawer.setDropShadowSize(0);
        //设置当滑动模式为TOUCH_MODE_BEZEL 可滑动区域
        mDrawer.setTouchBezelSize((int) (getWindowWidth(mActivity) * MENU_CAN_TOUCH));

        mDrawer.setOnInterceptMoveEventListener(mListener);

        tvMenuTitle = (TextView) mDrawer.findViewById(R.id.tv_menu_title);
        tvMenuLogin = (TextView) mDrawer.findViewById(R.id.tv_menu_login);
        tvMenuTime = (TextView) mDrawer.findViewById(R.id.tv_menu_time);
        llMenuLogout = (LinearLayout) mDrawer.findViewById(R.id.ll_menu_logout);

        mDrawer.findViewById(R.id.ll_menu_time).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.tv_menu_setting).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.tv_menu_exit).setOnClickListener(MenuDrawerHelper.this);
        llMenuLogout.setOnClickListener(this);
        tvMenuLogin.setOnClickListener(MenuDrawerHelper.this);

        refreshViewByLoginStatus(mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false));

        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (oldState == MenuDrawer.STATE_CLOSED) {
                    refreshViewByLoginStatus(mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false));
                }

                if (newState == MenuDrawer.STATE_OPEN) {
                    //TODO 数据请求
                }

                if (newState == MenuDrawer.STATE_CLOSED) {
                    checkAction(mAction);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {

            }
        });

    }

    private void refreshViewByLoginStatus(boolean isLogin) {
        if (isLogin) {
            tvMenuTitle.setText(mSharedPrefs.getStringSP(Constant.SharedPrefrence.USER_NAME, ""));
            tvMenuLogin.setVisibility(View.GONE);
            llMenuLogout.setVisibility(View.VISIBLE);
        } else {
            llMenuLogout.setVisibility(View.GONE);
        }
    }

    private void checkAction(int action) {
        Intent intent;
        switch (action) {
            case ACTION_LOGIN:
                intent = new Intent(mActivity, EntranceActivity.class);
                mActivity.startActivity(intent);
                break;
            case ACTION_TIMINGPLAY:
                showTimingPlayDialog();
                break;
            case ACTION_LOGOUT:
                CustomUtils.logout(mActivity);
                break;
            case ACTION_SETTING:
                intent = new Intent(mActivity, AboutAppActivity.class);
                mActivity.startActivity(intent);
                break;
        }
        mAction = ACTION_DEFAULT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_menu_time:
                toggleMenuWithAction(ACTION_TIMINGPLAY);
                break;
            case R.id.ll_menu_logout:
                toggleMenuWithAction(ACTION_LOGOUT);
                break;
            case R.id.tv_menu_login:
                toggleMenuWithAction(ACTION_LOGIN);
                break;
            case R.id.tv_menu_setting:
                toggleMenuWithAction(ACTION_SETTING);
                break;
            case R.id.tv_menu_exit:
                System.exit(0);
                break;
        }
    }

    private void toggleMenuWithAction(int action) {
        mAction = action;
        toggleMenu();
    }

    private void showTimingPlayDialog() {
        final AlertDialog dialog = CustomUtils.createCenterDialog(mActivity, R.layout.dialog_exittime);
        ListView listView = (ListView) dialog.findViewById(R.id.lv_exittime);
        listView.setAdapter(mExitAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCheckPosition = position;
                Intent service = new Intent(mActivity, ExitService.class);
                service.putExtra(ExitService.EXTRA_TIME, Constant.TimingPlay.getTime(position));
                service.putExtra(ExitService.EXTRA_CHECK_POSITION, position);
                mActivity.startService(service);
                if (position != 0) {
                    ToastUtils.showShort("设置成功，将于" + mExitTime.get(position) + "关闭");
                } else {
                    ToastUtils.showShort("定时停止播放已取消");
                }
                dialog.dismiss();
            }
        });
    }

    private void initExitAdapter() {
        mExitTime = new ArrayList<>();

        for (int i = 0; i < Constant.TimingPlay.getSize(); i++) {
            mExitTime.add(Constant.TimingPlay.getTimeText(i));
        }

        mExitAdapter = new CommonAdapter<String>(mActivity, mExitTime, R.layout.item_exitime) {
            @Override
            public void convert(ViewHolder holder, String item) {
                ImageView ivCheck = holder.getView(R.id.iv_check);
                holder.setText(R.id.tv_time, item);
                if (holder.getPosition() == mCheckPosition) {
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ivCheck.setVisibility(View.GONE);
                }
            }
        };
    }

    public void setTimeText(long time, int position) {
        if (time == 0) {
            tvMenuTime.setText("");
            mCheckPosition = 0;
        } else {
            tvMenuTime.setText(DateSDF.getDefaultSDF(time).toString());
            mCheckPosition = position;
        }
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    private int getWindowWidth(Context context) {

        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public void toggleMenu() {
        mDrawer.toggleMenu();
    }

    public void closeMenu() {
        mDrawer.closeMenu();
    }

    public boolean isMenuOpened() {
        int drawerState = mDrawer.getDrawerState();
        return drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING;
    }

}
