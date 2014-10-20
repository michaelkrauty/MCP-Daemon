package me.michaelkrauty.MCP;

import me.michaelkrauty.MCP.ClientConnection.ConnectionHandler;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by michael on 10/13/14.
 */
public class Main {

	public static boolean running;
	private static PrintStream out = System.out;

	public static String daemonIP;
	public static int daemonPort;
	public static String daemonPass;
	public static String databaseURL;
	public static String databaseUser;
	public static String databasePass;

	public static SQL sql;
	public static ServerManager serverManager;

	public static File serverdir;
	public static File jardir;

	public static void main(String[] args) {
		loadConfig();
		checkDirectories();
		sql = new SQL();
		serverManager = new ServerManager();
		new ConnectionHandler().start();
		mainLoop();
	}

	private static void mainLoop() {
		try {
			running = true;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (running) {
				if (br.ready()) {
					String command = br.readLine();
					if (command.equalsIgnoreCase("stop")) {
						serverManager.stopAllServers();
						running = false;
					}
				}
				Thread.sleep(100);
			}
			out.println("Waiting for servers to shut down...");
			while (serverManager.getOnlineServerCount() != 0) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			out.println("All servers shut down.");
			out.println("Closing SQL connection...");
			sql.closeConnection();
			out.println("Connection to SQL database closed.");
			out.println("Program exit.");
			System.exit(0);
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
				writer.write("ip=0.0.0.0\n");
				writer.write("port=35456\n");
				writer.write("serverdir=servers\n");
				writer.write("jardir=jar\n");
				writer.close();
			}
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(new File("daemon.conf"))));
			databaseURL = p.getProperty("database");
			daemonIP = p.getProperty("ip");
			daemonPort = Integer.parseInt(p.getProperty("port"));
			daemonPass = p.getProperty("pass");
			databaseUser = p.getProperty("database_user");
			databasePass = p.getProperty("database_pass");
			serverdir = new File(p.getProperty("serverdir"));
			jardir = new File(p.getProperty("jardir"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void checkDirectories() {
		if (!serverdir.exists()) {
			serverdir.mkdir();
			serverdir.setReadable(true, false);
			serverdir.setWritable(true, false);
			serverdir.setExecutable(true, false);
		}
		if (!jardir.exists()) {
			jardir.mkdir();
			jardir.setReadable(true, false);
			jardir.setWritable(false, false);
			jardir.setExecutable(true, false);
		}
	}
}
