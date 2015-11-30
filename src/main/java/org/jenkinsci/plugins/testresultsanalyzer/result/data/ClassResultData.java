package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import hudson.tasks.test.TabulatedResult;

public class ClassResultData extends ResultData {

	public ClassResultData(TabulatedResult classResult) {

		setName(classResult.getName());
		setPassed(classResult.getFailCount() == 0);
		setSkipped(classResult.getSkipCount() == classResult.getTotalCount());
		setTotalTests(classResult.getTotalCount());
		setTotalFailed(classResult.getFailCount());
		setTotalPassed(classResult.getPassCount());
		setTotalSkipped(classResult.getSkipCount());
		setTotalTimeTaken(classResult.getDuration());
		evaluateStatus();
	}

}
