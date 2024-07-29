package io.github.lucasstarsz.fxdex.misc;

public class FileLinks {
    public static final String DatabaseLocation = System.getProperty("user.home") + "/database.db";
    public static final String JDBCConnectionUrl = "jdbc:sqlite:" + DatabaseLocation;
}
