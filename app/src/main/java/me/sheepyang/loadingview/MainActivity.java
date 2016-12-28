package me.sheepyang.loadingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.loadingView)
    LoadingView loadingView;
    @BindView(R.id.seekbar_progress)
    SeekBar seekbarProgress;
    @BindView(R.id.seekbar_wave_size)
    SeekBar seekbarWaveSize;
    @BindView(R.id.cb_wave_mode)
    CheckBox cbWaveMode;
    @BindView(R.id.cb_is_fans_move)
    CheckBox cbIsFansMove;
    @BindView(R.id.cb_fans_mode)
    CheckBox cbFansMode;
    @BindView(R.id.seekbar_width)
    SeekBar seekbarWidth;
    @BindView(R.id.seekbar_height)
    TextView seekbarHeight;
    @BindView(R.id.ll_main)
    LinearLayout llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListener();
        initData();
    }

    private void initData() {
        seekbarWaveSize.setMax((int) (loadingView.getMaxWaveSize() - loadingView.getMinWaveSize()));
        seekbarWaveSize.setProgress((int) loadingView.getWaveSize());
        seekbarProgress.setMax(loadingView.getMax());
        seekbarProgress.setProgress(loadingView.getProgress());
        cbIsFansMove.setChecked(loadingView.isFansMove());
        cbWaveMode.setChecked(loadingView.getWaveMode() == LoadingView.WAVE_MODE_DEFAULT);
        cbFansMode.setChecked(loadingView.getFansMode() == LoadingView.FANS_MODE_AUTO_MOVE);
    }

    private void initListener() {
        cbWaveMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbWaveMode.setText("波浪模式：默认漂浮");
                    loadingView.setWaveMode(LoadingView.WAVE_MODE_DEFAULT);
                } else {
                    cbWaveMode.setText("波浪模式：左右漂浮");
                    loadingView.setWaveMode(LoadingView.WAVE_MODE_FLOATING);
                }
            }
        });
        cbFansMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbFansMode.setText("风扇转动模式：自动");
                    loadingView.setFansMode(LoadingView.FANS_MODE_AUTO_MOVE);
                } else {
                    cbFansMode.setText("风扇转动模式：跟随进度条");
                    loadingView.setFansMode(LoadingView.FANS_MODE_PROGRESS_MOVE);
                }
            }
        });
        cbIsFansMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                loadingView.setFansMove(isCheck);
            }
        });
        seekbarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int width = (int) ((((ViewGroup) seekbarWidth.getParent()).getMeasuredWidth() - llMain.getPaddingLeft() - llMain.getPaddingRight()) * progress / (float) 100);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
                lp.width = width;
                loadingView.setLayoutParams(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbarWaveSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                loadingView.setWaveSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                loadingView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
