package experiment.project;


import experiment.enums.ArtifactLevelEnum;

public class EBT implements Project{
    private static String projectName = "ebt";

    private static String processedLevel1Path = "dataset/ebt/processed/req";
    private static String processedLevel2Path = "dataset/ebt/processed/test_case";
    private static String processedLevel3Path = "dataset/ebt/processed/code/class";

    private static String level1ToLevel2MatricPath = "dataset/ebt/trace_matrices/req-tc.txt";
    private static String level2ToLevel3MatricPath = "dataset/ebt/trace_matrices/tc-code.txt";
    private static String level1ToLevel3MatricPath = "dataset/ebt/trace_matrices/req-code.txt";

    private static String unprocessedLevel1Path = "dataset/ebt/unprocessed/req/requirements.csv";
    private static String unprocessedLevel2Path = "dataset/ebt/unprocessed/test_case/test_case.csv";
    private static String unprocessedLevel3Path = "dataset/ebt/unprocessed/code/code";

    private static String clsNamePath = "dataset/ebt/processed/code/class_name";
    private static String codeCommentPath = "dataset/ebt/processed/code/code_comment";
    private static String methodNamePath = "dataset/ebt/processed/code/method_name";
    private static String invokeMethodNamePath = "dataset/ebt/processed/code/invoke_method";
    private static String fieldNamePath = "dataset/ebt/processed/code/field/field_name";
    private static String fieldTypePath = "dataset/ebt/processed/code/field/field_type";
    private static String paramNamePath = "dataset/ebt/processed/code/param/param_name";
    private static String paramTypePath = "dataset/ebt/processed/code/param/param_type";

    private static String unprocessedClsNamePath = "dataset/ebt/unprocessed/code/class_name";
    private static String unprocessedCodeCommentPath = "dataset/ebt/unprocessed/code/code_comment";
    private static String unprocessedMethodNamePath = "dataset/ebt/unprocessed/code/method_name";
    private static String unprocessedInvokeMethodNamePath = "dataset/ebt/unprocessed/code/invoke_method";
    private static String unprocessedFieldNamePath = "dataset/ebt/unprocessed/code/field/field_name";
    private static String unprocessedFieldTypePath = "dataset/ebt/unprocessed/code/field/field_type";
    private static String unprocessedParamNamePath = "dataset/ebt/unprocessed/code/param/param_name";
    private static String unprocessedParamTypePath = "dataset/ebt/unprocessed/code/param/param_type";

    private static String level1BitermPath = "dataset/ebt/biterm/req";
    private static String level2BitermPath = "dataset/ebt/biterm/test_case";
    private static String level3BitermPath = "dataset/ebt/biterm/code";

    public static String getUnprocessedClsNamePath() {
        return unprocessedClsNamePath;
    }

    public static String getUnprocessedCodeCommentPath() {
        return unprocessedCodeCommentPath;
    }

    public static String getUnprocessedMethodNamePath() {
        return unprocessedMethodNamePath;
    }

    public static String getUnprocessedInvokeMethodNamePath() {
        return unprocessedInvokeMethodNamePath;
    }

    public static String getUnprocessedFieldNamePath() {
        return unprocessedFieldNamePath;
    }

    public static String getUnprocessedFieldTypePath() {
        return unprocessedFieldTypePath;
    }

    public static String getUnprocessedParamNamePath() {
        return unprocessedParamNamePath;
    }

    public static String getUnprocessedParamTypePath() {
        return unprocessedParamTypePath;
    }

    public static String getClsNamePath() {
        return clsNamePath;
    }

    public static String getCodeCommentPath() {
        return codeCommentPath;
    }

    public static String getMethodNamePath() {
        return methodNamePath;
    }

    public static String getInvokeMethodNamePath() {
        return invokeMethodNamePath;
    }

    public static String getFieldNamePath() {
        return fieldNamePath;
    }

    public static String getFieldTypePath() {
        return fieldTypePath;
    }

    public static String getParamNamePath() {
        return paramNamePath;
    }

    public static String getParamTypePath() {
        return paramTypePath;
    }

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
        }else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL2)){
            path = getProcessedLevel2ArtifactPath();
        }else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL3)){
            path = getProcessedLevel3ArtifactPath();
        }
        return path;
    }

    @Override
    public String getBitermPath(ArtifactLevelEnum artifactLevelEnum) {
        String path = "";
        if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL1)) {
            path = getLevel1BitermPath();
        }else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL2)){
            path = getLevel2BitermPath();
        }else if (artifactLevelEnum.equals(ArtifactLevelEnum.LEVEL3)){
            path = getLevel3BitermPath();
        }
        return path;
    }

    @Override
    public String getMatricPath(ArtifactLevelEnum source, ArtifactLevelEnum target) {
        String path = "";
        if(source.equals(ArtifactLevelEnum.LEVEL1 )&& target.equals(ArtifactLevelEnum.LEVEL2)){
            path = getLevel1ToLevel2MatricPath();
        } else if (source.equals(ArtifactLevelEnum.LEVEL1 )&& target.equals(ArtifactLevelEnum.LEVEL3)){
            path = getLevel1ToLevel3MatricPath();

        }else if (source.equals(ArtifactLevelEnum.LEVEL2 )&& target.equals(ArtifactLevelEnum.LEVEL3)) {
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
