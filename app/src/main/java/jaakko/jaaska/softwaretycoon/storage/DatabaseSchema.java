package jaakko.jaaska.softwaretycoon.storage;

import android.provider.BaseColumns;

/**
 * Container for database schema constants and frequently used
 * SQL queries.
 *
 * Each database table has its own subclass that implements
 * BaseColumns.
 *
 * Created by jaakko on 23.4.2017.
 */

public class DatabaseSchema {

    private DatabaseSchema() {};

    //
    //
    // Table definitions.

    public class CompanyEntry implements BaseColumns {
        public static final String TABLE_NAME = "company";
        public static final String COLUMN_COMPANY_NAME = "compname"; // name of the company
        public static final String COLUMN_COMPANY_FUNDS = "compfunds"; // funds of the company
        public static final String COLUMN_COMPANY_VALUE = "compvalue"; // value of the company
        public static final String COLUMN_COMPANY_CEO = "compceo"; // ID of the employee that is the CEO of the company
        public static final String COLUMN_COMPANY_CURRENT_TIME = "compcurrenttime"; // current game time
    }

    public class EmployeeEntry implements BaseColumns {

    }
}
