package diep.esc.doctin.util;

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
    private String title;
    private String time;
    private String summary;
    private String imageUrl;
    private String url;
	//dhdhdhdhdhd
//    private Bitmap image;
    private String imagePath;
    private boolean hasRead;

    public News(String url, String title, String time, String summary, String imageUrl, String imagePath, boolean hasRead) {
        this.url=url;
        this.title = title;
        this.time = time;
        this.summary = summary;
        this.imageUrl=imageUrl;
//        image=null;
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
        this.url = url;
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
        Matcher matcher=Pattern.compile("src=\"[^\"]+").matcher(text);
        if(matcher.find()){
            imageUrl=matcher.group().substring(5);
        }
        matcher=Pattern.compile("(^|>)[^<]+(<|$)").matcher(text);
        while (matcher.find()){
            if(summary.length()!=0){
                summary=summary+" ";
            }
            String found=matcher.group();
            if(found.charAt(0)=='>') found=found.substring(1);
            if(found.charAt(found.length()-1)=='<') found=found.substring(0, found.length() - 2);
            summary=summary+found.trim();
        }
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public Bitmap getImage() {
//        return image;
//    }
//    public boolean isLoadedImage(){
//        return image!=null;
//    }

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
