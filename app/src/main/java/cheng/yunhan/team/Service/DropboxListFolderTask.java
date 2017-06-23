package cheng.yunhan.team.Service;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

import static android.net.wifi.WifiConfiguration.Status.strings;

/**
 * Created by Yunhan on 22.06.2017.
 */

public class DropboxListFolderTask extends AsyncTask<Void, Void, ListFolderResult>{
    private String accessToken;
    private String filePath;
    private FolderResultListener folderResultListener;

    public DropboxListFolderTask(String accessToken, String filePath, FolderResultListener folderResultListener) {
        this.accessToken = accessToken;
        this.filePath = filePath;
        this.folderResultListener = folderResultListener;
    }

    public interface FolderResultListener {
        public void onSuccess(ListFolderResult listFolderResult);
        public void onError();
    }

    @Override
    protected ListFolderResult doInBackground(Void... voids) {
        DbxClientV2 clientV2 = DropboxClientBuilder.build(accessToken);
        try {
            return clientV2.files().listFolder(filePath);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ListFolderResult listFolderResult) {
        super.onPostExecute(listFolderResult);
        if (listFolderResult != null) {
            folderResultListener.onSuccess(listFolderResult);
        } else {
            folderResultListener.onError();
        }
    }
}
