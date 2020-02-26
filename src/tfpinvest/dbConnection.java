package tfpinvest;

/*
--------------------------------------------------------------------------------
Connect to the Derby database.


--------------------------------------------------------------------------------
Multi-Connection Setup

*** Note, when using a file directory derby connection, the connection single user.
+ If connected using filename, with ij, must exit to run another connection program. "disconnect" is not enough.
https://db.apache.org/derby/papers/DerbyTut/embedded_intro.html

+ Use a Derby server to make multiple connections, via the server:
https://db.apache.org/derby/papers/DerbyTut/ns_intro.html

$ export CLASSPATH=/Users/dthurston/Projects/invest/lib/derbytools.jar:/Users/dthurston/Projects/invest/lib/derbynet.jar:.
$ echo $CLASSPATH
/Users/dthurston/Projects/invest/lib/derbytools.jar:/Users/dthurston/Projects/invest/lib/derbynet.jar:.
$ java -jar lib/derbyrun.jar server start
Wed Feb 26 10:51:25 PST 2020 : Security manager installed using the Basic server security policy.
Wed Feb 26 10:51:26 PST 2020 : Apache Derby Network Server - 10.14.2.0 - (1828579) started and ready to accept connections on port 1527
^c

--- or, run in the background ---
$ java -jar lib/derbyrun.jar server start &

...
ij> connect 'jdbc:derby://localhost:1527/investdb;user=tfp;password=tigers';

$ ps -ef | grep derby
29419189 32732 27042   0 10:51AM ttys003    0:01.44 /usr/bin/java -jar lib/derbyrun.jar server start

$ java -jar lib/derbyrun.jar server shutdown

--------------------------------------------------------------------------------
+++ Initial database setup.

+ Set variable DBDIR, to the database directory name:tr5
    private String DBDIR = "/Users/dthurston/Projects/invest/investdb";
+ Create the database.
    cd /Users/dthurston/Projects/invest
    java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
    ij> connect 'jdbc:derby:investdb;user=tfp;password=tigers;create=true';
    ij> exit;
+ Reconnect.
    java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
    ij> connect 'jdbc:derby:investdb;user=tfp;password=tigers';
--- or ---
    ij> connect 'jdbc:derby://localhost:1527/investdb;user=tfp;password=tigers';

--------------------------------------------------------------------------------
+++ Test creating a test database: p1.

cd /Users/dthurston/Projects/invest
java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby:p1;user=tfp;password=tigers;create=true';
ij> create table derbyTB(num int, addr varchar(40));
ij> insert into derbyTB values (1956,'Webster St.');
ij> insert into derbyTB values (1910,'Union St.');
ij> select * from derbyTb;
ij> disconnect;
ij> exit;
+ Reconnect.
java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby:p1;user=tfp;password=tigers;';
ij> show tables;

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
