package jaakko.jaaska.apptycoon.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.apptycoon.engine.Company;
import jaakko.jaaska.apptycoon.engine.core.GameState;
import jaakko.jaaska.apptycoon.engine.people.EmployeeType;

/**
 * Helper class for interacting with the SQLite database.
 *
 */

class LocalDatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = "LocalDatabaseHelper";

    private static int DB_VERSION = 1;
    private static String DB_FILE = "swtycoon_dev.db";

    private SQLiteDatabase mDb;

    LocalDatabaseHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDb = db;
        createAllTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * Empties the entire database.
     */
    private void dropAllTables() {
        mDb.execSQL(DatabaseSchema.EmployeeEntry.SQL_DROP_TABLE);
        mDb.execSQL(DatabaseSchema.CompanyEntry.SQL_DROP_TABLE);
    }

    /**
     * Creates all tables into the database.
     */
    private void createAllTables() {
        mDb.execSQL(DatabaseSchema.CompanyEntry.SQL_CREATE_TABLE);
        mDb.execSQL(DatabaseSchema.EmployeeEntry.SQL_CREATE_TABLE);
    }

    /**
     * Stores an entire GameState into the database.
     *
     * Does it the 'easy' overkill way - by first dropping all the tables
     * and then rebuilding the entire database.
     *
     * @param gs The GameState to store.
     */
    void storeGameState(GameState gs) {

        // Rebuild the database.
        dropAllTables();
        createAllTables();

        insertCompany(gs.getCompany());

        for (EmployeeType employee : gs.getCompany().getEmployees()) {
            insertEmployee(employee);
        }
    }

    /**
     * Checks if a saved game exists in the database.
     *
     * @return True when a saved game exists.
     */
    boolean storedGameStateExists() {
        String[] columns = { DatabaseSchema.CompanyEntry._ID };
        Cursor cursor = doPlainQuery(DatabaseSchema.CompanyEntry.TABLE_NAME, columns);
        return cursor.getCount() > 0;
    }

    /**
     * Selects (i.e. "reads") the data for the Company object from the database.
     * @return Company object built from data in the database.
     */
    private Company selectCompany() {
        Log.d(TAG, "selectCompany()");

        String[] columns = {
                DatabaseSchema.CompanyEntry.COLUMN_COMPANY_NAME,
                DatabaseSchema.CompanyEntry.COLUMN_COMPANY_FUNDS,
                DatabaseSchema.CompanyEntry.COLUMN_COMPANY_CODE_PER_SEC,
                DatabaseSchema.CompanyEntry.COLUMN_COMPANY_QUALITY_PER_SEC,
                DatabaseSchema.CompanyEntry.COLUMN_COMPANY_SALARY_COSTS
        };

        Cursor cur = doPlainQuery(DatabaseSchema.CompanyEntry.TABLE_NAME, columns);
        CursorHelper curHelper = new CursorHelper(cur);
        cur.moveToFirst();

        // Return null in case the table is empty.
        if (cur.getCount() < 1) {
            return null;
        }

        // There should be only one row. So, no need to iterate throughout the entire Cursor.
        String name = (String) curHelper.get(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_NAME);
        long funds = (Long) curHelper.get(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_FUNDS);
        long cps = (Long) curHelper.get(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_CODE_PER_SEC);
        long qps = (Long) curHelper.get(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_QUALITY_PER_SEC);
        double salaryCosts = (Double) curHelper.get(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_SALARY_COSTS);

        cur.close();
        return new Company(name, -1, -1, funds, cps, qps, salaryCosts);
    }

    /**
     * Insert a company entry into the database.
     * This only inserts the "primitive fields" of the Company object.
     * So, employee and project lists are not inserted as a result of calling
     * this method.
     *
     * @param company Company to insert.
     */
    private void insertCompany(Company company) {
        Log.d(TAG, "insertCompany() - company name = '" + company.getName() + "'");

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_NAME, company.getName());
        values.put(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_FUNDS, company.getFunds());
        values.put(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_CODE_PER_SEC, company.getCps());
        values.put(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_QUALITY_PER_SEC, company.getQuality());
        values.put(DatabaseSchema.CompanyEntry.COLUMN_COMPANY_SALARY_COSTS, company.getSalaryCosts());

        mDb.insert(DatabaseSchema.CompanyEntry.TABLE_NAME, null, values);
    }

    /**
     * Reads employees from the database.
     *
     * @return List of employees read from the database.
     */
    private List<EmployeeType> selectEmployees() {
        Log.d(TAG, "selectEmployees()");

        String[] columns = {
                DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_TYPE,
                DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_COUNT
        };

        Cursor cur = doPlainQuery(DatabaseSchema.EmployeeEntry.TABLE_NAME, columns);
        CursorHelper curHelper = new CursorHelper(cur);
        ArrayList<EmployeeType> employees = new ArrayList<>();

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            int type = ((Long) curHelper.get(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_TYPE)).intValue();
            int count = ((Long) curHelper.get(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_COUNT)).intValue();

            // Instead of creating new instances here, use the static ones.
            EmployeeType employee = EmployeeType.getTypeForId(type);
            employee.setCount(count);
            employees.add(employee);

            cur.moveToNext();
        }
        cur.close();

        return employees;
    }

    /**
     * Insert an employee entry into the database.
     *
     * @param employee Employee to insert.
     */
    private void insertEmployee(EmployeeType employee) {
        Log.d(TAG, "insertEmployee() - type = " + employee.getType());

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_TITLE, employee.getTitle());
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_DESCRIPTION, employee.getDescription());
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_TYPE, employee.getType());
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_SALARY, employee.getSalary());
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_COUNT, employee.getCount());
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_IS_UNIQUE, employee.isUnique() ? DatabaseSchema.VALUE_TRUE : DatabaseSchema.VALUE_FALSE);
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_CPS_GAIN, employee.getCpsGain());
        values.put(DatabaseSchema.EmployeeEntry.COLUMN_EMPLOYEE_QUALITY_GAIN, employee.getQualityGain());

        mDb.insert(DatabaseSchema.EmployeeEntry.TABLE_NAME, null, values);
    }

    /**
     * Reads the data for the GameState from the database, builds
     * the GameState object and returns it.
     *
     * @return A GameState loaded from the database.
     */
    GameState loadGameState() {
        Log.d(TAG, "loadGameState()");

        GameState gs = new GameState();
        Company company = selectCompany();

        company.setEmployees(selectEmployees());

        gs.setCompany(company);

        return gs;
    }

    /**
     * Opens the database in write mode.
     */
    void openWritable() {
        mDb = getWritableDatabase();
    }

    /**
     * Opens the database in read-only mode.
     */
    void openReadable() {
        mDb = getReadableDatabase();
    }

    /**
     * Executes a simple SELECT into the database without a conditions or sort ordering.
     * This is to reduce the need to write the "null,null,null,null" at every query
     * where the entire table is read.
     *
     * @param tableName Name of the table to query.
     * @param columns Columns to SELECT.
     * @return Cursor object with the
     */
    private Cursor doPlainQuery(String tableName, String[] columns) {
        Cursor cursor = mDb.query(tableName, columns, null, null, null, null, null);
        return cursor;
    }

    /**
     * Helper class that simplifies reading the data of columns in a cursor.
     */
    private class CursorHelper {
        private Cursor mCursor;

        CursorHelper(Cursor cursor) {
            mCursor = cursor;
        }

        /**
         * Returns an Object of the column. The receiving end is responsible for
         * casting back to a correct data type.
         *
         * An Object with larger data type will be returned in case of floating point
         * and integer numbers. I.e. they return doubles and longs respectively.
         *
         * @param columnName Name of the column within the cursor to get.
         * @return Object with the contents of the column.
         */
        public Object get(String columnName) {
            int columnIndex = mCursor.getColumnIndex(columnName);
            int type = mCursor.getType(columnIndex);

            switch (type) {
                case Cursor.FIELD_TYPE_FLOAT:
                    return mCursor.getDouble(columnIndex);
                case Cursor.FIELD_TYPE_INTEGER:
                    return mCursor.getLong(columnIndex);
                case Cursor.FIELD_TYPE_STRING:
                    return mCursor.getString(columnIndex);
                case Cursor.FIELD_TYPE_NULL:
                default:
                    return null;
            }
        }
    }

}
