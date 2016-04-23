package ming.cloudmusic.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.MusicsManager;

/**
 * Created by Lhy on 2016/4/23.
 */
public class ReviewLocalMusicByColumnFragment extends MusicListCommonFragment {

    public static final String EXTRA_KEY = "key";
    public static final String EXTRA_COLUMN = "colum";

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        String key = bundle.getString(EXTRA_KEY);
        String column = bundle.getString(EXTRA_COLUMN);
        List<DbMusic> dbMusics = new ArrayList<>();

        MusicsManager musicsManager = MusicsManager.getInstance();
        switch (column) {
            case DbMusic.COLUMN_ARTLIST:
                dbMusics.addAll(musicsManager.getLocalMusicByArt(key));
                break;
            case DbMusic.COLUMN_ALBUM:
                dbMusics.addAll(musicsManager.getLocalMusicByAlbum(key));
                break;
            case DbMusic.COLUMN_FILENAME:
                dbMusics.addAll(musicsManager.getLocalMusicByFilename(key));
                break;
        }

        topActionBar.setTitleText(key);
        musicListView.notifyDataSetChanged(dbMusics);
    }

}
