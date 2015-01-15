package me.michaelkrauty.MCP;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQL {

    private final static PrintStream out = System.out;

    private static Connection connection;

    /**
     * Initializes a new SQL class, checks tables in database
     */
    public SQL() {
        checkTables();
    }

    private static void openConnection() {
        try {
            connection = DriverManager.getConnection(Main.databaseURL, Main.databaseUser, Main.databasePass);
        } catch (Exception e) {
            out.println("Couldn't connect to database! Reason: " + e.getMessage());
        }
    }

    /**
     * Closes the connection to the database
     */
    public static void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies that necessary tables exist in the database
     *
     * @return success/failure
     */
    public static boolean checkTables() {
        openConnection();
        boolean res = true;
        try {
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `servers` (id int(11) PRIMARY KEY, owner int(11), host varchar(256), port int(11), memory int(11), jar int(11), suspended tinyint(4), name varchar(256));");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        }
        try {
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `jars` (`id` int(11) PRIMARY KEY, `name` varchar(256), `mod` varchar(256), `version` varchar(256), `build` varchar(256), `start_command` varchar(256));");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        }
        verifyOwnExistanceInDatabase();
        return res;
    }

    public static void verifyOwnExistanceInDatabase() {
        try {
            openConnection();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM daemons WHERE id=?;");
            sql.setInt(1, Main.daemonID);
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                sql = connection.prepareStatement("INSERT INTO daemons ('id', 'ip', 'port', 'memory') VALUES (?,?,?,?);");
                sql.setInt(1, Main.daemonID);
                sql.setString(2, Main.daemonIP);
                sql.setInt(3, Main.daemonPort);
                sql.setInt(4, Main.daemonMemory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies that the specified server exists in the database
     *
     * @param serverid The ID of the server in question
     * @return server existance in the database
     */
    public static boolean serverDataContainsServer(int serverid) {
        try {
            openConnection();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM daemons WHERE id=?;");
            sql.setInt(1, serverid);
            return sql.executeQuery().next();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the specified server's host
     *
     * @param serverid The ID of the server in question
     * @return the server's IP
     */
    public static String getServerHost(int serverid) {
        try {
            openConnection();
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
            sql.setInt(1, serverid);
            ResultSet result = sql.executeQuery();
            result.next();
            return result.getString("host");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the specified server's port
     *
     * @param serverid The ID of the server in question
     * @return the server's port
     */
    public static int getServerPort(int serverid) {
        try {
            openConnection();
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
            sql.setInt(1, serverid);
            ResultSet result = sql.executeQuery();
            result.next();
            return result.getInt("port");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the specified server's allowed memory in MB
     *
     * @param serverid The ID of the server in question
     * @return the server's allowed memory in MB
     */
    public static int getServerMemory(int serverid) {
        try {
            if (serverDataContainsServer(serverid)) {
                openConnection();
                PreparedStatement sql = connection
                        .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
                sql.setInt(1, serverid);
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getInt("memory");
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the specified server's jar ID
     *
     * @param serverid The ID of the server in question
     * @return the ID of the server's jar
     */
    public static int getServerJarId(int serverid) {
        try {
            if (serverDataContainsServer(serverid)) {
                openConnection();
                PreparedStatement sql = connection
                        .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
                sql.setInt(1, serverid);
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getInt("jar");
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the specified server's jar name
     *
     * @param serverid The ID of the server in question
     * @return the name of the server's jar
     */
    public static String getServerJarName(int serverid) {
        try {
            if (serverDataContainsServer(serverid)) {
                openConnection();
                PreparedStatement sql = connection
                        .prepareStatement("SELECT * FROM `jars` WHERE id=?;");
                sql.setInt(1, getServerJarId(serverid));
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getString("name");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the suspend status of the specified server
     *
     * @param serverid The ID of the server in question
     * @return true if the server is suspended, else return false
     */
    public static boolean getServerSuspended(int serverid) {
        try {
            if (serverDataContainsServer(serverid)) {
                openConnection();
                PreparedStatement sql = connection
                        .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
                sql.setInt(1, serverid);
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getBoolean("suspended");
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the specified server's name
     *
     * @param serverid The ID of the server in question
     * @return the server's name
     */
    public static String getServerName(int serverid) {
        try {
            if (serverDataContainsServer(serverid)) {
                openConnection();
                PreparedStatement sql = connection
                        .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
                sql.setInt(1, serverid);
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getString("name");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the crash detection status of the specified server
     *
     * @param serverid The ID of the server in question
     * @return true if crash detection is enabled, else return false
     */
    public static boolean getServerCrashDetection(int serverid) {
        try {
            if (serverDataContainsServer(serverid)) {
                openConnection();
                PreparedStatement sql = connection
                        .prepareStatement("SELECT * FROM `servers` WHERE id=?;");
                sql.setInt(1, serverid);
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getBoolean("crash_detection");
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the startup command of the specified jar
     *
     * @param jarid The ID of the jar in question
     * @return the startup command for the specified jar
     */
    public static String getJarStarupCommand(int jarid) {
        try {
            openConnection();
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM `jars` WHERE id=?;");
            sql.setInt(1, jarid);
            ResultSet result = sql.executeQuery();
            result.next();
            return result.getString("start_command");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the stop command of the specified jar
     *
     * @param jar The ID of the jar in question
     * @return the stop command for the specified jar
     */
    public static String getJarStopCommand(int jar) {
        try {
            openConnection();
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM `jars` WHERE id=?;");
            sql.setInt(1, jar);
            ResultSet result = sql.executeQuery();
            result.next();
            return result.getString("stop_command");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the ID of the specified jar
     *
     * @param jar The name of the jar in question
     * @return the ID of the specified jar
     */
    public static int getJarID(String jar) {
        try {
            openConnection();
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM `jars` WHERE name=?;");
            sql.setString(1, jar);
            ResultSet result = sql.executeQuery();
            result.next();
            return result.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}