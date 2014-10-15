package me.michaelkrauty.MCP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQL {

	private final static Logger log = Logger.getLogger("MCP");

	private static Connection connection;

	public SQL() {
		checkTables();
	}

	private static void openConnection() {
		try {
			connection = DriverManager.getConnection(Main.databaseURL, Main.databaseUser, Main.databasePass);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Couldn't connect to database! Reason: " + e.getMessage());
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
			PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `jars` (`id` int(11) PRIMARY KEY, `name` varchar(256), `mod` varchar(256), `version` varchar(256), `build` varchar(256), `startup_args` varchar(256));");
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

	public static ArrayList<String> getAllServers() {
		try {
			openConnection();
			PreparedStatement sql = connection
					.prepareStatement("SELECT `id` FROM `servers`;");
			ResultSet result = sql.executeQuery();
			result.last();
			int items = result.getRow();
			result.first();
			ArrayList<String> ids = new ArrayList<String>();
			for (int i = 0; i < items; i++) {
				ids.add(result.getString(1));
				result.next();
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getUserIdByEmail(String email) {
		try {
			if (userDataContainsEmail(email)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `users` WHERE email=?;");
				sql.setString(1, email);
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getInt("id");
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static String getUserEmail(int userid) {
		try {
			if (userDataContainsId(userid)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `users` WHERE id=?;");
				sql.setInt(1, userid);
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getString("email");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getUserUsername(int userid) {
		try {
			if (userDataContainsId(userid)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `users` WHERE id=?;");
				sql.setInt(1, userid);
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getString("userid");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getUserPassword(int userid) {
		try {
			if (userDataContainsId(userid)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `users` WHERE id=?;");
				sql.setInt(1, userid);
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getString("password");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getUserDate_Registered(int userid) {
		try {
			if (userDataContainsId(userid)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `users` WHERE id=?;");
				sql.setInt(1, userid);
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getString("date_registered");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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

	public static String getServerStartupCommand(int serverid) {
		try {
			if (serverDataContainsServer(serverid)) {
				openConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `jars` WHERE id=?;");
				sql.setInt(1, getServerJarId(serverid));
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getString("startup_args");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}