package com.liusiming.testpasswordview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.liusiming.spacedpasswordview.CustomPasswordView;
import com.liusiming.spacedpasswordview.SpacedPasswordView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SpacedPasswordView v = (SpacedPasswordView) findViewById(R.id.id_custom_password);
        // whether user can see the content of the password
        // v.setPasswordVisibility(true);
        v.setPasswordListener(new SpacedPasswordView.CustomListener() {
            @Override
            public void onTextChanged(String psw) {
                Log.d("lsm1993", psw);
            }

            @Override
            public void onInputFinish(String psw) {
                Log.d("lsm1993", psw);

                Log.d("lsm1993", v.getPassword());
            }
        });
    }
}
