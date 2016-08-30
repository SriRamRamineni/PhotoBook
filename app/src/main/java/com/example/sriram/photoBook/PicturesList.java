package com.example.sriram.photoBook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PicturesList extends AppCompatActivity {

    public int AlbumId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);
        Constants.pictureArray.clear();
        Constants.urlArray.clear();
        Constants.thumbnailArray.clear();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        Intent intent = getIntent();
        AlbumId = intent.getIntExtra("AlbumId",0);
        try {
            new ApiConection().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ListView listView = (ListView) findViewById(R.id.picture_list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("switchpref",true)) {
            CustomAdapter customAdapter = new CustomAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>(Constants.pictureArray.keySet()));
            if (listView != null) {
                listView.setAdapter(customAdapter);
            }
        }else{
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>(Constants.pictureArray.keySet()));
            if(listView!=null){
                listView.setAdapter(arrayAdapter);
            }
        }
        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(PicturesList.this,PictureView.class);
                    intent.putExtra("id",Constants.pictureArray.get(parent.getItemAtPosition(position)));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pictureslist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this,Settings.class));
                return true;
            case R.id.refresh:
                Intent intent = new Intent(this,PicturesList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("AlbumId",AlbumId);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class ApiConection extends AsyncTask< String[], Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(String[]... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PicturesList.this, "", "Fetching User Data...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("Https").authority("jsonplaceholder.typicode.com").appendPath("photos");
            URL url = null;
            String responseString = null;
            try {
                url = new URL(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(PicturesList.this, "API Connection Failed \n" +
                                              " Please try again", Toast.LENGTH_SHORT).show();
                                  }
                              }
                );
                return null;
            }
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(httpConnection.getInputStream());
                responseString = readStream(in);
                Log.i("Response", responseString);
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(PicturesList.this, "Album Data for the user is currently unavailable \n" +
                                              " Please try again", Toast.LENGTH_SHORT).show();
                                  }
                              }
                );
                return null;
            }
            String responseStringFinal = responseString.substring(responseString.indexOf("["), responseString.lastIndexOf("]") + 1);
            try {
                JSONArray jsonArray = new JSONArray(responseStringFinal);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.getInt("albumId")== AlbumId) {
                        Constants.pictureArray.put(jsonObject.getString("title"), jsonObject.getInt("id"));
                        Constants.urlArray.put(jsonObject.getInt("id"),jsonObject.getString("url"));
                        Constants.thumbnailArray.put(jsonObject.getInt("id"),jsonObject.getString("thumbnailUrl"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String readStream(InputStream in) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            in.close();
            return sb.toString();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    public class CustomAdapter extends ArrayAdapter<String>{


        public CustomAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String text = getItem(position);
            if(convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.imageelement,parent,false);
            TextView textView = (TextView) convertView.findViewById(R.id.picturetitle);
            textView.setText(text);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            Glide.with(PicturesList.this).load(Constants.thumbnailArray.get(Constants.pictureArray.get(text))).into(imageView);
            return convertView;
        }
    }



}
