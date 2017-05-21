package org.openshift;

/**
 * Created by dima on 21.05.17.
 */

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBInsultGenerator {
    public String generateInsult() {
        String vowels = "AEIOU";
        String article = "an";
        String theInsult = "";
        Connection connection = null;
        ResultSet rs = null;
        try {
            String databaseURL = "jdbc:postgresql://";
            databaseURL += System.getenv("POSTGRESQL_SERVICE_HOST");
            databaseURL += "/" + System.getenv("POSTGRESQL_DATABASE");
            String username = System.getenv("POSTGRESQL_USER");
            String password = System.getenv("PGPASSWORD");
            connection = DriverManager.getConnection(databaseURL, username, password);
            if (connection != null) {
                String SQL = "select a.string AS first, b.string AS second, c.string AS noun " +
                        "from short_adjective a , long_adjective b, noun c ORDER BY random() limit 1";
                Statement stmt = connection.createStatement();
                rs = stmt.executeQuery(SQL);
                while (rs.next()) {
                    if (vowels.indexOf(rs.getString("first").charAt(0)) == -1) {
                        article = "a";
                    }
                    theInsult = String.format("Thou art %s %s %s %s!", article,
                            rs.getString("first"), rs.getString("second"), rs.getString("noun"));
                }
                rs.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Database connection problem!";
        } finally {
            close(rs);
            close(connection);
        }
        return theInsult;
    }

    private void close(AutoCloseable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Exception ex) {
            // ignore
        }
    }
}