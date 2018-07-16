package nodes;

import exceptions.MultipleIDException;
import exceptions.TypeException;
import parser.FOOLParser.VarasmContext;
import symboltable.SymbolTable;
import type.IType;
import type.ObjectType;

import java.util.ArrayList;

public class VarAsmNode implements INode {
    private String id;
    private IType assignedType;
    private INode exp;
    private VarasmContext varasmContext;
    private boolean initialized;
    private boolean insideClass;

    public VarAsmNode(String id, IType assignedType, INode exp, VarasmContext varasmContext, boolean initialized) {
        this.id = id;
        this.assignedType = assignedType;
        this.exp = exp;
        this.varasmContext = varasmContext;
        this.initialized = initialized;
        this.insideClass = false;
    }

    public String getId() {
        return id;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("VarAsmNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();

        // gestisco il tipo ID
        // Se sto instanziando un nuovo oggetto, aggiorno le informazioni, e verifico se la classe esiste
        if (assignedType instanceof ObjectType) {
            ObjectType decType = (ObjectType) assignedType;
            res.addAll(decType.updateClassType(env));
        }

        res.addAll(exp.checkSemantics(env));

        try {
            env.processDeclarationClass(id, assignedType, env.getOffset(), initialized, insideClass);
            env.decreaseOffset();
        } catch (MultipleIDException e) {
            res.add(e.getMessage());
        }
        return res;
    }

    //valore di ritorno non utilizzato
    @Override
    public IType typeCheck() throws TypeException {
        //System.out.print("VarAsmNode: typeCheck -> \n");

        if (assignedType instanceof ObjectType) {
            if (exp instanceof NullNode) {
                return assignedType;
            }
            if (exp instanceof IfNode) {
                IfNode ifNode = (IfNode) exp;
                if (ifNode.getThenNode() instanceof NullNode && ifNode.getElseNode() instanceof NullNode) {
                    return assignedType;
                }
                assignedType = ((ObjectType) assignedType).getClassType();
            }

        } else {
            if (exp instanceof NullNode) {
                throw new TypeException("Valore incompatibile per la variabile: " + id, varasmContext);
            }
        }

        if (!exp.typeCheck().isSubType(assignedType)) {
            throw new TypeException("Valore incompatibile per la variabile: " + id, varasmContext.exp());
        }
        return assignedType;
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration();
    }
}
