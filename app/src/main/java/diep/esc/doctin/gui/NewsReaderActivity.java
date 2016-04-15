package diep.esc.doctin.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import diep.esc.doctin.R;
import diep.esc.doctin.gui.adapter.MyRecyclerViewAdapter;
import diep.esc.doctin.util.News;

/**
 * This is main activity.
 * This activity show a list of newses via a {@link NewsListFragment} and news content by
 * {@link WebView}
 * <br/>
 * Created by Diep on 31/03/2016.
 *
 * @author Diep
 */
public class NewsReaderActivity extends AppCompatActivity
        implements MyRecyclerViewAdapter.OnItemActionsListener {
//    private static final String TAG = "log_NewsReaderActivity";

    /**
     * To allow home button showed on actionbar
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * To show news content
     */
    private WebView mWebView;

    /**
     * To handle WebView event
     */
    private WebViewClient mWebViewClient;

    /**
     * Show if {@link #mWebView} is loading
     */
    private ProgressBar mProgressBar;

    /**
     * Fragment shows the list of newses
     */
    private NewsListFragment mNewsListFragment;

    /**
     * To contains {@link #mNewsListFragment} and {@link #mWebView}
     */
    private DrawerLayout mDrawerLayout = null;

    /**
     * The URL of the news which is being read.
     */
    private String selectedUrl = null;

    /**
     * Flag mark if {@link #mWebView} loading completed or not.
     */
    private boolean mWebViewPageLoaded = false;

    /**
     * Flag mark if this activity in reading states or not.
     */
    private boolean mReadingState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_news_reader_welcome);
        } else {
            mReadingState = savedInstanceState.getBoolean("state");
            selectedUrl = savedInstanceState.getString("url");
            if (mReadingState) {
                setContentView(R.layout.activity_news_reader);
            } else setContentView(R.layout.activity_news_reader_welcome);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mNewsListFragment =
                (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!mReadingState) {
            handleDrawerEvent();
        } else {
            attachWebView();
            mNewsListFragment.setRefreshItemEnable(true);
            mWebView.restoreState(savedInstanceState);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        mNewsListFragment.setOnItemActionListener(this);

        setupHomeButton();

        mNewsListFragment.setHasOptionsMenu(true);
        mNewsListFragment.setOnItemActionListener(this);
        if (savedInstanceState == null) mNewsListFragment.setRefreshFlag(true);

    }


    /**
     * Disable Drawer event by making it always closed if {@link #mReadingState} is false
     */
    private void handleDrawerEvent() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (!mReadingState) mDrawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (!mReadingState) mDrawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState != DrawerLayout.STATE_IDLE && !mReadingState)
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    /**
     * Show home button for opening/closing DrawerLayout
     */
    private void setupHomeButton() {
        mDrawerToggle = new ActionBarDrawerToggle(
                NewsReaderActivity.this,
                mDrawerLayout,
                R.string.app_name,
                R.string.app_name
        );
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.syncState();
    }


    /**
     * Create and attach a WebView to this activity.
     */
    private void attachWebView() {
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        ViewGroup webLayout = (ViewGroup) findViewById(R.id.webLayout);
        webLayout.addView(mWebView);
        webLayout.removeView(mProgressBar);
        webLayout.addView(mProgressBar);
        mWebViewClient = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mWebViewPageLoaded = true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                OptionPane.showMessageDialog(NewsReaderActivity.this, "loading Error", error.toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mWebViewPageLoaded) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                mWebView.loadUrl(url);
                return false;
            }
        };
        mWebView.setWebViewClient(mWebViewClient);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(false);
    }

    /**
     * Load/reload WebView content with given url
     *
     * @param url News Url
     */
    private void loadUrl(String url) {
        mWebViewPageLoaded = false;
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.loadUrl(url);
        selectedUrl = url;
    }


    /**
     * switch this activity to reading state. Attach WebView to the main layout of the DrawerLayout,
     * move list of
     */
    private void switchState() {
        mReadingState = true;
        attachWebView();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(mNewsListFragment);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.news_list_layout, mNewsListFragment);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
        mNewsListFragment.setOnItemActionListener(this);
        mNewsListFragment.setRefreshItemEnable(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    if (mReadingState) mDrawerLayout.openDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                return true;
            case R.id.action_refresh:
                if (mReadingState) {
                    loadUrl(selectedUrl);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("url", selectedUrl);
        outState.putBoolean("state", mReadingState);
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroyDrawingCache();
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(View v, News news, int itemType) {
        if (itemType == MyRecyclerViewAdapter.ITEM_TYPE_NEWS) {
            news.setHasRead(true);
            if (!mReadingState) switchState();
            loadUrl(news.getUrl());
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mNewsListFragment.refresh();
        }
    }


    /**
     * Make sure user press back button 2 times in 5s to exit.
     */
    boolean pressOneMore2Exit = false;

    @Override
    public void onBackPressed() {
        if (pressOneMore2Exit) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Now, press back button one more time to exit",
                    Toast.LENGTH_SHORT).show();
            pressOneMore2Exit = true;
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    pressOneMore2Exit = false;
                }
            }.execute();
        }
    }

}
