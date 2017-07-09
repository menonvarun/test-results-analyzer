package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TestResult;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;

public abstract class Info {

  protected String name;
  protected Map<Integer, ResultData> buildResults = new TreeMap<>(
      Collections.<Integer>reverseOrder());

  protected List<String> statuses = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<Integer, ResultData> getBuildPackageResults() {
    return buildResults;
  }

  public void setBuildPackageResults(Map<Integer, ResultData> buildResults) {
    this.buildResults = buildResults;
  }

  protected abstract JSONObject getChildrenJson();

  protected JSONObject getBuildJson() {
    JSONObject json = new JSONObject();
    buildResults.forEach((buildNumber, resultData) -> json.put(buildNumber.toString(), resultData));
    return json;
  }

  public JSONArray getBuildStatuses() {
    JSONArray buildStatuses = new JSONArray();
    buildStatuses.addAll(statuses);
    return buildStatuses;
  }

  public JSONObject getJsonObject() {
    JSONObject json = new JSONObject();
    json.put("name", name);
    json.put("type", "package");
    json.put("buildStatuses", getBuildStatuses());
    json.put("builds", getBuildJson());
    json.put("children", getChildrenJson());
    return json;
  }

  protected void evaluateStatuses(TestResult testResult) {
    boolean doTestNg = testResult.getClass().getName()
        .equals("hudson.plugins.testng.results.MethodResult");
    if (doTestNg) {
      try {
        Method method = testResult.getClass().getMethod("getStatus");
        Object returnValue = method.invoke(testResult);
        if (returnValue instanceof String) {
          String status = ((String) returnValue).toLowerCase();
          if (status.startsWith("fail")) {
            status = "FAILED";
          } else if (status.startsWith("pass")) {
            status = "PASSED";
          } else if (status.startsWith("skip")) {
            status = "SKIPPED";
          }
          if (!(statuses.contains(status))) {
            statuses.add(status);
          }
        }
      } catch (Exception e) {
        // fallback to non testng code
        doTestNg = false;
      }
    }
    if (!doTestNg) {
      List<String> testStatuses = new ArrayList<>();
      if (testResult.getFailCount() > 0) {
        testStatuses.add("FAILED");
      }
      if (testResult.getPassCount() > 0) {
        testStatuses.add("PASSED");
      }
      if (testResult.getSkipCount() > 0) {
        testStatuses.add("SKIPPED");
      }
      for (String testStatus : testStatuses) {
        if (!(statuses.contains(testStatus))) {
          statuses.add(testStatus);
        }
      }
    }
  }
}
