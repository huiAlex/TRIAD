import approach.IR_Only;
import approach.TRIAD;
import approach.TRIAD_NoBiterm;
import experiment.Result;
import experiment.enums.ArtifactLevelEnum;
import experiment.enums.IREnum;
import experiment.enums.ProjectEnum;
import experiment.project.Project;
import model.IR;
import model.IRModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RunTRIAD {


    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // evaluated parameter set up
        ProjectEnum projectEnum = ProjectEnum.LIBEST; // select project: EBT, DRONOLOGY, WARC, EASYCLINIC, LIBEST
        IREnum irEnum = IREnum.JSD; // select ir model: VSM, LSI, JSD
        run(projectEnum, irEnum);
    }

    private static void run(ProjectEnum projectEnum, IREnum irEnum) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class projectClass = Class.forName(projectEnum.getName());
        Class irModelClass = Class.forName(irEnum.getModel());

        ArtifactLevelEnum sourceArtifactEnum = ArtifactLevelEnum.getType("level1");
        ArtifactLevelEnum targetArtifactEnum = ArtifactLevelEnum.getType("level3");
        ArtifactLevelEnum middleArtifactEnum = ArtifactLevelEnum.getType("level2");

        Project project = (Project) projectClass.newInstance();
        IRModel irModel = (IRModel) irModelClass.newInstance();

        Map<String, Result> resultMap = new LinkedHashMap<>();

        // IR-ONLY
        IR ir = new IR();
        Result result_ir = ir.compute(project, sourceArtifactEnum, targetArtifactEnum, irModel, new IR_Only());
        resultMap.put(result_ir.getAlgorithmName(), result_ir);

        // TRIAD+b
        TRIAD triad_b = new TRIAD();
        Result result_triad_b = triad_b.compute(project, sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum, irModel, new IR_Only(), false, false);
        result_triad_b.setAlgorithmName("TRIAD+b");
        resultMap.put(result_triad_b.getAlgorithmName(), result_triad_b);

        // TRIAD+o
        TRIAD_NoBiterm triad_o = new TRIAD_NoBiterm();
        Result result_triad_o = triad_o.compute(project,sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum,irModel, new IR_Only(),true, false);
        result_triad_o.setAlgorithmName("TRIAD+o");
        resultMap.put(result_triad_o.getAlgorithmName(), result_triad_o);

        // TRIAD+b+o
        TRIAD triad_b_o = new TRIAD();
        Result result_triad_b_o = triad_b_o.compute(project, sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum, irModel, new IR_Only(), true, false);
        result_triad_b_o.setAlgorithmName("TRIAD+b+o");
        resultMap.put(result_triad_b_o.getAlgorithmName(), result_triad_b_o);

        // TRIAD+o+i
        TRIAD_NoBiterm triad_o_i = new TRIAD_NoBiterm();
        Result result_triad_o_i = triad_o_i.compute(project,sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum,irModel, new IR_Only(),true, true);
        result_triad_o_i.setAlgorithmName("TRIAD+o+i");
        resultMap.put(result_triad_o_i.getAlgorithmName(), result_triad_o_i);

        // TRIAD+b+o+i
        TRIAD triad_b_o_i = new TRIAD();
        Result result_triad_b_o_i = triad_b_o_i.compute(project, sourceArtifactEnum, targetArtifactEnum, middleArtifactEnum, irModel, new IR_Only(), true, true);
        result_triad_b_o_i.setAlgorithmName("TRIAD+b+o+i");
        resultMap.put(result_triad_b_o_i.getAlgorithmName(), result_triad_b_o_i);

        for (String resultName : resultMap.keySet()) {
            Result result = resultMap.get(resultName);
            double ApValue = result.getAveragePrecisionByRanklist();
            String AP = String.format("%.2f", ApValue * 100);
            double MapValue = result.getMeanAveragePrecisionByQuery();
            String MAP = String.format("%.2f", MapValue * 100);

            System.out.println("AP:" + AP
                    + "   " + "MAP:" + MAP
                    + "   " + irModel.getModelName()
                    + "   " + resultName);

            List<String> apAndMapList = new ArrayList<>();
            apAndMapList.add(AP);
            apAndMapList.add(MAP);
        }
    }
}
