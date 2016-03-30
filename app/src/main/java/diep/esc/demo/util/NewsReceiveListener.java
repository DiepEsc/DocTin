package diep.esc.demo.util;

import java.util.ArrayList;

/**
 * Created by Diep on 29/03/2016.
 */
public interface NewsReceiveListener {
    void receivedNews(ArrayList<News> news);
    void receivedImage();
}
