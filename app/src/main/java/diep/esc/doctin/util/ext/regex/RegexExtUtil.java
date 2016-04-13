package diep.esc.doctin.util.ext.regex;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;

/**
 * Created by Diep on 01/04/2016.
 */
public class RegexExtUtil {
    /**
     * This method replace all matching result in {@code matcher} with the result value of method
     * {@link ReplaceMan#getReplacement(String)}
     *
     * @param matcher          Given Matcher
     * @param replaceMan       Given ReplaceMan
     * @param bufferedCapacity Capacity of the StringBuffer, that use to store and process
     *                         result
     * @return The result after replace
     */
    public static String replaceAll(@NonNull Matcher matcher, @NonNull ReplaceMan replaceMan,
                                    int bufferedCapacity) {
        matcher.reset();
        StringBuffer buffer = new StringBuffer(bufferedCapacity);
        while (matcher.find()) {
            String replacement = replaceMan.getReplacement(matcher.group());
            matcher.appendReplacement(buffer, replacement);
        }
        return matcher.appendTail(buffer).toString();
    }
}
