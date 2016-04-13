package diep.esc.doctin.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provide some utilities to the app working with database
 * Created by Diep on 29/03/2016.
 */
public class DatabaseUtils {
//    private static final String TAG = "log_trace";

    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mHelper;

    /**
     * Listener handle news loading complete event, or an exception occur.
     */
    private NewsLoadedListener listener;

    /**
     * @param context  Current context
     * @param listener Handle news loading complete event, or an exception occur.
     */
    public DatabaseUtils(Context context, NewsLoadedListener listener) {
        mHelper = new NewsDbHelper(context);
        mDatabase = mHelper.getWritableDatabase();
        this.listener = listener;
    }

    /**
     * Save list of new to database
     *
     * @param lst The list to be saved
     */
    public void storeNewsList(List<News> lst) {
        clearNewsOnDb();
        for (int i = 0; i < lst.size(); i++) {
            storeNews(lst.get(i));
        }
    }

    /**
     * Start a task that loading newses
     */
    public void startLoadNewses() {
        new LoadingNewTask().execute();
    }


    /**
     * Save a news to database
     *
     * @param news The news to be saved
     */
    private void storeNews(News news) {
        if (!existNews(news)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NewsDbHelper.TITLE, news.getTitle());
            contentValues.put(NewsDbHelper.TIME, news.getTime());
            contentValues.put(NewsDbHelper.SUMMARY, news.getSummary());
            contentValues.put(NewsDbHelper.IMAGE_URL, news.getImageUrl());
            contentValues.put(NewsDbHelper.LINK, news.getUrl());
            contentValues.put(NewsDbHelper.IMAGE_TMP_PATH, news.getImagePath());
            contentValues.put(NewsDbHelper.HAS_READ, news.hasRead());
            Long newRowId =
                    mDatabase.insert(NewsDbHelper.TABLE_NAME, null, contentValues);
        }
    }

    /**
     * Clear all news on database
     */
    public void clearNewsOnDb() {
        mDatabase.execSQL("delete from " + NewsDbHelper.TABLE_NAME);
    }

    /**
     * Check if a given news exist on database.
     *
     * @param news The given news
     * @return exist or not.
     */
    public boolean existNews(News news) {
        Cursor cur = mDatabase.query(NewsDbHelper.TABLE_NAME,
                new String[]{NewsDbHelper.LINK},
                NewsDbHelper.LINK + "=?",
                new String[]{news.getUrl()},
                null, null, null
        );
        if (cur.moveToNext()) {
            cur.close();
            return true;
        }
        cur.close();
        return false;
    }

    public static class NewsDbHelper extends SQLiteOpenHelper {
        public static final String TABLE_NAME = "news";
        public static final String TITLE = "tit";
        public static final String TIME = "time";
        public static final String SUMMARY = "sum";
        public static final String IMAGE_URL = "img";
        public static final String LINK = "link";
        public static final String IMAGE_TMP_PATH = "ipath";
        public static final String HAS_READ = "read";


        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "newsInfo.db";

        private static final String SQL_CREATE =
                "Create table " + TABLE_NAME + "(" +
                        TITLE + " text," +
                        TIME + " text," +
                        SUMMARY + " text," +
                        IMAGE_URL + " text," +
                        LINK + " text primary key," +
                        IMAGE_TMP_PATH + " text," +
                        HAS_READ + " integer)";
        private static final String SQL_DELETE = "drop table if exist " + TABLE_NAME;

        public NewsDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    /**
     * The task to be executed to load list of newses from database
     */
    class LoadingNewTask extends AsyncTask<Void, Void, ArrayList<News>> {

        @Override
        protected ArrayList<News> doInBackground(Void... params) {
            return loadNews();
        }

        public ArrayList<News> loadNews() {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cur = db.rawQuery("select * from " + NewsDbHelper.TABLE_NAME, new String[]{});
            ArrayList<News> res = new ArrayList<>(cur.getCount());
            if (isCancelled()) return res;
            while (cur.moveToNext()) {
                if (isCancelled()) break;
                res.add(new News(
                        cur.getString(cur.getColumnIndex(NewsDbHelper.LINK)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.TITLE)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.TIME)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.SUMMARY)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.IMAGE_URL)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.IMAGE_TMP_PATH)),
                        cur.getInt(cur.getColumnIndex(NewsDbHelper.HAS_READ)) == 1
                ));
            }
            cur.close();
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<News> newses) {
            listener.onReceivedNews(newses);
        }
    }

//    public void logDataBase(String table){
//        Cursor cur = mDatabase.rawQuery("select * from "+table,new String[]{});
//        Log.d(TAG, "logDataBase:");
//        while (cur.moveToNext()){
//            String tmp=">>";
//            for(int i=0;i<cur.getColumnCount();i++){
//                tmp=tmp+"\t"+cur.getString(i);
//            }
//            Log.d(TAG, tmp);
//        }
//        cur.close();
//    }
}
