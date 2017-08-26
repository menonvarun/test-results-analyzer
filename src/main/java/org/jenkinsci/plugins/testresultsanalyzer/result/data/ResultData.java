package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestObject;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class ResultData {

  protected String name;
  protected boolean isPassed;
  protected boolean isSkipped;
  protected transient TabulatedResult packageResult;
  protected int totalTests;
  protected int totalFailed;
  protected int totalPassed;
  protected int totalSkipped;
  protected List<ResultData> children = new ArrayList<>();
  protected float totalTimeTaken;
  protected String status;
  protected String failureMessage = "";
  protected String url;

  //Used for constructing mock object
  public ResultData() {

  }

  public ResultData(TestObject result, String url) {
    this.name = result.getName();
    this.isPassed = result.getFailCount() == 0;
    this.isSkipped = result.getSkipCount() == result.getTotalCount();
    this.totalTests = result.getTotalCount();
    this.totalFailed = result.getFailCount();
    this.totalPassed = result.getPassCount();
    this.totalSkipped = result.getSkipCount();
    this.totalTimeTaken = result.getDuration();
    this.url = url;
    evaluateStatus();
  }

  public String getFailureMessage() {
    return failureMessage;
  }

  public String getName() {
    return name;
  }

  public boolean isPassed() {
    return isPassed;
  }

  public boolean isSkipped() {
    return isSkipped;
  }

  public TabulatedResult getPackageResult() {
    return packageResult;
  }

  public int getTotalTests() {
    return totalTests;
  }

  public int getTotalFailed() {
    return totalFailed;
  }

  public int getTotalPassed() {
    return totalPassed;
  }

  public int getTotalSkipped() {
    return totalSkipped;
  }

  public List<ResultData> getChildren() {
    return children;
  }

  public float getTotalTimeTaken() {
    return totalTimeTaken;
  }

  public String getUrl() {
    return url;
  }

  public String getStatus() {
    return status;
  }

  protected final void evaluateStatus() {
    if (isPassed) {
      status = "PASSED";
    } else if (isSkipped) {
      status = "SKIPPED";
    } else {
      status = "FAILED";
    }
  }



  public JSONObject getJsonObject() {
    JSONObject json = new JSONObject();
    json.put("name", name);
    json.put("totalTests", totalTests);
    json.put("totalFailed", totalFailed);
    json.put("totalPassed", totalPassed);
    json.put("totalSkipped", totalSkipped);
    json.put("isPassed", isPassed);
    json.put("isSkipped", isSkipped);
    json.put("totalTimeTaken", totalTimeTaken);
    json.put("status", status);
    json.put("url", url);
    JSONArray testsChildren = new JSONArray();
    for (ResultData childResult : children) {
      testsChildren.add(childResult.getJsonObject());
    }
    if (!(failureMessage.equalsIgnoreCase(""))) {
      json.put("failureMessage", failureMessage);
    }
    json.put("children", testsChildren);
    return json;
  }

}
