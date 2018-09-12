package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.HashMap;

public class DatabaseManager {
    private static SQLiteDatabase database;
    private static HashMap<String, File> customDatabaseFiles;

    public static void openDatabase(Context context, String databaseName) {
        synchronized (DatabaseManager.class) {
            if (database != null && database.isOpen() && new File(database.getPath()).getName().equals(databaseName)) {
            } else {
                if (databaseName == null || databaseName.trim().length() < 1) {
                    return;
                }
                closeDatabase();
                File file = context.getDatabasePath(databaseName);
                if (file == null && customDatabaseFiles.get(databaseName) != null) {
                    file = customDatabaseFiles.get(databaseName);
                }
                if (file == null || !file.exists()) {
                    return;
                }
                database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            }
        }
    }

    public static HashMap<String, File> getDatabaseFiles(Context context) {
        String[] d = context.databaseList();
        HashMap<String, File> fileHashMap = new HashMap<>();
        for (int i = 0; i < d.length; i++) {
            File file = context.getDatabasePath(d[i]);
            fileHashMap.put(file.getName(), file);
        }
        if (customDatabaseFiles != null) {
            fileHashMap.putAll(customDatabaseFiles);
        }
        return fileHashMap;
    }

    public static void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
        DatabaseManager.customDatabaseFiles = customDatabaseFiles;
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static SQLiteDatabase getDatabase(Context context, String databaseName) {
        openDatabase(context, databaseName);
        return database;
    }

    private static void closeDatabase() {
        synchronized (DatabaseManager.class) {
            if (database != null && database.isOpen()) {
                database.close();
            }
            database = null;
        }
    }
}
