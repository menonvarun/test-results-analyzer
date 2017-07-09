package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestResult;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ClassResultData;

public class ClassInfo extends Info {

  private Map<String, TestCaseInfo> testsByName = new TreeMap<>();

  public void putBuildClassResult(Integer buildNumber, TabulatedResult classResult, String url) {
    ClassResultData classResultData = new ClassResultData(classResult, url);

    evaluateStatuses(classResult);
    addTests(buildNumber, classResult, url);
    buildResults.put(buildNumber, classResultData);
  }

  public Map<String, TestCaseInfo> getTestsByName() {
    return testsByName;
  }

  private void addTests(Integer buildNumber, TabulatedResult classResult, String url) {
    for (TestResult testCaseResult : classResult.getChildren()) {

      String testCaseName = testCaseResult.getName();
      TestCaseInfo testCaseInfo;
      if (testsByName.containsKey(testCaseName)) {
        testCaseInfo = testsByName.get(testCaseName);
      } else {
        testCaseInfo = new TestCaseInfo();
        testCaseInfo.setName(testCaseName);
      }

      testCaseInfo
          .putTestCaseResult(buildNumber, testCaseResult, url + "/" + testCaseResult.getSafeName());
      testsByName.put(testCaseName, testCaseInfo);
    }
  }

  @Override
  protected JSONObject getChildrenJson() {
    JSONObject json = new JSONObject();

    testsByName.forEach((name, test) -> json.put(name, test.getJsonObject()));

    return json;
  }
}
