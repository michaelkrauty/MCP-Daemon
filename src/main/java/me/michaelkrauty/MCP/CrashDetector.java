package me.michaelkrauty.MCP;

import java.io.PrintStream;

/**
 * Created on 10/18/2014.
 *
 * @author michaelkrauty
 */
public class CrashDetector implements Runnable {

	private Server server;
	private Thread t;

	public CrashDetector(Server serverid) {
		if (t == null) {
			this.server = serverid;
			t = new Thread(this);
			t.start();
		}
	}

	public void run() {
		PrintStream out = System.out;
		try {
			// Pause for one second while the server initializes
			t.sleep(1000);

			int check = 10;
			int strike = 0;
			long lastCycle = 0;
			while (server.isRunning()) {
				if (lastCycle == server.getLatestOutputTime()) {
					if (strike > 2) {
						out.println("Server " + server.getID() + " is not responding, forcing restart...");
						server.forceStop();
						t.sleep(1000);
						server.start();
						strike = 0;
					}
					if (strike == 2) {
						strike++;
						server.executeCommand("list");
					}
					if (strike == 1) {
						strike++;
					}
					if (strike == 0) {
						strike++;
					}
				} else {
					strike = 0;
					lastCycle = server.getLatestOutputTime();
				}
				try {
					t.sleep(check * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
