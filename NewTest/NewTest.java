package NewTest;

import Type.IType;
import ast.INode;
import exceptions.LexerException;
import exceptions.ParserException;
import exceptions.SemanticException;
import exceptions.TypeException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import parserNew.FOOLLexer;
import parserNew.FOOLParser;
import parserNew.FOOLParser.ProgContext;
import util.FoolVisitorImpl;
import util.Semantic.SymbolTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NewTest {
    public static void main(String[] args) {

        // rimuovere lib->FOOLib prof, implementato in VM-> FunctionCode e Label

        try { //RILEVAZIONE INPUT
            System.out.println("Rilevazione Input...\n");
            String fileName = "prova";
            String foolFileName = fileName + ".fool";
            CharStream input = CharStreams.fromFileName(foolFileName);

            System.out.println("Analisi Lessicale...\n");

            System.out.println("input: " + input + "\n");

            FOOLLexer lexer = new FOOLLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();

            System.out.println("Tokens: " + tokens.getTokens() + "\n");
            System.out.println("Numero Tokens: " + tokens.getTokens().size() + "\n");

            //SIMPLISTIC BUT WRONG CHECK OF THE LEXER ERRORS
            if (lexer.errors.size() > 0) {
                System.out.println("The program was not in the right format. Exiting the compilation process now");
                throw new LexerException(lexer.errors);
            } else {
                System.out.println("LEXER OK");
            }

            System.out.println("Analisi Sintattica...\n");
            FOOLParser parser = new FOOLParser(tokens);
            ProgContext progContext = parser.prog(); //parser.prog riutilizzato


            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new ParserException("Errori rilevati: " + parser.getNumberOfSyntaxErrors() + "\n");
            }

            ParseTree tree = progContext;

            System.out.println("Sto per visualizzare l'AST...\n");
            //show AST in console
            System.out.println(tree.toStringTree(parser) + "\n");

            System.out.println("Analisi Semantica...\n");

            FoolVisitorImpl visitor = new FoolVisitorImpl();

            INode ast = visitor.visit(progContext); //generazione AST

            SymbolTable env = new SymbolTable();

            ArrayList<String> stringArrayListErr = ast.checkSemantics(env);


            if (stringArrayListErr.size() > 0) {
                throw new SemanticException(stringArrayListErr);
            }

            System.out.println("\nVisualizing AST...\n");
            System.out.println(ast.toPrint(""));
            System.out.println("--------------------------");

            IType type = ast.typeCheck(); //type-checking bottom-up
            System.out.println("Type checking ok! Il tipo del programma è: " + type.toPrint() + "\n");

            // CODE GENERATION
            String code = ast.codeGeneration();
            String asmFileName = fileName + ".asm";
            BufferedWriter out = new BufferedWriter(new FileWriter(asmFileName));
            out.write(code);
            out.close();
            System.out.println("Code generated! Assembling and running generated code.");
            System.out.println("Codice SVM Generato: #" + code.split("\n").length + " righe. Output: " + asmFileName);


            System.out.println("--------------------------");

            //TODO codeGeneration execution

/*            CharStream inputASM = CharStreams.fromFileName(asmFileName);
            SVMLexer lexerASM = new SVMLexer(inputASM);
            CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
            SVMParser parserASM = new SVMParser(tokensASM);
            parserASM.assembly();

            if (lexerASM.errors.size() > 0) {
                throw new LexerException(lexerASM.errors);
            }
            if (parserASM.getNumberOfSyntaxErrors() > 0) {
                throw new ParserException("Errore di parsing in SVM");
            }*/

            System.out.println("--------------------------");

        } catch (IOException | ParserException | LexerException | TypeException e) {
            System.out.println(e.getMessage() + "\n\n");
        }
    }
}
