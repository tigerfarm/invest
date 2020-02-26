package invest;

/*
--------------------------------------------------------------------------------
    Connect to the Derby database.


*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

abstract class dbConnection {

    static String theClassName = "dbConnection";

    // private static final String DBURL = "jdbc:derby:/.../investdb;user=tfp;password=tigers;";
    private String DBDIR = "//localhost:1527/investdb";
    private String DBURL = "jdbc:derby:" + DBDIR + ";user=tfp;password=tigers;";
    private String DBDIR_File = "/Users/dthurston/Projects/invest/investdb";
    private String DBURL_File = "jdbc:derby:" + DBDIR_File + ";user=tfp;password=tigers;";
    
    // jdbc Connection
    private Connection conn = null;
    private Statement stmt = null;

    // -------------------------------------------------------------------------
    // Directory where the text data files are stored.
    
    public String getDbTextDIR() {
        String currentDirectory = System.getProperty("user.dir");
        // Subdirectory to the current directory.
        String SOURCEDIRECTORY = "tableData/";
        return currentDirectory + "/" + SOURCEDIRECTORY;
    }

    // -------------------------------------------------------------------------
    // The Derby connection string which uses the Derby invest database directory.

    public void setDBDIR(String theDirectory) {
        if (theDirectory.compareTo("") == 0) {
            return;
        }
        // ij> connect 'jdbc:derby://localhost:1527/MyDbTest;create=true';
        // DBDIR = theDirectory;
        DBDIR = "//localhost:1527/investdb";
        DBURL = "jdbc:derby:" + DBDIR + ";user=tfp;password=tigers;";
        System.out.println("+ DB Directory set to: " + DBDIR);
    }

    public Connection createConnection() {
        // First, try the Derby server.
        // If the Derby server isn't available, try using a direct file connection.
        boolean isConnected = false;
        System.out.println("++ createConnection to: " + DBURL);
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DBURL);
            isConnected = true;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException except) {
            System.out.println("- createConnection failed.");
            // except.printStackTrace();
        }
        if (isConnected) {
            return conn;
        }
        System.out.println("++ createConnection to: " + DBURL_File);
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DBURL_File);
            isConnected = true;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException except) {
            System.out.println("- createConnection failed.");
            return null;
        }
        return conn;
    }

    public void shutdownConnection() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DriverManager.getConnection(DBURL + ";shutdown=true");
                conn.close();
            }
            System.out.println("++ shutdownConnection to: " + DBURL);
        } catch (SQLException sqlExcept) {
        }

    }

}
