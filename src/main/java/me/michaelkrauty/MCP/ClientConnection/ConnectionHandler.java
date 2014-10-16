package me.michaelkrauty.MCP.ClientConnection;

import me.michaelkrauty.MCP.Main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

	private final static PrintStream out = System.out;
	private static ServerSocket serverSocket;
	private Thread t;

	public ConnectionHandler() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(Main.daemonIP, Main.daemonPort));
			out.println("Daemon listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
		} catch (IOException e) {
			out.println("Couldn't bind to " + Main.daemonIP + ":" + Main.daemonPort + "!");
			out.println(e.getMessage());
		}
	}

	public void run() {
		out.println("Daemon ready to accept connections");
		while (Main.running) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (Exception e) {
				out.println("Couldn't accept client connection!");
				out.println(e.getMessage());
			}
			new ClientConnection(clientSocket).start();
		}
	}

	public void start() {
		if (t == null) {
			out.println("Starting Connection Handler");
			t = new Thread(this);
			t.start();
		}
	}
}