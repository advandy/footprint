package cheng.yunhan.team.Service;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

/**
 * Created by Yunhan on 19.06.2017.
 */

public class DropboxUserAccountTask extends AsyncTask<Void, Void, FullAccount> {

    public DbxClientV2 clientV2;
    public AccountListener listener;
    public DbxException error;

    public interface AccountListener {
        public void onAccountReceived(FullAccount account);
        public void onError(Exception error);
    }
    public DropboxUserAccountTask(String accessToken, AccountListener listener) {
        this.clientV2 = DropboxClientBuilder.build(accessToken);
        this.listener = listener;
    }

    @Override
    protected FullAccount doInBackground(Void... voids) {
        try {
            return clientV2.users().getCurrentAccount();
        } catch (DbxException e) {
            e.printStackTrace();
            error = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(FullAccount account) {
        super.onPostExecute(account);
        if (account != null && error == null) {
            listener.onAccountReceived(account);
        } else {
            listener.onError(error);
        }
    }
}
