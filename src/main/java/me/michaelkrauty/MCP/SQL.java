package me.michaelkrauty.MCP;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQL {

	private final static PrintStream out = System.out;

	private static Connection connection;

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

	public static void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		return res;
	}

	public static boolean serverDataContainsServer(int serverid) {
		try {
			openConnection();
			PreparedStatement sql = connection.prepareStatement("SELECT * FROM `servers` WHERE id=?;");
			sql.setInt(1, serverid);
			return sql.executeQuery().next();
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean userDataContainsEmail(String email) {
		try {
			openConnection();
			PreparedStatement sql = connection.prepareStatement("SELECT * FROM `users` WHERE email=?;");
			sql.setString(1, email);
			return sql.executeQuery().next();
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean userDataContainsId(int userid) {
		try {
			openConnection();
			PreparedStatement sql = connection.prepareStatement("SELECT * FROM `users` WHERE id=?;");
			sql.setInt(1, userid);
			return sql.executeQuery().next();
		} catch (Exception e) {
			return false;
		}
	}

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

	public static String getServerMod(int serverid) {
		try {
			if (serverDataContainsServer(serverid)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `jars` WHERE id=?;");
				sql.setInt(1, getServerJarId(serverid));
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getString("mod");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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

	public static String getJarLocation(int jarid) {
		try {
			openConnection();
			PreparedStatement sql = connection
					.prepareStatement("SELECT * FROM `jars` WHERE id=?;");
			sql.setInt(1, jarid);
			ResultSet result = sql.executeQuery();
			result.next();
			return new File(Main.jardir, result.getString("name")).getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}