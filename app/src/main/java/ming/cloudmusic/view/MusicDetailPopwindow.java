package ming.cloudmusic.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ming.cloudmusic.R;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.LogUtils;

/**
 * Created by Lhy on 2016/5/5.
 */
public class MusicDetailPopwindow extends PopupWindow {

    private TextView tvMusicTitle;
    private RelativeLayout rlNextPlay;
    private RelativeLayout rlFav;
    private RelativeLayout rlDld;
    private RelativeLayout rlDelete;
    private RelativeLayout rlRing;

    private Context mContext;
    private DbMusic mDbMusic;

    public MusicDetailPopwindow(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_music_detail, null);
        setContentView(view);
        
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        setFocusable(true);
        setOutsideTouchable(true);

        tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
        rlNextPlay = (RelativeLayout) view.findViewById(R.id.rl_next_play);
        rlFav = (RelativeLayout) view.findViewById(R.id.rl_fav);
        rlDld = (RelativeLayout) view.findViewById(R.id.rl_dld);
        rlDelete = (RelativeLayout) view.findViewById(R.id.rl_delete);
        rlRing = (RelativeLayout) view.findViewById(R.id.rl_ring);
    }

    public void showAtLocationAndSetDbMusic(DbMusic music, View parent, int gravity, int x, int y) {
        showAtLocation(parent, gravity, x, y);
        mDbMusic = music;
        refreView(mDbMusic);
        LogUtils.log("showAtLocationAndSetDbMusic");
    }

    private void refreView(DbMusic music) {
        tvMusicTitle.setText("歌曲：" + music.getTitle());
        if (music.isLocalMusic()) {
            rlDld.setVisibility(View.GONE);
            rlNextPlay.setVisibility(View.VISIBLE);
            rlFav.setVisibility(View.VISIBLE);
            rlDelete.setVisibility(View.VISIBLE);
            rlRing.setVisibility(View.VISIBLE);
        } else {
            rlDld.setVisibility(View.VISIBLE);
            rlNextPlay.setVisibility(View.GONE);
            rlFav.setVisibility(View.GONE);
            rlDelete.setVisibility(View.GONE);
            rlRing.setVisibility(View.GONE);
        }
    }

}
