package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ming.cloudmusic.R;
import ming.cloudmusic.db.MusicDao;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.view.MusicListView;

/**
 * Created by Lhy on 2016/4/19.
 */
public class LocalMusicBaseFragment extends DefaultBaseFragment {

    private static final String TYPE_LOCALMUSIC = "type";

    public static final String LOCALMUSIC_SINGLE = "single";
    public static final String LOCALMUSIC_ART = "art";
    public static final String LOCALMUSIC_ALBUM = "album";
    public static final String LOCALMUSIC_FILE = "file";

    private MusicListView mlvLocalMusic;

    private String mFragmentType;

    public static LocalMusicBaseFragment getNewInstance(String fragmentType) {
        LocalMusicBaseFragment fragment = new LocalMusicBaseFragment();
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
                MusicDao.getDefaultDao().getLocalMusicgroupByArt();
                break;
            case LOCALMUSIC_ALBUM:

                break;
            case LOCALMUSIC_FILE:

                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlvLocalMusic.onDestroy();
    }
}
