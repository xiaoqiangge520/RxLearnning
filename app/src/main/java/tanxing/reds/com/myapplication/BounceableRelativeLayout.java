package tanxing.reds.com.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 1. 原创作者:
 * 2. 谷哥的小弟
 * 3. 博客地址:
 * 4. http://blog.csdn.net/lfdfhl
 */
public class BounceableRelativeLayout extends RelativeLayout {
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private final String TAG = "stay4it";

    public BounceableRelativeLayout(Context context) {
        this(context, null);
    }

    public BounceableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setLongClickable(true);
        mScroller = new Scroller(context);
        mGestureDetector = new GestureDetector(context, new GestureListenerImpl());
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            Log.e(TAG, "computeScroll getFinalY=" + mScroller.getFinalY());
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                reset(0, 0);
                break;
            default:
                return mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }


    class GestureListenerImpl implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e(TAG, "onScroll: distanceY" + distanceY);

            int disY = (int) ((distanceY - 0.5) / 2);
            beginScroll(0, disY);
            return false;
        }

        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float x, float y) {
            return false;
        }

    }


    protected void reset(int x, int y) {
        int dx = x - mScroller.getFinalX();
        int dy = y - mScroller.getFinalY();

        Log.e(TAG, "reset getFinalY=" + mScroller.getFinalY() + ",dy=" + dy);
        beginScroll(dx, dy);
    }

    protected void beginScroll(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);

        Log.e(TAG, "beginScroll getFinalY=" + mScroller.getFinalY() + ",dy=" + dy);
        invalidate();
    }
}