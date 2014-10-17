package me.michaelkrauty.MCP;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

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
	private int jarid;
	private File jarFile;
	private ArrayList<String> latestOutput;

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
		jarid = getDBJarID();
		jarFile = new File(Main.jardir, jar);
		serverdir = new File(Main.serverdir, Integer.toString(id));
		refreshStartupCommand();
		latestOutput = new ArrayList<String>();
	}

	public void start() {
		out.println("Starting server " + id + " (startup command: " + startupCommand + ")");
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
				refreshStartupCommand();
				String[] sc = startupCommand.split(" ");
				String[] cmd = new String[sc.length + 3];
				cmd[0] = "sudo";
				cmd[1] = "-u";
				cmd[2] = "s" + id;
				for (int i = 0; i < sc.length; i++) {
					cmd[i + 3] = sc[i];
				}
				pb.command(cmd);
				Process p = pb.start();
				process = p;
				inputstream = p.getInputStream();
				outputstream = p.getOutputStream();
				starttime = System.currentTimeMillis();
				new ServerOutput(inputstream, id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			out.println("Server " + id + " is already online!");
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
		Socket socket;
		try {
			socket = SocketFactory.getDefault().createSocket();
			socket.connect(new InetSocketAddress(host, port), 5000);
			socket.close();
		} catch (Exception e) {
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

	public long getUptime() {
		return System.currentTimeMillis() - starttime;
	}

	public void refreshStartupCommand() {
		startupCommand = getDBStartupCommand()
				.replace("%JARPATH", jarFile.getAbsolutePath())
				.replace("%MEMORY", Integer.toString(getDBMemory()))
				.replace("%IP", getDBHost())
				.replace("%PORT", Integer.toString(getDBPort()));
	}

	public InputStream getInputStream() {
		return inputstream;
	}

	public ArrayList<String> getLatestOutput() {
		return latestOutput;
	}

	public void addToLatestOutput(String line) {
		if (latestOutput.size() < 100) {
			latestOutput.add(line);
		} else {
			ArrayList<String> temp = new ArrayList<String>();
			for (int i = 0; i < latestOutput.size(); i++) {
				if (i != latestOutput.size()-1)
					temp.add(latestOutput.get(i + 1));
				else
					temp.add(line);
			}
			latestOutput.clear();
			latestOutput = temp;
		}
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

	public int getDBJarID() {
		return Main.sql.getJarID(jar);
	}

	public String getDBStartupCommand() {
		return Main.sql.getJarStarupCommand(jarid);
	}
}
