package me.michaelkrauty.MCP.ClientConnection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.michaelkrauty.MCP.Main;
import me.michaelkrauty.MCP.Server;

import java.io.*;
import java.net.Socket;

class ClientConnection implements Runnable {

	private final static PrintStream out = System.out;

	private Thread t;
	private final Socket socket;

	public ClientConnection(Socket soc) {
		socket = soc;
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			String input = in.readLine();

			// For debug
			// out.println(socket.getRemoteSocketAddress().toString() + ": " + input);

			if (input == null) {
				output.write(new Gson().toJson(false));
				output.close();
				in.close();
				return;
			}
			JsonObject json = new Gson().fromJson(input, JsonElement.class).getAsJsonObject();

			String auth = "";
			int serverid = -1;
			String action = "";
			String command = "";
			if (json.get("auth") != null) auth = json.get("auth").getAsString();
			if (json.get("serverid") != null) serverid = json.get("serverid").getAsInt();
			if (json.get("action") != null) action = json.get("action").getAsString();
			if (json.get("command") != null) command = json.get("command").getAsString();

			if (auth.equals(Main.daemonPass)) {
				if (action.equalsIgnoreCase("GetConsole")) {
					if (Main.serverManager.getServer(serverid) != null) {
						BufferedReader console = new BufferedReader(new InputStreamReader(Main.serverManager.getServer(serverid).getInputStream()));
						String line;
						while ((line = console.readLine()) != null) {
							output.print(new Gson().toJson(line) + "\n");
						}
					}
				} else
					output.print(handleInput(action, command, serverid));
			} else {
				output.print(false);
			}
			output.close();
			in.close();

		} catch (IOException e) {
			out.println(socket.getRemoteSocketAddress().toString()
					+ " disconnected: " + e.getMessage());
		}
	}

	private String handleInput(String action, String command, int serverid) {
		Gson gson = new Gson();
		if (action.equalsIgnoreCase("ListOnlineServers")) {
			return gson.toJson(Main.serverManager.listOnlineServers());
		} else if (serverid > 0) {
			Server server = Main.serverManager.getServer(serverid);

			if (action.equalsIgnoreCase("Start")) {
				Main.serverManager.startServer(serverid);
				return gson.toJson(true);
			} else if (action.equalsIgnoreCase("Stop")) {
				if (server != null) {
					server.stop();
					return gson.toJson(true);
				}
			} else if (action.equalsIgnoreCase("Restart")) {
				if (server != null)
					server.restart();
				else
					Main.serverManager.startServer(serverid);
				return gson.toJson(true);
			} else if (action.equalsIgnoreCase("Kill")) {
				if (server != null) {
					server.forceStop();
					return gson.toJson(true);
				}
			} else if (action.equalsIgnoreCase("GetUptime")) {
				if (server != null)
					return gson.toJson(server.getUptime());
			} else if (action.equalsIgnoreCase("GetLatestOutput")) {
				if (server != null)
					return gson.toJson(server.getLatestOutput());
			} else if (action.equalsIgnoreCase("GetServerStatus")) {
				if (server == null)
					return gson.toJson("offline");
				if (server.isOnline())
					return gson.toJson("online");
				else if (server.isRunning())
					return gson.toJson("running");
				else
					return gson.toJson("offline");
			} else if (action.equalsIgnoreCase("GetOnlinePlayers")) {
				if (server != null)
					return gson.toJson(server.getOnlinePlayers());
			} else if (action.equalsIgnoreCase("getMaxPlayers")) {
				if (server != null)
					return gson.toJson(server.getMaxPlayers());
			} else if (action.equalsIgnoreCase("getMOTD")) {
				if (server != null)
					return gson.toJson(server.getMOTD());
			} else if (action.equalsIgnoreCase("Command")) {
				if (server != null) {
					if (!command.isEmpty())
						return gson.toJson(server.executeCommand(command));
				}
			} else if (action.equalsIgnoreCase("CreateServer")) {
				if (!command.isEmpty())
					return gson.toJson(Main.serverManager.createServer(serverid, command));
			}
		}
		return gson.toJson(false);
	}
}