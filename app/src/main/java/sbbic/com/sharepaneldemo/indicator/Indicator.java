package sbbic.com.sharepaneldemo.indicator;

import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by God on 2016/5/18.
 */
public interface Indicator {
    public IndicatorAdapter getAdapter();

    public void setAdapter(IndicatorAdapter adapter);

    public void setItemSelectListener(OnItemSelectedListener listener);

    public OnItemSelectedListener getOnItemSelectItemListener();

    public void setOnTransitionListener(OnTransitionListener listener);

    public OnTransitionListener onTransitionListener();

    public void setCurrentItem(int position, boolean anim);

    public View getItemView(int position);

    public int getCurrentItem();

    public void setCurrentItem(int position);

    public int getPreSelectedItem();


    interface DataSetObserver {
        public void onChanged();
    }


    interface OnItemSelectedListener {
        public void onItemSelected(View selectItemView, int select, int preSelect);
    }

    interface OnTransitionListener {
        public void onTransition(View view, int position, float selectPercent);
    }

    abstract class IndicatorAdapter {
        private Set<DataSetObserver> observers = new LinkedHashSet<>();

        public abstract int getCount();

        public abstract View getView(int position, View convertView, ViewGroup parent);

        public void notifyDataSetChanged() {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            observers.add(observer);
        }

        public void unRegisterDataSetObserver(DataSetObserver observer) {
            observers.remove(observer);
        }
    }

}
