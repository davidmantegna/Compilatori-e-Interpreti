package type;

import exceptions.UndeclaredMethodIDException;
import symboltable.Field;
import symboltable.Method;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassType implements IType {

    private String classID;
    private ClassType superClassType = null;

    private ArrayList<Field> fields = new ArrayList<>();
    private ArrayList<Method> methods = new ArrayList<>();

    public ClassType(String classID, ClassType superClass, ArrayList<Field> fields, ArrayList<Method> methods) {
        this.classID = classID;
        this.superClassType = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    public ClassType(String classID) {
        this.classID = classID;
    }

    public String getClassID() {
        return classID;
    }

    public ClassType getSuperClassType() {
        return superClassType;
    }

    public String getSuperClassID() {
        if (getSuperClassType() == null) {
            return "";
        }
        return getSuperClassType().getClassID();
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    public void setSuperClassType(ClassType superClassType) {
        this.superClassType = superClassType;
    }

    @Override
    public IDType getID() {
        return IDType.CLASS;
    }

    @Override
    public boolean isSubType(IType t) {
        //A è sottotipo di B, A.isSubTypeOf(B)
        // t parametro richiesto, this parametro passato

        // Controllo se altro tipo è classe
        if (t instanceof ClassType) {
            ClassType ct2 = (ClassType) t;
            // E' stessa classe
            if (this.getClassID().equals(ct2.getClassID())) {
                return true;
            }
            // Vado avanti solo se la classe corrente ha un supertipo
            if (superClassType != null) {
                ClassType tmp = superClassType;
                if (superClassType.isSubType(t)) {
                    return true;
                } else if (ct2.getSuperClassType() != null) {
                    if (superClassType.getClassID().equals(ct2.getSuperClassID())) {
                        return true;
                    }
                }
                while (tmp.getSuperClassType() != null) {
                    tmp = tmp.getSuperClassType();
                    if (tmp.getClassID().equals(ct2.getClassID())) {
                        return true;
                    }
                    while (ct2.getSuperClassType() != null) {
                        ct2 = ct2.getSuperClassType();
                        if (tmp.getClassID().equals(ct2.getClassID())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toPrint() {
        return "Object: " + classID;
    }

    // funzioni ausiliarie utilizzate nella checksemantics
    // ritorna un'HashMap di tutti i metodi di questa classe, con nome ed offset
    public HashMap<String, FunType> getMethodsMap() {
        HashMap<String, FunType> methodsMap = new HashMap<>();
        if (superClassType != null) {
            HashMap<String, FunType> superMethodsMap = superClassType.getMethodsMap();
            for (String m : superMethodsMap.keySet())
                methodsMap.put(m, superMethodsMap.get(m));
        }
        for (Method m : methods) {
            methodsMap.put(m.getMethodID(), m.getMethodType());
        }
        return methodsMap;
    }

    // ritorna l'offset del metodo situato nella dispatchTable
    public int getOffsetOfMethod(String methodID) throws UndeclaredMethodIDException {
        HashMap<String, Integer> methodsHashMap = methodsHashMapFromSuperClass();
        Integer offset = methodsHashMap.get(methodID);
        if (offset != null) {
            //ad offset 0 c'è la dispatch table, quindi vengono tutti aumentati di 1
            return offset + 1;
        } else {
            throw new UndeclaredMethodIDException(methodID, classID);
        }
    }

    // ritorna un'HashMap dei metodi della superclasse, con nome ed offset
    public HashMap<String, Integer> methodsHashMapFromSuperClass() {
        if (superClassType == null) {
            HashMap<String, Integer> methodsHashMap = new HashMap<>();
            for (Method method : methods) {
                methodsHashMap.put(method.getMethodID(), methodsHashMap.size());
            }
            return methodsHashMap;
        } else {
            HashMap<String, Integer> superMethodsMap = superClassType.methodsHashMapFromSuperClass();
            for (Method method : methods) {
                if (!superMethodsMap.containsKey(method.getMethodID())) {
                    superMethodsMap.put(method.getMethodID(), superMethodsMap.size());
                } else {
                    // aggiorno il valore dell'offset per via dell'override
                    superMethodsMap.put(method.getMethodID(), superMethodsMap.size());
                }
            }
            return superMethodsMap;
        }
    }

    //ritorna il tipo di un metodo dato l'ID
    public IType getTypeOfMethod(String id) {
        Method method = this.methods
                .stream()
                .filter(m -> m.getMethodID().equals(id))
                .reduce(null, (prev, curr) -> curr);

        if (method != null) {
            return method.getMethodType();
        } else {
            return superClassType.getTypeOfMethod(id);
        }
    }
}


