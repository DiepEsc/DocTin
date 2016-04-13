package diep.esc.doctin.util.ext.regex;

/**
 * Created by Diep on 01/04/2016.
 */
public interface ReplaceMan {

    /**
     * Get replacement for given String.s
     *
     * @param source The String that will be replaced by the result of this method
     * @return Replacement String for the given String
     */
    public String getReplacement(String source);
}
