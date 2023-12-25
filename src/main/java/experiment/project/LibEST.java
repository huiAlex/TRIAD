package experiment.project;

import experiment.enums.ArtifactLevelEnum;

public class LibEST implements Project {
    private static String projectName = "libest";

    private static String projectPath = "dataset/libest";

    private static String processedLevel1Path = "dataset/libest/processed/req";
    private static String processedLevel2Path = "dataset/libest/processed/test/code";
    private static String processedLevel3Path = "dataset/libest/processed/code/code";

    private static String level1ToLevel2MatricPath = "dataset/libest/trace_matrices/req-test.txt";
    private static String level2ToLevel3MatricPath = "dataset/libest/trace_matrices/test-code.txt";
    private static String level1ToLevel3MatricPath = "dataset/libest/trace_matrices/req-code.txt";

    private static String unprocessedLevel1Path = "dataset/libest/unprocessed/req";
    private static String unprocessedLevel2Path = "dataset/libest/unprocessed/test/code";
    private static String unprocessedLevel3Path = "dataset/libest/unprocessed/code/code";

    private static String codeFileNamePath = "dataset/libest/processed/code/code_file_name";
    private static String codeCommentPath = "dataset/libest/processed/code/comment";
    private static String methodNamePath = "dataset/libest/processed/code/method_name";
    private static String invokeMethodNamePath = "dataset/libest/processed/code/invoke_method";
    private static String fieldNamePath = "dataset/libest/processed/code/field_name";
    private static String paramNamePath = "dataset/libest/processed/code/param/param_name";
    private static String paramTypePath = "dataset/libest/processed/code/param/param_type";

    private static String unprocessedCodeFileNamePath = "dataset/libest/unprocessed/code/code_file_name";
    private static String unprocessedCodeCommentPath = "dataset/libest/unprocessed/code/comment";
    private static String unprocessedMethodNamePath = "dataset/libest/unprocessed/code/method_name";
    private static String unprocessedInvokeMethodNamePath = "dataset/libest/unprocessed/code/invoke_method";
    private static String unprocessedFieldNamePath = "dataset/libest/unprocessed/code/field_name";
    private static String unprocessedParamNamePath = "dataset/libest/unprocessed/code/param/param_name";
    private static String unprocessedParamTypePath = "dataset/libest/unprocessed/code/param/param_type";

    private static String testFileNamePath = "dataset/libest/processed/test/code_file_name";
    private static String testCommentPath = "dataset/libest/processed/test/comment";
    private static String testMethodNamePath = "dataset/libest/processed/test/method_name";
    private static String testInvokeMethodNamePath = "dataset/libest/processed/test/invoke_method";
    private static String testFieldNamePath = "dataset/libest/processed/test/field_name";
    private static String testParamNamePath = "dataset/libest/processed/test/param/param_name";
    private static String testParamTypePath = "dataset/libest/processed/test/param/param_type";

    private static String unprocessedTestFileNamePath = "dataset/libest/unprocessed/test/code_file_name";
    private static String unprocessedTestCommentPath = "dataset/libest/unprocessed/test/comment";
    private static String unprocessedTestMethodNamePath = "dataset/libest/unprocessed/test/method_name";
    private static String unprocessedTestInvokeMethodNamePath = "dataset/libest/unprocessed/test/invoke_method";
    private static String unprocessedTestFieldNamePath = "dataset/libest/unprocessed/test/field_name";
    private static String unprocessedTestParamNamePath = "dataset/libest/unprocessed/test/param/param_name";
    private static String unprocessedTestParamTypePath = "dataset/libest/unprocessed/test/param/param_type";

    private static String level1BitermPath = "dataset/libest/biterm/req";
    private static String level2BitermPath = "dataset/libest/biterm/test";
    private static String level3BitermPath = "dataset/libest/biterm/code";


    public String getTestFileNamePath() {
        return testFileNamePath;
    }

    public String getTestCommentPath() {
        return testCommentPath;
    }

    public String getTestMethodNamePath() {
        return testMethodNamePath;
    }

    public String getTestInvokeMethodNamePath() {
        return testInvokeMethodNamePath;
    }

    public String getTestFieldNamePath() {
        return testFieldNamePath;
    }

    public String getTestParamNamePath() {
        return testParamNamePath;
    }

    public String getTestParamTypePath() {
        return testParamTypePath;
    }

    public String getUnprocessedTestFileNamePath() {
        return unprocessedTestFileNamePath;
    }

    public String getUnprocessedTestCommentPath() {
        return unprocessedTestCommentPath;
    }

    public String getUnprocessedTestMethodNamePath() {
        return unprocessedTestMethodNamePath;
    }

    public String getUnprocessedTestInvokeMethodNamePath() {
        return unprocessedTestInvokeMethodNamePath;
    }

    public String getUnprocessedTestFieldNamePath() {
        return unprocessedTestFieldNamePath;
    }

    public String getUnprocessedTestParamNamePath() {
        return unprocessedTestParamNamePath;
    }

    public String getUnprocessedTestParamTypePath() {
        return unprocessedTestParamTypePath;
    }

    public String getUnprocessedCodeFileNamePath() {
        return unprocessedCodeFileNamePath;
    }

    public String getUnprocessedCodeCommentPath() {
        return unprocessedCodeCommentPath;
    }

    public String getUnprocessedMethodNamePath() {
        return unprocessedMethodNamePath;
    }

    public String getUnprocessedInvokeMethodNamePath() {
        return unprocessedInvokeMethodNamePath;
    }

    public String getUnprocessedFieldNamePath() {
        return unprocessedFieldNamePath;
    }


    public String getUnprocessedParamNamePath() {
        return unprocessedParamNamePath;
    }

    public String getUnprocessedParamTypePath() {
        return unprocessedParamTypePath;
    }

    public String getCodeFileNamePath() {
        return codeFileNamePath;
    }

    public String getCodeCommentPath() {
        return codeCommentPath;
    }

    public String getMethodNamePath() {
        return methodNamePath;
    }

    public String getInvokeMethodNamePath() {
        return invokeMethodNamePath;
    }

    public String getFieldNamePath() {
        return fieldNamePath;
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
        return projectPath;
    }
}
