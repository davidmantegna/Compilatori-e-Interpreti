package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import type.ClassType;
import type.FunType;
import type.IType;
import type.ObjectType;
import util.Semantic.Field;
import util.Semantic.Method;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassNode implements INode {

    private String idClass;
    private String idExtendedClass;
    private ArrayList<ParameterNode> fieldDeclarationArraylist;
    private ArrayList<MethodNode> methodDeclarationArraylist;

    private HashMap<String, IType> fieldHashMap = new HashMap<>();
    private HashMap<String, FunType> methodHashMap = new HashMap<>();
    private ClassType classType;

    public ClassNode(String idClass, String idExtendedClass, ArrayList<ParameterNode> fieldDeclarationArraylist, ArrayList<MethodNode> methodDeclarationArraylist) {
        this.idClass = idClass;
        this.idExtendedClass = idExtendedClass;
        this.fieldDeclarationArraylist = fieldDeclarationArraylist;
        this.methodDeclarationArraylist = methodDeclarationArraylist;
    }

    public String getIdClass() {
        return idClass;
    }

    public String getIdExtendedClass() {
        return idExtendedClass;
    }

    public ArrayList<ParameterNode> getFieldDeclarationArraylist() {
        return fieldDeclarationArraylist;
    }

    public ArrayList<MethodNode> getMethodDeclarationArraylist() {
        return methodDeclarationArraylist;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("ClassNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        ArrayList<Field> fieldArrayList = new ArrayList<>();
        ArrayList<Method> methodArrayList = new ArrayList<>();

        for (ParameterNode parameterNode : fieldDeclarationArraylist) {
            fieldArrayList.add(new Field(parameterNode.getId(), parameterNode.getType()));
            fieldHashMap.put(parameterNode.getId(), parameterNode.getType());
        }

        for (MethodNode methodNode : methodDeclarationArraylist) {
            ArrayList<IType> parameterTypeArrayList = new ArrayList<>();
            for (ParameterNode parameterNode : methodNode.getParameterNodeArrayList()) {
                // controlla i parametri di ogni metodo
                // se sono degli oggetti si controlla che siano definiti, altrimenti eccezione
                if (parameterNode.getType() instanceof ObjectType) {
                    ObjectType paramType = (ObjectType) parameterNode.getType();
                    String declaredClass = paramType.getClassType().getClassID();
                    try {
                        ClassType paramClassType = (ClassType) env.processUse(declaredClass).getType();
                        parameterTypeArrayList.add(new ObjectType(paramClassType));
                    } catch (UndeclaredIDException e) {
                        res.add("La classe '" + declaredClass + " non è stata definita");
                    }
                } else {
                    // se i parametri sono "base", non oggetti
                    parameterTypeArrayList.add(parameterNode.getType());
                }
            }

            //prende i campi passati in input e li inserisce in methodArrayList per la STentry e in
            //methodHashMap per accedervi velocemente
            methodArrayList.add(new Method(methodNode.getID(), new FunType(parameterTypeArrayList, methodNode.getReturnType())));
            methodHashMap.put(methodNode.getID(), new FunType(parameterTypeArrayList, methodNode.getReturnType()));
        }

        // TODO Superclasse
        ClassType superclassType;

        //controllo se la classe ha una superclasse per aggiornare correttamente
        //la Symbol Table
        try {
            superclassType = (ClassType) env.processUse(idExtendedClass).getType();
        } catch (UndeclaredIDException e) {
            superclassType = null;
        }

        // all'ID dichiarato si setta il tipo classe nella STentry
        try {
            classType = new ClassType(idClass, superclassType, fieldArrayList, methodArrayList);
            env.setDeclarationType(idClass, classType, 0);
        } catch (UndeclaredIDException e) {
            res.add(e.getMessage());
        }

        HashMap<String, SymbolTableEntry> hashMap = new HashMap<>();

        env.pushHashMap(hashMap);

        for (ParameterNode parameterNode : fieldDeclarationArraylist) {
            if (parameterNode.getType() instanceof ObjectType) {
                //sottoclassi non possono essere parametri di superclassi

                ClassType subClassAsParam = ((ObjectType) parameterNode.getType()).getClassType();

                if (subClassAsParam.isSubType(this.classType))
                    res.add("Non si può usare una sottoclasse nel costruttore della superclasse");
            }
            res.addAll(parameterNode.checkSemantics(env));
        }


        HashMap<String, SymbolTableEntry> hashmap2 = new HashMap<>();
        env.pushHashMap(hashmap2);

        //checkSemantic di ogni metodo
        for (MethodNode methodNode : methodDeclarationArraylist) {
            res.addAll(methodNode.checkSemantics(env));
        }
        env.popHashMap();
        env.popHashMap();

        //controllo costruttore con superclasse
        if (!idExtendedClass.isEmpty()) {
            try {
                //controllo che la classe che estende sia una classe
                if (!(env.getTypeOf(idExtendedClass) instanceof ClassType))
                    res.add("L'ID della superclasse " + idExtendedClass + " non è riferito a un tipo di classe");
            } catch (UndeclaredIDException exp) {
                res.add("La superclasse " + idExtendedClass + " non è definita");
            }

            try {
                ClassType superClassType = (ClassType) env.processUse(idExtendedClass).getType();

                // controllo che il numero di attributi del costruttore sia uguale a quello della superclasse
                if (fieldDeclarationArraylist.size() >= superClassType.getFields().size()) {

                    for (int i = 0; i < superClassType.getFields().size(); i++) {
                        ParameterNode currentParameterNode = fieldDeclarationArraylist.get(i);
                        Field superClassField = superClassType.getFields().get(i);
                        //controllo che tipo e nome degli attributi siano uguali
                        if (!superClassField.getFieldID().equals(currentParameterNode.getId())
                                || !currentParameterNode.getType().isSubType(superClassField.getFieldType())) {
                            res.add("Il campo '" + currentParameterNode.getId() + "' della classe '" + idClass + "' fa override della superclasse con tipo differente");
                        }
                    }
                } else {
                    res.add("La sottoclasse non ha i parametri della superclasse");
                }
            } catch (UndeclaredIDException e) {
                res.add("La superclasse " + idExtendedClass + " non è definita " + e.getMessage());
            }

            try {
                //controllo ovveride se possibile

                //prendo entry e tipo della superclasse
                SymbolTableEntry superClassEntry = env.processUse(idExtendedClass);
                ClassType superClassType = (ClassType) superClassEntry.getType();

                //se si trovano due metodi con lo stesso nome controllo che uno sia sottotipo dell'altro
                //altrimenti override non è compatibile
                HashMap<String, FunType> superClassMethodsHashMap = superClassType.getMethodsMap();
                for (String method : methodHashMap.keySet()) {
                    if (superClassMethodsHashMap.containsKey(method)) {
                        if (!methodHashMap.get(method).isSubType(superClassMethodsHashMap.get(method))) {
                            res.add("Override incompatibile del metodo '" + method + "' della classe '" + idClass + "'");
                        }
                    }
                }
            } catch (UndeclaredIDException e) {
                res.add("La superclasse " + idExtendedClass + " non è definita " + e.getMessage());
            }
        }

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("ClassNode: typeCheck ->\t");
        return null;
    }

    @Override
    public String codeGeneration() {
        return null;
    }
}
