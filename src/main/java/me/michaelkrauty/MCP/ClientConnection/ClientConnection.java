package me.michaelkrauty.MCP.ClientConnection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.michaelkrauty.MCP.Main;
import me.michaelkrauty.MCP.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientConnection implements Runnable {

	private final static PrintStream out = System.out;

	private Thread t;
	private final Socket socket;

	public ClientConnection(Socket soc) {
		out.println("Connection from " + soc.getRemoteSocketAddress().toString());
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
			String input = in.readLine();
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			JsonObject json = new Gson().fromJson(input, JsonElement.class).getAsJsonObject();

			out.println(socket.getRemoteSocketAddress().toString() + ": " + input);

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
							output.print(new Gson().toJson(line));
						}
					}
				} else
					output.print(handleInput(action, command, serverid));
			} else {
				output.print("Incorrect pass");
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
		if (serverid > 0) {
			if (action.equalsIgnoreCase("Start")) {
				Main.serverManager.startServer(serverid);
				return gson.toJson(true);
			} else if (action.equalsIgnoreCase("Stop")) {
				Main.serverManager.stopServer(serverid);
				return gson.toJson(true);
			} else if (action.equalsIgnoreCase("Kill")) {
				Main.serverManager.getServer(serverid).forceStop();
				return gson.toJson(true);
			} else if (action.equalsIgnoreCase("Command")) {
				if (!command.isEmpty())
					return gson.toJson(Main.serverManager.getServer(serverid).executeCommand(command));
			} else if (action.equalsIgnoreCase("CreateServer")) {
				if (!command.isEmpty())
					return gson.toJson(Main.serverManager.createServer(serverid, command));
			}
		}
		if (action.equalsIgnoreCase("ListOnlineServers")) {
			return gson.toJson(Main.serverManager.listOnlineServers());
		}
		return gson.toJson("Error");
	}
}