package diep.esc.doctin.util;

import com.android.volley.VolleyError;

/**
 * Created by Diep on 29/03/2016.
 */
public interface NewsReceiveListener extends NewsLoadedListener {

    /**
     * This method will be called when an VolleyError occur while downloading RSS or images
     *
     * @param error An error to be handled
     */
    void onReceivedError(VolleyError error);

    /**
     * This method will be called when an image has been downloaded completed and attach to news
     *
     * @param itemIndex the index of the news in the list
     */
    void onImageAttached(int itemIndex);
}
