package diep.esc.doctin.gui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import diep.esc.doctin.R;
import diep.esc.doctin.util.DbUtils;
import diep.esc.doctin.util.News;
import diep.esc.doctin.util.NewsReceiveListener;
import diep.esc.doctin.util.RssNewsUtils;

public class MainActivity extends AppCompatActivity implements NewsReceiveListener {
    private static final String TAG="log_MainActivity";
    private RecyclerView recyclerView;
    private RViewAdapter adapter;
    private DbUtils dbUtils;
    private RssNewsUtils rssUtils;
    private boolean refreshFlag=false;
    private ProgressBar mProgressBar;

    private String rssLink="http://news.zing.vn/rss/trang-chu.rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView= (RecyclerView) findViewById(R.id.rView);
        adapter=new RViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                RecyclerView.LayoutParams params = super.generateDefaultLayoutParams();
                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                return params;
            }
        });
        mProgressBar= (ProgressBar) findViewById(R.id.progressBar2);
        dbUtils=new DbUtils(this,this);
        rssUtils=new RssNewsUtils(this, this);
        if(savedInstanceState==null) {
            refreshFlag=true;
        }
        dbUtils.startLoadNews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings: return true;
            case R.id.action_refresh:
                mProgressBar.setVisibility(View.VISIBLE);
                dbUtils.startLoadNews();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receivedNews(ArrayList<News> news) {
        if(refreshFlag){
            refreshFlag=false;
            rssUtils.startGetNews(rssLink);
        }
        else {
            for (int i = 0; i < news.size(); i++) {
                News newNews = news.get(i);
                for (int j = 0; j < adapter.getListOfNews().size(); j++) {
                    News oldNew = adapter.getListOfNews().get(j);
                    if (newNews.equals(oldNew)) {
                        Log.d(TAG, "receivedNews eq");
                        newNews.setImagePath(oldNew.getImagePath());
                        newNews.setHasRead(oldNew.hasRead());
                        break;
                    }
                }
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        adapter.setListOfNews(news);
//        int count=0;
//        for(int i=0;i<adapter.getListOfNews().size();i++){
//            String imgPath=adapter.getListOfNews().get(i).getImagePath();
//            if (imgPath == null || imgPath.length() == 0) {
//                count++;
//                rssUtils.startGetImage(adapter.getListOfNews().get(i),i,i+".jpg",80,80);
//            }
//        }
//        Log.d(TAG, "receivedNews download: " + count + " images");
    }

    @Override
    public void receivedError(VolleyError error) {
        Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void imageAttached(int itemIndex) {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        new StoreNewsTask().execute(adapter.getListOfNews());
        
    }

    @Override
    protected void onStop() {
        dbUtils.storeNewsList(adapter.getListOfNews());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        for(int i=0;i<adapter.getListOfNews().size();i++){
            Bitmap bitmap=adapter.getListOfNews().get(i).getImage();
            if(bitmap!=null&&!bitmap.isRecycled()) bitmap.recycle();
        }
        super.onDestroy();
    }
}
