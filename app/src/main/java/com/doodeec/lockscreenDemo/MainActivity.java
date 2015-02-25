package com.doodeec.lockscreenDemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.doodeec.lockscreen.LockScreen;
import com.doodeec.lockscreen.LockScreenController;

public class MainActivity extends ActionBarActivity {

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
                LockScreenController.askForPIN(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Unlocked", Toast.LENGTH_SHORT).show();
                    }
                }, null, null, true);
            }
        });

        findViewById(R.id.my_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockScreenController.setPIN(null);
                LockScreenController.setupPIN(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Set", Toast.LENGTH_SHORT).show();
                    }
                }, true);
            }
        });
    }
}
