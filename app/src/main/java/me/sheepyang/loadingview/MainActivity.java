package me.sheepyang.loadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBarProgress;
    private SeekBar seekBarWaveSize;
    private LoadingView loadingView;
    private CheckBox cbIsFansMove;
    private CheckBox cbWaveMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarWaveSize = (SeekBar) findViewById(R.id.seekbar_wave_size);
        seekBarProgress = (SeekBar) findViewById(R.id.seekbar_progress);
        loadingView = (LoadingView) findViewById(R.id.loadingView);
        cbIsFansMove = (CheckBox) findViewById(R.id.cb_is_fans_move);
        cbWaveMode = (CheckBox) findViewById(R.id.cb_wave_mode);

        seekBarWaveSize.setMax((int) (loadingView.getMaxWaveSize() - loadingView.getMinWaveSize()));
        seekBarWaveSize.setProgress((int) loadingView.getWaveSize());
        seekBarProgress.setMax(loadingView.getMax());
        seekBarProgress.setProgress(loadingView.getProgress());
        cbWaveMode.setChecked(loadingView.getWaveMode() == LoadingView.WAVE_MODE_DEFAULT ? true : false);
        cbWaveMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbWaveMode.setText("默认波浪");
                    loadingView.setWaveMode(LoadingView.WAVE_MODE_DEFAULT);
                } else {
                    cbWaveMode.setText("左右浮动");
                    loadingView.setWaveMode(LoadingView.WAVE_MODE_FLOATING);
                }
            }
        });
        cbIsFansMove.setChecked(loadingView.isFansMove());
        cbIsFansMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                loadingView.setFansMove(isCheck);
            }
        });
        seekBarWaveSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
