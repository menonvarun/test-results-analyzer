package org.jenkinsci.plugins.testresultsanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class TestResultsAnalyzerExtension extends Publisher {


    private String chartBasedOn = "";
    private boolean useCustomStatusNames=false;
    private String passedRepresentation = "";
    private String failedRepresentation = "";
    private String skippedRepresentation = "";
    private String naRepresentation = "";

    @DataBoundConstructor
    public TestResultsAnalyzerExtension(String chartBasedOn, JSONObject useCustomStatusNames) {
        this.chartBasedOn = chartBasedOn;
        if(useCustomStatusNames != null){
            this.useCustomStatusNames = true;
            passedRepresentation = useCustomStatusNames.getString("passedRepresentation");
            failedRepresentation = useCustomStatusNames.getString("failedRepresentation");
            skippedRepresentation = useCustomStatusNames.getString("skippedRepresentation");
            skippedRepresentation = useCustomStatusNames.getString("skippedRepresentation");
        }

    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        Action a = new TestResultsAnalyzerAction(project, this);
        return Collections.singletonList(a);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public DiscriptorImpl getDescriptor() {
        return (DiscriptorImpl)super.getDescriptor();
    }

    public String getChartBasedOn() {
        if(chartBasedOn.equalsIgnoreCase(""))
            return getDescriptor().getChartBasedOn();
        return chartBasedOn;
    }

    public boolean isUseCustomStatusNames() {
        return useCustomStatusNames;
    }

    public String getPassedRepresentation() {
        if(passedRepresentation.equalsIgnoreCase(""))
            return getDescriptor().getPassedRepresentation();
        return passedRepresentation;
    }

    public String getFailedRepresentation() {
        if(failedRepresentation.equalsIgnoreCase(""))
            return getDescriptor().getFailedRepresentation();
        return failedRepresentation;
    }

    public String getSkippedRepresentation() {
        if(skippedRepresentation.equalsIgnoreCase(""))
            return getDescriptor().getSkippedRepresentation();
        return skippedRepresentation;
    }

    public String getNaRepresentation() {
        if(naRepresentation.equalsIgnoreCase(""))
            return getDescriptor().getNaRepresentation();
        return naRepresentation;
    }

    @Extension
    public static final class DiscriptorImpl extends BuildStepDescriptor<Publisher> {
        private String chartBasedOn = "count";
        private boolean useCustomStatusNames;
        private String passedRepresentation = "PASSED";
        private String failedRepresentation = "FAILED";
        private String skippedRepresentation = "SKIPPED";
        private String naRepresentation = "N/A";

        public DiscriptorImpl() {
            super(TestResultsAnalyzerExtension.class);
            load();
        }

        @Override
        public boolean isApplicable(Class jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Use Test Results Analyzer";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            chartBasedOn = json.getString("chartBasedOn");
            useCustomStatusNames = json.has("useCustomStatusNames");
            if (useCustomStatusNames) {
                JSONObject customStatusNames = json.getJSONObject("useCustomStatusNames");
                passedRepresentation = customStatusNames.getString("passedRepresentation");
                failedRepresentation = customStatusNames.getString("failedRepresentation");
                skippedRepresentation = customStatusNames.getString("skippedRepresentation");
                skippedRepresentation = customStatusNames.getString("skippedRepresentation");
            }
            save();
            return true;
        }

        public String getChartBasedOn() {
            return chartBasedOn;
        }

        public void setChartBasedOn(String chartBasedOn) {
            this.chartBasedOn = chartBasedOn;
        }

        public boolean isUseCustomStatusNames() {
            return useCustomStatusNames;
        }

        public void setUseCustomStatusNames(boolean useCustomStatusNames) {
            this.useCustomStatusNames = useCustomStatusNames;
        }

        public String getPassedRepresentation() {
            return passedRepresentation;
        }

        public void setPassedRepresentation(String passedRepresentation) {
            this.passedRepresentation = passedRepresentation;
        }

        public String getFailedRepresentation() {
            return failedRepresentation;
        }

        public void setFailedRepresentation(String failedRepresentation) {
            this.failedRepresentation = failedRepresentation;
        }

        public String getSkippedRepresentation() {
            return skippedRepresentation;
        }

        public void setSkippedRepresentation(String skippedRepresentation) {
            this.skippedRepresentation = skippedRepresentation;
        }

        public String getNaRepresentation() {
            return naRepresentation;
        }

        public void setNaRepresentation(String naRepresentation) {
            this.naRepresentation = naRepresentation;
        }

        public FormValidation doCheckPassedRepresentation(@QueryParameter String passedRepresentation){
            return valueValidation(passedRepresentation);
        }

        public FormValidation doCheckFailedRepresentation(@QueryParameter String failedRepresentation){
            return valueValidation(failedRepresentation);
        }

        public FormValidation doCheckSkippedRepresentation(@QueryParameter String skippedRepresentation){
            return valueValidation(skippedRepresentation);
        }

        public FormValidation doCheckNaRepresentation(@QueryParameter String naRepresentation){
            return valueValidation(naRepresentation);
        }

        private FormValidation valueValidation(String value) {
            if(value == "")
                return FormValidation.error("Entered value should not be empty");
            Pattern regex = Pattern.compile("[<>{}*\\\"\'$&+,:;=?@#|]");
            Matcher matcher = regex.matcher(value);
            if(matcher.find())
                return FormValidation.error("Entered value should not have special characters.");
            return FormValidation.ok();
        }
    }
}
