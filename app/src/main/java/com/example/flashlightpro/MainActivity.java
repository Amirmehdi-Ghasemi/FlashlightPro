package com.example.flashlightpro;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private Switch btnSos, switchLanguage;
    private CameraManager cameraManager;
    private SeekBar repeater;
    private Boolean flashlight = false;
    private TextView txtTitle, txtSeekbar;
    private final Handler handler = new Handler();
    private Boolean isFarsi;
    Language language = new Language();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.btnToggle);
        btnSos = findViewById(R.id.btnSos);
        switchLanguage = findViewById(R.id.swtichLanguage);
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        //cameraManager = getSystemService(CameraManager.class);
        repeater = findViewById(R.id.seekRepeater);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        language.setContext(this);
        checkLanguage();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            hasFlash();
        } else {
            if (isFarsi) Toast.makeText(this, "متاسفانه دستگاه شما فلش ندارد", Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "Your device has no flash :(", Toast.LENGTH_LONG).show();
            toggleButton.setEnabled(false);
            repeater.setEnabled(false);
            btnSos.setEnabled(false);
        }

        switchLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language.change(switchLanguage.isChecked());
                checkLanguage();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hasFlash() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        toggleButton.setText("Flash ON!");
                        if (!btnSos.isChecked()) {
                            repeater();
                            flashlight(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        btnSos.setChecked(false);
                        handler.removeCallbacksAndMessages(null);
                        toggleButton.setText("Flash Off!");
                        flashlight(false);
                        toggleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        repeater.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        repeater.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnSos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (btnSos.isChecked()) {
                    repeater.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    repeater.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    btnSos.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    btnSos.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    toggleButton.setChecked(true);
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            flashlight = false;
                            int delay = 400;// 1000 milliseconds == 1 second
                            for (byte count = 0; count <= 6; count++) {
                                Log.i("inside loop", "round " + count);
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        if (!flashlight) {
                                            flashlight = true;
                                            flashlight(flashlight);
                                        } else {
                                            flashlight = false;
                                            flashlight(flashlight);
                                        }
                                    }
                                }, delay * count);
                            }
                            for (byte count = 3; count < 9; count++) {
                                Log.i("inside loop", "round " + count);
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        if (!flashlight) {
                                            flashlight = true;
                                            flashlight(flashlight);
                                        } else {
                                            flashlight = false;
                                            flashlight(flashlight);
                                        }
                                    }
                                }, (delay * 3) * count);
                            }
                            handler.postDelayed(this, 9600);
                        }
                    }, 0);
                }
                if (!btnSos.isChecked()) {
                    btnSos.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    btnSos.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    handler.removeCallbacksAndMessages(null);
                    if (toggleButton.isChecked()){
                        flashlight(true);
                        repeater();
                    }
                }
            }
        });
        repeater.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                btnSos.setChecked(false);
                handler.removeCallbacksAndMessages(null);
                flashlight = true;
                flashlight(true);
                toggleButton.setChecked(true);
                repeater();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void flashlight(boolean flashlight){
        if (flashlight) {
            try {
                cameraManager.setTorchMode("0", true);
                toggleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                cameraManager.setTorchMode("0", false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void repeater(){
        int repeaterLevel = repeater.getProgress();
        if (repeaterLevel != 0) {
            repeater.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            repeater.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            final int delay = (7 -repeaterLevel) * 90; // 1000 milliseconds == 1 second
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void run() {
                    if (!flashlight) {
                        flashlight = true;
                        flashlight(flashlight);
                    }
                    else {
                        btnSos.setChecked(false);
                        handler.removeCallbacksAndMessages(null);
                        flashlight = false;
                        flashlight(flashlight);
                    }
                    handler.postDelayed(this, delay);
                }
            }, delay);
        }else{
            repeater.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            repeater.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && toggleButton.isEnabled()) {
            toggleButton.setChecked(true);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && toggleButton.isEnabled()) {
            toggleButton.setChecked(false);
        }
        return true;
    }
    public void checkLanguage(){
        isFarsi = language.checkUp();
        txtTitle = findViewById(R.id.txtTitle);
        txtSeekbar = findViewById(R.id.txtSeekBar);
        if (isFarsi) {
            txtTitle.setText("چراغ قوه حرفه ای");
            txtSeekbar.setText("چشمک زن");
            /*Typeface font = Typeface.createFromAsset(getAssets(), "vazir_bold.TTF");
            txtTitle.setTypeface(font);*/
            switchLanguage.setText("Fa ");
            switchLanguage.setChecked(true);
        } else {
            txtTitle.setText("Flashlight Pro");
            txtSeekbar.setText("Strobe");
            switchLanguage.setText("En ");
            switchLanguage.setChecked(false);
        }
    }
}
