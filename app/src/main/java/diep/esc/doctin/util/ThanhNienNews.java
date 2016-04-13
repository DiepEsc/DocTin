package diep.esc.doctin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diep.esc.doctin.util.ext.regex.RegexExtUtil;
import diep.esc.doctin.util.ext.regex.ReplaceMan;

/**
 * This class is specification for thanhnien.vn. The RSS using HTML encode. So, this class decode
 * some escape character before process it.
 * Created by Diep on 01/04/2016.
 */
public class ThanhNienNews extends News {

    /**
     * Extract the description to {@link #summary} text and image link ({@see #imageUrl}).
     *
     * @param text the input description. The text can be contains HTML encode escape character
     */
    @Override
    public void extractAndSetDescription(String text) {
        super.extractAndSetDescription(preProcess(text));
    }

    /**
     * Set title of this News object
     *
     * @param title The title, can be contains HTML encode escape character
     */
    @Override
    public void setTitle(String title) {
        super.setTitle(preProcess(title));
    }

    /**
     * Decode some HTML escape character
     *
     * @param txt The text that may contains escape character in encode {@code &lt;, &gt;,
     *            &quot;, &apos;, &amp;}
     * @return The text may that do not contain some escape character in encode {@code <, >, \, ',
     * &}
     */
    private String preProcess(String txt) {
        Matcher matcher = Pattern.compile("&(lt|gt|quot|apos|amp);").matcher(txt);
        return RegexExtUtil.replaceAll(matcher, new ReplaceMan() {
            @Override
            public String getReplacement(String source) {
                if (source.startsWith("&lt")) return "<";
                if (source.startsWith("&gt")) return ">";
                if (source.startsWith("&qout")) return "\"";
                if (source.startsWith("&apos")) return "'";
                if (source.startsWith("&amp")) return "&";
                return "";
            }
        }, txt.length());
    }
}
