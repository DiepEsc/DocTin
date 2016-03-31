package diep.esc.doctin.gui;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import diep.esc.doctin.R;
import diep.esc.doctin.util.News;

/**
 * Created by Diep on 28/03/2016.
 */
public class RViewAdapter extends RecyclerView.Adapter<RViewHolder> {
    private List<News> listOfNews=new ArrayList<>(0);
    private static final String TAG="log_Adapter";
    private View.OnLongClickListener longClickListener;
    private View.OnClickListener clickListener;

    class DelayerToNotifiDataChanged extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(60);
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

    public RViewAdapter() {
        longClickListener= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RViewHolder holder= (RViewHolder) v.getTag();
                News news=listOfNews.get(holder.getItemIndex());
                news.setHasRead(!news.hasRead());
                new DelayerToNotifiDataChanged().execute();
                return true;
            }
        };
        clickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"click",Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_list, null);
        view.setOnLongClickListener(longClickListener);
        view.setOnClickListener(clickListener);
        return new RViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RViewHolder rViewHolder, int i) {
        News news=listOfNews.get(i);
        rViewHolder.getViewInfo().setText(news.getTime());
        rViewHolder.getViewSummary().setText(news.getSummary());
        rViewHolder.getViewTitle().setText(news.getTitle());
       //Bitmap bitmap=news.getImage();
        //rViewHolder.getImgView().setImageBitmap(bitmap);
        rViewHolder.setItemIndex(i);
        rViewHolder.getLayout().setTag(rViewHolder);
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
        this.listOfNews = listOfNews;
        notifyDataSetChanged();
    }

    public List<News> getListOfNews() {
        return listOfNews;
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
}
