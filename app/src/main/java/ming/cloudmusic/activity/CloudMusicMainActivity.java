package ming.cloudmusic.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import net.simonvt.menudrawer.MenuDrawer;

import ming.cloudmusic.util.MenuDrawerHelper;

public class CloudMusicMainActivity extends FragmentActivity {

    private MenuDrawerHelper mDrawerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerHelper = new MenuDrawerHelper(this, new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(View v, int delta, int x, int y) {
                return false;
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (mDrawerHelper.isMenuOpened())
            mDrawerHelper.closeMenu();
        else
            super.onBackPressed();
    }


}
