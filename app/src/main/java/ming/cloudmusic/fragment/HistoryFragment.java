package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.R;
import ming.cloudmusic.adapter.CommonAdapter;
import ming.cloudmusic.adapter.ViewHolder;
import ming.cloudmusic.db.MusicDao;
import ming.cloudmusic.event.model.DataEvent;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.view.TopActionBar;

/**
 * Created by Lhy on 2016/3/19.
 */
public class HistoryFragment extends DefaultBaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TopActionBar tbTopbar;
    private RelativeLayout rlPlayall;
    private TextView tvMusicsCount;
    private ListView lvMusics;
    private View llContent;
    private View llCommonBg;

    private List<DbMusic> mHistoryMusics;
    private CommonAdapter<DbMusic> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();

    }

    @Override
    public void initView() {
        View view = getView();
        lvMusics = (ListView) view.findViewById(R.id.lv_musics);
        tbTopbar = (TopActionBar) view.findViewById(R.id.tb_topbar);
        rlPlayall = (RelativeLayout) view.findViewById(R.id.rl_playall);
        tvMusicsCount = (TextView) view.findViewById(R.id.tv_musics_count);
        lvMusics = (ListView) view.findViewById(R.id.lv_musics);
        llContent = view.findViewById(R.id.ll_content);
        llCommonBg = view.findViewById(R.id.ll_common_bg);

        tbTopbar.setOnRightClickListener(this);
        rlPlayall.setOnClickListener(this);

        lvMusics.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mHistoryMusics = new ArrayList<>();
        mHistoryMusics.addAll(MusicDao.getDefaultDao().getHistoryMusics());
        tvMusicsCount.setText("(共" + mHistoryMusics.size() + "首)");
        refreshBackGround(llCommonBg, llContent);
        setAdapter();

        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataEvent event) {
        switch (event.getMsg()) {
            case DataEvent.HISTORYMUSICS_CHANGGE:
                mHistoryMusics.addAll(MusicDao.getDefaultDao().getHistoryMusics());
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                clearHistoryMusics();
                break;
            case R.id.rl_playall:
                MusicsManager.getInstance().playAllMusic(mHistoryMusics);
                break;
        }
    }

    private void clearHistoryMusics() {
        MusicsManager.getInstance().clearHistroyMusics();
        mHistoryMusics.clear();
        mAdapter.notifyDataSetChanged();
        tvMusicsCount.setText("(共" + mHistoryMusics.size() + "首)");
        refreshBackGround(llCommonBg, llContent);
    }

    private void setAdapter() {
        lvMusics.setAdapter(mAdapter = new CommonAdapter<DbMusic>(mContext, mHistoryMusics, R.layout.item_localmusic_allmusic_music) {
            @Override
            public void convert(ViewHolder holder, DbMusic item) {
                holder.setText(R.id.tv_title, item.getTitle());
                holder.setText(R.id.tv_art, item.getArtlist());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicsManager.getInstance().playMusicByPosition(mHistoryMusics, position);
    }

}
