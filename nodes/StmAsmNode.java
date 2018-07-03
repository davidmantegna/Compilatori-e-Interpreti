package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import type.IType;
import parser.FOOLParser.StmAssignmentContext;
import type.ObjectType;
import type.VoidType;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;

public class StmAsmNode implements INode {

    private String id;
    private INode exp;
    private StmAssignmentContext stmAssignmentContext;

    private int nestingLevel;
    private SymbolTableEntry entry;
    private IType idType;

    public StmAsmNode(String id, INode e, StmAssignmentContext c) {
        this.exp = e;
        this.stmAssignmentContext = c;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("StmAsmNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        try {
            idType = env.getTypeOf(id);
            entry = env.processUseIgnoreFun(id);
            nestingLevel = env.getNestingLevel();
        } catch (UndeclaredIDException e) {
            res.add(id + ": identificativo non definito\n");
        }

        res.addAll(exp.checkSemantics(env));

        // TODO Probabilmente da fare nel codeGen
/*        if (idType instanceof ObjectType) {
            boolean istanziato = entry.isInstanziato();
            System.out.println("-----------------------------" + istanziato);
            switch (exp.getClass().getName()) {
                case "nodes.NewNode":
                    istanziato = true;
                    break;
                case "nodes.NullNode":
                    istanziato = false;
                    break;
            }
            entry.setInstanziato(istanziato);
        }*/
        // TODO Probabilmente da fare nel codeGen

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("StmAsmNode: typeCheck -> \t");
        if (!exp.typeCheck().isSubType(idType)) {
            throw new TypeException("Valore incompatibile per la variabile " + id, stmAssignmentContext.exp());
        }
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration() + "\n";
    }
}
