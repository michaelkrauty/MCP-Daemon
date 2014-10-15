package me.michaelkrauty.MCP;

import java.io.File;
import java.util.ArrayList;
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

	public String createServer(int serverid) {
		File serverdir = new File(Main.serverdir, Integer.toString(serverid));
		if (!serverdir.exists()) {
			try {
				String password = UUID.randomUUID().toString().replace("-", "");
				Runtime.getRuntime().exec(new String[] {"sudo", "useradd", "s" + serverid, "-p", password, "-d", serverdir.getAbsolutePath(), "-m"});
				return password;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}
