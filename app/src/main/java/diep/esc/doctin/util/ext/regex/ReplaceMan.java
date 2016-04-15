package diep.esc.doctin.util.ext.regex;

import java.util.regex.Matcher;

/**
 * This interface provide a callback to be call while using
 * {@link RegexExtUtil#replaceAll(Matcher, ReplaceMan, int)}
 * @author Diep
 */
public interface ReplaceMan {

    /**
     * Get replacement for given String.s
     *
     * @param source The String that will be replaced by the result of this method
     * @return Replacement String for the given String
     */
    String getReplacement(String source);
}
