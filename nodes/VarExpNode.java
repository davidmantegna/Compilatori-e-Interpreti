package nodes;

import codegen.VM.Label;
import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import org.antlr.v4.runtime.ParserRuleContext;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import type.*;

import java.util.ArrayList;

public class VarExpNode implements INode {

    private String identificatore;
    private ParserRuleContext parserRuleContext;
    private boolean isNegative;//variabile utilizzata per gestire il meno davanti agli identificatori
    private boolean isNot;//variabile utilizzata per gestire il not davanti agli identificatori

    private int nestingLevel;
    private SymbolTableEntry entry;

    //variabili utilizzate per i campi della classe
    private int thisNestingLevel;
    private int thisOffset;

    public VarExpNode(String identificatore, ParserRuleContext parserRuleContext, boolean isNegative, boolean isNot) {
        this.identificatore = identificatore;
        this.parserRuleContext = parserRuleContext;
        this.isNegative = isNegative;
        this.isNot = isNot;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("VarExpNode: checkSemantics -> \n");

        //cercare ID nella symbol table, con casi particolari per le classi
        ArrayList<String> res = new ArrayList<>();

        try {
            // è utilizzato per accedere ad un campo della classe
            entry = env.processUseIgnoreFun(identificatore);
            // true quando si è dentro ad una classe
            if (entry.isInsideClass()) {
                SymbolTableEntry thisPointer = env.processUse("this"); // ottengo le informazioni relative alla classe; this
                thisNestingLevel = thisPointer.getNestinglevel(); // scope di this (metodi) -> 2
                thisOffset = thisPointer.getOffset(); // offset del this (parte da 0) -> 0
            }
            nestingLevel = env.getNestingLevel(); // scope interno al metodo (per parametri e body)

            //serve per assegnare il supertipo dinamicamente agli oggetti

            if (entry.getType() instanceof ObjectType) {
                if (!entry.isInitialized()) {
                    res.add("L'oggetto '" + identificatore + "' non è stato inizializzato\n");
                } else {
                    ObjectType decType = (ObjectType) entry.getType();
                    res.addAll(decType.updateClassType(env));
                }
            }


        } catch (UndeclaredIDException e) {
            res.add("Errore:" + identificatore + ": identificativo non definito\n");
        }
        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
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
        //for e lwActivationRecord per gestire le funzioni annidate
        StringBuilder lwActivationRecord = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();

        if (entry.isInsideClass()) {
            for (int i = 0; i < nestingLevel - thisNestingLevel; i++)
                lwActivationRecord.append("lw\n");

            stringBuilder.append("push " + entry.getOffset() + "\n" + // pusho offset dell'ID
                    "lfp\n" + lwActivationRecord +
                    "heapoffset\n" +  // converto l'offset logico nell'offset fisico a cui l'identificatore si riferisce, poi lo carica sullo stack, utilizzato solo per i parametri dei metodi all'interno delle classi
                    "add\n" +
                    "lw\n"//carico sullo stack il valore dell'indirizzo ottenuto
            );

        } else {
            for (int i = 0; i < nestingLevel - entry.getNestinglevel(); i++) {// utilizzato al di fuori delle classi, per accedee alle variabili dichiarate nel Let
                lwActivationRecord.append("lw\n");
            }
            stringBuilder.append("push " + entry.getOffset() + "\n" + //metto offset dell'ID sullo stack
                    "lfp\n" + lwActivationRecord + //risalgo la catena statica
                    "add\n" +
                    "lw\n" //carico sullo stack il valore all'indirizzo ottenuto
            );
        }

        if (isNegative) {
            stringBuilder.append("push -1\n" + "mult\n");
        } else if (isNot) {
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
