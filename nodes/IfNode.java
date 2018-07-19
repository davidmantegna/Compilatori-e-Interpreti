package nodes;

import codegen.VM.Label;
import exceptions.TypeException;
import parser.FOOLParser.IfExpContext;
import symboltable.SymbolTable;
import type.BoolType;
import type.ClassType;
import type.IType;
import type.ObjectType;

import java.util.ArrayList;

public class IfNode implements INode {

    private INode conditionNode;
    private INode thenNode;
    private INode elseNode;
    private IfExpContext ctx;

    public IfNode(INode cond, INode then, INode el, IfExpContext c) {
        this.conditionNode = cond;
        this.thenNode = then;
        this.elseNode = el;
        this.ctx = c;
    }

    public INode getThenNode() {
        return thenNode;
    }

    public INode getElseNode() {
        return elseNode;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("IfNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();

        //checkSemantic sulla condizione
        res.addAll(conditionNode.checkSemantics(env));

        //checkSemantic sui rami then ed else
        res.addAll(thenNode.checkSemantics(env));
        res.addAll(elseNode.checkSemantics(env));

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        if (!conditionNode.typeCheck().isSubType(new BoolType()))
            throw new TypeException("Condizione non booleana", ctx);
        IType thenType = thenNode.typeCheck();
        IType elType = elseNode.typeCheck();
        ClassType superClassThen, superClassElse;

        if (elType instanceof ObjectType && thenType instanceof ObjectType) {
            ClassType classThen = ((ObjectType) thenType).getClassType();
            ClassType classElse = ((ObjectType) elType).getClassType();

            if (classThen.getClassID() == classElse.getClassID())
                return classThen;

            ArrayList<ClassType> hashMapThen = arrayListSuperClass(classThen);
            ArrayList<ClassType> hashMapElse = arrayListSuperClass(classElse);


            ClassType returnType;
            if (hashMapThen.size() < hashMapElse.size()) {
                returnType = confronto(hashMapThen, hashMapElse);
            } else {
                returnType = confronto(hashMapElse, hashMapThen);
            }

            if (returnType == null) {
                throw new TypeException("Tipi non compatibili nel then e nell'else", ctx);
            }
            System.out.println("Tipo restituito dall IF:  prima superclasse in comune-> class " + returnType.getClassID());
            return returnType;
/*

            superClassThen = classThen.getSuperClassType();
            superClassElse = classElse.getSuperClassType();


            if (superClassThen != null) {
                while (superClassThen.getSuperClassType() != null) {
                    superClassThen = superClassThen.getSuperClassType();
                }
            }

            if (superClassElse != null) {
                while (superClassElse.getSuperClassType() != null) {
                    superClassElse = superClassElse.getSuperClassType();
                }
            }

            if (superClassThen != null && superClassElse != null) {
                if (superClassThen.getClassID() == superClassElse.getClassID())
                    return superClassThen;
            }*/
        }

        if (thenType.isSubType(elType)) return thenType;
        else if (elType.isSubType(thenType)) return elType;
        else throw new TypeException("Tipi non compatibili nel then e nell'else", ctx);
    }

    @Override
    public String codeGeneration() {

        String thenBranch = Label.nuovaLabelString("Then");
        String exit = Label.nuovaLabelString("Exit");
        return conditionNode.codeGeneration()
                + "push 1\n"
                + "beq " + thenBranch + "\n"
                + elseNode.codeGeneration()
                + "b " + exit + "\n\n"
                + thenBranch + ":\n"
                + thenNode.codeGeneration() + "\n\n"
                + exit + ":\n";
    }


    private ArrayList<ClassType> arrayListSuperClass(ClassType classType) {
        ClassType superClass = classType.getSuperClassType();
        ArrayList<ClassType> classTypes = new ArrayList<>();

        classTypes.add(classType);
        if (superClass != null) {
            while (superClass != null) {
                classTypes.add(superClass);
                superClass = superClass.getSuperClassType();
            }
        }

        return classTypes;
    }

    private ClassType confronto(ArrayList<ClassType> uno, ArrayList<ClassType> due) {
        // uno arraylist più corto, due arraylist più lungo
        for (ClassType sUno : uno) {
            for (ClassType sDue : due) {
                if (sUno.equals(sDue)) {
                    return sUno;
                }
            }
        }
        return null;
    }
}