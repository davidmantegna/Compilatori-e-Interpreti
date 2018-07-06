package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import parser.FOOLParser;
import type.ClassType;
import type.IType;
import type.ObjectType;
import util.Semantic.Field;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

import static parser.FOOLParser.*;

public class NewNode implements INode {

    private String idClass;
    private ArrayList<INode> argumentsArrayList;
    private NewexpContext newMethodContext;

    private ClassType classType;

    public NewNode(String idClass, ArrayList<INode> argumentsArrayList, NewexpContext newMethodContext) {
        this.idClass = idClass;
        this.argumentsArrayList = argumentsArrayList;
        this.newMethodContext = newMethodContext;

    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("VarAsmNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);
        ArrayList<String> res = new ArrayList<>();

        try {
            try {
                //controllo se la classe è stata dichiarata
                classType = (ClassType) env.getTypeOf(idClass);

                //controllo se il numero di parametri è corretto
                if (classType.getFields().size() != argumentsArrayList.size())
                    res.add("Instanziazione di una nuova classe " + idClass + " con numero errato di parametri\n");

                //chiamo la checkSemantic su tutti gli argomenti
                if (argumentsArrayList.size() > 0) {
                    for (INode node : argumentsArrayList)
                        res.addAll(node.checkSemantics(env));
                }
                env.processUse(idClass);
            } catch (Exception e1) {
                throw new UndeclaredIDException(idClass);
            }
        } catch (UndeclaredIDException e) {
            res.add(e.getMessage());
        }
        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("VarAsmNode: typeCheck -> \n");
        ArrayList<Field> fieldArrayList = classType.getFields();

        for (int i = 0; i < argumentsArrayList.size(); i++) {
            //controllo sul costruttore che i tipi dei parametri siano gli stessi dei tipi dei campi
            IType argumentType = argumentsArrayList.get(i).typeCheck();
            IType fieldType = fieldArrayList.get(i).getFieldType();
            if (!argumentType.isSubType(fieldType)) {
                throw new TypeException("Tipo errato per il parametro " + (i + 1) + " nell'invocazione del costruttore di " + idClass + "\n", newMethodContext);
            }
        }
        return new ObjectType(classType);
    }

    @Override
    public String codeGeneration() {
        // TODO codeGeneration NewMethod

        /*//new pusha, in ordine, gli argomenti, il numero di argomenti e la label della classe
        StringBuilder argsCode = new StringBuilder();
        for (INode arg : argumentsArrayList) {
            argsCode.append(arg.codeGeneration());
        }
        return argsCode
                + "push " + argumentsArrayList.size() + "\n"
                + "push class" +idClass + "\n"
                + "new\n";
    }*/
        return null;
    }
}
