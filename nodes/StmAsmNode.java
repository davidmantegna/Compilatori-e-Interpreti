package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import parser.FOOLParser.StmAssignmentContext;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import type.BoolType;
import type.IType;
import type.IntType;
import type.VoidType;

import java.util.ArrayList;

public class StmAsmNode implements INode {

    private String id;
    private INode exp;
    private StmAssignmentContext stmAssignmentContext;

    private int nestingLevel;
    private SymbolTableEntry entry;
    private IType idType;

    private int thisNestingLevel;
    private int thisOffset;

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
        //System.out.print("StmAsmNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();


        try {
            idType = env.getTypeOf(id); // info ottenuta dal nestingLevel 1, dove sono dichiarati tutti i campi della classe
            entry = env.processUseIgnoreFun(id);

            if (entry.isInsideClass()) {
                SymbolTableEntry thisPointer = env.processUse("this"); // ottengo le informazioni relative alla classe; this
                thisNestingLevel = thisPointer.getNestinglevel(); // scope di this (metodi) -> 2
                thisOffset = thisPointer.getOffset(); // offset del this (parte da 0) -> 0
            }
            nestingLevel = env.getNestingLevel();

            if ((entry.getType() instanceof IntType || entry.getType() instanceof BoolType) && exp instanceof NullNode) {
                res.add("Errore: impossibile assegnare il valore null ad una variabile di tipo '" + entry.getType().getID().toString() + "'\n");
            }

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
        //System.out.print("StmAsmNode: typeCheck -> \t");

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
        if (entry.isInsideClass()) {
            for (int i = 0; i < nestingLevel - thisNestingLevel; i++)
                lwActivationRecord.append("lw\n");

            return exp.codeGeneration()
                    + "push " + entry.getOffset() + "\n" // push offset dell'ID
                    + "lfp \n" + lwActivationRecord  //risalgo la catena statica
                    + "heapoffset\n"   // converto l'offset logico nell'offset fisico a cui l'identificatore si riferisce, poi lo carica sullo stack, utilizzato solo per i parametri dei metodi all'interno delle classi
                    + "add\n"
                    + "sw\n"; // aggiorno il valore dell'ID

        } else {
            for (int i = 0; i < nestingLevel - entry.getNestinglevel(); i++)
                lwActivationRecord.append("lw\n");

            return exp.codeGeneration()
                    + "push " + entry.getOffset() + "\n"
                    + "lfp \n" + lwActivationRecord //risalgo la catena statica
                    + "add \n"
                    + "sw \n";
        }
    }
}
