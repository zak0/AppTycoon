package jaakko.jaaska.apptycoon.storage;

import android.provider.BaseColumns;

/**
 * Container for database schema constants and frequently used
 * SQL queries.
 *
 * Each database table has its own subclass that implements
 * BaseColumns.
 *
 */

class DatabaseSchema {

    static final int VALUE_TRUE = 1;
    static final int VALUE_FALSE = 0;

    //
    //
    // Table definitions.

    static class CompanyEntry implements BaseColumns {
        static final String TABLE_NAME = "company";
        static final String COLUMN_COMPANY_NAME = "compname"; // name of the company
        static final String COLUMN_COMPANY_FUNDS = "compfunds"; // funds of the company
        static final String COLUMN_COMPANY_VALUE = "compvalue"; // value of the company
        static final String COLUMN_COMPANY_CEO = "compceo"; // ID of the employee that is the CEO of the company
        static final String COLUMN_COMPANY_CURRENT_TIME = "compcurrenttime"; // current game time
        static final String COLUMN_COMPANY_SALARY_COSTS = "compsalarycosts"; // salary costs per second
        static final String COLUMN_COMPANY_CODE_PER_SEC = "compcps"; // cumulative 'code per second' value
        static final String COLUMN_COMPANY_QUALITY_PER_SEC = "compquality"; // cumulative 'quality per second' value

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COMPANY_NAME + " TEXT, " +
                COLUMN_COMPANY_FUNDS + " INTEGER, " +
                COLUMN_COMPANY_VALUE + " REAL, " +
                COLUMN_COMPANY_CEO + " TEXT, " +
                COLUMN_COMPANY_CURRENT_TIME + " INTEGER, " +
                COLUMN_COMPANY_SALARY_COSTS + " REAL, " +
                        COLUMN_COMPANY_CODE_PER_SEC + " INTEGER, " +
                        COLUMN_COMPANY_QUALITY_PER_SEC + " INTEGER)";

        static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static class EmployeeEntry implements BaseColumns {
        static final String TABLE_NAME = "employee";
        static final String COLUMN_EMPLOYEE_TITLE = "empltitle";
        static final String COLUMN_EMPLOYEE_DESCRIPTION = "empldescription";
        static final String COLUMN_EMPLOYEE_TYPE = "empltype";
        static final String COLUMN_EMPLOYEE_SALARY = "emplsalary";
        static final String COLUMN_EMPLOYEE_COUNT = "emplcount";
        static final String COLUMN_EMPLOYEE_IS_UNIQUE = "emplisunique";
        static final String COLUMN_EMPLOYEE_CPS_GAIN = "emplcpsgain";
        static final String COLUMN_EMPLOYEE_QUALITY_GAIN = "emplqualitygain";

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_EMPLOYEE_TITLE + " TEXT, " +
                COLUMN_EMPLOYEE_DESCRIPTION + " TEXT, " +
                COLUMN_EMPLOYEE_TYPE + " INTEGER, " +
                COLUMN_EMPLOYEE_SALARY + " REAL, " +
                COLUMN_EMPLOYEE_COUNT + " INTEGER, " +
                COLUMN_EMPLOYEE_IS_UNIQUE + " INTEGER, " +
                COLUMN_EMPLOYEE_CPS_GAIN + " REAL, " +
                COLUMN_EMPLOYEE_QUALITY_GAIN + " REAL)";

        static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
