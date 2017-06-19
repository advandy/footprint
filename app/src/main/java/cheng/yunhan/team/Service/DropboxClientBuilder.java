package cheng.yunhan.team.Service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by Yunhan on 19.06.2017.
 */

public class DropboxClientBuilder {
    private static DbxClientV2 sDbxClient;

    public static DbxClientV2 build(String accessToken) {
        if (DropboxClientBuilder.sDbxClient == null) {
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("footprint")
                    .build();

            DropboxClientBuilder.sDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
        return DropboxClientBuilder.sDbxClient;
    }
}
