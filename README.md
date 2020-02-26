--------------------------------------------------------------------------------
# Invest
  
Application to manage an investment Derby database, and generate reports.

--------------------------------------------------------------------------------
## Derby Database

-------------------------------------------
#### Test creating a test database: p1.
````
cd /.../Projects/invest
java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby:p1;user=tfp;password=tigers;create=true';
ij> create table derbyTB(num int, addr varchar(40));
ij> insert into derbyTB values (1956,'Webster St.');
ij> insert into derbyTB values (1910,'Union St.');
ij> select * from derbyTb;
ij> disconnect;
ij> exit;
````
Reconnect.
````
java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby:p1;user=tfp;password=tigers;';
ij> show tables;
ij> exit;
````
-------------------------------------------
#### Create investment database: investdb.

Initial database setup.

Default directory name, is the current user directory (System.getProperty("user.dir")).
To override with a full directory name,
in the [connection program](src/tfpinvest/dbConnection.java),
hardcode the DBDIR_File variable with the database directory name.
````
    private String DBDIR_File = "/.../Projects/invest/investdb";
````
Create the database.
````
$ cd /.../Projects/invest
$ java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby:investdb;user=tfp;password=tigers;create=true';
ij> exit;
````
Reconnect.
````
$ java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby:investdb;user=tfp;password=tigers';
````

-------------------------------------------
#### Multi-Connection Setup

Note, when using a file directory derby connection, the connection is single user.
If connected using filename, with ij, must exit to run another connection program. "disconnect" is not enough.
https://db.apache.org/derby/papers/DerbyTut/embedded_intro.html

Use a Derby database server to make multiple connections, via the server:
https://db.apache.org/derby/papers/DerbyTut/ns_intro.html
````
$ export CLASSPATH=/.../Projects/invest/lib/derbytools.jar:/.../Projects/invest/lib/derbynet.jar:.
$ echo $CLASSPATH
/.../Projects/invest/lib/derbytools.jar:/.../Projects/invest/lib/derbynet.jar:.
$ java -jar lib/derbyrun.jar server start
Wed Feb 26 10:51:25 PST 2020 : Security manager installed using the Basic server security policy.
Wed Feb 26 10:51:26 PST 2020 : Apache Derby Network Server - 10.14.2.0 - (1828579) started and ready to accept connections on port 1527
^c
````
Or, run the server in the background.
````
$ java -jar lib/derbyrun.jar server start &
...
$ ps -ef | grep derby
29419189 32732 27042   0 10:51AM ttys003    0:01.44 /usr/bin/java -jar lib/derbyrun.jar server start
````
Then, connect using the URL to the server.
````
java -cp lib/derby.jar:lib/derbyclient.jar:lib/derbytools.jar org.apache.derby.tools.ij
ij> connect 'jdbc:derby://localhost:1527/investdb;user=tfp;password=tigers';
````
Shutdown the server.
````
$ java -jar lib/derbyrun.jar server shutdown
````

--------------------------------------------------------------------------------
Cheers