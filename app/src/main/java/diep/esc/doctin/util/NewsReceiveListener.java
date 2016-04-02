package diep.esc.doctin.util;

import android.graphics.Bitmap;

import com.android.volley.VolleyError;

import java.util.ArrayList;

/**
 * Created by Diep on 29/03/2016.
 */
public interface NewsReceiveListener extends NewsLoadedListener {
    void receivedError(VolleyError error);
    void imageAttached(int itemIndex);
}
