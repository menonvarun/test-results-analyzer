package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import net.sf.json.JSONObject;

/**
 * Created by menonvarun on 19/7/15.
 */
public class BuildData {
    private int buildNumber = 0;
    private long duration = 0;
    private long startTime = 0;
    private long scheduledTime = 0;

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildNumber", buildNumber);
        jsonObject.put("startTime", startTime);
        jsonObject.put("scheduledTime", scheduledTime);
        jsonObject.put("duration", duration);
        return jsonObject;
    }
}
