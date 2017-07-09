package org.jenkinsci.plugins.testresultsanalyzer;

import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestResult;
import hudson.util.RunList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResultsAnalyzerAction extends Actionable implements Action {

  private final static Logger log = LoggerFactory.getLogger(TestResultsAnalyzerAction.class);

  @SuppressWarnings("rawtypes")
  private final Job project;
  private ResultInfo resultInfo;
  private List<Integer> builds = new ArrayList<>();

  public TestResultsAnalyzerAction(@SuppressWarnings("rawtypes") Job project) {
    this.project = project;
  }

  public final String getDisplayName() {
    return canRead() ? Constants.NAME : null;
  }

  public final String getIconFileName() {
    return canRead() ? Constants.ICONFILENAME : null;
  }

  public String getUrlName() {
    return canRead() ? Constants.URL : null;
  }

  public String getSearchUrl() {
    return canRead() ? Constants.URL : null;
  }

  private boolean canRead() {
    return project.hasPermission(Item.READ);
  }

  @SuppressWarnings("rawtypes")
  public Job getProject() {
    return project;
  }

  @JavaScriptMethod
  public JSONArray getNoOfBuilds(String noOfbuildsNeeded) {
    int noOfBuilds = parseNumberOfBuilds(noOfbuildsNeeded);
    return getBuildsArray(getBuilds(noOfBuilds));
  }

  @JavaScriptMethod
  public JSONObject getTreeResult(String noOfBuildsNeeded) {
    int noOfBuilds = parseNumberOfBuilds(noOfBuildsNeeded);
    List<Integer> buildList = getBuilds(noOfBuilds);

    JsTreeUtil jsTreeUtils = new JsTreeUtil();
    return jsTreeUtils.getJsTree(buildList, resultInfo);
  }

  @JavaScriptMethod
  public String getExportCsv(String isTimeBased, String noOfBuildsNeeded) {
    int noOfBuilds = parseNumberOfBuilds(noOfBuildsNeeded);
    List<Integer> buildList = getBuilds(noOfBuilds);

    return TestResultsAnalyzerExporter.exportToCsv(
        resultInfo,
        buildList,
        Boolean.parseBoolean(isTimeBased)
    );
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void getJsonLoadData() {
    if (!isUpdated()) {
      return;
    }

    resultInfo = new ResultInfo();
    builds = new ArrayList<>();

    RunList<Run> runs = project.getBuilds();
    for (Run run : runs) {
      if (!run.isBuilding()) {
        updateBuildsAndResultInfo(run, builds, resultInfo);
      }
    }
  }

  private JSONArray getBuildsArray(List<Integer> buildList) {
    JSONArray jsonArray = new JSONArray();
    for (Integer build : buildList) {
      jsonArray.add(build);
    }
    return jsonArray;
  }

  private List<Integer> getBuilds(int noOfBuilds) {
    if ((noOfBuilds <= 0) || (noOfBuilds >= builds.size())) {
      return builds;
    }
    return builds.subList(0, noOfBuilds);
  }

  private int parseNumberOfBuilds(String noOfBuildsNeeded) {
    try {
      return Integer.parseInt(noOfBuildsNeeded);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private boolean isUpdated() {
    int latestBuildNumber = project.getLastBuild().getNumber();
    return !(builds.contains(latestBuildNumber));
  }

  private void updateBuildsAndResultInfo(Run run, List<Integer> builds, ResultInfo resultInfo) {

    int buildNumber = run.getNumber();
    String buildUrl = project.getBuildByNumber(buildNumber).getUrl();
    builds.add(run.getNumber());

    List<AbstractTestResultAction> testActions = run.getActions(AbstractTestResultAction.class);
    for (AbstractTestResultAction testAction : testActions) {

      try {

        TabulatedResult testResult = (TabulatedResult) testAction.getResult();
        Collection<? extends TestResult> packageResults = testResult.getChildren();
        for (TestResult packageResult : packageResults) { // packageresult
          resultInfo.addPackage(buildNumber, (TabulatedResult) packageResult,
              Jenkins.getInstance().getRootUrl() + buildUrl);
        }

      } catch (ClassCastException ignore) {
        log.info("Got ClassCastException while converting results to TabulatedResult from action: "
                + "{}. Ignoring as we only want test results for processing.",
            testAction.getClass().getName());
      }
    }
  }

  public static String getNoOfBuilds() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getNoOfBuilds();
  }

  public boolean getShowAllBuilds() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getShowAllBuilds();
  }

  public boolean getShowLineGraph() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getShowLineGraph();
  }

  public boolean getShowBarGraph() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getShowBarGraph();
  }

  public boolean getShowPieGraph() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getShowPieGraph();
  }

  public boolean getShowBuildTime() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getShowBuildTime();
  }

  public String getChartDataType() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getChartDataType();
  }

  public String getRunTimeLowThreshold() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getRunTimeLowThreshold();
  }

  public String getRunTimeHighThreshold() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getRunTimeHighThreshold();
  }

  public boolean isUseCustomStatusNames() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.isUseCustomStatusNames();
  }


  public String getPassedColor() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getPassedColor();
  }

  public String getFailedColor() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getFailedColor();
  }

  public String getSkippedColor() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getSkippedColor();
  }

  public String getNaColor() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getNaColor();
  }
}
