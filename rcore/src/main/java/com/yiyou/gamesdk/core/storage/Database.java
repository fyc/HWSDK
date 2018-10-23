package com.yiyou.gamesdk.core.storage;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.OperationCanceledException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.event.EventDispatcher;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.storage.events.InternalStorageEvent;
import com.yiyou.gamesdk.core.storage.events.StorageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by levyyoung on 15/5/12.
 */
public abstract class Database {

    private static final String TAG = "RSDK:Database";


    public interface DatabaseTask {
        void process(SQLiteDatabase database);
    }

    private EventDispatcher internalDispatcher = new EventDispatcher();
    private SQLiteDatabase readWriteDB;
    private SQLiteDatabase readOnlyDB;
    private ConnectionThread dbConnection;
    private Context context;

    public Database(Context context) {
        this.context = context;
        addEvents();
        connectDB(context);
    }

    /**
     * Query the given URL, returning a {@link Cursor} over the result set.
     *
     * @param distinct      true if you want each row to be unique, false otherwise.
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param limit         Limits the number of rows returned by the query,
     *                      formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) {
        SQLiteDatabase db = getReadConnection();
        if (db != null) {
            return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
                    limit);
        }
        return null;
    }

    /**
     * Query the given URL, returning a {@link Cursor} over the result set.
     *
     * @param distinct           true if you want each row to be unique, false otherwise.
     * @param table              The table name to compile the query against.
     * @param columns            A list of which columns to return. Passing null will
     *                           return all columns, which is discouraged to prevent reading
     *                           data from storage that isn't going to be used.
     * @param selection          A filter declaring which rows to return, formatted as an
     *                           SQL WHERE clause (excluding the WHERE itself). Passing null
     *                           will return all rows for the given table.
     * @param selectionArgs      You may include ?s in selection, which will be
     *                           replaced by the values from selectionArgs, in order that they
     *                           appear in the selection. The values will be bound as Strings.
     * @param groupBy            A filter declaring how to group rows, formatted as an SQL
     *                           GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                           will cause the rows to not be grouped.
     * @param having             A filter declare which row groups to include in the cursor,
     *                           if row grouping is being used, formatted as an SQL HAVING
     *                           clause (excluding the HAVING itself). Passing null will cause
     *                           all row groups to be included, and is required when row
     *                           grouping is not being used.
     * @param orderBy            How to order the rows, formatted as an SQL ORDER BY clause
     *                           (excluding the ORDER BY itself). Passing null will use the
     *                           default sort order, which may be unordered.
     * @param limit              Limits the number of rows returned by the query,
     *                           formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @param cancellationSignal A signal to cancel the operation in progress, or null if none.
     *                           If the operation is canceled, then {@link OperationCanceledException} will be thrown
     *                           when the query is executed.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    @TargetApi(16)
    public Cursor query(boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadConnection();
        if (db != null) {
            return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
                    limit, cancellationSignal);
        }
        return null;
    }

    /**
     * Query the given table, returning a {@link Cursor} over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        SQLiteDatabase db = getReadConnection();
        if (db != null) {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        return null;
    }

    /**
     * Query the given table, returning a {@link Cursor} over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param limit         Limits the number of rows returned by the query,
     *                      formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit) {
        SQLiteDatabase db = getReadConnection();
        if (db != null) {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
        return null;
    }

    /**
     * Runs the provided SQL and returns a {@link Cursor} over the result set.
     *
     * @param sql           the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs You may include ?s in where clause in the query,
     *                      which will be replaced by the values from selectionArgs. The
     *                      values will be bound as Strings.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = getReadConnection();
        if (db != null) {
            return db.rawQuery(sql, selectionArgs);
        }
        return null;
    }

    /**
     * Runs the provided SQL and returns a {@link Cursor} over the result set.
     *
     * @param sql                the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs      You may include ?s in where clause in the query,
     *                           which will be replaced by the values from selectionArgs. The
     *                           values will be bound as Strings.
     * @param cancellationSignal A signal to cancel the operation in progress, or null if none.
     *                           If the operation is canceled, then {@link OperationCanceledException} will be thrown
     *                           when the query is executed.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     */
    @TargetApi(16)
    public Cursor rawQuery(String sql, String[] selectionArgs,
                           CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadConnection();
        if (db != null) {
            return db.rawQuery(sql, selectionArgs, cancellationSignal);
        }
        return null;
    }

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param table          the table to insert the row into
     * @param nullColumnHack optional; may be <code>null</code>.
     *                       SQL doesn't allow inserting a completely empty row without
     *                       naming at least one column name.  If your provided <code>values</code> is
     *                       empty, no column names are known and an empty row can't be inserted.
     *                       If not set to null, the <code>nullColumnHack</code> parameter
     *                       provides the name of nullable column name to explicitly insert a NULL into
     *                       in the case where your <code>values</code> is empty.
     * @param values         this map contains the initial column values for the
     *                       row. The keys should be the column names and the values the
     *                       column values
     * @param callback       the row ID of the newly inserted row, or -1 if an error occurred
     */
    public void insert(String table, String nullColumnHack, ContentValues values,
                       IOperateCallback<Long> callback) {
        dbConnection.insert(table, nullColumnHack, values, callback);
    }

    /**
     * General method for inserting a row into the database.
     *
     * @param table             the table to insert the row into
     * @param nullColumnHack    optional; may be <code>null</code>.
     *                          SQL doesn't allow inserting a completely empty row without
     *                          naming at least one column name.  If your provided <code>initialValues</code> is
     *                          empty, no column names are known and an empty row can't be inserted.
     *                          If not set to null, the <code>nullColumnHack</code> parameter
     *                          provides the name of nullable column name to explicitly insert a NULL into
     *                          in the case where your <code>initialValues</code> is empty.
     * @param initialValues     this map contains the initial column values for the
     *                          row. The keys should be the column names and the values the
     *                          column values
     * @param conflictAlgorithm for insert conflict resolver
     * @param callback          the row ID of the newly inserted row
     *                          OR the primary key of the existing row if the input param 'conflictAlgorithm' =
     *                          CONFLICT_IGNORE
     *                          OR -1 if any error
     */
    public void insertWithOnConflict(String table, String nullColumnHack,
                                     ContentValues initialValues, int conflictAlgorithm,
                                     IOperateCallback<Long> callback) {
        dbConnection.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm,
                callback);
    }

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param table       the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting.
     *                    Passing null will delete all rows.
     * @param whereArgs   You may include ?s in the where clause, which
     *                    will be replaced by the values from whereArgs. The values
     *                    will be bound as Strings.
     * @param callback    the number of rows affected if a whereClause is passed in, 0
     *                    otherwise. To remove all rows and get a count pass "1" as the
     *                    whereClause.
     */
    public void delete(String table, String whereClause, String[] whereArgs,
                       IOperateCallback<Long> callback) {
        dbConnection.delete(table, whereClause, whereArgs, callback);
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table       the table to update in
     * @param values      a map from column names to new column values. null is a
     *                    valid value that will be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating.
     *                    Passing null will update all rows.
     * @param whereArgs   You may include ?s in the where clause, which
     *                    will be replaced by the values from whereArgs. The values
     *                    will be bound as Strings.
     * @param callback    the number of rows affected
     */
    public void update(String table, ContentValues values, String whereClause, String[] whereArgs,
                       IOperateCallback<Long> callback) {
        dbConnection.update(table, values, whereClause, whereArgs, callback);
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table             the table to update in
     * @param values            a map from column names to new column values. null is a
     *                          valid value that will be translated to NULL.
     * @param whereClause       the optional WHERE clause to apply when updating.
     *                          Passing null will update all rows.
     * @param whereArgs         You may include ?s in the where clause, which
     *                          will be replaced by the values from whereArgs. The values
     *                          will be bound as Strings.
     * @param conflictAlgorithm for update conflict resolver
     * @param callback          the number of rows affected
     */
    public void updateWithOnConflict(String table, ContentValues values,
                                     String whereClause, String[] whereArgs, int conflictAlgorithm,
                                     IOperateCallback<Long> callback) {
        dbConnection.updateWithOnConflict(table, values, whereClause, whereArgs,
                conflictAlgorithm, callback);
    }

    public void executeTask(DatabaseTask task) {
        dbConnection.executeTask(task);
    }

    public void close() {
        dbConnection.close();
        internalDispatcher.removeEventListenersBySource(Database.this);
    }

    private void connectDB(Context context) {
        DatabaseInfo info = new DatabaseInfo();
        info.storagePath = storagePath();
        info.databaseName = databaseName();
        info.databaseVersion = databaseVersion();
        info.staticTables = staticTables();
        info.tablesToAddedOnUpgrade = tablesToAddedOnUpgrade();
        info.addTables = addTables();
        dbConnection = new ConnectionThread(context, "DBThread_" + hashCode(), info,
                internalDispatcher);
        dbConnection.start();
    }

    private void addEvents() {
        internalDispatcher
                .addEventListener(this, InternalStorageEvent.TYPE_ON_CONN_CREATE, onConnectionCreateListener);
        internalDispatcher
                .addEventListener(this, InternalStorageEvent.TYPE_ON_CONN_OPEN, onConnectionOpenListener);
        internalDispatcher
                .addEventListener(this, InternalStorageEvent.TYPE_ON_CONN_UPGRADE, onConnectionUpgradeListener);
    }

    private IEventListener<InternalStorageEvent.Param> onConnectionCreateListener
            = new IEventListener<InternalStorageEvent.Param>() {
        @TargetApi(16)
        @Override
        public void onEvent(String s, InternalStorageEvent.Param param) {

        }
    };

    private IEventListener<InternalStorageEvent.Param> onConnectionUpgradeListener
            = new IEventListener<InternalStorageEvent.Param>() {
        @TargetApi(16)
        @Override
        public void onEvent(String s, InternalStorageEvent.Param param) {
            EventDispatcherAgent.defaultAgent()
                    .broadcast(StorageEvent.TYPE_ON_DB_Upgrade,
                            new StorageEvent.DBEventParam(StatusCodeDef.SUCCESS, Database.this));
        }
    };

    private IEventListener<InternalStorageEvent.Param> onConnectionOpenListener
            = new IEventListener<InternalStorageEvent.Param>() {
        @TargetApi(16)
        @Override
        public void onEvent(String eventType, InternalStorageEvent.Param param) {
            if (param.readOnly) {
                readOnlyDB = param.data;
            } else {
                readWriteDB = param.data;
            }
            //WAL模式下DBOpen或双连接模式下只读连接Open，视为connection打开指令完成
            if (Build.VERSION.SDK_INT >= 16 || param.readOnly) {
                EventDispatcherAgent.defaultAgent()
                        .broadcast(StorageEvent.TYPE_ON_DB_OPEN,
                                new StorageEvent.DBEventParam(StatusCodeDef.SUCCESS, Database.this));
            }
        }
    };

    public abstract String storagePath();

    public abstract String databaseName();

    public abstract ITable[] staticTables();

    public abstract int databaseVersion();

    public abstract Map<Integer, ITable[]> tablesToAddedOnUpgrade();

    public abstract ITable[] addTables();

    @TargetApi(16)
    private SQLiteDatabase getReadConnection() {
        if (Build.VERSION.SDK_INT >= 16) {
            return readWriteDB;
        } else {
            return readOnlyDB;
        }
    }

    private static final class DatabaseInfo {
        String storagePath;
        String databaseName;
        int databaseVersion;
        ITable[] staticTables;
        ITable[] addTables;
        Map<Integer, ITable[]> tablesToAddedOnUpgrade = new ArrayMap<>();
        boolean readyOnly = false;

        public DatabaseInfo() {
        }

        public DatabaseInfo(String storagePath, String databaseName, int databaseVersion, ITable[] staticTables) {
            this.storagePath = storagePath;
            this.databaseName = databaseName;
            this.databaseVersion = databaseVersion;
            this.staticTables = staticTables;
        }

        public DatabaseInfo fromThis() {
            DatabaseInfo copyInfo = new DatabaseInfo();
            copyInfo.storagePath = this.storagePath;
            copyInfo.databaseName = this.databaseName;
            copyInfo.databaseVersion = this.databaseVersion;
            if (this.staticTables != null) {
                copyInfo.staticTables = new ITable[this.staticTables.length];
                System.arraycopy(this.staticTables, 0, copyInfo.staticTables,
                        0, this.staticTables.length);
            }
            if (this.addTables != null) {
                copyInfo.addTables = new ITable[this.addTables.length];
                System.arraycopy(this.addTables, 0, copyInfo.addTables,
                        0, this.addTables.length);
            }
            copyInfo.readyOnly = this.readyOnly;
            if (this.tablesToAddedOnUpgrade.size() > 0) {
                copyInfo.tablesToAddedOnUpgrade.clear();
                for (Map.Entry<Integer, ITable[]> entry : this.tablesToAddedOnUpgrade.entrySet()) {
                    copyInfo.tablesToAddedOnUpgrade.put(entry.getKey(), entry.getValue());
                }
            }
            return copyInfo;
        }

    }

    private static final class ConnectionThread extends HandlerThread {

        private static final int MSG_CONNECT_TO_DB = 1;
        private static final int MSG_INSERT = 2;
        private static final int MSG_INSERT_WITH_ON_CONFLICT = 3;
        private static final int MSG_DELETE = 4;
        private static final int MSG_UPDATE = 5;
        private static final int MSG_UPDATE_WITH_ON_CONFLICT = 6;
        private static final int MSG_CLOSE = 7;

        private DatabaseInfo databaseInfo = null;
        private EventDispatcher eventDispatcher = null;
        private List<CustomSQLiteOpenHelper> connectionList = new ArrayList<>(2);
        private Context context;
        private SQLiteDatabase readWriteDB;
        private SQLiteDatabase readOnlyDB;
        private Handler connectionHandler;

        public ConnectionThread(@NonNull Context context, @NonNull String name, @NonNull DatabaseInfo dbInfo,
                                @NonNull EventDispatcher dispatcher) {
            super(name);
            init(context, dbInfo, dispatcher);
        }

        /**
         * Constructs a HandlerThread.
         *
         * @param name
         * @param priority The priority to run the thread at. The value supplied must be from
         *                 {@link android.os.Process} and not from java.lang.Thread.
         */
        public ConnectionThread(@NonNull Context context, @NonNull String name, int priority, @NonNull DatabaseInfo dbInfo,
                                @NonNull EventDispatcher dispatcher) {
            super(name, priority);
            init(context, dbInfo, dispatcher);
        }

        private static class ParamInsert {
            public String table;
            public String nullColumnHack;
            public ContentValues values;
            public IOperateCallback<Long> callback;

            public ParamInsert() {
            }

            public ParamInsert(String table, String nullColumnHack, ContentValues values,
                               IOperateCallback<Long> callback) {
                this.table = table;
                this.nullColumnHack = nullColumnHack;
                this.values = values;
                this.callback = callback;
            }
        }

        private static class ParamsInsertWithOnConflict extends ParamInsert {
            public ParamsInsertWithOnConflict() {
            }

            public ParamsInsertWithOnConflict(String table, String nullColumnHack,
                                              ContentValues values,
                                              IOperateCallback<Long> callback,
                                              int conflictAlgorithm) {
                super(table, nullColumnHack, values, callback);
                this.conflictAlgorithm = conflictAlgorithm;
            }

            public int conflictAlgorithm;
        }

        private static class ParamDelete {
            public String table;
            public String whereClause;
            public String[] whereArgs;
            public IOperateCallback<Long> callback;

            public ParamDelete() {
            }

            public ParamDelete(String table, String whereClause, String[] whereArgs, IOperateCallback<Long> callback) {
                this.table = table;
                this.whereClause = whereClause;
                this.whereArgs = whereArgs;
                this.callback = callback;
            }
        }

        private static class ParamUpdate {
            public String table;
            public ContentValues values;
            public String whereClause;
            public String[] whereArgs;
            public IOperateCallback<Long> callback;

            public ParamUpdate() {
            }

            public ParamUpdate(String table, ContentValues values, String whereClause,
                               String[] whereArgs, IOperateCallback<Long> callback) {
                this.table = table;
                this.values = values;
                this.whereClause = whereClause;
                this.whereArgs = whereArgs;
                this.callback = callback;
            }
        }

        private static class ParamUpdateWithOnConflict extends ParamUpdate {
            public int conflictAlgorithm;

            public ParamUpdateWithOnConflict() {
            }

            public ParamUpdateWithOnConflict(String table, ContentValues values, String whereClause,
                                             String[] whereArgs, IOperateCallback<Long> callback,
                                             int conflictAlgorithm) {
                super(table, values, whereClause, whereArgs, callback);
                this.conflictAlgorithm = conflictAlgorithm;
            }
        }


        @TargetApi(16)
        private void connectToDatabase() {
            if (Build.VERSION.SDK_INT >= 16) {
                connectToDatabaseCompat16();
            } else {
                connectToDatabaseCompat();
            }
        }

        private Object closingFlag = new Object();

        @TargetApi(18)
        private void doClose() {
            if (this.isAlive()) {
                for (CustomSQLiteOpenHelper conn : connectionList) {
                    conn.close();
                }
                connectionHandler.removeCallbacksAndMessages(null);
                connectionHandler = null;
                if (Build.VERSION.SDK_INT >= 18) {
                    this.quitSafely();
                } else {

                    this.quit();

                }

            }
        }

        private void doUpdateWithOnConflict(ParamUpdateWithOnConflict param) {
            long updatedNum = 0;
            IOperateCallback<Long> callback = param.callback;
            if (readWriteDB != null) {
                updatedNum = readWriteDB.updateWithOnConflict(param.table, param.values,
                        param.whereClause, param.whereArgs, param.conflictAlgorithm);
            }
            if (callback != null) {
                callback.onResult(StatusCodeDef.SUCCESS, updatedNum);
            }
        }

        private void doUpdate(ParamUpdate param) {
            long updatedNum = 0;
            IOperateCallback<Long> callback = param.callback;
            if (readWriteDB != null) {
                updatedNum = readWriteDB.update(param.table, param.values, param.whereClause, param.whereArgs);
            }
            if (callback != null) {
                callback.onResult(StatusCodeDef.SUCCESS, updatedNum);
            }
        }

        private void doDelete(ParamDelete param) {
            long deleteNum = 0;
            IOperateCallback<Long> callback = param.callback;
            if (readWriteDB != null) {
                deleteNum = readWriteDB.delete(param.table, param.whereClause, param.whereArgs);
            }
            if (callback != null) {
                callback.onResult(StatusCodeDef.SUCCESS, deleteNum);
            }
        }

        private void doInsertWithOnConflict(ParamsInsertWithOnConflict param) {
            int result = StatusCodeDef.READ_WRITE_DB_NOT_FOUND;
            long rowId = -1;
            IOperateCallback<Long> cb = param.callback;
            if (readWriteDB != null) {
                rowId = readWriteDB.insertWithOnConflict(param.table, param.nullColumnHack,
                        param.values, param.conflictAlgorithm);
                if (rowId < 0) {
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                } else {
                    result = StatusCodeDef.SUCCESS;
                }
            }

            if (cb != null) {
                cb.onResult(result, rowId);
            }
        }

        private void doInsert(ParamInsert param) {
            int result = StatusCodeDef.READ_WRITE_DB_NOT_FOUND;
            long rowId = -1;
            IOperateCallback<Long> cb = param.callback;
            if (readWriteDB != null) {
                rowId = readWriteDB.insert(param.table, param.nullColumnHack, param.values);
                if (rowId < 0) {
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                } else {
                    result = StatusCodeDef.SUCCESS;
                }
            }

            if (cb != null) {
                cb.onResult(result, rowId);
            }
        }

        private void connectToDatabaseCompat() {
            DatabaseConnection rwConnection = new DatabaseConnection(context, databaseInfo,
                    eventDispatcher);
            connectionList.add(rwConnection);
            readWriteDB = rwConnection.getWritableDatabase();

            DatabaseInfo readOnlyDBInfo = databaseInfo.fromThis();
            readOnlyDBInfo.readyOnly = true;
            DatabaseConnection readOnlyConnection = new DatabaseConnection(context, readOnlyDBInfo,
                    eventDispatcher);
            connectionList.add(readOnlyConnection);
            readOnlyDB = readOnlyConnection.getReadableDatabase();
        }

        @TargetApi(16)
        private void connectToDatabaseCompat16() {
            DatabaseConnection walConnection = new DatabaseConnection(context, databaseInfo,
                    eventDispatcher);
            walConnection.setWriteAheadLoggingEnabled(true);
            connectionList.add(walConnection);
            readWriteDB = walConnection.getWritableDatabase();
        }

        private void init(Context context, DatabaseInfo dbInfo, EventDispatcher dispatcher) {
            databaseInfo = dbInfo;
            eventDispatcher = dispatcher;
            this.context = context;
        }

        /**
         * Starts the new Thread of execution. The <code>run()</code> method of
         * the receiver will be called by the receiver Thread itself (and not the
         * Thread calling <code>start()</code>).
         *
         * @throws IllegalThreadStateException - if this thread has already started.
         * @see Thread#run
         */
        @Override
        public synchronized void start() {
            super.start();
            connectionHandler = new Handler(getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_CONNECT_TO_DB:
                            connectToDatabase();
                            break;
                        case MSG_INSERT:
                            doInsert((ParamInsert) msg.obj);
                            break;
                        case MSG_INSERT_WITH_ON_CONFLICT:
                            doInsertWithOnConflict((ParamsInsertWithOnConflict) msg.obj);
                            break;
                        case MSG_DELETE:
                            doDelete((ParamDelete) msg.obj);
                            break;
                        case MSG_UPDATE:
                            doUpdate((ParamUpdate) msg.obj);
                            break;
                        case MSG_UPDATE_WITH_ON_CONFLICT:
                            doUpdateWithOnConflict((ParamUpdateWithOnConflict) msg.obj);
                            break;
                        case MSG_CLOSE:
                            doClose();
                            break;
                    }
                    return true;
                }
            });


            connectionHandler.sendMessage(Message.obtain(connectionHandler, MSG_CONNECT_TO_DB));
        }

        public void insert(String table, String nullColumnHack, ContentValues values,
                           IOperateCallback<Long> callback) {
            ParamInsert paramInsert = new ParamInsert(table, nullColumnHack, values, callback);
            Message message = Message.obtain(connectionHandler, MSG_INSERT, paramInsert);
            connectionHandler.sendMessage(message);
        }

        public void insertWithOnConflict(String table, String nullColumnHack,
                                         ContentValues initialValues, int conflictAlgorithm,
                                         IOperateCallback<Long> callback) {
            ParamsInsertWithOnConflict paramsInsertWithOnConflict
                    = new ParamsInsertWithOnConflict(table, nullColumnHack, initialValues,
                    callback, conflictAlgorithm);
            Message message = Message.obtain(connectionHandler, MSG_INSERT_WITH_ON_CONFLICT,
                    paramsInsertWithOnConflict);
            connectionHandler.sendMessage(message);
        }

        public void delete(String table, String whereClause, String[] whereArgs,
                           IOperateCallback<Long> callback) {
            ParamDelete paramDelete = new ParamDelete(table, whereClause, whereArgs, callback);
            Message message = Message.obtain(connectionHandler, MSG_DELETE, paramDelete);
            connectionHandler.sendMessage(message);
        }

        public void update(String table, ContentValues values, String whereClause,
                           String[] whereArgs, IOperateCallback<Long> callback) {
            ParamUpdate paramUpdate = new ParamUpdate(table, values, whereClause, whereArgs, callback);
            Message message = Message.obtain(connectionHandler, MSG_UPDATE, paramUpdate);
            connectionHandler.sendMessage(message);
        }

        public void updateWithOnConflict(String table, ContentValues values, String whereClause,
                                         String[] whereArgs, int conflictAlgorithm, IOperateCallback<Long> callback) {
            ParamUpdateWithOnConflict paramUpdateWithOnConflict = new ParamUpdateWithOnConflict(table,
                    values, whereClause, whereArgs, callback, conflictAlgorithm);
            Message message = Message.obtain(connectionHandler, MSG_UPDATE_WITH_ON_CONFLICT,
                    paramUpdateWithOnConflict);
            connectionHandler.sendMessage(message);
        }

        public void executeTask(final DatabaseTask task) {
            connectionHandler.post(new Runnable() {
                @Override
                public void run() {
                    task.process(readWriteDB);
                }
            });
        }

        public void close() {
            connectionHandler.sendMessage(Message.obtain(connectionHandler, MSG_CLOSE));
        }
    }

    private static class DatabaseConnection extends CustomSQLiteOpenHelper {

        private DatabaseInfo dbInfo = null;
        private EventDispatcher dispatcher = null;

        public DatabaseConnection(Context context, @NonNull DatabaseInfo dbInfo,
                                  @NonNull EventDispatcher dispatcher) {
            super(context, dbInfo.storagePath, dbInfo.databaseName, null, dbInfo.databaseVersion);
            this.dbInfo = dbInfo;
            this.dispatcher = dispatcher;
        }


        public DatabaseConnection(Context context, @NonNull DatabaseInfo dbInfo,
                                  @NonNull EventDispatcher dispatcher,
                                  @Nullable DatabaseErrorHandler errorHandler) {
            super(context, dbInfo.storagePath, dbInfo.databaseName, null, dbInfo.databaseVersion,
                    errorHandler);
            this.dbInfo = dbInfo;
            this.dispatcher = dispatcher;
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            if (dbInfo.staticTables != null) {
                for (ITable table : dbInfo.staticTables) {
                    Log.d(TAG,"onCreate createTableSQL: " + table);
                    db.execSQL(table.createTableSQL());
                }
            }


            dispatcher.broadcast(InternalStorageEvent.TYPE_ON_CONN_CREATE,
                    new InternalStorageEvent.Param(StatusCodeDef.SUCCESS, db, dbInfo.readyOnly));
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);


//            try {
//                String sql = "select game_id from game_history";
//
//                db.rawQuery(sql,null);
//            } catch (SQLException e) {
//
//                Log.e(TAG, "error table .......");
//
//                try {
//                    String sql = "drop table if  exists game_history";
//                    db.execSQL(sql);
//                    String sql2 = "drop table  if exists game_setting";
//                    db.execSQL(sql2);
//
//                    for (ITable table : dbInfo.staticTables) {
//                        db.execSQL(table.createTableSQL());
//                    }
//
//
//                } catch (SQLException e1) {
//
//                    Log.e(TAG, "drop table error .......");
//
//                }
//
//            }


            //TO ADD NEW STATIC TABLES


            for (ITable table : dbInfo.addTables) {
                Log.d(TAG,"onOpen createTableSQL: " + table);
                db.execSQL(table.createTableSQL());
            }
            dispatcher.broadcast(InternalStorageEvent.TYPE_ON_CONN_OPEN,
                    new InternalStorageEvent.Param(StatusCodeDef.SUCCESS, db, dbInfo.readyOnly));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TO ADD NEW STATIC TABLES
            for (Map.Entry<Integer, ITable[]> entry : dbInfo.tablesToAddedOnUpgrade.entrySet()) {
                if (entry.getKey() <= newVersion) {
                    for (ITable table : entry.getValue()) {
                        Log.d(TAG,"onUpgrade createTableSQL: " + table);
                        db.execSQL(table.createTableSQL());
                    }
                }
            }
            //Table Upgrade
            if (dbInfo.staticTables != null) {
                for (ITable table : dbInfo.staticTables) {
                    String[] alterSQLs = table.alterTableSQL(oldVersion, newVersion);
                    if (alterSQLs != null) {
                        for (String sql : alterSQLs) {
                            db.execSQL(sql);
                        }
                    }
                }
            }
            dispatcher.broadcast(InternalStorageEvent.TYPE_ON_CONN_UPGRADE,
                    new InternalStorageEvent.Param(StatusCodeDef.SUCCESS, db, dbInfo.readyOnly));
        }

    }

}
