package ming.cloudmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.MusicsManager;

/**
 * Created by Lhy on 2016/4/17.
 */
public class MusicListView extends FrameLayout implements View.OnClickListener {

    private RelativeLayout rlPlayAll;
    private TextView tvMusicsCount;
    private ListView lvMusics;
    private View llContent;
    private View llBg;
    private TextView tvHint;

    private Context mContext;

    private MusicsManager mMusicsManager;
    private MusicDetailPopwindow mMusicDetailPopwindow;

    private CommonAdapter mAdapter;
    private List<DbMusic> mMusicList;

    private OnClickListener mOnMenuClickListener;

    private String mOnDataLoadedHint;

    public MusicListView(Context context) {
        super(context);
    }

    public MusicListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context, attrs);
        initData();
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.common_musiclist, this);

        rlPlayAll = (RelativeLayout) findViewById(R.id.rl_playall);
        tvMusicsCount = (TextView) findViewById(R.id.tv_musics_count);
        lvMusics = (ListView) findViewById(R.id.lv_musics);
        llContent = findViewById(R.id.ll_content);
        llBg = findViewById(R.id.ll_bg);
        tvHint = (TextView) findViewById(R.id.tv_hint);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MusicListView);

        String hint = typedArray.getString(R.styleable.MusicListView_dataLoadedHintText);
        if (!TextUtils.isEmpty(hint)) {
            mOnDataLoadedHint = hint;
        } else {
            mOnDataLoadedHint = getResources().getString(R.string.onDataLoadedHint);
        }

        boolean isPlayAllGone = typedArray.getBoolean(R.styleable.MusicListView_playAllGone, false);
        if (isPlayAllGone) {
            rlPlayAll.setVisibility(GONE);
        } else {
            rlPlayAll.setVisibility(VISIBLE);
            rlPlayAll.setOnClickListener(this);
        }

        typedArray.recycle();

    }

    private void initData() {
        mMusicList = new ArrayList<>();
        mMusicsManager = MusicsManager.getInstance();
        setDefaultAdapter();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_playall:
                MusicsManager.getInstance().playAllMusic(mMusicList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {
        String msg = event.getMsg();
        switch (msg) {
            case ServiceEvent.SERVICE_POST_PLAYINGMUSIC:
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void refreBg(int dataSize) {
        if (dataSize > 0) {
            llBg.setVisibility(View.GONE);
            llContent.setVisibility(View.VISIBLE);
        } else {
            llBg.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
            tvHint.setText(mOnDataLoadedHint);
        }
    }

    private void showMusicDetailPopwindow(DbMusic music) {
        if (mMusicDetailPopwindow == null) {
            mMusicDetailPopwindow = new MusicDetailPopwindow(mContext);
        }
        mMusicDetailPopwindow.showAtLocationAndSetDbMusic(music, llContent, 0, 0, 0);
    }

    private void setDefaultAdapter() {
        lvMusics.setAdapter(mAdapter = new CommonAdapter<DbMusic>(mContext, mMusicList, R.layout.item_musiclist_common) {
            @Override
            public void convert(ViewHolder holder, final DbMusic item) {
                ImageView ivIsLocal = holder.getView(R.id.iv_tag);
                ImageView ivPlaying = holder.getView(R.id.iv_playing);
                holder.setText(R.id.tv_title, item.getTitle());
                holder.setText(R.id.tv_art, item.getArtlist());

                if (mOnMenuClickListener != null) {
                    LogUtils.log("mOnMenuClickListener");
                    holder.getView(R.id.iv_menu).setOnClickListener(mOnMenuClickListener);
                } else {
                    LogUtils.log("setOnClickListener" + holder.getPosition());
                    holder.getView(R.id.iv_menu).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMusicDetailPopwindow(item);
                        }
                    });
                }

                if (item.isLocalMusic()) {
                    ivIsLocal.setImageResource(R.drawable.list_icn_mobile);
                } else {
                    ivIsLocal.setImageResource(R.drawable.list_icn_dld_ok);
                }

                if (mMusicsManager.isMusicPlaying(item.getId())) {
                    ivPlaying.setVisibility(VISIBLE);
                } else {
                    ivPlaying.setVisibility(GONE);
                }
            }
        });

        lvMusics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicsManager.playMusicByPosition(mMusicList, position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setOnDataLoadedHint(String hint) {
        if (!TextUtils.isEmpty(hint)) {
            mOnDataLoadedHint = hint;
        } else {
            mOnDataLoadedHint = getResources().getString(R.string.onDataLoadedHint);
        }
    }

    public void setOnHintClickListener(OnClickListener listener) {
        tvHint.setOnClickListener(listener);
    }

    public void setPlayAllVisibility(int visibility) {
        rlPlayAll.setVisibility(visibility);
    }

    public void setOnItemMenuClickListener(OnClickListener listener) {
        mOnMenuClickListener = listener;
        mAdapter.notifyDataSetChanged();
    }

    public void setAdapter(CommonAdapter adapter, AdapterView.OnItemClickListener onItemClickListener) {
        mAdapter = adapter;
        lvMusics.setAdapter(mAdapter);
        lvMusics.setOnItemClickListener(onItemClickListener);
        mAdapter.notifyDataSetChanged();
        refreBg(mAdapter.getCount());
    }

    public void notifyDataSetChanged(List<DbMusic> musicList) {
        mMusicList.clear();
        mMusicList.addAll(musicList);
        mAdapter.notifyDataSetChanged();
        tvMusicsCount.setText("(共" + mMusicList.size() + "首)");
        refreBg(mMusicList.size());
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

}
