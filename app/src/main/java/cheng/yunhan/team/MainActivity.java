package cheng.yunhan.team;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cheng.yunhan.team.Service.DropboxClientBuilder;
import cheng.yunhan.team.Service.DropboxUploadTask;
import cheng.yunhan.team.Service.DropboxUserAccountTask;

public class MainActivity extends AppCompatActivity {

    private static final String DROPBOX_ACCESS_TOCKEN = "VfPwhhKoE5EAAAAAAAB94t4F_rQjfbRHAVFmZb8_09GOUZ3dYDf4a9KFCRDCywQw";
    private static final String APP_KEY = "35qtgpn2kymq71t";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String accessToken;
        SharedPreferences prefs = getSharedPreferences("dropboxIntegration", Context.MODE_PRIVATE);
        accessToken = prefs.getString("access-token", null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token(); //generate Access Token
        }
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            prefs.edit().putString("access-token", accessToken).apply();
            DbxClientV2 client = DropboxClientBuilder.build(accessToken);
            final String finalAccessToken = accessToken;
            new DropboxUserAccountTask(client, new DropboxUserAccountTask.AccountListner() {
                @Override
                public void onAccountReceived(FullAccount account) {
                    File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File gpxfile = new File(root, "test_file.txt");
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(gpxfile);
                        writer.append("hello world");
                        writer.flush();
                        writer.close();
                        new DropboxUploadTask(finalAccessToken, gpxfile, getApplicationContext(), new DropboxUploadTask.DropboxUploadListener() {
                            @Override
                            public void onUploaded(File file) {
                                Log.e("","");
                            }

                            @Override
                            public void onError(Exception error) {
                                Log.e("","");
                            }
                        }).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Exception error) {

                }
            }).execute();
        } else {
            Auth.startOAuth2Authentication(getApplicationContext(), APP_KEY);
        }
    }


    private class DownloadFilesTask extends AsyncTask<Object, Object, JSONObject> {
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            String sUrl = "http://10.0.2.2:8081/listUsers";
            URL url = null;
            try {
                url = new URL(sUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream input = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                try {
                    jsonObject = new JSONObject(s.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TextView tx = (TextView)findViewById(R.id.txt);
            tx.setText(jsonObject.toString());
        }
    }
}
