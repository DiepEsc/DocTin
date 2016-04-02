package diep.esc.doctin.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Diep on 29/03/2016.
 */
public class News {
    private static final String TAG="log_News";
    private String title;
    private String time;
    private String summary;
    private String imageUrl;
    private String url;
	//dhdhdhdhdhd
    private Bitmap image;
    private String imagePath;
    private boolean hasRead;

    public News(String url, String title, String time, String summary, String imageUrl, String imagePath, boolean hasRead) {
        this.url=url;
        this.title = title;
        this.time = time;
        this.summary = summary;
        this.imageUrl=imageUrl;
        image=null;
        this.imagePath=imagePath;
        this.hasRead=hasRead;
    }

    public News() {
        this("","","","","","",false);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if(url!=null&&url.length()>0) this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void extractAndSetDescription(String text){
        text=text.replaceFirst("<ul>.+</ul>","");
        Matcher matcher=Pattern.compile("src=(\"[^\"]+|[^ ]+)").matcher(text);
        if(matcher.find()){
            String t=matcher.group();
            if(t.charAt(4)=='"'){
                imageUrl=t.substring(5);
            }
            else imageUrl=t.substring(4);
        }
        Log.d(TAG, "extractAndSetDescription "+imageUrl);
        matcher=getSummaryFilter().matcher(text);
        while (matcher.find()){
            if(summary.length()!=0){
                summary=summary+" ";
            }
            String found=matcher.group();
            int start=found.indexOf('>')+1;
            int end=found.lastIndexOf('<');
            if(start<0) start=0;
            if(end<=0) end=found.length();
//            Log.d("log_N", "extractAndSetDescription "+found);
            found=found.substring(start,end);
            summary=summary+found.trim();
        }
    }
    protected Pattern getSummaryFilter(){
        return Pattern.compile("(^|>)[^<]+(<|$)");
    }
    public void extractAndSetDescription2(String text){
        text="<esc>"+text+"</esc>";
        Log.d("D_log", "text: "+text);
        XmlPullParser parser= Xml.newPullParser();
        try {
            parser.setInput(new StringReader(text));
            while (parser.getEventType()!=XmlPullParser.END_DOCUMENT){
                if(parser.getEventType()==XmlPullParser.START_TAG){
                    if("esc".equals(parser.getName())){
                        summary=parser.getText();
                    }
                    else if("img".equals(parser.getName())){
                        for(int i=0;i<parser.getAttributeCount();i++){
                            if("src".equals(parser.getAttributeName(i))){
                                imageUrl=parser.getAttributeValue(i);
                            }
                        }
                        break;
                    }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            throw new RssLoadingException("Failed to parse XML",e);
        } catch (IOException e) {
            throw new RssLoadingException("Failed to parse XML",e);
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean hasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    @Override
    public boolean equals(Object o) {
        if(o==null||!(o instanceof News)) return false;
        //Log.d("log_M", "equals "+(((News)o).url.equals(url)));
        return ((News)o).url.equals(url);
    }
}
