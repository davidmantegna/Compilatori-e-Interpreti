package util.Semantic;

import type.IType;

public class Field {

    private String fieldID;
    private IType fieldType;

    public Field(String fieldId, IType fieldType) {
        this.fieldID = fieldId;
        this.fieldType = fieldType;
    }

    public String getFieldID() {
        return fieldID;
    }

    public IType getFieldType() {
        return fieldType;
    }
}
