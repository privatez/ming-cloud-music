package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.R;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.util.ToastUtils;
import ming.cloudmusic.view.MusicListView;

/**
 * Created by Lhy on 2016/4/17.
 */
public class SearchMusicActivity extends DefalutBaseActivity implements View.OnClickListener {

    public static final String EXTAR_SEARCHTYPE = "type";

    public static final int SEARCH_LOCALMUSIC = 31;
    public static final int SEARCH_CLOUDMUSIC = 32;

    private ImageView ivDelete;
    private EditText etSearch;
    private MusicListView mlvSearch;

    private List<DbMusic> musics;

    private int mSearchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchmusic);

        initView();
        initData();
    }


    @Override
    public void initView() {
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        etSearch = (EditText) findViewById(R.id.et_search);
        mlvSearch = (MusicListView) findViewById(R.id.mlv_search);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ivDelete.setVisibility(View.VISIBLE);
                    startSearch(s.toString());
                } else {
                    ivDelete.setVisibility(View.INVISIBLE);
                }
            }
        });
        ivDelete.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void initData() {
        musics = new ArrayList<>();

        mSearchType = getIntent().getIntExtra(EXTAR_SEARCHTYPE, -1);

        if (mSearchType == SEARCH_LOCALMUSIC) {
            etSearch.setHint("搜索本地音乐");
            mlvSearch.setOnDataLoadedHint("点击试试搜索网络音乐库吧");
            mlvSearch.setOnHintClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SearchMusicActivity.class);
                    intent.putExtra(SearchMusicActivity.EXTAR_SEARCHTYPE, SearchMusicActivity.SEARCH_CLOUDMUSIC);
                    mContext.startActivity(intent);
                }
            });
        } else if (mSearchType == SEARCH_CLOUDMUSIC) {
            etSearch.setHint("搜索音乐、歌手、专辑...");
            mlvSearch.setOnDataLoadedHint("暂无搜索数据");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                break;
        }
    }

    private void startSearch(String msg) {
        if (mSearchType == SEARCH_LOCALMUSIC) {
            musics.clear();
            musics.addAll(MusicsManager.getInstance().searchLocalMusic(msg));
            mlvSearch.notifyDataSetChanged(musics);
        } else if (mSearchType == SEARCH_CLOUDMUSIC) {
            ToastUtils.showShort(mContext,"网路搜索");
        }
    }

}
