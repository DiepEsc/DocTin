package diep.esc.doctin.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Diep on 05/04/2016.
 * Utilities for user options, working with {@link SharedPreferences}
 */
public class OptionsUtils {
    public static final String OPTIONS_FILE = "rss_options";
    public static final String SELECTED_RSS = "selected";
    public static final String CUSTOM_RSS_COUNT = "rss_count";
    public static final String CUSTOM_RSS_PREFIX = "rss_";

    /**
     * Get selected RSS that is saved in {@link SharedPreferences}
     *
     * @param context Current context
     * @return selected RSS link
     */
    public static String getSelectedRss(Context context) {
        context = context.getApplicationContext();
        SharedPreferences prefers = context.getSharedPreferences(OPTIONS_FILE, Context.MODE_PRIVATE);
        return prefers.getString(SELECTED_RSS, null);
    }

    /**
     * Save selected rss to {@link SharedPreferences}
     *
     * @param context Current context
     * @param rss     selected RSS link
     */
    public static void saveSelectedRss(Context context, String rss) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(OPTIONS_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(SELECTED_RSS, rss);
        editor.commit();
    }

    /**
     * Get the custom RSSs as List that are customize by user. (Saved in {@link SharedPreferences})
     *
     * @param context Current context
     * @return A list of custom RSS
     */
    public static ArrayList<String> getCustomRssList(Context context) {
        SharedPreferences prefers = context.getSharedPreferences(OPTIONS_FILE, Context.MODE_PRIVATE);
        int count = prefers.getInt(CUSTOM_RSS_COUNT, 0);
        ArrayList<String> res = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            res.add(prefers.getString(CUSTOM_RSS_PREFIX + i, ""));
        }
        return res;
    }

    /**
     * Add a RSS in to custom RSS list. (Saved in {@link SharedPreferences})
     *
     * @param context Current context
     * @param rss     RSS link to be added.
     */
    public static void addRss(Context context, String rss) {
        SharedPreferences prefers = context.getSharedPreferences(OPTIONS_FILE, Context.MODE_PRIVATE);
        int count = prefers.getInt(CUSTOM_RSS_COUNT, 0);
        SharedPreferences.Editor editor = prefers.edit();
        editor.putString(CUSTOM_RSS_PREFIX + count, rss);
        editor.putInt(CUSTOM_RSS_COUNT, count + 1);
        editor.commit();
    }

    /**
     * Remove RSS at specify index in custom RSS list in {@link SharedPreferences}
     *
     * @param context Current context
     * @param index   the index of RSS in custom RSS list
     */
    public static void removeRssAt(Context context, int index) {
        SharedPreferences prefers = context.getSharedPreferences(OPTIONS_FILE, Context.MODE_PRIVATE);
        int count = prefers.getInt(CUSTOM_RSS_COUNT, 0) - 1;
        SharedPreferences.Editor editor = prefers.edit();
        editor.putInt(CUSTOM_RSS_COUNT, count);
        for (int i = index; i < count; i++) { //count has been subtract by 1 in above statement
            editor.putString(CUSTOM_RSS_PREFIX + i,
                    prefers.getString(CUSTOM_RSS_PREFIX + (i + 1), ""));
        }
        editor.commit();
    }
}
