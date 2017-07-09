package org.jenkinsci.plugins.testresultsanalyzer;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ClassInfo;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.PackageInfo;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.TestCaseInfo;

public class TestResultsAnalyzerExporter {

  public static String exportToCsv(ResultInfo resultInfo, List<Integer> builds,
      boolean isTimeBased) {

    Map<String, PackageInfo> packageResults = resultInfo.getPackageResults();
    String buildsString = "";
    for (Integer build : builds) {
      buildsString += ",\"" + build + "\"";
    }
    String header = "\"Package\",\"Class\",\"Test\"";
    header += buildsString;

    StringBuilder exportBuilder = new StringBuilder();
    exportBuilder
        .append(header)
        .append(System.lineSeparator());
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    decimalFormat.setRoundingMode(RoundingMode.CEILING);

    for (PackageInfo packageInfo : packageResults.values()) {
      String packageName = packageInfo.getName();
      //loop the classes
      for (ClassInfo classInfo : packageInfo.getClasses().values()) {
        String className = classInfo.getName();
        //loop the tests
        for (TestCaseInfo testCaseInfo : classInfo.getTests().values()) {
          String testName = testCaseInfo.getName();
          exportBuilder.append("\"")
              .append(packageName)
              .append("\",\"")
              .append(className)
              .append("\",\"")
              .append(testName)
              .append("\"");

          Map<Integer, ResultData> buildPackageResults = testCaseInfo.getBuildPackageResults();

          for (Integer buildNumber : builds) {
            String data = getCustomStatus("NA");
            if (buildPackageResults.containsKey(buildNumber)) {
              ResultData buildResult = buildPackageResults.get(buildNumber);
              data = isTimeBased
                  ? decimalFormat.format(buildResult.getTotalTimeTaken())
                  : getCustomStatus(buildResult.getStatus());

            }
            exportBuilder.append(",\"").append(data).append("\"");
          }
          exportBuilder.append(System.lineSeparator());
        }
      }
    }
    return exportBuilder.toString();
  }

  private static String getCustomStatus(String status) {
    ResultStatus resultStatus;
    try {
      resultStatus = ResultStatus.valueOf(status);
    } catch (IllegalArgumentException e) {
      // Status not recognized, we will return it as is
      return status;
    }

    switch (resultStatus) {
      case PASSED:
        return getPassedRepresentation();
      case FAILED:
        return getFailedRepresentation();
      case SKIPPED:
        return getSkippedRepresentation();
      case NA:
        return getNaRepresentation();
      default:
        return resultStatus.toString();
    }
  }

  public static String getPassedRepresentation() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getPassedRepresentation();
  }

  public static String getFailedRepresentation() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getFailedRepresentation();
  }

  public static String getSkippedRepresentation() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getSkippedRepresentation();
  }

  public static String getNaRepresentation() {
    return TestResultsAnalyzerExtension.DESCRIPTOR.getNaRepresentation();
  }
}
