package diep.esc.demo.util;


import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Diep on 29/03/2016.
 */
public class NewsUtils {
    private RequestQueue requestQueue;

    private NewsReceiveListener listener=null;

    public NewsUtils(Context context, NewsReceiveListener listener) {
//        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.listener=listener;
        requestQueue.start();
    }

    public void setListener(NewsReceiveListener listener) {
        this.listener = listener;
    }

    public void startGetNews(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processRss(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("D_log", "onErrorResponse: "+error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }
    private void processRss(String rss){
        XmlPullParser parser=Xml.newPullParser();
        try {
            parser.setInput(new StringReader(rss));
            ArrayList<News> listOfNews=new ArrayList<>();
            News news=null;
            int eventType=parser.getEventType();
            boolean itemParsing=false;
            while (eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if("item".equals(parser.getName())){
                            itemParsing=true;
                            news=new News();
                        }
                        else if(itemParsing){
                            String text=parser.nextText();
                            if("title".equals(parser.getName())){
                                news.setTitle(text);
//                                log("title: "+text);
                            }
                            else if("description".equals(parser.getName())){
                                news.extractAndSetDescription(text);
//                                log("sum: " + news.getSummary());
//                                log("img: "+news.getImageUrl());
                            }else if("link".equals(parser.getName())){
                                news.setUrl(text);
//                                log("link: "+text);
                            }else if("pubDate".equals((parser.getName()))){
                                news.setDetails(text);
//                                log("date: "+text);
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if("item".equals(parser.getName())){
                            listOfNews.add(news);
                        }
                        break;
                }
                eventType=parser.next();
            }
            listener.receivedNews(listOfNews);
        } catch (XmlPullParserException e) {
            throw new RssLoadingException("Failed to parse RSS",e);
        } catch (IOException e) {
            throw new RssLoadingException("Failed to parse RSS",e);
        }
    }

//    private void log(String s){
//        android.util.Log.d("D_log",">>"+s);
//    }
}
