package edu.temple.projectblz;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ParkingAdapter extends BaseAdapter {
    private final String MESSAGE_KEY = "message";
    private Context context;
    private ArrayList<String> title1;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    public ParkingAdapter (Context context, ArrayList<String> title1) {
        this.context = context;
        this.title1 = title1;
        title1.add("user1");
        title1.add("user2");
        title1.add("user3");
        title1.add("user4");

    }

    @Override
    public int getCount() {
        return title1.size();
    }

    @Override
    public Object getItem(int position) {
        return title1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_items, null, true);
        TextView textView;
        //TextView textView = row.findViewById(R.id.textView);
        ImageView imageView = row.findViewById(R.id.imageView);
        imageView.setColorFilter(Color.RED);
        imageView.setMaxHeight(7);
        //textView.setGravity(Gravity.CENTER);
        // textView.setTextSize(20);
        // TextView textView;
        if (convertView instanceof TextView) {
            textView = (TextView) convertView;
        } else {
            // textView = new TextView(context);
            textView = row.findViewById(R.id.textView);
            textView.setPadding(5,8,8,5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
        }
        final int pos = position;
        textView.setText(getItem(position).toString());
        prefs = context.getSharedPreferences(MESSAGE_KEY, MODE_PRIVATE);
        editor = prefs.edit();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.remove(getItem(pos).toString());
                                title1.remove(getItem(pos).toString());
                                notifyDataSetChanged();
                                editor.apply();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        return row;
    }

}

