package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import ming.cloudmusic.R;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.util.MusicsManager;

/**
 * Created by Lhy on 2016/3/19.
 */
public class MyMusicFragment extends DefaultBaseFragment implements View.OnClickListener {

    private ImageView ivMenu;
    private ImageView ivSearch;
    private View rlLocalmusic;
    private TextView tvLocalmusicNum;
    private View rlHistory;
    private TextView tvHistoryNum;
    private View rlDld;
    private TextView tvDldNum;
    private View rlMyart;
    private TextView tvMyartNum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mymusic, container, false);
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
        ivMenu = (ImageView) view.findViewById(R.id.iv_menu);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        rlLocalmusic = view.findViewById(R.id.rl_localmusic);
        tvLocalmusicNum = (TextView) view.findViewById(R.id.tv_localmusic_num);
        rlHistory = view.findViewById(R.id.rl_history);
        tvHistoryNum = (TextView) view.findViewById(R.id.tv_history_num);
        rlDld = view.findViewById(R.id.rl_dld);
        tvDldNum = (TextView) view.findViewById(R.id.tv_dld_num);
        rlMyart = view.findViewById(R.id.rl_myart);
        tvMyartNum = (TextView) view.findViewById(R.id.tv_myart_num);

        ivMenu.setOnClickListener(this);
        rlLocalmusic.setOnClickListener(this);
        rlHistory.setOnClickListener(this);
        rlDld.setOnClickListener(this);
        rlMyart.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Map<String,String> map = MusicsManager.getInstance().getMusicsCount();
        tvLocalmusicNum.setText("(" + map.get(MusicsManager.KEY_LOCALMUSICS_COUNT) + ")");
        tvHistoryNum.setText("(" + map.get(MusicsManager.KEY_HISTORYMUSICS_COUNT) + ")");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                postEventMsg(KeyEvent.TOGGLE_MENU);
                break;
            case R.id.rl_localmusic:
                postEventMsg(KeyEvent.ACTION_LOCALMUSIC);
                break;
            case R.id.rl_history:
                postEventMsg(KeyEvent.ACTION_HISTORYMUSIC);
                break;
            case R.id.rl_dld:
                postEventMsg(KeyEvent.ACTION_DLD);
                break;
            case R.id.rl_myart:
                postEventMsg(KeyEvent.ACTION_ARTLIST);
                break;
        }
    }

}
