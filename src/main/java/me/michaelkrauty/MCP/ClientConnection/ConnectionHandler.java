package me.michaelkrauty.MCP.ClientConnection;

import me.michaelkrauty.MCP.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

	private final static Logger log = Logger.getLogger("MCP");
	private static ServerSocket serverSocket;
	private Thread t;

	public ConnectionHandler() {
		try {
			serverSocket = new ServerSocket(Main.daemonPort);
			log.info("Server socket created on port " + Main.daemonPort + ".");
		} catch (IOException e) {
			log.log(Level.SEVERE, "Couldn't bind to port " + Main.daemonPort + "!");
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	public void run() {
		while (Main.running) {
			Socket clientSocket = null;
			try {
				log.info("Waiting for connections...");
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Couldn't accept client connection!");
				log.log(Level.SEVERE, e.getMessage());
			}
			new ClientConnection(clientSocket);
		}
	}

	public void start() {
		log.info("Starting Connection Handler");
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
}