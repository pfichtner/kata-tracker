package com.github.pfichtner.samman.kata.redgreentracker.reporter;

import static java.lang.System.getProperty;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.write;
import static java.time.ZoneOffset.UTC;

import java.io.IOException;
import java.nio.file.Path;

import com.github.pfichtner.samman.kata.redgreentracker.RedGreenResult;

public class FileReporter implements Reporter {

	private final Path directory;

	public FileReporter() throws Exception {
		directory = getOrCreateDirectory();
	}

	public static synchronized Path getOrCreateDirectory() throws IOException {
		Path directory = Path.of(getProperty("java.io.tmpdir"), "redgreen");
		return exists(directory) ? directory : createDirectory(directory);
	}

	@Override
	public void report(RedGreenResult result) throws Exception {
		write(fileFor(result), content(result));
	}

	private Path fileFor(RedGreenResult result) {
		return directory.resolve(String.format("%s-%s.txt", result.timestamp().toEpochSecond(UTC),
				result.hasFailures() ? "red" : "green"));
	}

	private byte[] content(RedGreenResult result) {
		return result.displayName().getBytes();
	}

	@Override
	public void close() throws Exception {
	}

}
