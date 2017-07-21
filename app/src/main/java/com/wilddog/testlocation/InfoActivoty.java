package com.wilddog.testlocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InfoActivoty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_activoty);
         findViewById (R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });
    }
}
