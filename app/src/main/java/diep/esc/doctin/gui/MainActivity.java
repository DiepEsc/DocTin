package diep.esc.doctin.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;

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
        dbUtils=new DbUtils(this);
        adapter.setListOfNews(dbUtils.loadNews());
        if(savedInstanceState==null) {
            new RssNewsUtils(this, this).startGetNews("http://vietbao.vn/rss2/trang-nhat.rss");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receivedNews(ArrayList<News> news) {

        //new StoreNewsTask().execute(news.get(0));
//        adapter.setListOfNews(news);
        for(int i=0;i<news.size();i++){
            News newNews=news.get(i);
            for(int j=0;j<adapter.getListOfNews().size();j++){
                News oldNew=adapter.getListOfNews().get(j);
                if(newNews.equals(oldNew)) {
                    newNews.setHasRead(oldNew.hasRead());
                    break;
                }
            }
        }
        adapter.setListOfNews(news);
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void receivedError(VolleyError error) {
        Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void receivedImage() {

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
    //    class StoreNewsTask extends AsyncTask<List<News>,Void,Void>{
//
//        @Override
//        protected Void doInBackground(List<News>... params) {
//            dbUtils.storeNewsList(params[0]);
//            return null;
//        }
//    }
}
