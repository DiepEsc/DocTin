package diep.esc.doctin.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import diep.esc.doctin.R;
import diep.esc.doctin.gui.adapter.RViewAdapter;
import diep.esc.doctin.util.News;

/**
 * Created by Diep on 07/04/2016.
 */
public class WelcomeActivity extends AppCompatActivity implements RViewAdapter.OnItemActionsListener {
    private NewsListFragment fragment;
    private static final String TAG="log_Welcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_welcome);
        fragment = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setHasOptionsMenu(true);
        fragment.setOnItemActionListener(this);
        fragment.setRefreshFlag(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 255, 148, 0)));
    }

    @Override
    public void onItemClick(View v, News news) {
        Intent intent = new Intent(this, NewsReaderActivity.class);
        intent.putExtra("url", news.getUrl());
        news.setHasRead(true);
        startActivity(intent);
        Log.d(TAG, "onItemClick shit");
    }
}
