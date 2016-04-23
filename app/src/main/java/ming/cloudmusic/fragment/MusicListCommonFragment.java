package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ming.cloudmusic.R;
import ming.cloudmusic.view.MusicListView;
import ming.cloudmusic.view.TopActionBar;

/**
 * Created by Lhy on 2016/4/23.
 */
public abstract class MusicListCommonFragment extends DefaultBaseFragment {

    protected TopActionBar topActionBar;
    protected MusicListView musicListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_musiclist_common, null);
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
        topActionBar = (TopActionBar) view.findViewById(R.id.top_action_bar);
        musicListView = (MusicListView) view.findViewById(R.id.music_list_view);
    }

    @Override
    public abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicListView.onDestroy();
    }
}
