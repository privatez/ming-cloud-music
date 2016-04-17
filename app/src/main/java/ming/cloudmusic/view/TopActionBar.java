package ming.cloudmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ming.cloudmusic.R;

/**
 * Created by lhy on 16/3/21.
 */
public class TopActionBar extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivSearch;
    private ImageView ivMore;

    private Context mContext;

    public TopActionBar(Context context) {
        super(context);
    }

    public TopActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context, attrs);
    }

    public TopActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.common_top_bar, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_text);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        ivMore = (ImageView) findViewById(R.id.iv_more);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopActionBar);
        String titleValue = typedArray.getString(R.styleable.TopActionBar_titleText);
        String rightValue = typedArray.getString(R.styleable.TopActionBar_rightTextBtn);
        tvTitle.setText(titleValue);
        if (rightValue != null && rightValue.length() > 0) {
            tvRight.setText(rightValue);
            tvRight.setVisibility(View.VISIBLE);
        } else {
            tvRight.setVisibility(View.GONE);
        }
        typedArray.recycle();
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack(mContext);
            }
        });
    }

    private void doBack(Context context) {
        Class c = context.getClass();
        try {
            Method method = c.getMethod("onBackPressed");
            method.invoke(context);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setOnRightClickListener(OnClickListener listener) {
        tvRight.setOnClickListener(listener);
    }

    public void setOnSearchClickListener(OnClickListener listener) {
        ivSearch.setVisibility(VISIBLE);
        ivSearch.setOnClickListener(listener);
    }

    public void setOnMoreClickListener(OnClickListener listener) {
        ivMore.setVisibility(VISIBLE);
        ivMore.setOnClickListener(listener);
    }

    public void setRightVisibility(int visibility) {
        tvRight.setVisibility(visibility);
    }

    public void setRightText(String text) {
        tvRight.setText(text);
    }

    public void setTitleText(String text) {
        tvTitle.setText(text);
    }

}
