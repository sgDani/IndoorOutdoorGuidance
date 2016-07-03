package com.indooratlas.android.sdk.examples.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.indooratlas.android.sdk.examples.Fragments.MainFragment;
import com.indooratlas.android.sdk.examples.R;
import com.indooratlas.android.sdk.examples.imageview.ImageViewActivity;
import android.support.v7.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    public static boolean SELLO=false;
    public static boolean OUT = false;
    public static boolean IN2IN=false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commit();
        }
    }
}
