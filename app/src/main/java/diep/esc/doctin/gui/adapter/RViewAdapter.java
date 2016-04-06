package diep.esc.doctin.gui.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import diep.esc.doctin.R;
import diep.esc.doctin.gui.NewsReaderActivity;
import diep.esc.doctin.util.News;
import diep.esc.doctin.util.RssNewsUtils;

/**
 * Created by Diep on 28/03/2016.
 */
public class RViewAdapter extends RecyclerView.Adapter<RViewHolder> {
    private List<News> listOfNews=new ArrayList<>(0);
    private static final String TAG="log_Adapter";
    private View.OnLongClickListener longClickListener;
    private View.OnClickListener clickListener;
    private RssNewsUtils mNewsUtils;

    public RViewAdapter(RssNewsUtils utils) {
        mNewsUtils=utils;
        longClickListener= new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RViewHolder holder= (RViewHolder) v.getTag();
                News news=listOfNews.get(holder.getItemIndex());
                news.setHasRead(!news.hasRead());
                new DelayerToNotifyDataChanged().execute();
                return true;
            }
        };
        clickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RViewHolder holder= (RViewHolder) v.getTag();
                News news=listOfNews.get(holder.getItemIndex());
                Intent intent=new Intent(v.getContext(),NewsReaderActivity.class);
                intent.putExtra("url",news.getUrl());
                news.setHasRead(true);
                v.getContext().startActivity(intent);
                notifyDataSetChanged();
                //Toast.makeText(v.getContext(),"click",Toast.LENGTH_SHORT).show();
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
        if(news.getImage()!=null){
            rViewHolder.getImgView().setImageBitmap(news.getImage());
            Log.d(TAG, "onBindViewHolder img not nu;ll;");
        }
        else{//Check local image and read it if exist.
            String path=news.getImagePath();
            if(path!=null&&path.length()>0&& new File(path).exists()){
                Log.d(TAG, "onBindViewHolder read local img");
                Bitmap readImg=BitmapFactory.decodeFile(path);
                if(readImg==null||readImg.getHeight()==0){
                    Log.d(TAG, "onBindViewHolder local img is empty");
                }
                news.setImage(readImg);
                rViewHolder.getImgView().setImageBitmap(readImg);
            }
            else{
                mNewsUtils.startGetImage(news,i,i+".jpg",80,80);
                rViewHolder.getImgView().setImageBitmap(null);
//                if(path==null){
//                    Log.d(TAG, "onBindViewHolder path null");
//                }
//                else if(path.length()==0) Log.d(TAG, "onBindViewHolder path len0");
//                else if (!new File(path).exists()) Log.d(TAG, "onBindViewHolder File do not exist");
//                else Log.d(TAG, "onBindViewHolder wtf");

            }
        }
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

    class DelayerToNotifyDataChanged extends AsyncTask<Void,Void,Void>{

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
}
