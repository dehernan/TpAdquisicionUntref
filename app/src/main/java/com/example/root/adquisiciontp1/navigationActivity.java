package com.example.root.adquisiciontp1;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class navigationActivity extends ListActivity {

    private ArrayList<String> listItems=new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<Pair<Long,Long>> navigationPlan= new ArrayList<>();
    private EditText degrees;
    private EditText seconds;
    static final int NAVIGATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        this.degrees = (EditText)findViewById(R.id.degrees);
        this.seconds = (EditText)findViewById(R.id.seconds);

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
    }

    public void addItems(View v) {


        if (!(this.degrees.getText().toString().matches("") || this.seconds.getText().toString().matches(""))){
            listItems.add("Grados: "+this.degrees.getText().toString()+" - Segundos: "+this.seconds.getText().toString());

            this.degrees.requestFocus();
            Pair<Long, Long> navigationMap= new Pair<>(Long.valueOf(this.degrees.getText().toString()), Long.valueOf(this.seconds.getText().toString()));
            this.navigationPlan.add(navigationMap);
            this.degrees.setText(null);
            this.seconds.setText(null);
        }
        adapter.notifyDataSetChanged();

    }

    public void endPlan(View v){

        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("navigationPlan", (Serializable) this.navigationPlan);
        startActivity(intent);
    }

}
