package sbbic.com.sharepaneldemo;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import sbbic.com.sharepaneldemo.indicator.Indicator;

/**
 * Created by God on 2016/5/18.
 */
public class MyAdapter extends Indicator.IndicatorAdapter {

    private LayoutInflater infalter;
    private Context context;
    private List<ShareModeList.ShareMode> list;
    private boolean entended = false;


    public MyAdapter(Context context, List<ShareModeList.ShareMode> modeList) {
        this(context, modeList, false);
    }

    public MyAdapter(Context context, List<ShareModeList.ShareMode> modeList, boolean entended) {
        this.context = context;
        this.infalter = LayoutInflater.from(context);
        this.list = modeList;
        this.entended = entended;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (TextView) infalter.inflate(R.layout.tab_main, parent, false);
            convertView = createItemLayout();
        }
        TextView textView = (TextView) convertView;
        if (!entended) {//非扩展状态
            textView.setTextSize(0f);
            int padding = dipToPix(8);
            int paddingTopAndBottom = dipToPix(4);
            textView.setPadding(padding, paddingTopAndBottom, padding, paddingTopAndBottom);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, list.get(position).defaultIcon);
        } else {//扩展状态
            textView.setText(list.get(position).tabName);
            Log.d("MyAdapter", list.get(position).tabName);
            int padding = dipToPix(15);
            int paddingTopAndBottom = dipToPix(10);
            textView.setPadding(padding, paddingTopAndBottom, padding, paddingTopAndBottom);

            textView.setCompoundDrawablesWithIntrinsicBounds(0, list.get(position).tabIcon, 0, 0);
        }

        return convertView;
    }

    private View createItemLayout() {
        TextView tab = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        params.gravity = Gravity.CENTER;
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        return tab;

    }

    private int dipToPix(float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return size;
    }

}
