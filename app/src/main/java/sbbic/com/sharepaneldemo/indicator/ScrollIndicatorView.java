package sbbic.com.sharepaneldemo.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by God on 2016/5/19.
 */
public class ScrollIndicatorView extends HorizontalScrollView implements Indicator {

    private SFixedIndicatorView mFixedIndicatorView;
    private boolean isPainnedTabView = false;
    private View mPainnedTabView;
    private Runnable mTabSelector;
    private boolean mActionDownHappened;

    private Paint defaultShadowPaint = null;
    private Drawable customShadowDrawable;
    private int shadoWidth;

    private float positionOffset = 0;
    private int unScrollPosition;
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (mTabSelector != null) {
                removeCallbacks(mTabSelector);
            }
            positionOffset = 0;
            setCurrentItem(mFixedIndicatorView.getCurrentItem(), false);
            if (isPainnedTabView) {
                if (mFixedIndicatorView.getChildCount() > 0) {
                    mPainnedTabView = mFixedIndicatorView.getChildAt(0);
                }
            }
        }
    };

    public ScrollIndicatorView(Context context) {
        this(context, null);
    }

    public ScrollIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFixedIndicatorView = new SFixedIndicatorView(context);
        addView(mFixedIndicatorView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setHorizontalScrollBarEnabled(false);
        setAutoSplit(true);

        defaultShadowPaint = new Paint();
        defaultShadowPaint.setAntiAlias(true);
        defaultShadowPaint.setColor(0x33AAAAAA);
        shadoWidth = dipToPix(3);

        defaultShadowPaint.setShadowLayer(shadoWidth, 0, 0, 0xFF000000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);//XXX
        }

    }

    public void setAutoSplit(boolean autoSplit) {
        setFillViewport(autoSplit);
        mFixedIndicatorView.setAutoSplit(autoSplit);
    }

    private int dipToPix(float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return size;
    }

    @Override
    public void setAdapter(IndicatorAdapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unRegisterDataSetObserver(mDataSetObserver);
        }


        mFixedIndicatorView.setAdapter(adapter);
        adapter.registerDataSetObserver(mDataSetObserver);
        mDataSetObserver.onChanged();
    }

    public boolean isSplitAuto() {
        return mFixedIndicatorView.isAutoSplit();
    }

    @Override
    public IndicatorAdapter getAdapter() {
        return mFixedIndicatorView.getAdapter();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            post(mTabSelector);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }


    public void setPinnedTabView(boolean isPainnedTabView) {
        this.isPainnedTabView = isPainnedTabView;
        if (isPainnedTabView && this.mFixedIndicatorView.getChildCount() > 0) {
            this.mPainnedTabView = this.mFixedIndicatorView.getChildAt(0);
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setPainnedShadow(int rsid, int shadoWidth) {
        setPainnedShadow(ContextCompat.getDrawable(getContext(), rsid), shadoWidth);
    }

    public void setPainnedShadow(Drawable shadowDrawable, int shadoWidth) {
        this.customShadowDrawable = shadowDrawable;
        this.shadoWidth = shadoWidth;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mFixedIndicatorView.getCount() > 0) {
            animateToTab(mFixedIndicatorView.getCurrentItem());
        }


    }

    private void animateToTab(final int position) {
        if (position < 0 || position > mFixedIndicatorView.getCount() - 1) {
            return;
        }
        final View tabView = mFixedIndicatorView.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }

        mTabSelector = new Runnable() {
            @Override
            public void run() {
                final int scroolPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scroolPos, 0);//XXX
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void setItemSelectListener(OnItemSelectedListener listener) {
        mFixedIndicatorView.setItemSelectListener(listener);
    }

    @Override
    public OnItemSelectedListener getOnItemSelectItemListener() {
        return mFixedIndicatorView.getOnItemSelectItemListener();
    }

    @Override
    public void setOnTransitionListener(OnTransitionListener listener) {

    }

    @Override
    public OnTransitionListener onTransitionListener() {
        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isPainnedTabView) {
            float x = ev.getX();
            float y = ev.getY();
            if (mPainnedTabView != null && y >= mPainnedTabView.getTop() && y <= mPainnedTabView.getBottom() && x > mPainnedTabView.getLeft() && x < mPainnedTabView.getRight()) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    mActionDownHappened = true;
                } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                    if (mActionDownHappened) {
                        mPainnedTabView.performClick();//XXX
                        invalidate(0, 0, mPainnedTabView.getMeasuredWidth(), mPainnedTabView.getMeasuredHeight());
                        mActionDownHappened = false;
                    }
                }
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public View getItemView(int item) {
        return mFixedIndicatorView.getItemView(item);
    }

    @Override
    public int getCurrentItem() {
        return mFixedIndicatorView.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean anim) {
        int count = mFixedIndicatorView.getCount();
        if (count == 0) {
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item > count - 1) {
            item = count - 1;
        }

        unScrollPosition = -1;
        if (positionOffset < 0.02f || positionOffset
                > 0.98f) {
            if (anim) {
                animateToTab(item);
            } else {
                final View tabView = mFixedIndicatorView.getChildAt(item);
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                if (scrollPos >= 0) {
                    scrollTo(scrollPos, 0);
                } else {
                    unScrollPosition = item;
                }
            }
        }
        mFixedIndicatorView.setCurrentItem(item, anim);
    }

    @Override
    public int getPreSelectedItem() {
        return mFixedIndicatorView.getPreSelectedItem();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (unScrollPosition != -1) {
            final View tabView = mFixedIndicatorView.getChildAt(unScrollPosition);
            if (tabView != null) {
                int scrollPos = tabView.getLeft() - (getMeasuredWidth() - tabView.getMeasuredWidth()) / 2;
                if (scrollPos >= 0) {
                    smoothScrollTo(scrollPos, 0);
                    unScrollPosition = -1;
                }
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isPainnedTabView) {
            int scrollX = getScrollX();
            if (mPainnedTabView != null && scrollX > 0) {
                int saveCount = canvas.save();

                canvas.translate(scrollX + getPaddingLeft(), getPaddingTop());
                mPainnedTabView.draw(canvas);

                int x = mPainnedTabView.getWidth();

                canvas.translate(x, 0);

                int shawdowHeight = getHeight() - getPaddingTop() - getPaddingBottom();
                if (customShadowDrawable != null) {
                    customShadowDrawable.setBounds(0, 0, shadoWidth, shawdowHeight);
                    customShadowDrawable.draw(canvas);

                } else {
                    canvas.clipRect(0, 0, shadoWidth + dipToPix(1), shawdowHeight);
                    canvas.drawRect(0, 0, dipToPix(1), shawdowHeight, defaultShadowPaint);

                }

                canvas.restoreToCount(saveCount);
            }


        }

    }

    private static class SFixedIndicatorView extends FixedIndicatorView {
        private boolean isAutoSplit;

        public SFixedIndicatorView(Context context) {
            super(context);
        }


        public boolean isAutoSplit() {
            return isAutoSplit;
        }


        public void setAutoSplit(boolean isAutoSplit) {
            if (this.isAutoSplit != isAutoSplit) {
                this.isAutoSplit = isAutoSplit;
                if (!isAutoSplit) {
                    setSplitMethod(SPLITMETHOD_WRAP);
                }
                requestLayout();
                invalidate();
            }
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (isAutoSplit) {
                ScrollIndicatorView group = (ScrollIndicatorView) getParent();
                int layoutwidth = group.getMeasuredWidth();
                if (layoutwidth != 0) {
                    int totalWidth = 0;
                    int count = getChildCount();
                    int maxCellWidth = 0;
                    for (int i = 0; i < count; i++) {
                        int width = measureChildrenWidth(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
                        maxCellWidth = maxCellWidth < width ? width : maxCellWidth;
                        totalWidth += width;
                    }

                    if (totalWidth > layoutwidth) {
                        group.setFillViewport(false);//XXX
                        setSplitMethod(SPLITMETHOD_WRAP);

                    } else if (maxCellWidth * count > layoutwidth) {
                        group.setFillViewport(true);
                        setSplitMethod(SPLITMETHOD_WEIGHT);

                    } else {
                        group.setFillViewport(true);
                        setSplitMethod(SPLITMETHOD_EQUALS);

                    }

                }

            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        private int measureChildrenWidth(View view, int widthMeasureSpec, int heightMeasureSpec) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            int childWdithSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), ViewGroup.LayoutParams.WRAP_CONTENT);
            int childHegithSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
            view.measure(childWdithSpec, childHegithSpec);
            return view.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
        }
    }


}
