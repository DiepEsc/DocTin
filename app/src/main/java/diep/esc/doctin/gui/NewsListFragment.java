package diep.esc.doctin.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import diep.esc.doctin.gui.adapter.RViewAdapter;
import diep.esc.doctin.util.DbUtils;
import diep.esc.doctin.util.News;
import diep.esc.doctin.util.NewsReceiveListener;
import diep.esc.doctin.util.OptionsUtils;
import diep.esc.doctin.util.RssNewsUtils;

public class NewsListFragment extends Fragment implements NewsReceiveListener {
    private static final String TAG = "log_List";
    private RecyclerView recyclerView;
    private RViewAdapter adapter;
    private DbUtils dbUtils;
    private RssNewsUtils rssUtils;
    private boolean refreshFlag = false;
    private ProgressBar mProgressBar;

    private String rssLink = "http://nld.com.vn/tin-moi-nhat.rss";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_news_list, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String savedRssLink = OptionsUtils.getSelectedRss(getActivity());
        if (savedRssLink != null) {
            rssLink = savedRssLink;
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.rView);
        dbUtils = new DbUtils(getActivity(), this);
        rssUtils = new RssNewsUtils(getActivity(), this);
        adapter = new RViewAdapter(rssUtils);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                RecyclerView.LayoutParams params = super.generateDefaultLayoutParams();
                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                return params;
            }
        });
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
    }

    public boolean isRefreshFlag() {
        return refreshFlag;
    }

    public void setRefreshFlag(boolean refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    @Override
    public void onResume() {
        String savedRssLink = OptionsUtils.getSelectedRss(getActivity());
        if (savedRssLink != null && !savedRssLink.equals(rssLink)) {
            rssLink = savedRssLink;
            refresh();
        }
        Log.d(TAG, "onResume link=" + savedRssLink);
        adapter.notifyDataSetChanged();
        dbUtils.startLoadNews();
        super.onResume();
    }

    @Override
    public void onPause() {
        dbUtils.storeNewsList(adapter.getListOfNews());
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_options:
                Intent intent = new Intent(getActivity(), OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_about:
                OptionPane.showMessageDialog(getActivity(), "About me", "This app was written by Điệp Esc");
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        rssUtils.startGetNews(rssLink);
    }

    public void setOnItemActionListener(RViewAdapter.OnItemActionsListener listener) {
        adapter.setOnItemActionListener(listener);
    }

    @Override
    public void receivedNews(ArrayList<News> news) {
        if (refreshFlag) {
            refreshFlag = false;
            refresh();
        } else {
            for (int i = news.size() - 1; i >= 0; i--) {
                News newNews = news.get(i);
                for (int j = adapter.getListOfNews().size() - 1; j >= 0; j--) {
                    News oldNew = adapter.getListOfNews().get(j);
                    if (newNews.equals(oldNew)) {
                        String oldImgPath = oldNew.getImagePath();
                        File oldImgFile = new File(oldImgPath);
                        if (oldImgFile.exists()) {
                            String newPath;
                            if (i != j) {
                                newPath = rssUtils.generateImgPath(i + ".jpg");
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
            mProgressBar.setVisibility(View.INVISIBLE);

        }
        adapter.setListOfNews(news);
    }

    @Override
    public void receivedException(Exception exception) {
        OptionPane.showMessageDialog(getActivity(), "An exception occur", exception.getMessage()
                + ".\n" + (exception.getCause() != null
                ? "Cause: " + exception.getCause().getMessage() : ""));
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void receivedError(VolleyError error) {
        OptionPane.showMessageDialog(getActivity(), "An error occur", "Can't download resource. Reason: " + error.getMessage());
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void imageAttached(int itemIndex) {
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        for (int i = 0; i < adapter.getListOfNews().size(); i++) {
            Bitmap bitmap = adapter.getListOfNews().get(i).getImage();
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }
        super.onDestroy();
    }
}
