package diep.esc.doctin.gui.adapter;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diep.esc.doctin.R;

/**
 * Created by Diep on 04/04/2016.
 */
public class RssSource {
    private static final String TAG="log_RssSource";
    String url,title;

    public RssSource(String title, String url) {
        this.url = url;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if(o==null||url==null) return false;
        if(!(o instanceof RssSource)) return false;
        return url.equals(((RssSource)o).url);
    }

    public static HashMap<String,List<RssSource>> loadDefault(Context context){
        XmlPullParser parser=context.getResources().getXml(R.xml.rss_list);
        HashMap<String,List<RssSource>> res=new HashMap<>();
        try {
            int eventType=parser.getEventType();
            ArrayList<RssSource> list=null;
            RssSource source;
            while (eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if("provider".equals(parser.getName())){
                            list=new ArrayList<>();
                            res.put(parser.getAttributeValue(null,"name"),list);
                            Log.d(TAG, "loadDefault "+parser.getAttributeValue(null,"name"));
                            break;
                        }
                        if("rss_link".equals(parser.getName())){
                            source=new RssSource(parser.getAttributeValue(null,"title"),parser.nextText());
                            list.add(source);
                            break;
                        }
                        break;
                }
                eventType=parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
