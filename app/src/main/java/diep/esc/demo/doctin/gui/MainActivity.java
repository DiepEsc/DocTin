package diep.esc.demo.doctin.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import diep.esc.demo.doctin.R;
import diep.esc.demo.util.News;
import diep.esc.demo.util.NewsReceiveListener;
import diep.esc.demo.util.NewsUtils;

public class MainActivity extends AppCompatActivity implements NewsReceiveListener {
    private RecyclerView recyclerView;
    private RViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView= (RecyclerView) findViewById(R.id.rView);
        adapter=new RViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                RecyclerView.LayoutParams params= super.generateDefaultLayoutParams();
                params.width=RecyclerView.LayoutParams.MATCH_PARENT;
                return params;
            }
        });
        new NewsUtils(this,this).startGetNews("http://vietbao.vn/rss2/trang-nhat.rss");
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
        adapter.setListOfNews(news);
    }

    @Override
    public void receivedImage() {

    }
}
