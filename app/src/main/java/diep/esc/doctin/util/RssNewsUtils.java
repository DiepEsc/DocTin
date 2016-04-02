package diep.esc.doctin.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import diep.esc.doctin.util.ext.volley.StringRequestExt;

/**
 * Created by Diep on 29/03/2016.
 */
public class RssNewsUtils {
    private static final String TAG="RssNewsUtils";
    private RequestQueue requestQueue;
    private Context context;

    private NewsReceiveListener listener = null;

    public RssNewsUtils(Context context, NewsReceiveListener listener) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.listener = listener;
        requestQueue.start();
    }

//    public void setListener(NewsReceiveListener listener) {
//        this.listener = listener;
//    }

    public void startGetNews(final String url) {
        StringRequest stringRequest = new StringRequestExt(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processRss(response, url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.receivedError(error);
                //Log.e("D_log", "onErrorResponse: "+error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }
    private void processRss(String rss, String link) {
        final int HOST_OTHERS = 0;
//        final int HOST_VOV = 1;
        final int HOST_THANH_NIEN = 2;
        int host = HOST_OTHERS;

//        if (link.startsWith("http://vov.vn/")) {
//            host = HOST_VOV;
//        } else
        if (link.startsWith("http://thanhnien.vn/")) {
            host = HOST_THANH_NIEN;
        }

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(rss));
            ArrayList<News> listOfNews = new ArrayList<>();
            News news = null;
            int eventType = parser.getEventType();
            boolean itemParsing = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("item".equals(parser.getName())) {
                            itemParsing = true;
                            switch (host) {
//                                case HOST_VOV:
//                                    news = new VovNews();
//                                    break;
                                case HOST_THANH_NIEN:
                                    news = new ThanhNienNews();
                                    break;
                                default:
                                    news = new News();
                            }
                        } else if (itemParsing) {
                            String text = parser.nextText();
                            if ("title".equals(parser.getName())) {
                                news.setTitle(text);
//                                log("title: "+text);
                            } else if ("description".equals(parser.getName())) {
                                news.extractAndSetDescription(text);
//                                log("sum: " + news.getSummary());
//                                log("img: "+news.getImageUrl());
                            } else if ("link".equals(parser.getName())) {
                                news.setUrl(text);
//                                log("link: "+text);
                            } else if ("pubDate".equals((parser.getName()))) {
                                news.setTime(text);
//                                log("date: "+text);
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(parser.getName())) {
                            listOfNews.add(news);
                        }
                        break;
                }
                eventType = parser.next();
            }
            listener.receivedNews(listOfNews);
        } catch (XmlPullParserException e) {
            throw new RssLoadingException("Failed to parse RSS", e);
        } catch (IOException e) {
            throw new RssLoadingException("Failed to parse RSS", e);
        }
    }
    public void startGetImage(final News news, final int itemIndex, final String fileToSave, int maxWidth, int maxHeight){
        Response.Listener<Bitmap> bitmapListener=new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                File dir=context.getDir("tmp_images",Context.MODE_PRIVATE);
                File file=new File(dir,fileToSave);
                String absPath=file.getAbsolutePath();
                saveImageToFile(response, absPath);
                news.setImagePath(absPath);
                news.setImage(response);
                listener.imageAttached(itemIndex);
            }
        };
        ImageRequest imageRequest=new ImageRequest(news.getImageUrl(), bitmapListener, maxWidth, maxHeight,
                ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("lk",error);
                //listener.receivedError(error);
            }
        });
        requestQueue.add(imageRequest);
    }
    private void saveImageToFile(Bitmap bitmap, String filePath){
        if(bitmap!=null){
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filePath);
//                =context.openFileOutput(filePath,Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            } catch (FileNotFoundException e) {
                Log.w("log_save_Img",e);
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.w("log_save_Img",e);
                }

            }
        }
    }

//    private void log(String s){
//        android.util.Log.d("D_log",">>"+s);
//    }
}

