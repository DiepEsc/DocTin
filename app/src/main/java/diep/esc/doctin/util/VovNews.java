package diep.esc.doctin.util;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by Diep on 31/03/2016.
 */
public class VovNews extends News{
    @Override
    protected Pattern getSummaryFilter() {
        return Pattern.compile("(^|</[^>]>)[^<]+(<|$)");
    }

    @Override
    public void setUrl(String url) {
//        Log.d("log_N", "setUrl " + url);

        if(url.length()>8&&!url.startsWith("http://m.")){
            url="http://m."+url.substring(7);
        }
        super.setUrl(url);
    }
}
