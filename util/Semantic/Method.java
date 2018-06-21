package util.Semantic;

import type.FunType;

public class Method {

    private String methodID;
    private FunType methodType;

    public Method(String methodID, FunType methodType) {
        this.methodID = methodID;
        this.methodType = methodType;
    }

    public String getMethodID() {
        return methodID;
    }

    public FunType getMethodType() {
        return methodType;
    }
}

