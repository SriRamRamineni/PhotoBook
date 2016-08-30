package com.example.sriram.photoBook;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sriram on 7/29/2016.
 */
public class pictureViewFragment extends Fragment {

    ImageView imageView;
    int id = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picureview_fragment,container,false);
        Bundle args  = getArguments();
        id = args.getInt("id");
        imageView = (ImageView) view.findViewById(R.id.pictureview);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            new ImageLoader().execute(Constants.urlArray.get(id));
        }else{
            Glide.with(getActivity()).load(Constants.urlArray.get(id)).into(imageView);
        }
        return view;
    }

    public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Image Loading...");
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.i("url", params[0]);
            final String urlStr = params[0];
            Bitmap img = null;
            int sdk = Build.VERSION.SDK_INT;
            Log.i("version", String.valueOf(sdk));
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlStr);
            HttpResponse response;
            try {
                response = (HttpResponse) client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                img = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null) {
                imageView.setImageBitmap(bitmap);
            }
            progressDialog.dismiss();
        }
    }
}
