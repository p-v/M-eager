package com.pv.m_eager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.pv.m_eager.model.Meaning;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p-v
 */
public class MeagerDbHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.pv.m_eager/databases/";

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dictionary.db";

    private SQLiteDatabase mDataBase;

    private final Context mContext;

    public MeagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public void createDatabase(){
        boolean dbExist = checkDataBase();
        if(!dbExist){
            //By calling this method an empty database will be created into the default system path
            //of the application
            this.getReadableDatabase();

            try {
                //overwrite database with dictionary.db database
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException{

        //Open db as the input stream
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database doesn't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();

        super.close();
    }

    public List<Meaning> getMeaning(String word) throws SQLException {
        try{
            openDataBase();
            Cursor cur = mDataBase.rawQuery("SELECT * FROM entries where word = ? COLLATE NOCASE", new String[]{word});

            List<Meaning> meaning = new ArrayList<>(cur.getCount());
            while(cur.moveToNext()){
                meaning.add(new Meaning(cur.getString(1),cur.getString(2).replaceAll("\\\\n"," ")));
            }
            return meaning;
        }finally {
            close();
        }
    }
}
