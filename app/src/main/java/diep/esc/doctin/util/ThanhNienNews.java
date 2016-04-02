package diep.esc.doctin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diep.esc.doctin.util.ext.regex.RegexExtUtil;
import diep.esc.doctin.util.ext.regex.ReplaceMan;

/**
 * Created by Diep on 01/04/2016.
 */
public class ThanhNienNews extends News {

//    protected Pattern getSummaryFilter() {
//        return Pattern.compile("(^|(</[^>]|div|p|a)>)[^<]+(<|$)");
//    }
    @Override
    public void extractAndSetDescription(String text) {
        super.extractAndSetDescription(preProcess(text));
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(preProcess(title));
    }

    private String preProcess(String txt){
        Matcher matcher=Pattern.compile("&(lt|gt|quot|apos|amp);").matcher(txt);
        return RegexExtUtil.replaceAll(matcher, new ReplaceMan() {
            @Override
            public String getReplacement(String source) {
                if(source.startsWith("&lt")) return "<";
                if(source.startsWith("&gt")) return ">";
                if(source.startsWith("&qout")) return "\"";
                if(source.startsWith("&apos")) return "'";
                if(source.startsWith("&amp")) return "&";
                return "";
            }
        },txt.length());
    }
}
