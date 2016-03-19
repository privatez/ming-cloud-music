package ming.cloudmusic.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.simonvt.menudrawer.MenuDrawer;

import ming.cloudmusic.R;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.SharedPrefsUtil;


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
    private static final int ACTION_ADDINFO = 1;
    private static final int ACTION_ACCONUT = 2;
    private static final int ACTION_LEADER = 3;
    private static final int ACTION_AREA = 4;

    private MenuDrawer mDrawer;

    private Activity mActivity;

    private SharedPrefsUtil mSharedPrefs;

    private MenuDrawer.OnInterceptMoveEventListener mListener;

    private int mAction = ACTION_DEFAULT;

    public MenuDrawerHelper(Activity activity, MenuDrawer.OnInterceptMoveEventListener listener) {
        mActivity = activity;
        mSharedPrefs = new SharedPrefsUtil(mActivity.getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);
        mListener = listener;
        initMenu();
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

        mDrawer.findViewById(R.id.ll_menu_time).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_menu_night).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_menu_setting).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_menu_exit).setOnClickListener(MenuDrawerHelper.this);


        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                //android.util.Log.d("lhy","oldState:"+oldState+".....newState:"+newState);
                if (newState == MenuDrawer.STATE_OPEN) {

                }

                if (newState == MenuDrawer.STATE_CLOSED) {
                    startActivity(mAction);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {

            }
        });

    }


    private void startActivity(int action) {
        Intent intent;
        switch (action) {

        }
        mAction = ACTION_DEFAULT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    /*private void exit() {
        SystemUtils.showToast(mActivity, "退出登录成功");
        mSharedPrefs.clearAll();
        Server.resetServerApi();
        Intent intent = new Intent();
        intent.setAction(Constant.LOGIN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }*/

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
