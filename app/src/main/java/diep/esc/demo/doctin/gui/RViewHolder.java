package diep.esc.demo.doctin.gui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import diep.esc.demo.doctin.R;
import diep.esc.demo.util.News;

/**
 * Created by Diep on 28/03/2016.
 */
public class RViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgView;
    private TextView viewTitle,viewInfo, viewSummary;
    private ViewGroup layout;

    public RViewHolder(View itemView) {
        super(itemView);
        imgView= (ImageView) itemView.findViewById(R.id.imageView);
        viewTitle= (TextView) itemView.findViewById(R.id.news_title);
        viewInfo= (TextView) itemView.findViewById(R.id.news_time_site_category);
        viewSummary= (TextView) itemView.findViewById(R.id.news_summary);
        layout=(ViewGroup)itemView.findViewById(R.id.item_layout);
    }


    public ImageView getImgView() {
        return imgView;
    }

    public TextView getViewTitle() {
        return viewTitle;
    }

    public TextView getViewInfo() {
        return viewInfo;
    }

    public TextView getViewSummary() {
        return viewSummary;
    }

    public ViewGroup getLayout() {
        return layout;
    }
}
