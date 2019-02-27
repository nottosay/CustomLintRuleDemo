package com.tal.xes.lint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = null;
        this.setContentView(R.layout.item_test);
        Log.d("wally", "onCreate: ");
        System.out.println("111");

        List<String> list = new ArrayList<>();

        int num = 1;

        try {
            Log.e("wally", "onCreate: " + list.get(num));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Integer.parseInt("1");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Float.parseFloat("1");
        Double.parseDouble("1");
    }
}
