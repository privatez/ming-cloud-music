package ming.cloudmusic.activity;

import ming.cloudmusic.util.CustomUtils;

/**
 * Created by lihaiye on 16/4/27.
 */
public abstract class DrawableBaseActivity extends DefalutBaseActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        CustomUtils.setTranslucent(this);
    }

    @Override
    protected void setStatusBar() {

    }

}
