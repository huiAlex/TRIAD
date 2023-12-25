package experiment.project;


import experiment.enums.ArtifactLevelEnum;

public interface Project {

    public String getProcessedLevel1ArtifactPath();
    public String getProcessedLevel2ArtifactPath();
    public String getProcessedLevel3ArtifactPath();

    public String getUnprocessedLevel1ArtifactPath();
    public String getUnprocessedLevel2ArtifactPath();
    public String getUnprocessedLevel3ArtifactPath();

    public String getLevel1ToLevel2MatricPath();
    public String getLevel2ToLevel3MatricPath();
    public String getLevel1ToLevel3MatricPath();

    public String getLevel1BitermPath();
    public String getLevel2BitermPath();
    public String getLevel3BitermPath();

    public String getArtifactPath (ArtifactLevelEnum artifactLevelEnum);

    public String getBitermPath (ArtifactLevelEnum artifactLevelEnum);

    public String getMatricPath (ArtifactLevelEnum source, ArtifactLevelEnum target);

    public String getProjectName();

    public String getProjectPath();

}
