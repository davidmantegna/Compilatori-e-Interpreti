package codegen.VM;

public class Label {
    private static int labelCount = 0;
    private static int functionLabelCount = 0;

    public static String nuovaLabelString(String string) {
        return "label" + string + incrementLabel();
    }

    public static String nuovaLabelFunzioneString(String string) {
        return "label" + string + incrementFunctionLabel();
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
