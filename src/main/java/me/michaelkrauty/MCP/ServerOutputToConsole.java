package me.michaelkrauty.MCP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Created by michael on 10/15/14.
 */
public class ServerOutputToConsole implements Runnable {

	private InputStream inputStream;
	private int serverid;
	private Logger log = Logger.getLogger("MCP");
	private Thread t;

	public ServerOutputToConsole(InputStream inputStream, int serverid) {
		if (t == null) {
			this.inputStream = inputStream;
			this.serverid = serverid;
			t = new Thread(this);
			t.start();
		}
	}

	public void run() {
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			while ((line = reader.readLine()) != null) {
				log.info("Server " + serverid + ": " + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
