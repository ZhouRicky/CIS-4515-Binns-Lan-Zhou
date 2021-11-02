package edu.temple.projectblz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;
    String lat = "0.4589";
    String lon = "1.5698";
    String id = "13";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = new SharedPrefs(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePark();
            }
        });

    }


    private void savePark() {

        final String URL = "http://172.20.10.8/insertpark.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("TAG", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.getString("status");
                        if(result.equals("success")){
                            //sharedPrefs.setLoggedInUser(username);
                            Toast.makeText(this, "Location saved", Toast.LENGTH_LONG).show();
                        }
                        Toast toast =  Toast.makeText(this, result, Toast.LENGTH_LONG);
                        toast.show();
                        Log.d("TAG", "resultKey1 " + result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error, Please try again " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error, Please try again" + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("park_lat", lat); //TODO
                params.put("park_lon", lon);//TODO
                params.put("driver_id", id);//TODO
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

}
