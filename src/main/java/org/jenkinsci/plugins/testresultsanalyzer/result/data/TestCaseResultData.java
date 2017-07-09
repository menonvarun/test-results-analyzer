package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import hudson.tasks.test.TestResult;
import java.lang.reflect.Method;

public class TestCaseResultData extends ResultData {

  public TestCaseResultData(TestResult testResult, String url) {
    this.name = testResult.getName();
    boolean doTestNg = testResult.getClass().getName()
        .equals("hudson.plugins.testng.results.MethodResult");
    if (doTestNg) {
      try {
        Method method = testResult.getClass().getMethod("getStatus");
        Object returnValue = method.invoke(testResult);
        if (returnValue instanceof String) {
          String status = ((String) returnValue).toLowerCase();
          this.isPassed = status.startsWith("pass");
          this.isSkipped = status.startsWith("skip");
          this.totalTests = 1;
          this.totalFailed = status.startsWith("fail") ? 1 : 0;
          this.totalPassed = status.startsWith("pass") ? 1 : 0;
          this.totalSkipped = status.startsWith("skip") ? 1 : 0;
        }
      } catch (Exception e) {
        // fallback to non testng code
        doTestNg = false;
      }
    }
    if (!doTestNg) {
      this.isPassed = testResult.isPassed();
      this.isSkipped = testResult.getSkipCount() == testResult.getTotalCount();
      this.totalTests = testResult.getTotalCount();
      this.totalFailed = testResult.getFailCount();
      this.totalPassed = testResult.getPassCount();
      this.totalSkipped = testResult.getSkipCount();
    }
    this.totalTimeTaken = testResult.getDuration();
    this.url = url;
    evaluateStatus();
    if ("FAILED".equalsIgnoreCase(getStatus())) {
      this.failureMessage = testResult.getErrorStackTrace();
    }
  }

}
