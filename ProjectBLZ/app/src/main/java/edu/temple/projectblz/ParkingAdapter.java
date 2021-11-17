package edu.temple.projectblz;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.MyViewHolder> {

     private Context context;
    private ArrayList<LocationObject> listItem;

    public ParkingAdapter (Context context, ArrayList<LocationObject> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.row_items, null, true);
        return new MyViewHolder(row);
    }
     
       
        
@Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        holder.audioText.setText(listItem.get(position));
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

   public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setColorFilter(Color.RED);
            imageView.setMaxHeight(7);
            
           /**set the specs for the textview and attach to row alongside image*/
            textView = itemView.findViewById(R.id.textView);
            textView.setPadding(5,8,8,5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);

            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  
                    new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete Parking Location")
                        .setMessage("Do you want to delete this location?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                itemsList.remove(index);

                                listItem.remove(position);
                                //TODO: DELETE FUNC CALL

                                notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
        }
        });
    }

    //TODO: DELETE FUNCTION


}
                  
 