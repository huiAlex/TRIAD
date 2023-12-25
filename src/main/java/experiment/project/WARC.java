package experiment.project;


import experiment.enums.ArtifactLevelEnum;

public class WARC implements Project {
    private static String projectName = "warc";

    private static String processedLevel1Path = "dataset/warc/processed/NFR";
    private static String processedLevel2Path = "dataset/warc/processed/SRS";
    private static String processedLevel3Path = "dataset/warc/processed/FRS";

    private static String level1ToLevel2MatricPath = "dataset/warc/trace_matrices/NFR-SRS.txt";
    private static String level2ToLevel3MatricPath = "dataset/warc/trace_matrices/SRS-FRS.txt";
    private static String level1ToLevel3MatricPath = "dataset/warc/trace_matrices/NFR-FRS.txt";

    private static String unprocessedLevel1Path = "dataset/warc/unprocessed/NFR";
    private static String unprocessedLevel2Path = "dataset/warc/unprocessed/SRS";
    private static String unprocessedLevel3Path = "dataset/warc/unprocessed/FRS";

    private static String level1BitermPath = "dataset/warc/biterm/NFR";
    private static String level2BitermPath = "dataset/warc/biterm/SRS";
    private static String level3BitermPath = "dataset/warc/biterm/FRS";

    @Override
    public String getProcessedLevel1ArtifactPath() {
        return processedLevel1Path;
    }

    @Override
    public String getProcessedLevel2ArtifactPath() {
        return processedLevel2Path;
    }

    @Override
    public String getProcessedLevel3ArtifactPath() {
        return processedLevel3Path;
    }

    @Override
    public String getUnprocessedLevel1ArtifactPath() {
        return unprocessedLevel1Path;
    }

    @Override
    public String getUnprocessedLevel2ArtifactPath() {
        return unprocessedLevel2Path;
    }

    @Override
    public String getUnprocessedLevel3ArtifactPath() {
        return unprocessedLevel3Path;
    }

    @Override
    public String getLevel1ToLevel2MatricPath() {
        return level1ToLevel2MatricPath;
    }

    @Override
    public String getLevel2ToLevel3MatricPath() {
        return level2ToLevel3MatricPath;
    }

    @Override
    public String getLevel1ToLevel3MatricPath() {
        return level1ToLevel3MatricPath;
    }

    @Override
    public String getLevel1BitermPath() {
        return level1BitermPath;
    }

    @Override
    public String getLevel2BitermPath() {
        return level2BitermPath;
    }

    @Override
    public String getLevel3BitermPath() {
        return level3BitermPath;
    }

    @Override
    public String getArtifactPath(ArtifactLevelEnum artifactLevelEnum) {
        String path = "";
        if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL1)) {
            path = getProcessedLevel1ArtifactPath();
        } else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL2)) {
            path = getProcessedLevel2ArtifactPath();
        } else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL3)) {
            path = getProcessedLevel3ArtifactPath();
        }
        return path;
    }

    @Override
    public String getBitermPath(ArtifactLevelEnum artifactLevelEnum) {
        String path = "";
        if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL1)) {
            path = getLevel1BitermPath();
        } else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL2)) {
            path = getLevel2BitermPath();
        } else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL3)) {
            path = getLevel3BitermPath();
        }
        return path;
    }

    @Override
    public String getMatricPath(ArtifactLevelEnum source, ArtifactLevelEnum target) {
        String path = "";
        if (source.equals(ArtifactLevelEnum.LEVEL1) && target.equals(ArtifactLevelEnum.LEVEL2)) {
            path = getLevel1ToLevel2MatricPath();
        } else if (source.equals(ArtifactLevelEnum.LEVEL1) && target.equals(ArtifactLevelEnum.LEVEL3)) {
            path = getLevel1ToLevel3MatricPath();

        } else if (source.equals(ArtifactLevelEnum.LEVEL2) && target.equals(ArtifactLevelEnum.LEVEL3)) {
            path = getLevel2ToLevel3MatricPath();
        }
        return path;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getProjectPath() {
        return null;
    }
}
