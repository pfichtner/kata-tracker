package com.github.pfichtner.samman.kata.redgreentracker;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import com.github.pfichtner.samman.kata.redgreentracker.reporter.MqttReporter;
import com.github.pfichtner.samman.kata.redgreentracker.reporter.Reporter;

public class RedGreenTrackerExtension implements AfterAllCallback, TestExecutionExceptionHandler {

	private final CompletableFuture<Reporter> completableFuture = new CompletableFuture<>();
	private boolean failures;

	public RedGreenTrackerExtension() {
		newSingleThreadExecutor().submit(() -> {
			try {
				completableFuture.complete(new MqttReporter());
			} catch (Exception e) {
				completableFuture.completeExceptionally(e);
			}
		});
	}

	@Override
	public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
		failures = true;
		throw throwable;
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		var result = new RedGreenResult(now()).displayName(context.getDisplayName()).hasFailures(failures);
		// TODO JUnit seems to kill threads, shutdownHooks seems to work (most of the
		// time?)
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try (Reporter reporter = completableFuture.get()) {
				reporter.report(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
	}

}
