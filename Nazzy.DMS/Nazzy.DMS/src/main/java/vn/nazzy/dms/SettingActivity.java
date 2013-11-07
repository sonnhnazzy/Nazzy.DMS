package vn.nazzy.dms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        AppPreferences _appPrefs = new AppPreferences(getApplicationContext());

        String someString = _appPrefs.getSmsBody();

        EditText txt = (EditText) findViewById(R.id.editText);
        Button save = (Button) findViewById(R.id.btnSAVE);
        Button exit = (Button) findViewById(R.id.btnEXIT);

        txt.setText(someString);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                // setContentView(R.layout.activity_main);
            }

        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txt = (EditText) findViewById(R.id.editText);
                AppPreferences _appPrefs = new AppPreferences(
                        getApplicationContext());
                String someString = txt.getText().toString();
                _appPrefs.saveSmsBody(someString);
                Toast.makeText(getApplicationContext(),
                        "Đã ghi cấu hình hệ thống...", Toast.LENGTH_LONG)
                        .show();
                finish();
                // setContentView(R.layout.activity_main);
            }
        });

    }

}

