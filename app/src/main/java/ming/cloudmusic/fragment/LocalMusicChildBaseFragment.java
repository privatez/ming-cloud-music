package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.xutils.db.table.DbModel;

import java.util.List;

import ming.cloudmusic.R;
import ming.cloudmusic.adapter.CommonAdapter;
import ming.cloudmusic.adapter.ViewHolder;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.FragmentTaskManager;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.view.MusicListView;

/**
 * Created by Lhy on 2016/4/19.
 */
public class LocalMusicChildBaseFragment extends DefaultBaseFragment {

    private static final String TYPE_LOCALMUSIC = "type";

    public static final String LOCALMUSIC_SINGLE = "single";
    public static final String LOCALMUSIC_ART = "art";
    public static final String LOCALMUSIC_ALBUM = "album";
    public static final String LOCALMUSIC_FILE = "file";

    private MusicListView mlvLocalMusic;

    private String mFragmentType;

    public static LocalMusicChildBaseFragment getNewInstance(String fragmentType) {
        LocalMusicChildBaseFragment fragment = new LocalMusicChildBaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE_LOCALMUSIC, fragmentType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_localmusic_common, null);
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
        mlvLocalMusic = (MusicListView) view.findViewById(R.id.mlv_localmusic);
    }

    @Override
    public void initData() {
        mFragmentType = getArguments().getString(TYPE_LOCALMUSIC);

        switch (mFragmentType) {
            case LOCALMUSIC_SINGLE:
                mlvLocalMusic.notifyDataSetChanged(MusicsManager.getInstance().getLocalMusics());
                break;
            case LOCALMUSIC_ART:
                groupByArt();
                break;
            case LOCALMUSIC_ALBUM:
                groupByAlbum();
                break;
            case LOCALMUSIC_FILE:
                groupByPath();
                break;
        }

    }

    private void groupByArt() {
        final List<DbModel> dbModels = MusicsManager.getInstance().getMusicInfoGroupByArt();

        CommonAdapter adapter = new CommonAdapter<DbModel>(mContext, dbModels, R.layout.item_localmusic_common) {
            @Override
            public void convert(ViewHolder holder, DbModel item) {
                holder.setText(R.id.tv_title, item.getString(DbMusic.COLUMN_ARTLIST));
                holder.setText(R.id.tv_count, item.getString(DbMusic.COLUMN_COUNT) + "首");
            }
        };

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(ReviewLocalMusicByColumnFragment.EXTRA_KEY, dbModels.get(position).getString(DbMusic.COLUMN_ARTLIST));
                bundle.putString(ReviewLocalMusicByColumnFragment.EXTRA_COLUMN, DbMusic.COLUMN_ARTLIST);
                FragmentTaskManager.getInstance().switchFragment(mFragment.getParentFragment(), ReviewLocalMusicByColumnFragment.class, bundle);
            }
        };

        mlvLocalMusic.setPlayAllVisibility(View.GONE);
        mlvLocalMusic.setAdapter(adapter, listener);
    }

    private void groupByAlbum() {
        final List<DbModel> dbModels = MusicsManager.getInstance().getMusicInfoGroupByAlbum();

        CommonAdapter adapter = new CommonAdapter<DbModel>(mContext, dbModels, R.layout.item_localmusic_common) {
            @Override
            public void convert(ViewHolder holder, DbModel item) {
                holder.setText(R.id.tv_title, item.getString(DbMusic.COLUMN_ALBUM));
                holder.setText(R.id.tv_count, item.getString(DbMusic.COLUMN_COUNT) + "首");
                holder.setText(R.id.tv_music_info, item.getString(DbMusic.COLUMN_ARTLIST));
            }
        };

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(ReviewLocalMusicByColumnFragment.EXTRA_KEY, dbModels.get(position).getString(DbMusic.COLUMN_ALBUM));
                bundle.putString(ReviewLocalMusicByColumnFragment.EXTRA_COLUMN, DbMusic.COLUMN_ALBUM);
                FragmentTaskManager.getInstance().switchFragment(mFragment.getParentFragment(), ReviewLocalMusicByColumnFragment.class, bundle);
            }
        };

        mlvLocalMusic.setPlayAllVisibility(View.GONE);
        mlvLocalMusic.setAdapter(adapter, listener);
    }

    private void groupByPath() {
        final List<DbModel> dbModels = MusicsManager.getInstance().getMusicInfoGroupByFileName();

        CommonAdapter adapter = new CommonAdapter<DbModel>(mContext, dbModels, R.layout.item_localmusic_common) {
            @Override
            public void convert(ViewHolder holder, DbModel item) {
                String filename = item.getString(DbMusic.COLUMN_FILENAME);
                int lastPosition = filename.lastIndexOf("/") + 1;
                holder.setText(R.id.tv_title, filename.substring(0, lastPosition));
                holder.setText(R.id.tv_music_info, filename.substring(lastPosition, filename.length()));
                holder.setText(R.id.tv_count, item.getString(DbMusic.COLUMN_COUNT) + "首");
            }
        };

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(ReviewLocalMusicByColumnFragment.EXTRA_KEY, dbModels.get(position).getString(DbMusic.COLUMN_FILENAME));
                bundle.putString(ReviewLocalMusicByColumnFragment.EXTRA_COLUMN, DbMusic.COLUMN_FILENAME);
                FragmentTaskManager.getInstance().switchFragment(mFragment.getParentFragment(), ReviewLocalMusicByColumnFragment.class, bundle);
            }
        };

        mlvLocalMusic.setPlayAllVisibility(View.GONE);
        mlvLocalMusic.setAdapter(adapter, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlvLocalMusic.onDestroy();
    }
}
