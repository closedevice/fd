package sbbic.com.sharepaneldemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sbbic.com.sharepaneldemo.indicator.FixedIndicatorView;
import sbbic.com.sharepaneldemo.indicator.Indicator;
import sbbic.com.sharepaneldemo.indicator.ScrollIndicatorView;

/**
 * Created by God on 2016/5/20.
 */
public class DefaultPanelFactory {

    public static View createSharePanel(final Activity activity, Indicator.OnItemSelectedListener listener) {
        LinearLayout panel = new LinearLayout(activity);
        final LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setLayoutParams(p);
        MyAdapter adapter = new MyAdapter(activity, new ShareModeList().need("1", "5", "3", "8"));
        Indicator fixedPanel = getFixedPanel(activity, adapter, FixedIndicatorView.SPLITMETHOD_EQUALS, listener);
        if (listener != null)
            fixedPanel.setItemSelectListener(listener);
        panel.addView((View) fixedPanel);
        return panel;
    }

    private static Indicator getFixedPanel(Activity activity, Indicator.IndicatorAdapter adapter, int splitMethod, Indicator.OnItemSelectedListener listener) {
        FixedIndicatorView container = new FixedIndicatorView(activity);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setSplitMethod(splitMethod);
        container.setAdapter(adapter);
        container.setItemSelectListener(listener);

        return container;
    }

    public static View createSharePanelExtend(Activity activity, Indicator.OnItemSelectedListener itemlistener, Indicator.OnItemSelectedListener copyListener, View.OnClickListener listener) {
        LinearLayout panel = new LinearLayout(activity);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setLayoutParams(p);
        TextView textView = new TextView(activity);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dipToPix(activity, 32));
        textView.setText("分享至");
        textView.setTextSize(15);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(p1);
        textView.setBackgroundColor(Color.parseColor("#ABB4BB"));

        panel.addView(textView);
        panel.addView(getScrollPanel(activity, new MyAdapter(activity, new ShareModeList().need("1", "2", "3", "4", "5", "6"), true), itemlistener));


        panel.addView(getSperatorLine(activity));

        View indicator = (View) getFixedPanel(activity, new MyAdapter(activity, new ShareModeList().need("7"), true), FixedIndicatorView.SPLITMETHOD_WRAP, copyListener);
        panel.addView(indicator);
        panel.addView(getSperatorLine(activity));
        RelativeLayout rl = new RelativeLayout(activity);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.setGravity(Gravity.CENTER);
        ImageButton close = new ImageButton(activity);
        close.setBackgroundResource(R.drawable.icon_share_close);
        close.setOnClickListener(listener);
        rl.addView(close);
        panel.addView(rl);

        return panel;
    }

    private static int dipToPix(Context context, float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return size;
    }

    private static View getScrollPanel(Activity activity, Indicator.IndicatorAdapter adapter, Indicator.OnItemSelectedListener listener) {
        ScrollIndicatorView container = new ScrollIndicatorView(activity);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setAdapter(adapter);
        container.setItemSelectListener(listener);
        return container;
    }

    @NonNull
    private static View getSperatorLine(Activity activity) {
        View line = new View(activity);
        LinearLayout.LayoutParams sp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dipToPix(activity, 1));
        line.setBackgroundColor(Color.parseColor("#E3E3E5"));
        line.setLayoutParams(sp1);
        return line;
    }

}
