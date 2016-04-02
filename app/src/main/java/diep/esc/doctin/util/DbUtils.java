package diep.esc.doctin.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diep on 29/03/2016.
 */
public class DbUtils {
    private static final String TAG = "log_trace";
    private SQLiteDatabase database;
//    private Context context;
    private SQLiteOpenHelper helper;
    private NewsLoadedListener listener;

    public DbUtils(Context context, NewsLoadedListener listener) {
//        this.context = context;
        helper = new NewsDbHelper(context);
        database=helper.getWritableDatabase();
        this.listener=listener;
    }

    public void storeNewsList(List<News> lst){

        clearNewsOnDb();
        for(int i=0;i<lst.size();i++){
            storeNews(lst.get(i));
        }
        logDataBase(NewsDbHelper.TABLE_NAME);
    }
    public void startLoadNews(){
        new LoadingNewTask().execute();
    }

    public void storeNews(News news) {
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
                    database.insert(NewsDbHelper.TABLE_NAME, null, contentValues);
        }
    }
    public void clearNewsOnDb(){
        database.execSQL("delete from "+NewsDbHelper.TABLE_NAME);
    }
    public boolean existNews(News news) {
        Cursor cur = database.query(NewsDbHelper.TABLE_NAME,
                new String[]{NewsDbHelper.LINK},
                NewsDbHelper.LINK + "=?",
                new String[]{news.getUrl()},
                null, null, null
        );
        if (cur.moveToNext()) {
            return true;
        }
        return false;
    }

    public static class NewsDbHelper extends SQLiteOpenHelper {
        public static final String TABLE_NAME = "news";
        //        public static final String ID = "id";
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
                "Create table " + TABLE_NAME + "("+
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
    class LoadingNewTask extends AsyncTask<Void, Void, ArrayList<News>>{

        @Override
        protected ArrayList<News> doInBackground(Void... params) {
            logDataBase(NewsDbHelper.TABLE_NAME);
            return loadNews();
        }
        public ArrayList<News> loadNews(){
            SQLiteDatabase db=helper.getReadableDatabase();
            Cursor cur =db.rawQuery("select * from " + NewsDbHelper.TABLE_NAME, new String[]{});
            ArrayList<News> res=new ArrayList<>(cur.getCount());
            if(isCancelled()) return res;
            while (cur.moveToNext()){
                if(isCancelled()) break;
                res.add(new News(
                        cur.getString(cur.getColumnIndex(NewsDbHelper.LINK)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.TITLE)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.TIME)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.SUMMARY)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.IMAGE_URL)),
                        cur.getString(cur.getColumnIndex(NewsDbHelper.IMAGE_TMP_PATH)),
                        cur.getInt(cur.getColumnIndex(NewsDbHelper.HAS_READ)) == 1
                ));
                Log.d(TAG, cur.getString(cur.getColumnIndex(NewsDbHelper.HAS_READ))
                        +"loadNews "+(cur.getInt(cur.getColumnIndex(NewsDbHelper.HAS_READ))==1));
            }
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<News> newses) {
            listener.receivedNews(newses);
        }
    }



    public void logDataBase(String table){
        Cursor cur =database.rawQuery("select * from "+table,new String[]{});
        Log.d(TAG, "logDataBase:");
        while (cur.moveToNext()){
            String tmp=">>";
            for(int i=0;i<cur.getColumnCount();i++){
                tmp=tmp+"\t"+cur.getString(i);
            }
            Log.d(TAG, tmp);
        }
    }
}
