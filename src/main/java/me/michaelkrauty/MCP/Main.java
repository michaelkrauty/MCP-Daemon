package me.michaelkrauty.MCP;

import me.michaelkrauty.MCP.ClientConnection.ConnectionHandler;

import java.io.*;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by michael on 10/13/14.
 */
public class Main {

	public static boolean running;
	private static Logger log = Logger.getLogger("MCP");

	public static int daemonPort;
	public static String daemonPass;
	public static String databaseURL;
	public static String databaseUser;
	public static String databasePass;

	public static SQL sql;
	public static ServerManager serverManager;

	public static void main(String[] args) {
		loadConfig();
		sql = new SQL();
		serverManager = new ServerManager();
		new ConnectionHandler().start();
		mainLoop();
	}

	private static void mainLoop() {
		try {
			running = true;
			while (running) {
				// Running
				Thread.sleep(100);
			}
			sql.closeConnection();
			log.info("Program exit.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadConfig() {
		try {
			File conf = new File("daemon.conf");
			if (!conf.exists()) {
				conf.createNewFile();
				FileWriter writer = new FileWriter(conf);
				writer.write("database=jdbc:mysql://n1.domvps.net:3306/webadmin\n");
				writer.write("database_user=MCP\n");
				writer.write("database_pass=MCP\n");
				writer.write("pass=" + UUID.randomUUID().toString().replace("-", "") + "\n");
				writer.write("port=35456\n");
				writer.close();
			}
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(new File("daemon.conf"))));
			databaseURL = p.getProperty("database");
			daemonPort = Integer.parseInt(p.getProperty("port"));
			daemonPass = p.getProperty("pass");
			databaseUser = p.getProperty("database_user");
			databasePass = p.getProperty("database_pass");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
