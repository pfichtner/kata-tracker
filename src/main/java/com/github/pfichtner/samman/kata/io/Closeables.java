package com.github.pfichtner.samman.kata.io;

public final class Closeables {

	private Closeables() {
		super();
	}

	public static void closeQuiety(AutoCloseable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
