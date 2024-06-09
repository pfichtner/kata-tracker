package com.github.pfichtner.samman.kata.io;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public final class Sockets {
	
	private Sockets() {
		super();
	}

	public static boolean portIsAvailable(int port) {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			serverSocket.setReuseAddress(true);
			try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
				datagramSocket.setReuseAddress(true);
			}
			return true;
		} catch (IOException e) {
		}
		return false;
	}

}
