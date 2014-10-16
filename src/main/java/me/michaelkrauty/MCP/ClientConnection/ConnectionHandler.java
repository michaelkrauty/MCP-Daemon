package me.michaelkrauty.MCP.ClientConnection;

import me.michaelkrauty.MCP.Main;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

	private final static Logger log = Logger.getLogger("MCP");
	private static ServerSocket serverSocket;
	private Thread t;

	public ConnectionHandler() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(Main.daemonIP, Main.daemonPort));
			log.info("Daemon listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
		} catch (IOException e) {
			log.log(Level.SEVERE, "Couldn't bind to " + Main.daemonIP + ":" + Main.daemonPort + "!");
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	public void run() {
		log.info("Daemon ready to accept connections");
		while (Main.running) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Couldn't accept client connection!");
				log.log(Level.SEVERE, e.getMessage());
			}
			new ClientConnection(clientSocket).start();
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