package diep.esc.doctin.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.io.File;
import java.util.ArrayList;

import diep.esc.doctin.R;
import diep.esc.doctin.gui.adapter.MyRecyclerViewAdapter;
import diep.esc.doctin.util.DatabaseUtils;
import diep.esc.doctin.util.News;
import diep.esc.doctin.util.NewsReceiveListener;
import diep.esc.doctin.util.OptionsUtils;
import diep.esc.doctin.util.RssNewsUtils;

/**
 * The fragment shows a list of newses
 *
 * @author Diep
 */
public class NewsListFragment extends Fragment implements NewsReceiveListener {
//    private static final String TAG = "log_List";

    /**
     * The RecyclerView which shows a list of news
     */
    private RecyclerView mRecyclerView;

    /**
     * A adapter which adapts data with {@link #mRecyclerView}
     */
    private MyRecyclerViewAdapter mAdapter;

    /**
     * A utilities object that works with database
     */
    private DatabaseUtils mDatabaseUtils;

    /**
     * A utilities object that allow downloading RSS
     */
    private RssNewsUtils mRssUtils;

    /**
     * Flag mark if the list must be refreshed against
     */
    private boolean refreshFlag = false;

    /**
     * ProgressBar that will be showed if list of content is being loaded
     */
    private ProgressBar mProgressBar;

    /**
     * The current RSS link.
     */
    private String mRssLink = "http://nld.com.vn/tin-moi-nhat.rss";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_news_list, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String savedRssLink = OptionsUtils.getSelectedRss(getActivity());
        if (savedRssLink != null) {
            mRssLink = savedRssLink;
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rView);
        mDatabaseUtils = new DatabaseUtils(getActivity(), this);
        mRssUtils = new RssNewsUtils(getActivity(), this);
        mAdapter = new MyRecyclerViewAdapter(mRssUtils);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                RecyclerView.LayoutParams params = super.generateDefaultLayoutParams();
                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                return params;
            }
        });
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
    }

    /**
     * Mark if the content should be refresh against or not.
     *
     * @param refreshFlag the value of the flag
     */
    public void setRefreshFlag(boolean refreshFlag) {
        this.refreshFlag = refreshFlag;
    }


    /**
     * Load the news store in database.<br/>
     * If the current RSS is changed, mark refreshFlag, make sure the list of newses refresh against
     */
    @Override
    public void onResume() {
        mProgressBar.setVisibility(View.VISIBLE);
        mDatabaseUtils.startLoadNewses();
        String savedRssLink = OptionsUtils.getSelectedRss(getActivity());
        if (savedRssLink != null && !savedRssLink.equals(mRssLink)) {
            mRssLink = savedRssLink;
            refreshFlag = true;
        }
        super.onResume();
    }

    /**
     * Save newses to database
     */
    @Override
    public void onPause() {
        mDatabaseUtils.storeNewsList(mAdapter.getNewses());
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_options:
                Intent intent = new Intent(getActivity(), OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_about:
                OptionPane.showMessageDialog(getActivity(), "About me",
                        "This app was written by Điệp Esc");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refresh the list of newses. Load it against from {@link #mRssLink}
     */
    public void refresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRssUtils.startGetNews(mRssLink);
    }

    /**
     * Delegate from {@link
     * MyRecyclerViewAdapter#setOnItemActionListener(MyRecyclerViewAdapter.OnItemActionsListener)}
     *
     * @param listener The listener to register for RecyclerView item click event.
     */
    public void setOnItemActionListener(MyRecyclerViewAdapter.OnItemActionsListener listener) {
        mAdapter.setOnItemActionListener(listener);
    }

    /**
     * Delegate from {@link MyRecyclerViewAdapter#isRefreshItemEnable()}
     *
     * @return enable state of the refresh item on the RecyclerView.
     * @see MyRecyclerViewAdapter#ITEM_TYPE_REFRESH_ITEM
     */
    public boolean isRefreshItemEnable() {
        return mAdapter.isRefreshItemEnable();
    }

    /**
     * Delegate from {@link MyRecyclerViewAdapter#setRefreshItemEnable(boolean)}
     *
     * @see MyRecyclerViewAdapter#ITEM_TYPE_REFRESH_ITEM
     */
    public void setRefreshItemEnable(boolean refreshItemEnable) {
        mAdapter.setRefreshItemEnable(refreshItemEnable);
    }

    /**
     * This method will be called when a list of news was loaded completely
     *
     * @param newses The list of newses has just loaded
     */
    @Override
    public void onReceivedNews(ArrayList<News> newses) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (refreshFlag) {
            refreshFlag = false;
            refresh();
        } else {
            for (int i = newses.size() - 1; i >= 0; i--) {
                News newNews = newses.get(i);
                for (int j = mAdapter.getNewses().size() - 1; j >= 0; j--) {
                    News oldNew = mAdapter.getNewses().get(j);
                    if (newNews.equals(oldNew)) {
                        String oldImgPath = oldNew.getImagePath();
                        File oldImgFile = new File(oldImgPath);
                        if (oldImgFile.exists()) {
                            String newPath;
                            if (i != j) {
                                newPath = mRssUtils.generateImgPath(i + ".jpg");
                                File newFile = new File(newPath);
                                if (newFile.exists()) newFile.delete();
                                oldImgFile.renameTo(newFile);
                            } else {
                                newPath = oldImgPath;
                            }
                            newNews.setImagePath(newPath);
                        }
                        newNews.setHasRead(oldNew.hasRead());
                        break;
                    }
                }
            }

        }
        mAdapter.setNewses(newses);
    }

    /**
     * This method will be called when an exception occur while loading newses from database
     *
     * @param exception An exception to be handled
     */
    @Override
    public void onReceivedException(Exception exception) {
        OptionPane.showMessageDialog(getActivity(), "An exception occur", exception.getMessage()
                + ".\n" + (exception.getCause() != null
                ? "Cause: " + exception.getCause().getMessage() : ""));
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will be called when an VolleyError occur while downloading RSS or images
     *
     * @param error An error to be handled
     */
    @Override
    public void onReceivedError(VolleyError error) {
        OptionPane.showMessageDialog(getActivity(), "An error occur",
                "Can't download resource. Reason: " + error.getMessage());
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will be called when an image has been downloaded completed and attach to news
     *
     * @param itemIndex the index of the news in the list
     */
    @Override
    public void onImageAttached(int itemIndex) {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        for (int i = 0; i < mAdapter.getNewses().size(); i++) {
            Bitmap bitmap = mAdapter.getNewses().get(i).getImage();
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }
        super.onDestroy();
    }
}
