package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TestResult;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.TestCaseResultData;

public class TestCaseInfo extends Info {

  public void putTestCaseResult(Integer buildNumber, TestResult testCaseResult, String url) {
    TestCaseResultData testCaseResultData = new TestCaseResultData(testCaseResult, url);
    evaluateStatuses(testCaseResult);
    buildResults.put(buildNumber, testCaseResultData);
  }

  @Override
  protected JSONObject getChildrenJson() {
    return new JSONObject();
  }
}
