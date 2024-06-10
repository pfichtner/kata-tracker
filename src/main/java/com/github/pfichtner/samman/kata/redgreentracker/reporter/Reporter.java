package com.github.pfichtner.samman.kata.redgreentracker.reporter;

import com.github.pfichtner.samman.kata.redgreentracker.RedGreenResult;

public interface Reporter extends AutoCloseable {

	void report(RedGreenResult result) throws Exception;

}
