package edu.temple.projectblz;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ParkingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LocationObject> listItem;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    public ParkingAdapter (Context context, ArrayList<LocationObject> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position).getCreatedAt();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       /**inflate the image - then add specs - this is the delete icon which will show on every row - for each item*/
        View row = inflater.inflate(R.layout.row_items, null, true);
        ImageView imageView = row.findViewById(R.id.imageView);
        imageView.setColorFilter(Color.RED);
        imageView.setMaxHeight(7);

        /**set the specs for the textview and attach to row alongside image*/
        TextView textView;
        if (convertView instanceof TextView) {
            textView = (TextView) convertView;
        } else {
            textView = row.findViewById(R.id.textView);
            textView.setPadding(5,8,8,5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
        }
        textView.setText(getItem(position).toString());

        /**when the delete icon is click - confirm the users request*/
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
                                listItem.remove(position);
                                //TODO: DELETE FUNC CALL
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

    //TODO: DELETE FUNCTION

}

