package nodes;

import type.BoolType;
import type.FunType;
import type.IType;
import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import org.antlr.v4.runtime.ParserRuleContext;
import type.IntType;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;

public class VarExpNode implements INode {

    private String identificatore;
    private ParserRuleContext parserRuleContext;
    private boolean isNegative;//variabile utilizzata per gestire il meno davanti agli identificatori
    private boolean isNot;//variabile utilizzata per gestire il not davanti agli identificatori

    private int nestingLevel;
    private SymbolTableEntry entry;

    public VarExpNode(String identificatore, ParserRuleContext parserRuleContext, boolean isNegative, boolean isNot) {
        this.identificatore = identificatore;
        this.parserRuleContext = parserRuleContext;
        this.isNegative = isNegative;
        this.isNot = isNot;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("VarExpNode: checkSemantics -> \n\t" + env.toString() + "\n");
        //TODO gestire caso not(int) e ritornare eccezione e nodo relativo
        //cercare ID nella symbol table, con casi particolari per le classi
        ArrayList<String> res = new ArrayList<>();

        try {
            //è utilizzato per poter definire un oggetto con lo stesso nome di un metodo
            //all'interno di una classe
            entry = env.processUseIgnoreFun(identificatore);
            //getInsideClass è true quando si è dentro ad una classe
            if (entry.getInsideClass()) {
                // TODO Object Orientation
                //utilizzato per conoscere il nestingLevel e offset di this
//                SymbolTableEntry thisPointer = env.processUse("this");
//                thisNestingLevel = thisPointer.getNestinglevel();
//                thisOffset = thisPointer.getOffset();
            }
            nestingLevel = env.getNestingLevel();

            //serve per assegnare il supertipo dinamicamente agli oggetti
            //vedi TestNew
            // TODO Object Orientation
//            if (entry.getType() instanceof ObjectType) {
//                ObjectType decType = (ObjectType) entry.getType();
//                res.addAll(decType.updateClassType(env));
//            }


        } catch (UndeclaredIDException e) {
            res.add(identificatore + ": identificativo non definito\n");
        }

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        // TODO Object Orientation
        if(isNot){
            if (!entry.getType().isSubType(new BoolType())) {
                throw new TypeException("Tipo incompatibile per l'operatore NOT. È richiesto un booleano.", parserRuleContext);
            }
        }
        if(isNegative){
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

        if (entry.getInsideClass()) {
            // TODO Object Orientation
            return "OO";
        } else {
            //for e getActivationRecord per gestire le funzioni annidate
            for (int i = 0; i < nestingLevel - entry.getNestinglevel(); i++)
                getActivationRecord.append("lw\n");
            if (isNegative) {
                return "push " + entry.getOffset() + "\n" + //metto offset sullo stack
                        "lfp\n" + getActivationRecord + //risalgo la catena statica
                        "add\n" +
                        "lw\n" + //carico sullo stack il valore all'indirizzo ottenuto
                        "push -1\n" +
                        "mult\n";
            } else {
                return "push " + entry.getOffset() + "\n" + //metto offset sullo stack
                        "lfp\n" + getActivationRecord + //risalgo la catena statica
                        "add\n" +
                        "lw\n"; //carico sullo stack il valore all'indirizzo ottenuto
            }
        }
    }
}