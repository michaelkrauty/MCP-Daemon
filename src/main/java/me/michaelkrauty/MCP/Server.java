package me.michaelkrauty.MCP;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michael on 10/13/14.
 */
public class Server {

	private final static PrintStream out = System.out;

	private int id;
	private File serverdir;
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
		serverdir = new File(Main.serverdir, Integer.toString(id));
		/*
		startupCommand = getDBStartupCommand()
				.replace("%JARPATH", getDBJarLocation())
				.replace("%MEMORY", Integer.toString(getDBMemory()))
				.replace("%HOST", getDBHost())
				.replace("%PORT", Integer.toString(getDBPort()));
				*/
	}

	public void start() {
		out.println("Starting server " + id);
		out.println("Server startup command: " + startupCommand);
		if (!isRunning()) {
			try {
				if (!serverdir.exists()) {
					Process asdf = Runtime.getRuntime().exec(new String[]{"sudo", "-u", "s" + id, "mkdir", serverdir.getAbsolutePath()});
					String line;
					while ((line = new BufferedReader(new InputStreamReader(asdf.getInputStream())).readLine()) != null) {
						out.println(line);
					}
				}
				ProcessBuilder pb = new ProcessBuilder();
				pb.directory(serverdir);
				pb.command("sudo", "-u", "s" + id, "java", "-jar", new File(Main.jardir, "server.jar").getAbsolutePath());
				Process p = pb.start();
				process = p;
				inputstream = p.getInputStream();
				outputstream = p.getOutputStream();
				starttime = System.currentTimeMillis();
				new ServerOutput(inputstream, id);
				out.println("Server " + id + " started.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			out.println("Server is already online!");
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
				out.println("Error creating socket");
				open = false;
			}
		} else {
			out.println("Server doesn't exist!");
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
