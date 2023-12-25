package document;

import util.BitermUtil;
import util.FileIOUtil;
import util.ReadRtmUtil;

import java.util.Map;

public class TextDataset {
    private ArtifactsCollection sourceCollection;
    private ArtifactsCollection targetCollection;

    private SimilarityMatrix rtm;

    public TextDataset(String sourceDirPath, String targetDirPath, String rtmPath) {
        this.setSourceCollection(FileIOUtil.getCollections(sourceDirPath, ".txt"));
        this.setTargetCollection(FileIOUtil.getCollections(targetDirPath, ".txt"));
        this.setRtm(ReadRtmUtil.createSimilarityMatrix(rtmPath));
    }

    public TextDataset(String sourceDirPath, String targetDirPath) {
        this.setSourceCollection(FileIOUtil.getCollections(sourceDirPath, ".txt"));
        this.setTargetCollection(FileIOUtil.getCollections(targetDirPath, ".txt"));
    }

    public TextDataset(ArtifactsCollection sourceCollection, ArtifactsCollection targetCollection, SimilarityMatrix rtm) {
        this.setSourceCollection(sourceCollection);
        this.setTargetCollection(targetCollection);
        this.setRtm(rtm);
    }

    public TextDataset(ArtifactsCollection sourceCollection, ArtifactsCollection targetCollection) {
        this.setSourceCollection(sourceCollection);
        this.setTargetCollection(targetCollection);
    }


    public TextDataset updateTextDataSet(Map<String, Map<String, Integer>> sourceBitermMap,
                                         Map<String, Map<String, Integer>> targetBitermMap) {
        ArtifactsCollection sc = new ArtifactsCollection();
        ArtifactsCollection tc = new ArtifactsCollection();

        Map<String, String> sourceAddMap = BitermUtil.getAddString(sourceBitermMap);
        Map<String, String> targetAddMap = BitermUtil.getAddString(targetBitermMap);

        for (String source : sourceCollection.keySet()) {
            Artifact artifact = sourceCollection.get(source);
            String newText;
            if (sourceAddMap.containsKey(source)) {
                newText = artifact.text.trim() + " " + sourceAddMap.get(source).trim();
            } else {
                newText = artifact.text.trim();
            }
            sc.put(source, new Artifact(artifact.id, newText));
        }

        for (String target : targetCollection.keySet()) {
            Artifact artifact = targetCollection.get(target);
            String newText;
            if (targetAddMap.containsKey(target)) {
                newText = artifact.text.trim() + " " + targetAddMap.get(target).trim();
            } else {
                newText = artifact.text.trim();
            }
            tc.put(target, new Artifact(artifact.id, newText));
        }
        TextDataset newTd = new TextDataset(sc, tc, rtm);
        return newTd;
    }

    public TextDataset updateTextDataSet(Map<String, Map<String, Integer>> bitermMap) {
        ArtifactsCollection collection = new ArtifactsCollection();
        Map<String, String> addMap = BitermUtil.getAddString(bitermMap);
        for (String source : sourceCollection.keySet()) {
            Artifact artifact = sourceCollection.get(source);
            String newText;
            if (addMap.containsKey(source)) {
                newText = artifact.text.trim() + " " + addMap.get(source).trim();
            } else {
                newText = artifact.text.trim();
            }
            collection.put(source, new Artifact(artifact.id, newText));
        }

        TextDataset newTd = new TextDataset(collection, collection);
        return newTd;
    }


    public ArtifactsCollection getSourceCollection() {
        return sourceCollection;
    }

    public void setSourceCollection(ArtifactsCollection sourceCollection) {
        this.sourceCollection = sourceCollection;
    }

    public ArtifactsCollection getTargetCollection() {
        return targetCollection;
    }

    public void setTargetCollection(ArtifactsCollection targetCollection) {
        this.targetCollection = targetCollection;
    }

    public SimilarityMatrix getRtm() {
        return rtm;
    }

    public void setRtm(SimilarityMatrix rtm) {
        this.rtm = rtm;
    }
}
