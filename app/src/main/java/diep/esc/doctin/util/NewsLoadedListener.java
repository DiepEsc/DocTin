package diep.esc.doctin.util;

import java.util.ArrayList;

/**
 * This interface for callback method when the list of newses loading complete, or an exception
 * occur
 * Created by Diep on 01/04/2016.
 *
 * @author Diep
 */
public interface NewsLoadedListener {
    /**
     * This method will be called when the list of newses loading completed.
     *
     * @param newses The list of newses received
     */
    void onReceivedNews(ArrayList<News> newses);

    /**
     * This method will be called when the list of newses loading failed, an exception occur.
     *
     * @param exception The exception
     */
    void onReceivedException(Exception exception);
}
