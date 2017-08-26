package org.jenkinsci.plugins.testresultsanalyzer;


public enum ResultStatus {
  PASSED("PASSED"), FAILED("FAILED"), SKIPPED("SKIPPED"), NA("N/A");

  private final String value;

  ResultStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
