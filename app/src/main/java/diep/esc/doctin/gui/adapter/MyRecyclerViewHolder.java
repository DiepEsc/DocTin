package diep.esc.doctin.gui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import diep.esc.doctin.R;

/**
 * Created by Diep on 28/03/2016.
 */
public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
    /**
     * The ImageView insides of {@link #itemView}
     */
    public final ImageView imgView;

    /**
     * TextViews inside of {@link #itemView}.
     * If the type of {@code itemView} is {@link MyRecyclerViewAdapter#ITEM_TYPE_REFRESH_ITEM},
     * they will be null
     */
    public final TextView viewTitle, viewInfo, viewSummary;

    /**
     * The position of this {@link #itemView}. Default value is 0.
     */
    public int itemPosition = 0;

    /**
     * Constructor of MyRecyclerViewHolder
     *
     * @param itemView The itemView to be hold by new instance object
     */
    public MyRecyclerViewHolder(View itemView) {
        super(itemView);
        imgView = (ImageView) itemView.findViewById(R.id.imageView);
        viewTitle = (TextView) itemView.findViewById(R.id.news_title);
        viewInfo = (TextView) itemView.findViewById(R.id.news_time_site_category);
        viewSummary = (TextView) itemView.findViewById(R.id.news_summary);
    }
}
