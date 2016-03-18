package ming.cloudmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezbox.api.api.Callback;
import com.gezbox.api.api.Server;
import com.gezbox.library.utils.utils.SharedPrefsUtil;
import com.gezbox.library.utils.utils.SystemUtils;
import com.gezbox.model.callback.GetAccountInfoCallback;
import com.gezbox.model.callback.GetUserInfoCallback;
import com.gezbox.mrwind.QiniuUtil;
import com.gezbox.winder.parttime.R;
import com.gezbox.winder.parttime.activity.AccountActivity;
import com.gezbox.winder.parttime.activity.information.AddUserInfoActivity;
import com.gezbox.winder.parttime.activity.information.ReviewUserInfoActivity;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

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

    private ImageView ivDriverAvatar;
    private TextView tvHint;
    private TextView tvDriverName;
    private TextView tvDriverTel;
    private ImageView ivUserinfo;
    private LinearLayout llArea;
    private LinearLayout llUserinfo;

    private MenuDrawer mDrawer;

    private Activity mActivity;

    private SharedPrefsUtil mSharedPrefs;

    private MenuDrawer.OnInterceptMoveEventListener mListener;


    private List<GetAccountInfoCallback.AccountEntity> mPhoneList;
    private int mCheckingOrder;
    private double mCheckingMoney;

    private int mAction = ACTION_DEFAULT;

    public MenuDrawerHelper(Activity activity, MenuDrawer.OnInterceptMoveEventListener listener) {
        mActivity = activity;
        mSharedPrefs = new SharedPrefsUtil(mActivity.getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);
        mListener = listener;
        initMenu();
    }

    private void initMenu() {

        mPhoneList = new ArrayList<>();

        //初始化 TYPE.OVERLAY 为覆盖模式
        mDrawer = MenuDrawer.attach(mActivity, MenuDrawer.Type.OVERLAY);

        //设置菜单栏的Layout
        mDrawer.setMenuView(R.layout.menu_main);
        //设置Activity的Layout
        mDrawer.setContentView(R.layout.activity_parttime_main);
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


        tvHint = (TextView) mDrawer.findViewById(R.id.tv_hint);
        ivUserinfo = (ImageView) mDrawer.findViewById(R.id.iv_userinfo);
        ivDriverAvatar = (ImageView) mDrawer.findViewById(R.id.iv_driver_avatar);
        tvDriverName = (TextView) mDrawer.findViewById(R.id.tv_driver_name);
        tvDriverTel = (TextView) mDrawer.findViewById(R.id.tv_driver_tel);
        llArea = (LinearLayout) mDrawer.findViewById(R.id.ll_area);
        llUserinfo = (LinearLayout) mDrawer.findViewById(R.id.ll_userinfo);

        tvDriverTel.setText(mSharedPrefs.getStringSP(Constant.SharedPrefrence.TEL, null));

        tvDriverName.setText(mSharedPrefs.getStringSP(Constant.SharedPrefrence.USER_NAME, null));

        ivUserinfo.setOnClickListener(MenuDrawerHelper.this);

        mDrawer.findViewById(R.id.tv_exit).setOnClickListener(MenuDrawerHelper.this);

        mDrawer.findViewById(R.id.ll_account).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_leader).setOnClickListener(MenuDrawerHelper.this);
        mDrawer.findViewById(R.id.ll_area).setOnClickListener(MenuDrawerHelper.this);

        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                //android.util.Log.d("lhy","oldState:"+oldState+".....newState:"+newState);
                if (newState == MenuDrawer.STATE_OPEN) {
                    getUserStatus();
                    getLeaderInfo();
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
            case ACTION_ADDINFO:
                intent = new Intent(mActivity, AddUserInfoActivity.class);
                mActivity.startActivity(intent);
                break;
            case ACTION_ACCONUT:
                intent = new Intent(mActivity, AccountActivity.class);
                intent.putExtra("num", mCheckingOrder);
                intent.putExtra("money", mCheckingMoney);
                mActivity.startActivity(intent);
                break;
            case ACTION_LEADER:
                if (mPhoneList == null || mPhoneList.size() == 0) {
                    SystemUtils.showToast(mActivity, "暂无区域负责人联系电话");
                } else {
                    showCallListDialog();
                }
                break;
            case ACTION_AREA:
                intent = new Intent(mActivity, ReviewUserInfoActivity.class);
                mActivity.startActivity(intent);
                break;
        }
        mAction = ACTION_DEFAULT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exit:
                exit();
                break;
            case R.id.iv_userinfo:
                mAction = ACTION_ADDINFO;
                mDrawer.toggleMenu();
                break;
            case R.id.ll_account:
                mAction = ACTION_ACCONUT;
                mDrawer.toggleMenu();
                break;
            case R.id.ll_leader:
                mAction = ACTION_LEADER;
                mDrawer.toggleMenu();
                break;
            case R.id.ll_area:
                mAction = ACTION_AREA;
                mDrawer.toggleMenu();
                break;
        }
    }

    private void exit() {
        SystemUtils.showToast(mActivity, "退出登录成功");
        mSharedPrefs.clearAll();
        Server.resetServerApi();
        Intent intent = new Intent();
        intent.setAction(Constant.LOGIN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }


    private void refershMenuByUserStatus(String status) {
        switch (status) {
            case Constant.UserStatus.STATUS_INIT:
            case Constant.UserStatus.STATUS_INFO_COMPLETED:
            case Constant.UserStatus.STATUS_BANNED:
                llArea.setVisibility(View.GONE);
                tvHint.setVisibility(View.VISIBLE);
                ivUserinfo.setImageResource(R.drawable.ic_menu_addinfo);
                ivDriverAvatar.setImageResource(R.drawable.ic_menu_default_avatar);
                break;
            case Constant.UserStatus.STATUS_WORKING:
                llArea.setVisibility(View.VISIBLE);
                tvHint.setVisibility(View.GONE);
                ivUserinfo.setImageResource(R.drawable.ic_menu_ok);
                getUserInfo();
                break;
        }
        llUserinfo.setVisibility(View.VISIBLE);
    }

    private void refreshDriverInfo(GetUserInfoCallback callback) {
        tvDriverName.setText(callback.name);
        QiniuUtil.loadImg(mActivity, ivDriverAvatar, callback.avatar);
    }

    /**
     * 联系区域负责人
     */
    private void showCallListDialog() {
        final AlertDialog dialog = CustomUtils.createDialog(mActivity, R.layout.dialog_call_director_phone, R.style.DialogInOutBottomAnimation, false);

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 动态添加item
        LinearLayout ll_call_list = (LinearLayout) dialog.findViewById(R.id.ll_call_list);
        for (final GetAccountInfoCallback.AccountEntity phone : mPhoneList) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_dialig_call_director, null);
            ll_call_list.addView(view);
            ((TextView) view.findViewById(R.id.tv_take_phone)).setText(phone.name + " " + phone.tel);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtils.makeCall(mActivity, phone.tel);
                    dialog.dismiss();
                }
            });
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
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            return true;
        }
        return false;
    }

    private void getUserStatus() {

        Callback<String> callback = new Callback<String>() {
            @Override
            public void onSuccess(String s, Response response) {
                if (!TextUtils.isEmpty(s)) {
                    refershMenuByUserStatus(s);
                }
            }

            @Override
            public void onFailure(RetrofitError error) {

            }
        };

        Server.getServerApi(mActivity).getReviewStatus(callback);
    }

    private void getUserInfo() {

        Callback<GetUserInfoCallback> callback = new Callback<GetUserInfoCallback>() {
            @Override
            public void onSuccess(GetUserInfoCallback result, Response response) {
                if (result != null) {
                    refreshDriverInfo(result);
                } else {
                    llUserinfo.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(RetrofitError error) {
                llUserinfo.setVisibility(View.INVISIBLE);
            }
        };

        Server.getServerApi(mActivity).getUserInfo(callback);
    }


    private void getLeaderInfo() {

        Callback<GetAccountInfoCallback> callback = new Callback<GetAccountInfoCallback>() {
            @Override
            public void onSuccess(GetAccountInfoCallback result, Response response) {
                if (result != null && result.content != null) {
                    mCheckingOrder = result.content.finish_count;
                    mCheckingMoney = result.content.fee;
                    if (result.content.fence_managers != null) {
                        mPhoneList.clear();
                        mPhoneList.addAll(result.content.fence_managers);
                    }
                }

            }

            @Override
            public void onFailure(RetrofitError error) {

            }
        };
        Server.getServerApi(mActivity).getAccountInfo(callback);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDrawerHelper.isMenuOpened())
            mMenuDrawerHelper.closeMenu();
        else
            super.onBackPressed();
    }


}
