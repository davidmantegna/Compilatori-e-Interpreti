import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.ExecuteVM;
import parser.FOOLLexer;
import parser.FOOLParser;
import parser.SVMLexer;
import parser.SVMParser;
import util.Environment;
import util.Semantic.SymbolTable;
import util.SemanticError;
import astBrutto.FoolVisitorImpl;
import astBrutto.Node;

public class Test {
    public static void main(String[] args) throws Exception {

        String fileName = "prova.fool";

        //FileInputStream is = new FileInputStream(fileName);
        //ANTLRInputStream input = new ANTLRInputStream(is);
        CharStream input = CharStreams.fromFileName(fileName);

        FOOLLexer lexer = new FOOLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        //SIMPLISTIC BUT WRONG CHECK OF THE LEXER ERRORS
        if (lexer.lexicalErrors > 0) {
            System.out.println("The program was not in the right format. Exiting the compilation process now");
        } else {

            FOOLParser parser = new FOOLParser(tokens);

            FoolVisitorImpl visitor = new FoolVisitorImpl();

            Node ast = visitor.visit(parser.prog()); //generazione AST

            SymbolTable env = new SymbolTable();
            ArrayList<SemanticError> err = ast.checkSemantics(env);

            if (err.size() > 0) {
                System.out.println("You had: " + err.size() + " errors:");
                for (SemanticError e : err)
                    System.out.println("\t" + e);
            } else {


                System.out.println("Visualizing AST...");
                System.out.println(ast.toPrint(""));

                Node type = ast.typeCheck(); //type-checking bottom-up
                System.out.println(type.toPrint("Type checking ok! Type of the program is: "));


                // CODE GENERATION  prova.fool.asm
                String code = ast.codeGeneration();
                //code += DispatchTable.generaCodiceDispatchTable();

                File svmFile = new File(fileName + ".svm");
                BufferedWriter svmWriter = new BufferedWriter(new FileWriter(svmFile.getAbsoluteFile()));
                svmWriter.write(code);
                svmWriter.close();

                System.out.println("Codice SVM generato: (" + code.split("\n").length + " linee). Output visibile in " + fileName + ".svm. \n");

                //Scommenta se vuoi vedere l'output del codice a console
                //System.out.println(code);

                CharStream inputASM = CharStreams.fromFileName(svmFile.getAbsolutePath());
                SVMLexer lexerASM = new SVMLexer(inputASM);
                CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
                SVMParser parserASM = new SVMParser(tokensASM);

                parserASM.assembly();

                System.out.println("You had: " + lexerASM.lexicalErrors + " lexical errors and " + parserASM.getNumberOfSyntaxErrors() + " syntax errors.");
                if (lexerASM.lexicalErrors > 0 || parserASM.getNumberOfSyntaxErrors() > 0) System.exit(1);

                System.out.println("Starting Virtual Machine...");
                ExecuteVM vm = new ExecuteVM(parserASM.code);
                vm.cpu();
            }
        }


    }
}
