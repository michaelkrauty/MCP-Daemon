package me.michaelkrauty.MCP;

import java.util.ArrayList;

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
}
