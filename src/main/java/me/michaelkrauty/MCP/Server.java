package me.michaelkrauty.MCP;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michael on 10/13/14.
 */
public class Server {

	private final static Logger log = Logger.getLogger("MCP");

	private int id;
	private String serverdir;
	private String host;
	private int port;
	private int memory;
	private Process process;
	private InputStream inputstream;
	private OutputStream outputstream;
	private long starttime;
	private String startupCommand;
	private String jar;

	public Server(int id) {
		this.id = id;
		host = getDBHost();
		port = getDBPort();
		memory = getDBMemory();
		process = null;
		inputstream = null;
		outputstream = null;
		starttime = -1;
		jar = getDBJarName();
		serverdir = "/home/michael/Desktop/tmp/servers/" + id;
		/*
		startupCommand = getDBStartupCommand()
				.replace("%JARPATH", getDBJarLocation())
				.replace("%MEMORY", Integer.toString(getDBMemory()))
				.replace("%HOST", getDBHost())
				.replace("%PORT", Integer.toString(getDBPort()));
				*/
	}

	public void start() {
		log.info("Starting server...");
		log.info("Server startup command: " + startupCommand);
		if (!isRunning()) {
			try {
				ProcessBuilder pb = new ProcessBuilder();
				pb.directory(new File(serverdir));
				pb.command("sudo", "-u", "s" + id, "java", "-jar", "/home/michael/Desktop/tmp/jar/" + jar);
				Process p = pb.start();
				process = p;
				inputstream = p.getInputStream();
				outputstream = p.getOutputStream();
				starttime = System.currentTimeMillis();
			} catch (IOException e) {
				log.info(e.getMessage());
				log.info("Attempting to create the server directory & restart...");
				File sdir = new File(serverdir);
				sdir.mkdir();
				start();
			} catch (NullPointerException e) {
				log.info("Server doesn't exist in SQL database.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.info("Server is already online!");
		}
	}

	public void stop() {
		executeCommand("stop");
	}

	public void forceStop() {
		process.destroy();
	}

	public boolean executeCommand(String command) {
		if (isRunning()) {
			PrintWriter out = new PrintWriter(process.getOutputStream(), true);
			out.println(command);
			return true;
		}
		return false;
	}

	public boolean isOnline() {
		boolean open = true;
		// TODO
		//if (exists()) {
		if (true) {
			Socket socket;
			try {
				socket = SocketFactory.getDefault().createSocket();
				try {
					socket.setSoTimeout(5000);
					socket.connect(new InetSocketAddress(host, port));
					socket.close();
				} catch (Exception e) {
					open = false;
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error creating socket");
				open = false;
			}
		} else {
			log.info("Server doesn't exist!");
			open = false;
		}
		return open;
	}

	public boolean isRunning() {
		if (process == null) {
			return false;
		}
		try {
			process.exitValue();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public InputStream getInputStream() {
		return inputstream;
	}

	public int getID() {
		return id;
	}

	public String getDBHost() {
		return Main.sql.getServerHost(id);
	}

	public int getDBPort() {
		return Main.sql.getServerPort(id);
	}

	public int getDBMemory() {
		return Main.sql.getServerMemory(id);
	}

	public String getDBJarName() {
		return Main.sql.getServerJarName(id);
	}
}
