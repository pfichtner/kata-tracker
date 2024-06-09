package com.github.pfichtner.samman.kata.redgreentracker;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RedGreenTrackerExtension.class)
@Retention(RUNTIME)
public @interface RedGreenTracking {
}
