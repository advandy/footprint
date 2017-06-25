package cheng.yunhan.team;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dropbox.core.android.Auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import cheng.yunhan.team.Service.DetailImageActivity;
import cheng.yunhan.team.Service.DropboxUploadTask;
import cheng.yunhan.team.Service.LocationService;
import cheng.yunhan.team.Service.NetworkStatusService;
import cheng.yunhan.team.Service.TakePictureService;
import cheng.yunhan.team.model.Photo;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoUri;
    private boolean continueUpload = false;
    private FloatingActionButton uploadBtn;
    private String accessToken = null;
    private ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_main);

        FloatingActionButton cameraBtn = (FloatingActionButton) findViewById(R.id.takePhoto);
        uploadBtn = (FloatingActionButton) findViewById(R.id.upload);
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

        GridView gridView = (GridView)findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getApplicationContext(), getImages());
        gridView.setAdapter(imageAdapter);


    //    new DropboxFileMetadataRequest("20170622_150527_-1041569194.jpg","VfPwhhKoE5EAAAAAAAB-EDn_eBRII64S_Iat8OjOhCU27mOir6N2tnoOuPL8n4t_").execute();

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Footprint");

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("dropboxIntegration", Context.MODE_PRIVATE);
                accessToken = prefs.getString("access-token", null);

                if (accessToken == null) {
                    Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.dropbox_app_key));
                    continueUpload = true;
                } else {
                    uploadToDropBox(accessToken, photoUri);
                }
            }
        });
    }

    private ArrayList<String> getImages() {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Footprint");
        ArrayList<String> list = new ArrayList<>();
        if (storageDir != null) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file: files) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }

    public class ImageAdapter extends BaseAdapter {
        public ArrayList<String> paths;
        private Context context;
        private LayoutInflater inflater;

        public ImageAdapter( Context context, ArrayList<String> paths) {
            this.paths = paths;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override

        public int getCount() {
            return this.paths.size();
        }

        @Override
        public Object getItem(int i) {
            return paths.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                convertView= inflater.inflate(R.layout.photo_thumbnail, viewGroup, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            final String path = paths.get(i);
            Glide.with(this.context)
                    .load(path)
                    .apply(options)
                    .thumbnail(0.2f)
                    .into(imageView);
            final int currentItem = i;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DetailImageActivity.class);
                    intent.putExtra("currentItem", currentItem);
                    intent.putExtra("paths", paths);
                    startActivity(intent);
                }
            });


            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(photoUri);
            imageAdapter.paths.add(photoUri.getEncodedPath());
            imageAdapter.notifyDataSetChanged();
            this.sendBroadcast(mediaScanIntent);
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void uploadToDropBox(String accessToken, Uri photoUri) {
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
