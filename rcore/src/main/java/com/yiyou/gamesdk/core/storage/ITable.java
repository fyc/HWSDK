package com.yiyou.gamesdk.core.storage;

/**
 * Created by levyyoung on 15/5/12.
 */
public interface ITable {
    String tableName();
    int tableVersion();
    String createTableSQL();
    String[] alterTableSQL(int oldVersion, int newVersion);
}
