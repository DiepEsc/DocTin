package diep.esc.doctin.util;

import android.graphics.Bitmap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Diep on 29/03/2016.
 */
public class News {
//    private static final String TAG="log_News";

    /**
     * Title of the news
     */
    private String title;
    /**
     * publish time of the news
     */
    private String time;
    /**
     * summary of the news
     */
    private String summary;
    /**
     * image contains in summary of the news
     */
    private String imageUrl;
    /**
     * The link of the news
     */
    private String url;
    /**
     * Bitmap image for this news
     */
    private Bitmap image;
    /**
     * Image path to save or load Bitmap image
     */
    private String imagePath;
    /**
     * Mark if this news has read or not.
     */
    private boolean hasRead;

    /**
     * A constructor with full field
     */
    public News(String url, String title, String time, String summary, String imageUrl, String imagePath, boolean hasRead) {
        this.url = url;
        this.title = title;
        this.time = time;
        this.summary = summary;
        this.imageUrl = imageUrl;
        image = null;
        this.imagePath = imagePath;
        this.hasRead = hasRead;
    }

    /**
     * A constructor with no argument. Default field are empty String or false value
     */
    public News() {
        this("", "", "", "", "", "", false);
    }

    /**
     * Extract the description to {@link #summary} text and image link ({@see #imageUrl})
     *
     * @param text the input description
     */
    public void extractAndSetDescription(String text) {
        text = text.replaceFirst("<ul>.+</ul>", "");
        Matcher matcher = Pattern.compile("src=(\"[^\"]+|[^ ]+)").matcher(text);
        if (matcher.find()) {
            String t = matcher.group();
            if (t.charAt(4) == '"') {
                imageUrl = t.substring(5);
            } else imageUrl = t.substring(4);
        }
        matcher = getSummaryFilter().matcher(text);
        while (matcher.find()) {
            if (summary.length() != 0) {
                summary = summary + " ";
            }
            String found = matcher.group();
            int start = found.indexOf('>') + 1;
            int end = found.lastIndexOf('<');
            if (start < 0) start = 0;
            if (end <= 0) end = found.length();
            found = found.substring(start, end);
            summary = summary + found.trim();
        }
    }

    /**
     * Get regex {@link Pattern} for extractAndSetDescription to identify the image url
     * contains in description
     *
     * @return the Pattern to identify the image url
     */
    public String getUrl() {
        return url;
    }

    protected Pattern getSummaryFilter() {
        return Pattern.compile("(^|>)[^<]+(<|$)");
    }

    public void setUrl(String url) {
        if (url != null && url.length() > 0) this.url = url;
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
        if (o == null || !(o instanceof News)) return false;
        return ((News) o).url.equals(url);
    }
}
