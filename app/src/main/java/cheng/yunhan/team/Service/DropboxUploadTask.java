package cheng.yunhan.team.Service;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yunhan on 19.06.2017.
 */

public class DropboxUploadTask extends AsyncTask {

    private DropboxUploadListener listener;
    private DbxClientV2 clientV2;
    private File file;
    private Exception error;

    public interface DropboxUploadListener {
        public void onUploaded(File file);
        public void onError(Exception error);
    }

    public DropboxUploadTask(String accessToken, File file, Context context, DropboxUploadListener listener) {
        this.clientV2 = DropboxClientBuilder.build(accessToken);
        this.file = file;
        this.context = context;
        this.listener = listener;
    }

    private Context context;

    @Override
    protected Void doInBackground(Object[] objects) {

        try {
            InputStream inputStream = new FileInputStream(file);

             clientV2.files().uploadBuilder("/" + file.getName())
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            error = e;
        } catch (DbxException e) {
            e.printStackTrace();
            error = e;
        } catch (IOException e) {
            e.printStackTrace();
            error = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (error != null) {
            this.listener.onError(error);
        } else {
            this.listener.onUploaded(file);
        }
    }
}
