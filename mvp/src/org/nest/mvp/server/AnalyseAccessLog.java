package org.nest.mvp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class AnalyseAccessLog {
	private static final Logger logger = Logger
			.getLogger(AnalyseAccessLog.class);

	private static AnalyseAccessLog aam = new AnalyseAccessLog();

	private AnalyseAccessLog() {

	}

	public static AnalyseAccessLog getInstance() {
		return aam;
	}

	public void writeAnalyseLog(String startDate, long times,
			HttpServletRequest req) {

	}
}
