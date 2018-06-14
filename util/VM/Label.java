package util.VM;

public class Label {
    private static int labelCount = 0;
    private static int functionLabelCount = 0;

    public static String nuovaLabel() {
        return "label_" + incrementLabel();
    }

    public static String nuovaLabelFunzione() {
        return "fun_label_" + incrementFunctionLabel();
    }

    private static int incrementLabel() {
        return labelCount++;
    }

    private static int incrementFunctionLabel() {
        return functionLabelCount++;
    }

    public static void resetLabel() {
        labelCount = 0;
        functionLabelCount = 0;
    }
}
