package experiment.project;


import experiment.enums.ArtifactLevelEnum;

public class Dronology implements Project {

    private static String projectName = "dronology";

    private static String level1ToLevel2MatricPath = "dataset/dronology/trace_matrices/req-dd.txt";
    private static String level2ToLevel3MatricPath = "dataset/dronology/trace_matrices/dd-code.txt";
    private static String level1ToLevel3MatricPath = "dataset/dronology/trace_matrices/req-code.txt";

    private static String processedLevel1Path = "dataset/dronology/processed/req/req";
    private static String processedLevel2Path = "dataset/dronology/processed/design_definition/dd";
    private static String processedLevel3Path = "dataset/dronology/processed/code/class";

    private static String level1BitermPath = "dataset/dronology/biterm/req";
    private static String level2BitermPath = "dataset/dronology/biterm/design_definition";
    private static String level3BitermPath = "dataset/dronology/biterm/code";

    private static String unprocessedLevel1Path = "dataset/dronology/unprocessed/req";
    private static String unprocessedLevel2Path = "dataset/dronology/unprocessed/design_definition";
    private static String unprocessedLevel3Path = "dataset/dronology/unprocessed/code/class";

    private static String clsNamePath = "dataset/dronology/processed/code/class_name";
    private static String codeCommentPath = "dataset/dronology/processed/code/comment/term_pair";
    private static String methodNamePath = "dataset/dronology/processed/code/method_name";
    private static String invokeMethodNamePath = "dataset/dronology/processed/code/invoke_method";
    private static String fieldNamePath = "dataset/dronology/processed/code/field/field_name";
    private static String fieldTypePath = "dataset/dronology/processed/code/field/field_type";
    private static String paramNamePath = "dataset/dronology/processed/code/param/param_name";
    private static String paramTypePath = "dataset/dronology/processed/code/param/param_type";


    private static String unprocessedDdPath = "dataset/dronology/unprocessed/design_definition";
    private static String processedDdPath = "dataset/dronology/processed/design_definition/dd";
    private static String processedDdSummaryPath = "dataset/dronology/processed/design_definition/part/summary";
    private static String processedDdDescriptionPath = "dataset/dronology/processed/design_definition/part/description";
    private static String processedDdSummaryTpPath = "dataset/dronology//processed/design_definition/term_pair/summary";
    private static String processedDdDescriptionTpPath = "dataset/dronology/processed/design_definition/term_pair/description";

    private static String unprocessedReqPath = "dataset/dronology/unprocessed/req";
    private static String processedReqPath = "dataset/dronology/processed/req/req";
    private static String processedReqSummaryPath = "dataset/dronology/processed/req/part/summary";
    private static String processedReqDescriptionPath = "dataset/dronology/processed/req/part/description";
    private static String processedReqSummaryTpPath = "dataset/dronology/processed/req/term_pair/summary";
    private static String processedReqDescriptionTpPath = "dataset/dronology/processed/req/term_pair/description";

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

    public String getClsNameDirPath() {
        return clsNamePath;
    }

    public String getMethodNameDirPath() {
        return methodNamePath;
    }

    public String getCommentDirPath() {
        return codeCommentPath;
    }

    public String getInvokeMethodDirPath() {
        return invokeMethodNamePath;
    }

    public String getFieldNameDirPath() {
        return fieldNamePath;
    }

    public String getFieldTypeDirPath() {
        return fieldTypePath;
    }

    public String getParamNameDirPath() {
        return paramNamePath;
    }

    public String getParamTypeDirPath() {
        return paramTypePath;
    }

    public static String getUnprocessedDdPath() {
        return unprocessedDdPath;
    }

    public static String getProcessedDdPath() {
        return processedDdPath;
    }

    public static String getProcessedDdSummaryPath() {
        return processedDdSummaryPath;
    }

    public static String getProcessedDdDescriptionPath() {
        return processedDdDescriptionPath;
    }

    public static String getProcessedDdSummaryTpPath() {
        return processedDdSummaryTpPath;
    }

    public static String getProcessedDdDescriptionTpPath() {
        return processedDdDescriptionTpPath;
    }

    public static String getUnprocessedReqPath() {
        return unprocessedReqPath;
    }

    public static String getProcessedReqPath() {
        return processedReqPath;
    }

    public static String getProcessedReqSummaryPath() {
        return processedReqSummaryPath;
    }

    public static String getProcessedReqDescriptionPath() {
        return processedReqDescriptionPath;
    }

    public static String getProcessedReqSummaryTpPath() {
        return processedReqSummaryTpPath;
    }

    public static String getProcessedReqDescriptionTpPath() {
        return processedReqDescriptionTpPath;
    }

}
