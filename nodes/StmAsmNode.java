package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import parser.FOOLParser.StmAssignmentContext;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import type.IType;
import type.VoidType;

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
        System.out.print("StmAsmNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();


        try {
            idType = env.getTypeOf(id);
            entry = env.processUseIgnoreFun(id);
            nestingLevel = env.getNestingLevel();

            if (exp instanceof NewNode) {
                if (entry.isInitialized()) {// vieto di istanziare più volte l'oggetto
                    // res.add("L'oggetto '" + id + "' è già stato istanziato\n");
                } else {
                    entry.setInitialized(true);
                }
            } else if (exp instanceof MethodCallNode) {
                entry.setInitialized(true);
            }

        } catch (UndeclaredIDException e) {
            res.add("Errore: " + id + ": identificativo non definito\n");
        }

        res.addAll(exp.checkSemantics(env));

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("StmAsmNode: typeCheck -> \t");

        if (exp instanceof NullNode) {
            throw new TypeException("Oggetto istanziato, impossibile annullare l'istanza di '" + id + "'", stmAssignmentContext);
        }

        if (!exp.typeCheck().isSubType(idType)) {
            throw new TypeException("Valore incompatibile per la variabile " + id, stmAssignmentContext.exp());
        }
        return new VoidType();
    }

    @Override
    public String codeGeneration() {

        StringBuilder lwActivationRecord = new StringBuilder();
        for (int i = 0; i < nestingLevel - entry.getNestinglevel(); i++)
            lwActivationRecord.append("lw\n");

        return exp.codeGeneration()
                + "push " + entry.getOffset() + "\n"
                + "lfp \n" +
                lwActivationRecord
                + "add \n"
                + "sw \n";
    }
}
