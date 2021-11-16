package edu.temple.projectblz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class ParkingItemsActivity extends AppCompatActivity {

    private final String MESSAGE_KEY = "message";
    ArrayList<String> title;
    ListView listView;
    BaseAdapter adapter;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ImageView imageView;
    Map<String, String> allEntries;
    int pos;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_items);
        setTitle("BookmarksActivity");
        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.imageView);
        prefs = getSharedPreferences(MESSAGE_KEY, MODE_PRIVATE);
        editor = prefs.edit();
        title = new ArrayList<>();

        addTitle();//add title(s) to list
        adapter = new ParkingAdapter(this, title);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iIntent = getIntent();
               // iIntent.putExtra(BrowserActivity.WORD, allEntries.get(parent.getItemAtPosition(position).toString()));
                setResult(RESULT_OK, iIntent);
                pos = position;
                ParkingItemsActivity.this.finish();
            }

        });


        //long click to delete item
       /* listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(BookmarksActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.remove(parent.getItemAtPosition(position).toString());
                                title.remove(parent.getItemAtPosition(position));
                                editor.apply();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });*/

        findViewById(R.id.closeItemBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParkingItemsActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    //get title
    public void addTitle(){
        count = prefs.getAll().size();
        if(count>0) {
            allEntries = (Map<String, String>) prefs.getAll();
            for (Map.Entry<String, String> entry : allEntries.entrySet()) {
                title.add(entry.getKey());
            }
        }
    }

}
