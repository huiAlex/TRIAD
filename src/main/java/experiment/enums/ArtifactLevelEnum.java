package experiment.enums;

public enum ArtifactLevelEnum {
    LEVEL1, LEVEL2, LEVEL3;
    public static ArtifactLevelEnum getType(String name) {
        switch (name) {
            case "level1": // source
                return LEVEL1;
            case "level2": // intermediate
                return LEVEL2;
            case "level3": // target
                return LEVEL3;
            default:
                return null;
        }
    }
}
