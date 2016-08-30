package com.example.sriram.photoBook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
import java.util.concurrent.ExecutionException;

public class UserSelectionPage extends AppCompatActivity {


    boolean isFinished;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection_page);
        if(savedInstanceState != null){
            isFinished = savedInstanceState.getBoolean("tutorial");
        }
        Constants.userArray.clear();
        Constants.userInfo.clear();
        cardView = (CardView) findViewById(R.id.tutorial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setLogo(R.mipmap.ic_launcher);
        }
        Button button = (Button) findViewById(R.id.cancel);
        if (button != null && !isFinished ) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFinished =true;
                    cardView.setVisibility(View.GONE);
                }
            });
        }
        try {
            new ApiConection().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ListView listView = (ListView) findViewById(R.id.user_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1, new ArrayList<>(Constants.userArray.keySet()));
        assert listView != null;
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, parent.getItemAtPosition(position).toString(), Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSelectionPage.this, AlbumsList.class);
                intent.putExtra("UserId", Constants.userArray.get(parent.getItemAtPosition(position)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("tutorial",isFinished);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFinished = savedInstanceState.getBoolean("tutorial");
        if(isFinished && cardView.getVisibility()!=View.GONE){
            cardView.setVisibility(View.GONE);
        }
    }

    public class ApiConection extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UserSelectionPage.this, "", "Fetching User Data...", true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Constants.userArray.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("Https").authority("jsonplaceholder.typicode.com").appendPath("users");
            URL url = null;
            String responseString = null;
            try {
                url = new URL(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserSelectionPage.this, "API Connection Failed \n" +
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
                Log.i("Response",responseString);
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(UserSelectionPage.this, "User Data is currently unavailable \n" +
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
                    Constants.userArray.put( jsonObject.getString("name"),jsonObject.getInt("id"));
                    String username = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    String  phone = jsonObject.getString("phone");
                    String website = jsonObject.getString("website");
                    JSONObject addjsonObject = jsonObject.getJSONObject("address");
                    String street = addjsonObject.getString("street");
                    String suit = addjsonObject.getString("suite");
                    String city = addjsonObject.getString("city");
                    String zipcode = addjsonObject.getString("zipcode");
                    JSONObject geojsonObject = addjsonObject.getJSONObject("geo");
                    LatLng location = new LatLng(geojsonObject.getDouble("lat"),geojsonObject.getDouble("lng"));
                    JSONObject companyjsonObject = jsonObject.getJSONObject("company");
                    String companyname = companyjsonObject.getString("name");
                    String catchphrase = companyjsonObject.getString("catchPhrase");
                    String bs = companyjsonObject.getString("bs");
                    Constants.userInfo.put(jsonObject.getInt("id"), new UserInformation(username,email,phone,companyname,catchphrase,
                                        website,bs,street,suit,city,zipcode,location));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_selection_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent());
            return true;
        }
        else if(id == R.id.refresh){
            Intent intent =  new Intent(UserSelectionPage.this,UserSelectionPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
