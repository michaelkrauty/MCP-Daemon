package me.michaelkrauty.MCP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by michael on 10/14/14.
 */
public class ServerManager {

	private ArrayList<Server> servers = new ArrayList<Server>();

	public ServerManager() {

	}

	public ArrayList<Server> getServers() {
		return servers;
	}

	public Server getServer(int serverid) {
		for (Server server : servers) {
			if (server.getID() == serverid) {
				return server;
			}
		}
		return null;
	}

	public void startServer(int serverid) {
		Server server;
		if ((server = getServer(serverid)) != null)
			server.start();
		else {
			server = new Server(serverid);
			server.start();
			servers.add(server);
		}
	}

	public boolean stopServer(int serverid) {
		Server server;
		if ((server = getServer(serverid)) != null) {
			server.stop();
			return true;
		}
		return false;
	}

	public boolean createServer(int serverid, String password) {
		File serverdir = new File(Main.serverdir, Integer.toString(serverid));
		if (!serverdir.exists()) {
			try {
				Runtime.getRuntime().exec(new String[] {"sudo", "useradd", "s" + serverid, "-p", password, "-d", serverdir.getAbsolutePath(), "-m"});
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public List<Integer> listOnlineServers() {
		List<Integer> online = new ArrayList<Integer>();
		for (Server server : servers) {
			if (server.isRunning())
				online.add(server.getID());
		}
		return online;
	}
}
