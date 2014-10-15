package me.michaelkrauty.MCP.ClientConnection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.michaelkrauty.MCP.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientInput implements Runnable {

	private final static Logger log = Logger.getLogger("MCP");

	private Thread t;
	private final Socket socket;

	public ClientInput(Socket soc) {
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
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
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
				// TODO: handle input now that request is authenticated, then output results
				out.print(true);
			} else {
				out.print(false);
			}
			log.info(socket.getRemoteSocketAddress().toString() + ": " + json);
			out.close();
			in.close();

		} catch (IOException e) {
			log.log(Level.SEVERE, socket.getRemoteSocketAddress().toString()
					+ " disconnected: " + e.getMessage());
		}
	}

}
