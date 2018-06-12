package Type;

public interface IType {
    enum IDType { //tipi supportati dal linguaggio
        INT,
        BOOL,
        ARROW,
        CLASS,
        OBJECT
    }

    IDType getID(); //restituisce uno dei tipi possibili del linguaggio

    boolean isSubType(IType t); //per gestire le regole di subtyping

    String toPrint(); //per stampare ad output il tipo finale del programma
}
