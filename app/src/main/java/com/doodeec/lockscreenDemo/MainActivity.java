package com.doodeec.lockscreenDemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.doodeec.lockscreen.LockScreenController;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.my_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockScreenController.setPIN("1234");
                LockScreenController.askForPIN(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Unlocked", Toast.LENGTH_SHORT).show();
                    }
                }, null, null, false);
            }
        });
    }
}
