package com.github.pfichtner.samman.kata.swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public final class Windows {

	private Windows() {
		super();
	}

	public static void centerWindow(Window window) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension w = window.getSize();
		int dx = (int) w.getWidth();
		int dy = (int) w.getHeight();
		int x = (int) ((d.getWidth() - dx) / 2.0D);
		int y = (int) ((d.getHeight() - dy) / 2.0D);
		window.setBounds(x, y, dx, dy + 1);
	}

	

}
