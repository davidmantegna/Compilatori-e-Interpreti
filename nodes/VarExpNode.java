package nodes;

import codegen.VM.Label;
import type.*;
import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import org.antlr.v4.runtime.ParserRuleContext;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;

import java.util.ArrayList;

public class VarExpNode implements INode {

    private String identificatore;
    private ParserRuleContext parserRuleContext;
    private boolean isNegative;//variabile utilizzata per gestire il meno davanti agli identificatori
    private boolean isNot;//variabile utilizzata per gestire il not davanti agli identificatori

    private int nestingLevel;
    private SymbolTableEntry entry;

    //variabili utilizzate da this
    /*private int thisNestingLevel;
    private int thisOffset;*/

    public VarExpNode(String identificatore, ParserRuleContext parserRuleContext, boolean isNegative, boolean isNot) {
        this.identificatore = identificatore;
        this.parserRuleContext = parserRuleContext;
        this.isNegative = isNegative;
        this.isNot = isNot;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("VarExpNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);

        //cercare ID nella symbol table, con casi particolari per le classi
        ArrayList<String> res = new ArrayList<>();

        try {
            //è utilizzato per poter definire un oggetto con lo stesso nome di un metodo
            //all'interno di una classe
            entry = env.processUseIgnoreFun(identificatore);
            nestingLevel = env.getNestingLevel();

            //serve per assegnare il supertipo dinamicamente agli oggetti
            //vedi Test

            if (entry.getType() instanceof ObjectType) {
                ObjectType decType = (ObjectType) entry.getType();
                res.addAll(decType.updateClassType(env));
            }


        } catch (UndeclaredIDException e) {
            res.add(identificatore + ": identificativo non definito\n");
        }
        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        // TODO Object Orientation
        if (isNot) {
            if (!entry.getType().isSubType(new BoolType())) {
                throw new TypeException("Tipo incompatibile per l'operatore NOT. È richiesto un booleano.", parserRuleContext);
            }
        }
        if (isNegative) {
            if (!entry.getType().isSubType(new IntType())) {
                throw new TypeException("Tipo incompatibile per l'operatore MINUS. È richiesto un intero", parserRuleContext);
            }
        }

        if (entry.getType() instanceof FunType) {
            throw new TypeException("Utilizzo errato di identificativo di funzione", parserRuleContext);
        }
        return entry.getType();
    }

    @Override
    public String codeGeneration() {
        StringBuilder getActivationRecord = new StringBuilder();

        //for e getActivationRecord per gestire le funzioni annidate
        for (int i = 0; i < nestingLevel - entry.getNestinglevel(); i++) {
            getActivationRecord.append("lw\n");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("push " + entry.getOffset() + "\n" + //metto offset sullo stack
                "lfp\n" + getActivationRecord + //risalgo la catena statica
                "add\n" +
                "lw\n" //carico sullo stack il valore all'indirizzo ottenuto
        );

        if (isNegative) {
            stringBuilder.append("push -1\n" +
                    "times\n");
        } else if (isNot) {
            //stringBuilder.append("not\n"); // TODO al momento l'ho rimosso
            String thenBranch = Label.nuovaLabelString("Then");
            String exit = Label.nuovaLabelString("Exit");
            stringBuilder.append("push 1 \n" +
                    "beq " + thenBranch + "\n" +
                    "push 1\n" +
                    "b " + exit + "\n" +
                    thenBranch + ":\n" +
                    "push 0\n" +
                    exit + ":\n"
            );
        }

        return String.valueOf(stringBuilder);
    }
}
