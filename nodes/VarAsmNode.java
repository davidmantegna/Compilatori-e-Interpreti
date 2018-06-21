package nodes;

import type.IType;
import exceptions.MultipleIDException;
import exceptions.TypeException;
import parser.FOOLParser.VarasmContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class VarAsmNode implements INode {

    private String id;
    private IType assignedType;
    private INode exp;
    private VarasmContext varasmContext;

    public VarAsmNode(String id, IType type, INode exp, VarasmContext varasmContext) {
        this.id = id;
        this.assignedType = type;
        this.exp = exp;
        this.varasmContext = varasmContext;
    }

    public String getId() {
        return id;
    }

    //valore di ritorno non utilizzato
    @Override
    public IType typeCheck() throws TypeException {
        if (!exp.typeCheck().isSubType(assignedType)) {
            throw new TypeException("Valore incompatibile per la variabile " + id, varasmContext.exp());
        }
        return assignedType;
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("VarAsmNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        // TODO aggiungere controllo per instanziazione di un nuovo oggetto

        res.addAll(exp.checkSemantics(env));

        try {
            env.processDeclaration(id, assignedType, env.getOffset());
            //Perché decrementa offset nella symbolTable?!
            env.decreaseOffset();
        } catch (MultipleIDException e) {
            res.add(e.getMessage());
        }

        return res;
    }
}
