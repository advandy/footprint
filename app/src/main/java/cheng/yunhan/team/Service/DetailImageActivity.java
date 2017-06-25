package cheng.yunhan.team.Service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cheng.yunhan.team.R;

public class DetailImageActivity extends AppCompatActivity {
    private ArrayList<String> paths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.view_pager);
        Intent intent = getIntent();
        int currentItem = intent.getIntExtra("currentItem", 0);
        paths = intent.getStringArrayListExtra("paths");
        SlidingImageAdapter slidingImageAdapter = new SlidingImageAdapter(this, paths);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(slidingImageAdapter);
        viewPager.setCurrentItem(currentItem);
    }

    public class SlidingImageAdapter extends PagerAdapter {
        private LayoutInflater inflater;
        private Context context;
        private ArrayList<String> paths;

        public SlidingImageAdapter(Context context, ArrayList<String> paths) {
            this.inflater = LayoutInflater.from(context);
            this.context = context;
            this.paths = paths;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(R.layout.detail_image, container, false);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.detailImage);
            Glide.with(context)
                    .load(paths.get(position))
                    .into(imageView);
            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
