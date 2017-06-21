package cheng.yunhan.team.Service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by D060753 on 21.06.2017.
 */

public class NetworkStatusService {
    public static boolean isConnectedWithWifi(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.getType() == connMgr.TYPE_WIFI) {
            return true;
        }

        return false;
    }
}
