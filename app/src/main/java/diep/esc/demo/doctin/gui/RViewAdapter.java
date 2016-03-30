package diep.esc.demo.doctin.gui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import diep.esc.demo.doctin.R;
import diep.esc.demo.util.News;

/**
 * Created by Diep on 28/03/2016.
 */
public class RViewAdapter extends RecyclerView.Adapter<RViewHolder> {
    private List<News> listOfNews;
    private static final String TAG="D_log";
    
    @Override
    public RViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_list, null);
        return new RViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RViewHolder rViewHolder, int i) {
        News news=listOfNews.get(i);
        rViewHolder.getViewInfo().setText(news.getInfo());
        rViewHolder.getViewSummary().setText(news.getSummary());
        rViewHolder.getViewTitle().setText(news.getTitle());

        Bitmap bitmap=news.getImage();
        rViewHolder.getImgView().setImageBitmap(bitmap);
        if(news.hasRead()){
            rViewHolder.getLayout().setBackgroundResource(R.drawable.item_old_bg);
        }
        else{
            rViewHolder.getLayout().setBackgroundResource(R.drawable.itembg);
        }
    }

    @Override
    public int getItemCount() {
        if(listOfNews!=null) return listOfNews.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setListOfNews(List<News> listOfNews) {
        Log.d(TAG, "setListOfNews ");
        this.listOfNews = listOfNews;
        notifyDataSetChanged();
    }
}
