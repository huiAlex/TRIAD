package experiment.preprocess.dronology.entity;

import java.util.Set;

public class DroneArtifact {
    private String issueId;
    private String issueType;
    private String status;
    private String summary;
    private String description;
    private Set<String> childrenSet;
    private String childrenType;
    private Set<Code> codeSet;

    public String getChildrenType() {
        return childrenType;
    }

    public void setChildrenType(String childrenType) {
        this.childrenType = childrenType;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getChildrenSet() {
        return childrenSet;
    }

    public void setChildrenSet(Set<String> childrenSet) {
        this.childrenSet = childrenSet;
    }

    public Set<Code> getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(Set<Code> codeSet) {
        this.codeSet = codeSet;
    }
}

