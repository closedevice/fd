package sbbic.com.sharepaneldemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import sbbic.com.sharepaneldemo.indicator.Indicator;

public class MainActivity extends Activity {

    private RelativeLayout mView;
    private View mSharePanel;
    private View mSharePanelExtend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(mView);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Log.d("MainActivity", "Density is " + displayMetrics.density + " densityDpi is " + displayMetrics.densityDpi + " height: " + displayMetrics.heightPixels +
                " width: " + displayMetrics.widthPixels);


        final Button btnDefault = (Button) findViewById(R.id.btn_default);

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSharePanel = DefaultPanelFactory.createSharePanel(MainActivity.this, new Indicator.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(View selectItemView, int select, int preSelect) {
                        Toast.makeText(MainActivity.this, "select:" + select, Toast.LENGTH_SHORT).show();
                        if (select == 3) {//
                            ViewGroup rootView = (ViewGroup) MainActivity.this.getWindow().getDecorView().findViewById(android.R.id.content);

                            if (rootView instanceof FrameLayout) {
                                Log.d("MainActivity", "is FrameLaout");
                            }

                            ViewGroup view = (ViewGroup) rootView.getChildAt(0);
                            if (view instanceof RelativeLayout) {
                                Log.d("MainActivity", "is RelativeLayout");
                            }

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


                            if (mSharePanel != null) {
                                mSharePanel.setVisibility(View.INVISIBLE);
                            }
                            mSharePanelExtend = openSharePanelExtend();
                            view.addView(mSharePanelExtend, params);


                        }


                    }
                });
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mView.addView(mSharePanel, params);
                btnDefault.setEnabled(false);

            }
        });


    }


    private View openSharePanelExtend() {

        return DefaultPanelFactory.createSharePanelExtend(MainActivity.this, new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                Toast.makeText(MainActivity.this, "select:" + select, Toast.LENGTH_SHORT).show();
            }
        }, new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                Toast.makeText(MainActivity.this, "复制链接", Toast.LENGTH_SHORT).show();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSharePanelExtend != null) {
                    ViewGroup content = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
                    ViewGroup child = (ViewGroup) content.getChildAt(0);
                    child.removeView(mSharePanelExtend);
                    mSharePanelExtend = null;
                }
                if (mSharePanel != null) {
                    mSharePanel.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}
