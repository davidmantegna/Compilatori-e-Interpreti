package codegen.VM;

public class Label {
    private static int labelCount = 0;

    public static String nuovaLabelString(String string) {
        return "label" + string + incrementLabel();
    }

    public static String nuovaLabelFunzioneString(String string) {
        return "function" + string + incrementLabel();
    }

    public static String nuovaLabelMetodoString(String string) {
        return "method" + string + incrementLabel();
    }

    private static int incrementLabel() {
        return ++labelCount;
    }

    public static void resetLabel() {
        labelCount = 0;
    }
}
