package com.example.sriram.photoBook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

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
import java.util.concurrent.ExecutionException;

public class AlbumsList extends AppCompatActivity {

    public int userId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);
        Constants.albumArray.clear();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        Intent intent = getIntent();
        userId = intent.getIntExtra("UserId",0);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        ViewPager pager= (ViewPager) findViewById(R.id.viewpager);
        if (pager != null) {
            pager.setOffscreenPageLimit(2);
            pager.setAdapter(adapter);
            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            if (tabs != null) {
                tabs.setViewPager(pager);
            }
        }
        try {
            new ApiConection().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_albumslist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this,Settings.class));
                return true;
            case R.id.refresh:
                Intent intent = new Intent(this,AlbumsList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UserId",userId);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    public class ApiConection extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AlbumsList.this, "", "Fetching User Data...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("Https").authority("jsonplaceholder.typicode.com").appendPath("albums");
            URL url = null;
            String responseString = null;
            try {
                url = new URL(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(AlbumsList.this, "API Connection Failed \n" +
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
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(AlbumsList.this, "Album Data for the user is currently unavailable \n" +
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
                    if(jsonObject.getInt("userId")== userId)
                        Constants.albumArray.put( jsonObject.getString("title"),jsonObject.getInt("id"));
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

    public class Adapter extends FragmentPagerAdapter {


        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fragment = null;
            if(position == 0){
                fragment = new AlbumList();
            }
            else {
                Bundle args = new Bundle();
                args.putInt("userId",userId);
                fragment = new UserInfo();
                fragment.setArguments(args);
            }

            return fragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "Albums";
            else
                return "User Information";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}