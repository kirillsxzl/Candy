package cf.ga2mer.candy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountDatabaseHandler extends SQLiteOpenHelper implements IDatabaseAccountHandler {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "accountsManager";
    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String KEY_ID = "id";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_FIRSTNAME = "first_name";
    private static final String KEY_LASTNAME = "last_name";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_STATUS = "status";

    public AccountDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERID + " INTEGER," +  KEY_TOKEN + " TEXT,"
                + KEY_SECRET + " TEXT," + KEY_FIRSTNAME + " TEXT," + KEY_LASTNAME + " TEXT," + KEY_AVATAR_URL + " TEXT," + KEY_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);

        onCreate(db);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, account.getUserId());
        values.put(KEY_TOKEN, account.getAccessToken());
        values.put(KEY_SECRET, account.getSecret());
        values.put(KEY_FIRSTNAME, account.getFirstName());
        values.put(KEY_LASTNAME, account.getLastName());
        values.put(KEY_AVATAR_URL, account.getAvatarURL());
        values.put(KEY_STATUS, account.getStatus());

        db.insert(TABLE_ACCOUNTS, null, values);
        db.close();
    }

    @Override
    public Account getAccount(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[] { KEY_ID,
                KEY_USERID, KEY_TOKEN, KEY_SECRET, KEY_FIRSTNAME, KEY_LASTNAME, KEY_AVATAR_URL, KEY_STATUS}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Account contact = new Account(Long.parseLong(cursor.getString(0)), Long.parseLong(cursor.getString(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7));

        return contact;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<Account>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account account = new Account();
                account.setId(Long.parseLong(cursor.getString(0)));
                account.setUserId(Long.parseLong(cursor.getString(1)));
                account.setAccessToken(cursor.getString(2));
                account.setSecret(cursor.getString(3));
                account.setFirstName(cursor.getString(4));
                account.setLastName(cursor.getString(5));
                account.setAvatarURL(cursor.getString(6));
                account.setStatus(cursor.getString(7));
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        return accountList;
    }

    @Override
    public int updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, account.getUserId());
        values.put(KEY_TOKEN, account.getAccessToken());
        values.put(KEY_SECRET, account.getSecret());
        values.put(KEY_FIRSTNAME, account.getFirstName());
        values.put(KEY_LASTNAME, account.getLastName());
        values.put(KEY_AVATAR_URL, account.getAvatarURL());
        values.put(KEY_STATUS, account.getStatus());

        return db.update(TABLE_ACCOUNTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(account.getId()) });
    }

    @Override
    public void deleteAccount(Account contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNTS, KEY_ID + " = ?", new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNTS, null, null);
        db.close();
    }

    @Override
    public int getCountAccounts() {
        String countQuery = "SELECT  * FROM " + TABLE_ACCOUNTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
