package com.medizine.db;

import android.database.Cursor;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DBMigration {
    private static boolean isTableExists(SupportSQLiteDatabase database, String tableName) {
        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'";
        try (Cursor cursor = database.query(query)) {
            if (cursor != null) {
                return cursor.getCount() > 0;
            }
            return false;
        }
    }
}
