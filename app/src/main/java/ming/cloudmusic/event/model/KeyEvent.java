package ming.cloudmusic.event.model;

/**
 * Created by Lhy on 2016/3/6.
 */
public class KeyEvent extends BaseEvent {

    //TODO 删除KEY_

    public static final String PLAY_OR_PAUSE = "PLAY_OR_PAUSE";

    public static final String NEXT = "K_NEXT";

    public static final String PREVIOUS = "K_PREVIOUS";

    public static final String PLAY_MODE = "K_PLAY_MODE";

    public static final String BAR_CHANGE = "K_BAR_CHANGE";
    
    public static final String PLAY_ALL = "play_all";

    public static final String PLAY_BY_POSITION = "PLAY_BY_POSITION";

    public static final String GET_PLAYINGMUSIC = "GET_PLAYINGMUSIC";

    public static final String TOGGLE_MENU = "TOGGLE_MENU";

    public static final String ACTION_LOCALMUSIC = "localmusic";

    public static final String ACTION_HISTORYMUSIC = "history";

    public static final String ACTION_DLD = "dld";

    public static final String ACTION_ARTLIST = "artlist";

    public static final String POST_MILLISUNTILFINISHED = "millisUntilFinished";

}
