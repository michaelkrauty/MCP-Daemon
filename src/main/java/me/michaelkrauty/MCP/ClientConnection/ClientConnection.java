package me.michaelkrauty.MCP.ClientConnection;

import java.net.Socket;
import java.util.logging.Logger;

class ClientConnection {

	private final static Logger log = Logger.getLogger("MCP");

	public ClientConnection(Socket socket) {
		log.info("Connection from "
				+ socket.getRemoteSocketAddress().toString());
		new ClientInput(socket).start();
	}
}