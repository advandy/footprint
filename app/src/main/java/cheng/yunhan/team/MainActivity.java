package cheng.yunhan.team;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import cheng.yunhan.team.Service.DropboxUploadTask;
import cheng.yunhan.team.Service.DropboxUserAccountTask;
import cheng.yunhan.team.Service.LocationService;
import cheng.yunhan.team.Service.NetworkStatusService;
import cheng.yunhan.team.Service.TakePictureService;
import cheng.yunhan.team.model.Photo;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoUri;
    private ImageView imageView;
    private TextView textView;
    private boolean continueUpload = false;
    private String accessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_main);

        Button cameraBtn = (Button)findViewById(R.id.camera);
        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.description);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = TakePictureService.createImageFile(getApplicationContext());
                        if (photoFile != null) {
                            photoUri = Uri.fromFile(photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Footprint");
        /*if(storageDir.exists()) {
            File[] files = storageDir.listFiles();
            for (File file : files) {
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(file.getAbsolutePath()),
                        400, 400);
                Log.e("","");
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            continueUpload = true;
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(photoUri.getEncodedPath()),
                    400, 400);


            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(photoUri);
            this.sendBroadcast(mediaScanIntent);
            imageView.setImageBitmap(thumbImage);
            final Photo photo = new Photo(null,
                    photoUri,
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
            LocationService.getCurrentLocation(getApplicationContext(), new LocationService.LocationGotListner() {
                @Override
                public void onLocationGot(Location location) {
                    photo.setLocation(location);

                    try {
                        String locationName = LocationService.getLocationName(getApplicationContext(), location);
                        textView.setText(locationName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            SharedPreferences prefs = getSharedPreferences("dropboxIntegration", Context.MODE_PRIVATE);
            accessToken = prefs.getString("access-token", null);

            if (accessToken == null) {
                Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.dropbox_app_key));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accessToken != null) {
            new DropboxUserAccountTask(accessToken, new DropboxUserAccountTask.AccountListener() {
                @Override
                public void onAccountReceived(FullAccount account) {
                    String name = account.getName().getDisplayName();
                    textView.setText("You are signed in with " + name);
                }

                @Override
                public void onError(Exception error) {

                }
            }).execute();
        }
        if (continueUpload) {
            continueUpload = false;
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token(); //generate Access Token
            }
            if (accessToken != null) {
                //Store accessToken in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("dropboxIntegration", Context.MODE_PRIVATE);
                prefs.edit().putString("access-token", accessToken).apply();

                if (NetworkStatusService.isConnectedWithWifi(getApplicationContext())){
                    new DropboxUploadTask(accessToken, new File(photoUri.getEncodedPath()), getApplicationContext(), new DropboxUploadTask.DropboxUploadListener() {
                        @Override
                        public void onUploaded(File file) {
                            Toast.makeText(getApplicationContext(), file.getName() + " is uploaded to Dropbox",Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onError(Exception error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Not connected with wifi", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        }
    }

}
