package sbbic.com.sharepaneldemo.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by God on 2016/5/18.
 */
public class FixedIndicatorView extends LinearLayout implements Indicator {
    public static final int SPLITMETHOD_EQUALS = 0;
    public static final int SPLITMETHOD_WEIGHT = 1;
    public static final int SPLITMETHOD_WRAP = 2;
    private IndicatorAdapter mAdapter;
    private OnItemSelectedListener mOnItemSelectedListener;
    private int splitMethod = SPLITMETHOD_EQUALS;

    private int mPreSelectedTabIndex = -1;
    private int mSelectedTabIndex = -1;

    private InRun mInRun;

    private int[] prePositions = {-1, -1};
    private float positionOffset;

    private List<ViewGroup> views = new LinkedList<>();

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            ViewGroup parent = (ViewGroup) v;
            setCurrentItem(tag);
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(parent.getChildAt(0), tag, mPreSelectedTabIndex);
            }
        }
    };
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (!mInRun.isFinished()) {
                mInRun.stop();
            }

            positionOffset = 0;
            int count = getChildCount();
            int newCount = mAdapter.getCount();
            views.clear();

            for (int i = 0; i < count && i < newCount; i++) {
                views.add((ViewGroup) getChildAt(i));
            }
            removeAllViews();

            int size = views.size();
            for (int i = 0; i < newCount; i++) {
                LinearLayout result = new LinearLayout(getContext());
                View view;
                if (i < size) {
                    View temp = views.get(i).getChildAt(0);
                    views.get(i).removeView(temp);
                    view = mAdapter.getView(i, temp, result);
                } else {
                    view = mAdapter.getView(i, null, result);
                }

                result.addView(view);
                result.setOnClickListener(mOnClickListener);
                result.setTag(i);
                addView(result, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            }

            mPreSelectedTabIndex = -1;
            setCurrentItem(mSelectedTabIndex, false);
            measureTabs();


        }
    };

    public FixedIndicatorView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mInRun = new InRun();
    }

    public FixedIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixedIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mInRun.stop();
    }

    @Override
    public IndicatorAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(IndicatorAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unRegisterDataSetObserver(mDataSetObserver);
        }

        this.mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void setItemSelectListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    @Override
    public OnItemSelectedListener getOnItemSelectItemListener() {
        return this.mOnItemSelectedListener;
    }

    @Override
    public void setOnTransitionListener(OnTransitionListener listener) {
    }

    @Override
    public OnTransitionListener onTransitionListener() {
        return null;
    }

    @Override
    public View getItemView(int item) {
        if (item < 0 || item > mAdapter.getCount() - 1) {
            return null;
        }
        final ViewGroup group = (ViewGroup) getChildAt(-1);
        return group.getChildAt(0);
    }

    @Override
    public int getCurrentItem() {
        return mSelectedTabIndex;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean anim) {
        int count = getCount();
        if (count == 0) {
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item > count - 1) {
            item = count - 1;
        }

        if (mSelectedTabIndex != item) {
            mPreSelectedTabIndex = mSelectedTabIndex;
            mSelectedTabIndex = item;
            final int tabCount = mAdapter.getCount();
            for (int i = 0; i < tabCount; i++) {
                final ViewGroup group = (ViewGroup) getChildAt(i);
                View child = group.getChildAt(0);
                final boolean isSelected = (i == item);
                child.setSelected(isSelected);
            }

            if (!mInRun.isFinished()) {
                mInRun.stop();
            }

            if (positionOffset < 0.02f || positionOffset > 0.98f || !anim) {
                Log.d("FixedIndicatorView", "notify page need changed");
            }

            if (getWidth() != 0 && anim && positionOffset < 0.01f && mPreSelectedTabIndex >= 0 && mPreSelectedTabIndex < getChildCount()) {
                int sx = getChildAt(mPreSelectedTabIndex).getLeft();
                int ex = getChildAt(item).getLeft();
                final float pageDelta = Math.abs(ex - sx) / ((getChildAt(item)).getWidth());
                int duration = (int) ((pageDelta + 1) * 100);
                duration = Math.min(duration, 600);
                mInRun.startScroll(sx, ex, duration);
            }

        }
    }

    public int getCount() {
        if (mAdapter == null) {
            return 0;
        }
        return mAdapter.getCount();
    }

    @Override
    public int getPreSelectedItem() {
        return mPreSelectedTabIndex;
    }

    public void setSplitMethod(int splitMethod) {
        this.splitMethod = splitMethod;
        measureTabs();
    }

    private void measureTabs() {
        int count = getChildCount();

        switch (splitMethod) {
            case SPLITMETHOD_EQUALS:
                for (int i = 0; i < count; i++) {
                    View view = getChildAt(i);
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.width = 0;
                    layoutParams.weight = 1;
                    view.setLayoutParams(layoutParams);
                }
                break;
            case SPLITMETHOD_WEIGHT:
                for (int i = 0; i < count; i++) {
                    View view = getChildAt(i);
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.width = LayoutParams.WRAP_CONTENT;
                    layoutParams.weight = 1;
                    view.setLayoutParams(layoutParams);
                }
                break;

            case SPLITMETHOD_WRAP:
                for (int i = 0; i < count; i++) {
                    View view = getChildAt(i);
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.width = LayoutParams.WRAP_CONTENT;
                    layoutParams.weight = 0;
                    view.setLayoutParams(layoutParams);
                }

                break;
        }

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        super.measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private class InRun implements Runnable {
        private final Interpolator mInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                input -= 1.0f;
                return input * input * input * input * input + 1.0f;
            }
        };
        private int updateTime = 20;
        private Scroller mScroller;

        public InRun() {
            super();
            mScroller = new Scroller(getContext(), mInterpolator);
        }

        public void startScroll(int startX, int endX, int duration) {
            mScroller.startScroll(startX, 0, endX - startX, 0, duration);
            ViewCompat.postInvalidateOnAnimation(FixedIndicatorView.this);
            post(this);

        }

        public boolean isFinished() {
            return mScroller.isFinished();
        }

        public boolean computeScrollOffset() {
            return mScroller.computeScrollOffset();
        }

        public int getCurrentX() {
            return mScroller.getCurrX();
        }

        public void stop() {
            if (mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            removeCallbacks(this);
        }

        @Override
        public void run() {
            ViewCompat.postInvalidateOnAnimation(FixedIndicatorView.this);
            if (!mScroller.isFinished()) {
                postDelayed(this, updateTime);
            }
        }
    }


}
