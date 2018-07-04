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
import util.VM.DispatchTable;
import util.VM.DispatchTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassNode implements INode {

    private String idClass;
    private String idSuperClass;
    private ArrayList<ParameterNode> fieldDeclarationArraylist;
    private ArrayList<MethodNode> methodDeclarationArraylist;

    private HashMap<String, IType> fieldHashMap = new HashMap<>();
    private HashMap<String, FunType> methodHashMap = new HashMap<>();
    private ClassType classType;

    public ClassNode(String idClass, String idSuperClass, ArrayList<ParameterNode> fieldDeclarationArraylist, ArrayList<MethodNode> methodDeclarationArraylist) {
        this.idClass = idClass;
        this.idSuperClass = idSuperClass;
        this.fieldDeclarationArraylist = fieldDeclarationArraylist;
        this.methodDeclarationArraylist = methodDeclarationArraylist;
    }

    public String getIdClass() {
        return idClass;
    }

    public String getIdSuperClass() {
        return idSuperClass;
    }

    public ArrayList<ParameterNode> getFieldDeclarationArraylist() {
        return fieldDeclarationArraylist;
    }

    public ArrayList<MethodNode> getMethodDeclarationArraylist() {
        return methodDeclarationArraylist;
    }

    public ClassType getClassType() {
        return classType;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("ClassNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        ArrayList<Field> fieldArrayList = new ArrayList<>();
        ArrayList<Method> methodArrayList = new ArrayList<>();


        // TODO  Test superclasse
        ClassType superclassType;

        //controllo se la classe ha una superclasse per aggiornare correttamente la SymbolTable
        try {
            superclassType = (ClassType) env.processUse(idSuperClass).getType();
        } catch (UndeclaredIDException e) {
            superclassType = null;
        }

        // Eredito i Campi dell'eventuale SuperClasse
        if (superclassType != null) {
            ArrayList<Field> listFields = new ArrayList<>();
            ClassType supetType = superclassType;
            while (supetType != null) {
                listFields.addAll(0, supetType.getFields());
                String idsuperClass = supetType.getSuperClassID();
                try {
                    supetType = (ClassType) env.processUse(idsuperClass).getType();
                } catch (UndeclaredIDException e) {
                    supetType = null;
                }
            }
            fieldArrayList.addAll(listFields);
        }

        // inserisco i parametri della classe attuale
        for (ParameterNode parameterNode : fieldDeclarationArraylist) {
            fieldArrayList.add(new Field(parameterNode.getId(), parameterNode.getType()));
            fieldHashMap.put(parameterNode.getId(), parameterNode.getType());
        }

        // inserisco i metodi della classe attuale
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
                        res.add("La classe '" + declaredClass + " non è stata definita\n");
                    }
                } else {
                    // se i parametri sono "base", non oggetti
                    parameterTypeArrayList.add(parameterNode.getType());
                }
            }

            //prende i campi passati in input e li inserisce in methodArrayList per la SymbolTableEntry e in
            //methodHashMap per accedervi velocemente
            methodArrayList.add(new Method(methodNode.getID(), new FunType(parameterTypeArrayList, methodNode.getReturnType())));
            methodHashMap.put(methodNode.getID(), new FunType(parameterTypeArrayList, methodNode.getReturnType()));
        }


        // all'ID dichiarato si setta il tipo classe nella SymbolTableEntry
        try {
            classType = new ClassType(idClass, superclassType, fieldArrayList, methodArrayList);
            env.setDeclarationType(idClass, classType, 0); // TODO punto in cui viene dichiarato il tipo
        } catch (UndeclaredIDException e) {
            res.add(e.getMessage());
        }

        HashMap<String, SymbolTableEntry> hashMap = new HashMap<>();
        env.pushHashMap(hashMap);

        for (ParameterNode parameterNode : fieldDeclarationArraylist) {
            if (parameterNode.getType() instanceof ObjectType) {
                ClassType subClassAsParam = ((ObjectType) parameterNode.getType()).getClassType();

                //Ricavo la superclasse del parametro nel costruttore
                ClassType paramClass = ((ObjectType) parameterNode.getType()).getClassType();
                String TypeParamClass = paramClass.getClassID();
                ClassType superParams = null;
                try {
                    superParams = (ClassType) env.processUse(TypeParamClass).getType();
                } catch (UndeclaredIDException e) {
                    e.printStackTrace();
                }
                paramClass.setSuperClassType(superParams.getSuperClassType());

                //sottoclassi non possono essere parametri di superclassi
                if (subClassAsParam.isSubType(this.classType))
                    res.add("Non si può usare una sottoclasse nel costruttore della superclasse\n");
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
        if (!idSuperClass.isEmpty()) {
            try {
                //controllo che la classe che estende sia una classe
                if (!(env.getTypeOf(idSuperClass) instanceof ClassType))
                    res.add("L'ID usato come superclasse, " + idSuperClass + ", non è riferito a un tipo di classe\n");
            } catch (UndeclaredIDException exp) {
                res.add("La superclasse " + idSuperClass + " non è definita\n");
            }

            try {
                //controllo ovveride se possibile
                // TODO verificare campi, override metodi
                //prendo entry e tipo della superclasse
                SymbolTableEntry superClassEntry = env.processUse(idSuperClass);
                ClassType superClassType = (ClassType) superClassEntry.getType();

                //se si trovano due metodi con lo stesso nome controllo che uno sia sottotipo dell'altro
                //altrimenti override non è compatibile
                HashMap<String, FunType> superClassMethodsHashMap = superClassType.getMethodsMap();
                for (String method : methodHashMap.keySet()) {
                    if (superClassMethodsHashMap.containsKey(method)) {
                        if (!methodHashMap.get(method).isSubType(superClassMethodsHashMap.get(method))) {
                            res.add("Override incompatibile del metodo '" + method + "' della classe '" + idClass + "'\n");
                        }
                    }
                }
            } catch (UndeclaredIDException e) {
                res.add("La superclasse " + idSuperClass + " non è definita " + e.getMessage());
            }
        }

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("ClassNode: typeCheck ->\t");
        //typecheck di ogni parametro
        for (ParameterNode parameterNode : fieldDeclarationArraylist) {
            parameterNode.typeCheck();
        }
        // typecheck di otni metodo
        for (MethodNode methodNode : methodDeclarationArraylist) {
            methodNode.typeCheck();
        }

        return classType;
    }

    @Override
    public String codeGeneration() {
        // TODO Da rivedere e testare

        ArrayList<DispatchTableEntry> dispatchTable;
        // Creo una nuova dispatch table da zero se la classe non ha superclasse
        if(idSuperClass.equals("")) {
            dispatchTable=new ArrayList<>();
        }
        // Altrimenti la copio come base
        else {
            dispatchTable= DispatchTable.getDispatchTable(idSuperClass);
        }

        //contiene i metodi della superclasse
        HashMap<String, String> superClassMethodsHashMap = new HashMap<>();
        //aggiungo i metodi della superclasse alla hashmap
        for (DispatchTableEntry d : dispatchTable) {
            superClassMethodsHashMap.put(d.getMethodID(), d.getMethodLabel());
        }
        //contiene i metodi della classe attuale
        HashMap<String, String> currentClassMethodsHashMap = new HashMap<>();
        //aggiungo i metodi della classe attuale
        for (MethodNode m : methodDeclarationArraylist) {
            currentClassMethodsHashMap.put(m.getID(), m.codeGeneration());
        }
        //per ogni elemento della dispatch table:
        for (int i = 0; i < dispatchTable.size(); i++) {
            //gestione ovverride
            //prende il metodo dalla dispatch table, se presente
            String oldMethodID = dispatchTable.get(i).getMethodID();
            //lo sostituisce con il metodo proprio della classe
            String newMethodCode = currentClassMethodsHashMap.get(oldMethodID);
            //se l'ID esiste, vuol dire che è stato fatto override e la dispatch table viene aggiornata
            if (newMethodCode != null) {
                dispatchTable.set(i, new DispatchTableEntry(oldMethodID, newMethodCode));
            }
        }
        //per ogni metodo:
        for (MethodNode m : methodDeclarationArraylist) {
            //gestisce le funzioni aggiuntive della sottoclasse rispetto alla superclasse
            //contiene l'ID del metodo corrente
            String currentMethodID = m.getID();
            //se la superclasse non ha il metodo che si sta esaminando, lo si aggiunge alla dispatch table.
            if (superClassMethodsHashMap.get(currentMethodID) == null) {
                dispatchTable.add(new DispatchTableEntry(currentMethodID, currentClassMethodsHashMap.get(currentMethodID)));
            }

        }

        //viene aggiunta la dispatch table corrispondente alla classe esaminata
        //questa operazione viene eseguita anche se la dispatch table è vuota
        //in quanto può capitare di implementare una classe senza metodi
        DispatchTable.addDispatchTable(idClass, dispatchTable);

        return "";
    }
}
