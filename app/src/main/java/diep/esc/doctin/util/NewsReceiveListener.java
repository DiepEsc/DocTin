package diep.esc.doctin.util;

import com.android.volley.VolleyError;

import java.util.ArrayList;

/**
 * Created by Diep on 29/03/2016.
 */
public interface NewsReceiveListener {
    void receivedNews(ArrayList<News> news);
    void receivedError(VolleyError error);
    void receivedImage();
}
