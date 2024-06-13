package com.github.pfichtner.samman.kata.redgreentracker.consumer;

import static com.github.pfichtner.samman.kata.redgreentracker.reporter.FileReporter.getOrCreateDirectory;
import static java.lang.Integer.parseInt;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;

import com.github.pfichtner.samman.kata.chart.Period;

public class FileResultConsumer implements ResultConsumer {

	private final WatchService watchService;
	private Listener listener = Listener.NULL;

	public FileResultConsumer() throws Exception {
		watchService = FileSystems.getDefault().newWatchService();
		Executors.newSingleThreadExecutor().submit(() -> {
			try {
				getOrCreateDirectory().register(watchService, ENTRY_CREATE);
				WatchKey key;
				while ((key = watchService.take()) != null) {
					for (WatchEvent<?> event : key.pollEvents()) {
						String filename = event.context().toString();
						int dash = filename.indexOf('-');
						String suffix = filename.substring(dash + 1);
						int dot = suffix.indexOf('.');
						Period nextPeriod = new Period(parseInt(filename.substring(0, dash)), suffix.substring(0, dot));
						System.out.println("+++ " + nextPeriod);
						listener.received(nextPeriod);
					}
					key.reset();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener == null ? Listener.NULL : listener;
	}

	@Override
	public void close() throws Exception {
		// we should not close the watchservice, right?
//		watchService.close();
	}

}
