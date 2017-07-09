package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestResult;
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.PackageResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;

public class PackageInfo extends Info {

  protected Map<String, ClassInfo> classesByName = new TreeMap<>();

  public void putPackageResult(Integer buildNumber, TabulatedResult packageResult, String url) {
    PackageResultData packageResultData = new PackageResultData(packageResult, url);
    evaluateStatuses(packageResult);

    addClasses(buildNumber, packageResult, url);
    buildResults.put(buildNumber, packageResultData);
  }

  public ResultData getPackageResult(Integer buildNumber) {
    return buildResults.get(buildNumber); // may return null if buildNumber is missing
  }

  public Map<String, ClassInfo> getClassesByName() {
    return classesByName;
  }

  public void addClasses(Integer buildNumber, TabulatedResult packageResult, String url) {
    for (TestResult classResult : packageResult.getChildren()) {
      String className = classResult.getName();
      ClassInfo classInfo;
      if (classesByName.containsKey(className)) {
        classInfo = classesByName.get(className);
      } else {
        classInfo = new ClassInfo();
        classInfo.setName(className);
      }
      classInfo.putBuildClassResult(buildNumber, (TabulatedResult) classResult,
          url + "/" + classResult.getSafeName());
      classesByName.put(className, classInfo);
    }
  }

  @Override
  protected JSONObject getChildrenJson() {
    JSONObject json = new JSONObject();
    classesByName.forEach((name, clazz) -> json.put(name, clazz.getJsonObject()));
    return json;
  }

}
