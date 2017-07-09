package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestResult;
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ClassResultData;

public class ClassInfo extends Info {

  private Map<String, TestCaseInfo> tests = new TreeMap<>();

  public void putBuildClassResult(Integer buildNumber, TabulatedResult classResult, String url) {
    ClassResultData classResultData = new ClassResultData(classResult, url);

    evaluateStatuses(classResult);
    addTests(buildNumber, classResult, url);
    buildResults.put(buildNumber, classResultData);
  }

  public Map<String, TestCaseInfo> getTests() {
    return tests;
  }

  private void addTests(Integer buildNumber, TabulatedResult classResult, String url) {
    for (TestResult testCaseResult : classResult.getChildren()) {

      String testCaseName = testCaseResult.getName();
      TestCaseInfo testCaseInfo;
      if (tests.containsKey(testCaseName)) {
        testCaseInfo = tests.get(testCaseName);
      } else {
        testCaseInfo = new TestCaseInfo();
        testCaseInfo.setName(testCaseName);
      }

      testCaseInfo
          .putTestCaseResult(buildNumber, testCaseResult, url + "/" + testCaseResult.getSafeName());
      tests.put(testCaseName, testCaseInfo);
    }
  }

  @Override
  protected JSONObject getChildrenJson() {
    JSONObject json = new JSONObject();
    for (String testName : tests.keySet()) {
      json.put(testName, tests.get(testName).getJsonObject());
    }
    return json;
  }
}
