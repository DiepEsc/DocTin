package diep.esc.doctin.gui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import diep.esc.doctin.R;
import diep.esc.doctin.util.News;
import diep.esc.doctin.util.RssNewsUtils;

/**
 * The adapter for RecyclerView. That show the list of newses,
 * and a refresh item (if {@link #refreshItemEnable} is {@code true})
 * <br/>Created by Diep on 28/03/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder> {
    public static final int ITEM_TYPE_NEWS = 0;
    public static final int ITEM_TYPE_REFRESH_ITEM = 1;
//    private static final String TAG = "log_Adapter";

    /**
     * The list of news, that will be showed on the RecyclerView
     */
    private List<News> newses = new ArrayList<>(0);

    /**
     * Event handler when OnLongClickListener event occur on item layout
     */
    private View.OnLongClickListener mLongClickListener;

    /**
     * Event handler when OnClickListener event occur on item layout
     */
    private View.OnClickListener mClickListener;

    /**
     * Utilities for downloading RSS content
     */
    private RssNewsUtils mNewsUtils;

    /**
     * A flag mark if refresh item will be showed or not
     */
    private boolean refreshItemEnable = false;

    /**
     * @param utils Utilities for downloading RSS content
     */
    public MyRecyclerViewAdapter(RssNewsUtils utils) {
        mNewsUtils = utils;

        mLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MyRecyclerViewHolder holder = (MyRecyclerViewHolder) v.getTag();
                News news = getNewsAtItem(holder.itemPosition);
                news.setHasRead(!news.hasRead());
                new DelayerToNotifyDataChanged().execute();
                return true;
            }
        };
    }

    /**
     * Set callback object for OnItemClickListener event
     *
     * @param listener The callback object
     */
    public void setOnItemActionListener(final OnItemActionsListener listener) {
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyRecyclerViewHolder holder = (MyRecyclerViewHolder) v.getTag();
                listener.onItemClick(
                        v,
                        getNewsAtItem(holder.itemPosition),
                        getItemViewType(holder.itemPosition)
                );
                notifyDataSetChanged();
            }
        };
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link MyRecyclerViewHolder} of the given type to
     * represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MyRecyclerViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(MyRecyclerViewHolder, int)
     */
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE_NEWS) {
            View view = inflater.inflate(R.layout.item_news_list, null);
            view.setOnLongClickListener(mLongClickListener);
            return new MyRecyclerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_news_refresh, null);
            view.setOnClickListener(mClickListener);
            return new MyRecyclerViewHolder(view);
        }
    }

    /**
     * Get the news at item in item at given position
     *
     * @param position The position of View item
     * @return The news which is contained in View item at given position. If the item type is
     * {@link #ITEM_TYPE_REFRESH_ITEM}, the return value will be null
     */
    public News getNewsAtItem(int position) {
        if (refreshItemEnable) {
            if (position > 0) return newses.get(position - 1);
            return null;
        }
        return newses.get(position);
    }


    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link MyRecyclerViewHolder#itemView} to reflect the item at the
     * given position.
     *
     * @param myRecyclerViewHolder the MyRecyclerViewHolder to be update
     * @param position             the position of the item.
     */
    @Override
    public void onBindViewHolder(MyRecyclerViewHolder myRecyclerViewHolder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_NEWS) {
            News news = getNewsAtItem(position);
            myRecyclerViewHolder.viewInfo.setText(news.getTime());
            myRecyclerViewHolder.viewSummary.setText(news.getSummary());
            myRecyclerViewHolder.viewTitle.setText(news.getTitle());
            myRecyclerViewHolder.itemView.setOnClickListener(mClickListener);
            if (news.getImage() != null) {
                myRecyclerViewHolder.imgView.setImageBitmap(news.getImage());
            } else {//Check local image and read it if exist.
                String path = news.getImagePath();
                if (path != null && path.length() > 0 && new File(path).exists()) {
                    Bitmap readImg = BitmapFactory.decodeFile(path);
                    news.setImage(readImg);
                    myRecyclerViewHolder.imgView.setImageBitmap(readImg);
                } else {
                    mNewsUtils.startGetImage(news, position, position + ".jpg", 80, 80);
                    myRecyclerViewHolder.imgView.setImageBitmap(null);

                }
            }
            myRecyclerViewHolder.itemPosition = position;
            myRecyclerViewHolder.itemView.setTag(myRecyclerViewHolder);
            if (news.hasRead()) {
                myRecyclerViewHolder.itemView.setBackgroundResource(R.drawable.item_old_bg);
            } else {
                myRecyclerViewHolder.itemView.setBackgroundResource(R.drawable.itembg);
            }
        } else {
            myRecyclerViewHolder.itemView.setOnClickListener(mClickListener);
            myRecyclerViewHolder.itemView.setTag(myRecyclerViewHolder);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter include refresh item
     * if it is enabled (the flag {@link #refreshItemEnable is true}).
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (refreshItemEnable) {
            if (newses != null) return newses.size() + 1;
            return 1;
        }
        if (newses != null) return newses.size();
        return 0;
    }

    /**
     * Get view type at {@code position}. If refresh item is enable, the item at 0 must be
     * {@link #ITEM_TYPE_REFRESH_ITEM}.
     *
     * @param position The position of the item
     * @return Type of the item. It can be {@link #ITEM_TYPE_REFRESH_ITEM} or
     * {@link #ITEM_TYPE_NEWS}
     */
    @Override
    public int getItemViewType(int position) {
        if (refreshItemEnable && position == 0) return ITEM_TYPE_REFRESH_ITEM;
        return ITEM_TYPE_NEWS;
    }

    /**
     * Set the list of newses which is to be showed on RecyclerView
     *
     * @param newses The list of newses
     */
    public void setNewses(List<News> newses) {
        this.newses = newses;
        notifyDataSetChanged();
    }

    /**
     * Get the list of newses which is hold by this Adapter
     *
     * @return The list of newses
     */
    public List<News> getNewses() {
        return newses;
    }

    /**
     * Check if refresh is enable
     *
     * @return the status of the refresh: enable or not
     */
    public boolean isRefreshItemEnable() {
        return refreshItemEnable;
    }

    /**
     * Change status of the refresh item
     *
     * @param refreshItemEnable The value which is the status will be changed to
     */
    public void setRefreshItemEnable(boolean refreshItemEnable) {
        this.refreshItemEnable = refreshItemEnable;
        notifyDataSetChanged();
    }

    /**
     * An AsyncTask to do {@link #notifyDataSetChanged()} after 100ms
     */
    private class DelayerToNotifyDataChanged extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }

    /**
     * An interface callback when click event occur on Recycler item
     */
    public interface OnItemActionsListener {

        /**
         * This method will be called when click event occur on Recycler item
         *
         * @param v        the View that click event occur on
         * @param news     the respective news with item that click event occur. If the item doesn't
         *                 contain a news (or {@code itemType} is {@link #ITEM_TYPE_REFRESH_ITEM}) ,
         *                 this parameter will be null
         * @param itemType The
         */
        void onItemClick(View v, @Nullable News news, int itemType);
    }
}
