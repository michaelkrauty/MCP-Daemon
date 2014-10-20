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
	private Process process;
	private long starttime;
	private File jarFile;
	private ArrayList<String> latestOutput;
	private long latestOutputTime;
	private String stopCommand;

	public Server(int id) {
		this.id = id;
		process = null;
		starttime = -1;
		jarFile = new File(Main.jardir, getJarName());
		serverdir = new File(Main.serverdir, Integer.toString(id));
		latestOutput = new ArrayList<String>();
	}

	public void start() {
		String startupCommand = getStartupCommand();
		stopCommand = getDBStopCommand();
		out.println("Starting server " + id + " (startup command: " + startupCommand + ")");
		if (!isRunning()) {
			try {
				if (!serverdir.exists()) {
					Runtime.getRuntime().exec(new String[]{"sudo", "-u", "s" + id, "mkdir", serverdir.getAbsolutePath()});
				}
				ProcessBuilder pb = new ProcessBuilder();
				pb.directory(serverdir);
				String[] sc = startupCommand.split(" ");
				String[] cmd = new String[sc.length + 3];
				cmd[0] = "sudo";
				cmd[1] = "-u";
				cmd[2] = "s" + id;
				for (int i = 0; i < sc.length; i++) {
					cmd[i + 3] = sc[i];
				}
				pb.command(cmd);
				process = pb.start();
				starttime = System.currentTimeMillis();
				// new ServerOutput(process.getInputStream(), id);
				new CrashDetector(this);
				new Thread(new Runnable() {
					public void run() {
						while (isRunning()) {
							try {
								Thread.sleep(1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						latestOutput.clear();
					}
				}).start();
				new Thread(new Runnable() {
					public void run() {
						BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream()));
						String line;
						try {
							while ((line = br.readLine()) != null) {
								System.out.println("Server " + id + ": " + line);
								addToLatestOutput(line);
							}
						} catch (Exception ignored) {
							// Ignored, only throws error when server is killed
						}
					}
				}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			out.println("Server " + id + " is already online!");
		}
	}

	public void stop() {
		executeCommand(stopCommand);
	}

	public void forceStop() {
		process.destroy();
		latestOutput.clear();
	}

	public void restart() {
		stop();
		new Thread(new Runnable() {
			public void run() {
				while (isRunning()) {
					try {
						Thread.sleep(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				start();
			}
		}).start();
	}

	public boolean executeCommand(String command) {
		if (isRunning()) {
			new PrintWriter(process.getOutputStream(), true).println(command);
			return true;
		}
		return false;
	}

	public boolean isOnline() {
		try {
			Socket socket = SocketFactory.getDefault().createSocket();
			socket.connect(new InetSocketAddress(getHost(), getPort()), 5000);
			socket.close();
			return true;
		} catch (Exception e) {
			return false;
		}
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

	public String getStartupCommand() {
		return Main.sql.getJarStarupCommand(getJarID())
				.replace("%JARPATH", jarFile.getAbsolutePath())
				.replace("%MEMORY", Integer.toString(getMemory()))
				.replace("%IP", getHost())
				.replace("%PORT", Integer.toString(getPort()));
	}

	public InputStream getInputStream() {
		if (process != null)
			return process.getInputStream();
		return null;
	}

	public ArrayList<String> getLatestOutput() {
		return latestOutput;
	}

	public void addToLatestOutput(String line) {
		latestOutputTime = System.currentTimeMillis();
		if (latestOutput.size() <= 100)
			latestOutput.add(line);
		else {
			ArrayList<String> temp = new ArrayList<String>();
			for (int i = 0; i < latestOutput.size(); i++) {
				if (i != latestOutput.size() - 1)
					temp.add(latestOutput.get(i + 1));
				else
					temp.add(line);
			}
			latestOutput.clear();
			latestOutput = temp;
		}
	}

	public long getLatestOutputTime() {
		return latestOutputTime;
	}

	private String getJarName() {
		return Main.sql.getServerJarName(id);
	}

	public int getID() {
		return id;
	}

	public String getHost() {
		return Main.sql.getServerHost(id);
	}

	public int getPort() {
		return Main.sql.getServerPort(id);
	}

	public int getMemory() {
		return Main.sql.getServerMemory(id);
	}

	public int getJarID() {
		return Main.sql.getJarID(getJarName());
	}

	public String getDBStopCommand() {
		return Main.sql.getJarStopCommand(getJarID());
	}
}
