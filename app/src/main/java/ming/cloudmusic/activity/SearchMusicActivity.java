package ming.cloudmusic.activity;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.R;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.view.MusicListView;

/**
 * Created by Lhy on 2016/4/17.
 */
public class SearchMusicActivity extends DefalutBaseActivity {

    private ImageView ivBack;
    private MusicListView mlvSearch;

    private List<DbMusic> musics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchmusic);

        initView();
        initData();
    }


    @Override
    public void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        mlvSearch = (MusicListView) findViewById(R.id.mlv_search);
    }

    @Override
    public void initData() {
        musics = new ArrayList<>();
        musics.addAll(MusicsManager.getInstance().getLocalMusics());
        mlvSearch.notifyDataSetChanged(musics);
    }
}
