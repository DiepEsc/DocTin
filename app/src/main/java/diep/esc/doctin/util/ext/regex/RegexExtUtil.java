package diep.esc.doctin.util.ext.regex;

import java.util.regex.Matcher;

/**
 * Created by Diep on 01/04/2016.
 */
public class RegexExtUtil {
    public static String replaceAll(Matcher matcher, ReplaceMan replaceMan, int bufferedCapacity) {
        matcher.reset();
        StringBuffer buffer = new StringBuffer(bufferedCapacity);
        while (matcher.find()) {
            String replacement = replaceMan.getReplacement(matcher.group());
            matcher.appendReplacement(buffer, replacement);
        }
        return matcher.appendTail(buffer).toString();
    }
}
