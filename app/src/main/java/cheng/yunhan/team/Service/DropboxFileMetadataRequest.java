package cheng.yunhan.team.Service;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yunhan on 22.06.2017.
 */

public class DropboxFileMetadataRequest extends AsyncTask<Void, Void, Void> {
    private String filePath;
    private String accessToken;
    private DropboxFileMetadataRequestCallback callback;
    private static String url = "https://api.dropboxapi.com/2/files/alpha/get_metadata";

    public DropboxFileMetadataRequest(String filePath, String accessToken, DropboxFileMetadataRequestCallback callback) {
        this.filePath = filePath;
        this.accessToken = accessToken;
        this.callback = callback;
    }

    public interface DropboxFileMetadataRequestCallback {
        public void onSuccess(JSONObject jsonObject);
        public void onError();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL urlEncoded = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)urlEncoded.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.connect();
            JSONObject params = new JSONObject();

            params.put("path", filePath);
            params.put("include_media_info", false);
            params.put("include_deleted", false);
            params.put("include_has_explicit_shared_members", false);

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream ());
            printout.writeBytes(params.toString());
            printout.flush ();
            printout.close ();

            int HttpResult = connection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),"utf-8"));
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                JSONObject jsonObj = new JSONObject(sb.toString());
                this.callback.onSuccess(jsonObj);
                return null;


            } else {
                System.out.println(connection.getResponseMessage());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.callback.onError();
        return null;
    }
}
