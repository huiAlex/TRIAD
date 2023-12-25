package experiment.enums;

public enum IREnum {
    VSM("model.VSM"), LSI("model.LSI"), JSD("model.JSD");

    private final String model;

    private IREnum(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
