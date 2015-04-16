package com.doodeec.lockscreenDemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.doodeec.lockscreen.LockScreen;
import com.doodeec.lockscreen.LockScreenController;

public class MainActivity extends ActionBarActivity implements LockScreen.IPINDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LockScreen.setActiveColor(getResources().getColor(R.color.testa));
//        LockScreen.setActiveColor(getResources().getColor(R.color.testb));

        findViewById(R.id.my_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockScreenController.setPIN("1234");
                LockScreenController.askForPIN(MainActivity.this, getSupportFragmentManager(), null, false, true);
            }
        });

        findViewById(R.id.my_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockScreenController.setPIN("1234");
                LockScreenController.askForPIN(MainActivity.this, getSupportFragmentManager(), null, true, true);
            }
        });

        findViewById(R.id.my_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockScreenController.setPIN(null);
                LockScreenController.setupPIN(MainActivity.this, getSupportFragmentManager(), true);
            }
        });
    }

    @Override
    public void onPINSetup(String pin) {
        Toast.makeText(this, "Setup " + pin, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPINEntered() {
        Toast.makeText(this, "Unlock", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWrongEntry() {
        Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
    }
}
