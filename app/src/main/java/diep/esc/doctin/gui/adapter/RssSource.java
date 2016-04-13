package diep.esc.doctin.gui.adapter;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import diep.esc.doctin.R;

/**
 * Created by Diep on 04/04/2016.
 */
public class RssSource {
//    private static final String TAG="log_RssSource";

    /**
     * The URL of RSS source
     */
    String url;

    /**
     * The title of RSS source
     */
    String title;

    /**
     * @param title The URL of RSS source
     * @param url   The title of RSS source
     */
    public RssSource(String title, String url) {
        this.url = url;
        this.title = title;
    }

    /**
     * Check if this object is equals with another.
     *
     * @param object An object to be compare with.
     * @return the compare result, equals or not.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null || url == null) return false;
        if (!(object instanceof RssSource)) return false;
        return url.equals(((RssSource) object).url);
    }

    /**
     * Load default map of RSS lists which is store as xml resource at {@code R.xml.rss_list}
     *
     * @param context Current context
     * @return A hash map witch keys are RSS provider name and values are
     * RssSource lists
     * @see MyExpandableListAdapter#MyExpandableListAdapter(Context, HashMap)
     */
    public static HashMap<String, List<RssSource>> loadDefault(Context context) {
        XmlPullParser parser = context.getResources().getXml(R.xml.rss_list);
        HashMap<String, List<RssSource>> res = new HashMap<>();
        try {
            int eventType = parser.getEventType();
            ArrayList<RssSource> list = null;
            RssSource source;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("provider".equals(parser.getName())) {
                            list = new ArrayList<>();
                            res.put(parser.getAttributeValue(null, "name"), list);
                            break;
                        }
                        if ("rss_link".equals(parser.getName())) {
                            source = new RssSource(parser.getAttributeValue(null, "title"),
                                    parser.nextText());
                            list.add(source);
                            break;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
