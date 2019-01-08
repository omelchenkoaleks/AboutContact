package com.omelchenkoaleks.aboutcontact;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        Button ok = (Button) findViewById(R.id.about_done_button);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_done_button:
                finish();
                break;
        }
    }
}