package ming.cloudmusic.activity;

import android.os.Bundle;

import ming.cloudmusic.R;
import ming.cloudmusic.activity.BaseActivity.DefalutBaseActivity;
import ming.cloudmusic.util.ReaderMusicDao;

/**
 * Created by Lhy on 2016/3/5.
 */
public class LaunchActivity extends DefalutBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ReaderMusicDao dao = new ReaderMusicDao();
        dao.findLocalMusic(getContentResolver());
        dao.getMusics();
    }
}
