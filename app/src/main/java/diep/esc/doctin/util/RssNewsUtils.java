package diep.esc.doctin.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
//    private static final String TAG="RssNewsUtils";
    /**
     * The Queue that store and execute WebRequest
     */
    private RequestQueue mRequestQueue;
    private Context mContext;

    /**
     * The listener to callback when event occur.
     */
    private NewsReceiveListener listener = null;

    public RssNewsUtils(Context context, NewsReceiveListener listener) {
        this.mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
        this.listener = listener;
        mRequestQueue.start();
    }


    /**
     * Start get news by putting request in {@link #mRequestQueue}
     *
     * @param url The url of the RSS source
     */
    public void startGetNews(final String url) {
        StringRequest stringRequest = new StringRequestExt(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processRss(response, url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onReceivedError(error);
            }
        });

        mRequestQueue.add(stringRequest);
    }

    /**
     * This method called when downloading RSS completed to process RSS File, parse it to list
     * of {@link News} objects. After that, call the callback.
     *
     * @param rss  Received RSS text format.
     * @param link The link to know what is the host of this RSS
     */
    private void processRss(String rss, String link) {
        final int HOST_OTHERS = 0;
        final int HOST_THANH_NIEN = 2;
        int host = HOST_OTHERS;

        if (link.startsWith("http://thanhnien.vn/")) {
            //Because thanhnien.vn RSS encode html, when others do not encode.
            // So process thanhnien.vn RSS by difference way.
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
                            } else if ("description".equals(parser.getName())) {
                                news.extractAndSetDescription(text);
                            } else if ("link".equals(parser.getName())) {
                                news.setUrl(text);
                            } else if ("pubDate".equals((parser.getName()))) {
                                news.setTime(text);
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
            listener.onReceivedNews(listOfNews);
        } catch (XmlPullParserException | IOException e) {
            listener.onReceivedException(new RssLoadingException("Failed to parse RSS", e));
        }
    }

    /**
     * Generate the path for the image file to be saved
     *
     * @param fileToSave The name of the image file that going to save
     * @return The path of the image file that going to save
     */
    public String generateImgPath(String fileToSave) {
        File dir = mContext.getDir("tmp_images", Context.MODE_PRIVATE);
        File file = new File(dir, fileToSave);
        return file.getAbsolutePath();
    }

    /**
     * This method start an image request by put it in to {@link #mRequestQueue}.
     * While the task done, save image int to private storage, attach bitmap to the news
     *
     * @param news         The given news
     * @param itemPosition The position of item in RecyclerView
     * @param fileToSave   file name to save image as
     * @param maxWidth     Max image width in pixel or zero for unset
     * @param maxHeight    Max image height in pixel or zero for unset
     */
    public void startGetImage(final News news, final int itemPosition, final String fileToSave, int maxWidth, int maxHeight) {
        Response.Listener<Bitmap> bitmapListener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                String absPath = generateImgPath(fileToSave);
                saveImageToFile(response, absPath);
                news.setImagePath(absPath);
                news.setImage(response);
                listener.onImageAttached(itemPosition);
            }
        };
        ImageRequest imageRequest = new ImageRequest(news.getImageUrl(), bitmapListener, maxWidth, maxHeight,
                ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("lk", error);
            }
        });
        mRequestQueue.add(imageRequest);
    }

    /**
     * Save bitmap image to file.
     *
     * @param bitmap   Image to be saved
     * @param filePath destination path to save
     */
    private void saveImageToFile(Bitmap bitmap, String filePath) {
        if (bitmap != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            } catch (FileNotFoundException e) {
                Log.w("log_save_Img", e);
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.w("log_save_Img", e);
                }

            }
        }
    }

//    private void log(String s){
//        android.util.Log.d("D_log",">>"+s);
//    }
}

