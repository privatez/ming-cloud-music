package ming.cloudmusic.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

    private TextView tvMenuTitle;
    private TextView tvMenuLogin;
    private TextView tvMenuTime;

    private MenuDrawer mDrawer;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private Activity mActivity;

    private SharedPrefsUtil mSharedPrefs;

    private MenuDrawer.OnInterceptMoveEventListener mListener;

    private List<String> mExitTime;
    private CommonAdapter<String> mExitAdapter;

    private int mAction = ACTION_DEFAULT;
    private int mCheckPosition;

    public MenuDrawerHelper(Activity activity, MenuDrawer.OnInterceptMoveEventListener listener) {
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

        mDrawer.findViewById(R.id.ll_menu_time).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_menu_night).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_menu_setting).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_menu_exit).setOnClickListener(MenuDrawerHelper.this);
        tvMenuLogin.setOnClickListener(MenuDrawerHelper.this);

        if (mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false)) {
            tvMenuTitle.setText(mSharedPrefs.getStringSP(Constant.SharedPrefrence.USER_NAME, ""));
            tvMenuLogin.setVisibility(View.GONE);
        }

        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                //android.util.Log.d("lhy","oldState:"+oldState+".....newState:"+newState);
                if (oldState == MenuDrawer.STATE_CLOSED) {
                    if (mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false)) {
                        tvMenuTitle.setText(mSharedPrefs.getStringSP(Constant.SharedPrefrence.USER_NAME, ""));
                        tvMenuLogin.setVisibility(View.GONE);
                    }
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

    private void checkAction(int action) {
        Intent intent;
        switch (action) {
            case ACTION_LOGIN:
                intent = new Intent(mActivity, EntranceActivity.class);
                mActivity.startActivity(intent);
                break;
            case ACTION_SETTING:
                intent = new Intent(mActivity, AboutAppActivity.class);
                mActivity.startActivity(intent);
                break;
            case ACTION_TIMINGPLAY:
                initDialog();
                dialog = builder.show();
                break;
        }
        mAction = ACTION_DEFAULT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_menu_login:
                mAction = ACTION_LOGIN;
                toggleMenu();
                break;
            case R.id.ll_menu_setting:
                mAction = ACTION_SETTING;
                toggleMenu();
                break;
            case R.id.ll_menu_time:
                mAction = ACTION_TIMINGPLAY;
                toggleMenu();
                break;
            case R.id.ll_menu_exit:
                System.exit(0);
                break;
        }
    }

    /*private void exit() {
        SystemUtils.showToast(mActivity, "退出登录成功");
        mSharedPrefs.clearAll();
        BombServer.resetServerApi();
        Intent intent = new Intent();
        intent.setAction(Constant.LOGIN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.checkAction(intent);
    }*/

    private void initDialog() {
        builder = new AlertDialog.Builder(mActivity);
        View view = null;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(
                    R.layout.dialog_exittime, null);
            ListView listView = (ListView) view.findViewById(R.id.lv_exittime);
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
                        ToastUtils.showShort(mActivity, "设置成功，将于" + mExitTime.get(position) + "关闭");
                    } else {
                        ToastUtils.showShort(mActivity, "定时播放已取消");
                    }
                    dialog.dismiss();
                }
            });
            builder.setView(view);
        }
    }

    private void initExitAdapter() {
        mExitTime = new ArrayList<>();

        for (int i = 0; i < Constant.TimingPlay.getSize(); i++) {
            mExitTime.add(Constant.TimingPlay.getTimeText(i));
        }

        mExitAdapter = new CommonAdapter<String>(mActivity, mExitTime, R.layout.item_exitime_normal) {
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
        tvMenuTime.setText(DateSDF.getSDF(time).toString());
        mCheckPosition = position;
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
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            return true;
        }
        return false;
    }

}
