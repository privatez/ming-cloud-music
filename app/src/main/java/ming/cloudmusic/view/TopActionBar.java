package ming.cloudmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ming.cloudmusic.R;

/**
 * Created by lhy on 16/3/21.
 */
public class TopActionBar extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_right;

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
        LayoutInflater.from(context).inflate(R.layout.activity_top_bar, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopActionBar);
        String titleValue = typedArray.getString(R.styleable.TopActionBar_titleText);
        String rightValue = typedArray.getString(R.styleable.TopActionBar_menuText);
        tv_title.setText(titleValue);
        if (rightValue != null && rightValue.length() > 0) {
            tv_right.setText(rightValue);
            tv_right.setVisibility(View.VISIBLE);
        } else {
            tv_right.setVisibility(View.GONE);
        }
        typedArray.recycle();
        tv_title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack(mContext);
            }
        });
    }


    private void doBack(final Context context) {
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
        tv_right.setOnClickListener(listener);
    }

    public void setRightVisibility(int visibility) {
        tv_right.setVisibility(visibility);
    }

    public void setRightText(String text) {
        tv_right.setText(text);
    }

}
