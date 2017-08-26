package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TabulatedResult;
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONObject;

public class ResultInfo {

  private Map<String, PackageInfo> packageResultsByName = new TreeMap<>();

  public void addPackage(Integer buildNumber, TabulatedResult packageResult, String url) {
    String packageName = packageResult.getName();
    PackageInfo packageInfo;
    if (packageResultsByName.containsKey(packageName)) {
      packageInfo = packageResultsByName.get(packageName);
    } else {
      packageInfo = new PackageInfo();
      packageInfo.setName(packageName);
    }
    packageInfo.putPackageResult(buildNumber, packageResult,
        url + getResultUrl(packageResult) + "/" + packageResult.getSafeName());
    packageResultsByName.put(packageName, packageInfo);
  }

  public JSONObject getJsonObject() {
    JSONObject json = new JSONObject();
    packageResultsByName.forEach((name, result) -> json.put(name, result.getJsonObject()));
    return json;
  }

  public Map<String, PackageInfo> getPackageResultsByName() {
    return packageResultsByName;
  }

  protected String getResultUrl(TabulatedResult result) {
    boolean isTestng = result.getClass().getName().startsWith("hudson.plugins.testng.results");
    return isTestng ? "testngreports" : "testReport";
  }
}
