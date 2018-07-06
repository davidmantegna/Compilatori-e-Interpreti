package codegen.VM;

public class FunctionCode {
    private static String functionsCode = "";
    public static void insertFunctionsCode(String c) {
        functionsCode = functionsCode + "\n" + c; // andata a capo prima di una funzione
    }

    public static String getFunctionsCode() {
        return functionsCode;
    }
    public static void reset() {
        functionsCode="";
    }
}
