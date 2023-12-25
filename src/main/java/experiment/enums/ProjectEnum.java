package experiment.enums;

public enum ProjectEnum {
    EBT("experiment.project.EBT"),
    DRONOLOGY("experiment.project.Dronology"),
    WARC("experiment.project.WARC"),
    EASYCLINIC("experiment.project.EasyClinic"),
    LIBEST("experiment.project.LibEST");

    String name;

    ProjectEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProjectEnum getProject(String projectName) {
        switch (projectName) {
            case "dronology":
                return DRONOLOGY;
            case "warc":
                return WARC;
            case "ebt":
                return EBT;
            case "easyclinic":
                return EASYCLINIC;
            case "libest":
                return LIBEST;
            default:
                return null;
        }
    }
}
