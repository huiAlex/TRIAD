package approach;


import document.SimilarityMatrix;
import document.TextDataset;
import experiment.Result;
import experiment.enums.ProjectEnum;
import experiment.project.Project;
import util.FileIOUtil;

import java.io.File;
import java.util.List;

public class COMET {
    private static Project project;
    private static String associationModel;

    private static String resultPath;
    private static TextDataset textDataset;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class projectClass = Class.forName(ProjectEnum.EBT.getName()); // select project
        Project project = (Project) projectClass.newInstance();

        COMET comet = new COMET(project,"MAP");
        Result result = COMET.getResult();
        System.out.println(result.getAveragePrecisionByRanklist()+" "+result.getMeanAveragePrecisionByQuery() );

    }
    public COMET(Project project,String associationModel) {
        this.project = project;
        this.associationModel = associationModel;
        this.resultPath = "result/baseline_result/comet/" + project.getProjectName()+File.separator+associationModel+".tm";
        this.textDataset = new TextDataset(project.getProcessedLevel1ArtifactPath(), project.getProcessedLevel3ArtifactPath(), project.getLevel1ToLevel3MatricPath());
    }


    public static Result getResult() {
        File resultFile = new File(resultPath);
        List<String> traceList = FileIOUtil.readFile2List(resultFile);
        traceList = traceList.subList(6,traceList.size()-1);
        SimilarityMatrix matrix = new SimilarityMatrix();
        for(String trace:traceList) {
            String[] ary = trace.split(" ");
            String source="", target="";
            if(project.getProjectName().equals("ebt")) {
                source = ary[0].replaceAll("RQ","");
                target = ary[1].replaceAll("\\.java","");
            } else if(project.getProjectName().equals("libest")){
                source = ary[0].replaceAll("\\.txt","");
                target = ary[1];
            }

            String scoreStr = ary[2];
            if(scoreStr.equals("nan"))
                scoreStr = "0.0";
            double score = Double.valueOf(scoreStr);
            matrix.addLink(source, target, score);
        }
        Result result = new Result(matrix, textDataset.getRtm());
        String resultName = resultFile.getName().split("\\.tm")[0];
        result.setAlgorithmName("COMET-"+resultName);
        return result;
    }

}
