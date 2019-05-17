package utd.cs.contactmanagerp1;

/*
    Database Handler to create, add, edit, and delete values
*/
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.LinkedList;
import android.util.Log;

    public class sqlDatabase extends SQLiteOpenHelper {

        private static final String TAG = "DataBaseHelper";
        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;
        // String variable declarations
        private static final String DATABASE_NAME = "contacts.db";
        private static final String TABLE_NAME = "Contacts";
        private static final String KEY_ID = "";
        private static final String KEY_FNAME = "FirstName";
        private static final String KEY_LNAME = "LastName";
        private static final String KEY_PHN = "Phone";
        private static final String KEY_DOB = "DOB";
        private static final String KEY_DATE = "Date";
        private static final String KEY_ADD1 = "AddressL1";
        private static final String KEY_ADD2 = "Address2";
        private static final String KEY_CITY = "City";
        private static final String KEY_ZIP = "ZIP";
        private static final String KEY_STATE = "State";

        ArrayList<Contacts> contactList = new ArrayList<Contacts>();


        public sqlDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {

            String createTable = "CREATE TABLE " + TABLE_NAME +
                    "( id INTEGER PRIMARY KEY AUTOINCREMENT, FirstName STRING, LastName STRING, Phone STRING, DOB STRING, Date STRING," +
                    " Address1 STRING, Address2 STRING, City STRING, ZIP STRING, State STRING)";
            db.execSQL(createTable);
        }

        public boolean checkDB(SQLiteDatabase db) {
            //check database
            //SQLiteDatabase checkDB = db;
            try {
                db = SQLiteDatabase.openDatabase("contacts.db", null,
                        SQLiteDatabase.OPEN_READONLY);
                db.close();
            } catch (SQLException e) {
                //database doesn't exist
            }
            return db != null;
        }
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            this.onCreate(db);
        }

        //delete contact from db
        public void deleteContact(Contacts contacts, int position) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(position) });
            db.close();
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        //read from the database
        public ArrayList<Contacts> allContacts() {

            contactList = new ArrayList<Contacts>();

            String query = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            Contacts contacts  = new Contacts(null, null, null, null, null, null, null, null, null, null);

            int row = cursor.getCount();
            int col = cursor.getColumnCount();

            String values[] = new String[col];

            if (cursor.moveToNext()) {
                for (int i = 0; i < row; i++) {
                    for (int j = 1; j < col; j++) {
                        values[j] = cursor.getString(j);

                    }
                    // create a new contact for each row
                    contacts = new Contacts(values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9], values[10]);
                    //add contact to list
                    contactList.add(contacts);

                }

            }
            db.close();
            return contactList;
        }

        // reinitialize database
        public void reInit() {
            SQLiteDatabase db = this.getWritableDatabase();
            String clearDBQuery = "DELETE FROM "+TABLE_NAME;
            db.execSQL(clearDBQuery);


        }
        //insert values in table database
        public boolean addContacts(Contacts contacts) {
            SQLiteDatabase db = this.getWritableDatabase();
            String fn = contacts.getFirstname();
            String ln = contacts.getLastname();
            String phone = contacts.getPhone();
            String dob = contacts.getBirthdate();
            String doc = contacts.getFirstmeeting();

            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_FNAME, fn);
                values.put(KEY_LNAME, ln);
                values.put(KEY_PHN, phone);
                values.put(KEY_DOB, dob);
                values.put(KEY_DATE, doc);
                db.insert(TABLE_NAME, null, values);

                db.setTransactionSuccessful();
            } catch(Exception e) {

            } finally {
                db.endTransaction();

            }

            return true;

        }
        // reorder list
        public void orderList(){

        }

        public void modifyContacts(Contacts contacts, int position) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            //get values
            String fn = contacts.getFirstname();
            String ln = contacts.getLastname();
            String phone = contacts.getPhone();
            String dob = contacts.getBirthdate();
            String doc = contacts.getFirstmeeting();
            //

            values.put(KEY_FNAME, fn);
            values.put(KEY_LNAME, ln);
            values.put(KEY_PHN, phone);
            values.put(KEY_DOB, dob);
            values.put(KEY_DATE, doc);

            db.update(TABLE_NAME, // table
                    values, // column/value
                    "id = ?", // selections
                    new String[] {String.valueOf(position)});

            db.close();
        }
    }
