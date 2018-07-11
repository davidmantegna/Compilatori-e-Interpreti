package codegen;

import exceptions.HeapOverflowError;
import exceptions.SegmentationFaultError;
import exceptions.StackOverflowError;
import parser.SVMParser;

import java.util.*;

public class ExecuteVM {

    public static final int START_ADDRESS = 1234;   //indirizzo di partenza
    private static final int MEMSIZE = 135;        //dimensione totale della memoria

    private ArrayList<String> output = new ArrayList<>();   //contiene l'esito della print o gli errori

    private int[] memory = new int[MEMSIZE];    //memoria
    private int[] code;                         //codice da eseguire

    private int hp = START_ADDRESS;             //heap pointer
    private int ip = 0;                         //istruction pointer
    private int sp = START_ADDRESS + MEMSIZE;   //stack pointer
    private int fp = START_ADDRESS + MEMSIZE;   //frame pointer all'inizio punta alla stessa locazione dello stack pointer
    private int ra;
    private int rv;

    private Heap heap = new Heap(MEMSIZE);
    private HashSet<HeapCell> heapInUso = new HashSet<>();

    public ExecuteVM(int[] c) {
        code = c;
    }

    //  prende il valore contenuto nell'indirizzo di memoria 'address'
    private int getMemory(int address) throws SegmentationFaultError {
        int location = address - START_ADDRESS;
        if (location < 0 || location >= MEMSIZE) {
            throw new SegmentationFaultError();
        }
        return memory[location];
    }

    //  inserisce il valore 'value' nell'indirizzo di memoria indicato da'address'
    private void setMemory(int address, int value) throws SegmentationFaultError {
        int location = address - START_ADDRESS;
        if (location < 0 || location >= MEMSIZE) {
            throw new SegmentationFaultError();
        }
        memory[location] = value;
    }


    // ritorna il valore presente sullo stack e reimposta il valore dell'indirizzo a '0'
    private int pop() throws SegmentationFaultError {
        int res = getMemory(sp);
        setMemory(sp++, 0);
        return res;
    }

    //  inserisce il valore 'v' sullo stack nella locazione di memoria puntata da 'sp'
    private void push(int v) throws StackOverflowError, SegmentationFaultError {
        if (sp - 1 < hp) {
            throw new StackOverflowError();
        }
        setMemory(--sp, v);
    }

    private HashMap<Integer, Integer> memoryFinalMap = new HashMap<>();

    public ArrayList<String> cpu() {
        try {
            while (true) {
                int bytecode = code[ip++];
                int v1, v2;
                int address;
                switch (bytecode) {
                    case SVMParser.PUSH:
                        push(code[ip++]);
                        break;
                    case SVMParser.POP:
                        pop();
                        break;
                    case SVMParser.STOREW: //store in the memory cell pointed by top the value next
                        address = pop();
                        setMemory(address, pop());
                        break;
                    case SVMParser.LOADW: //load a value from the memory cell pointed by top
                        push(getMemory(pop()));
                        break;
                    case SVMParser.BRANCH: //jump to label
                        address = code[ip];
                        ip = address;
                        break;
                    case SVMParser.BRANCHEQ:
                        address = code[ip++]; // address ramo then
                        v2 = pop();
                        v1 = pop();
                        if (v1 == v2) ip = address;
                        break;
                    case SVMParser.BRANCHLESS:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v1 < v2) ip = address;
                        break;
                    case SVMParser.BRANCHLESSEQ:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v1 <= v2) ip = address;
                        break;
                    case SVMParser.BRANCHGREATHER:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v1 > v2) ip = address;
                        break;
                    case SVMParser.BRANCHGREATHEREQ:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v1 >= v2) ip = address;
                        break;
                    case SVMParser.JS: // jump to instruction pointed by top of stack and store next instruction in ra
                        address = pop();
                        ra = ip;
                        ip = address;
                        break;
                    case SVMParser.STORERA: // store top into ra
                        ra = pop();
                        break;
                    case SVMParser.LOADRA:  // load from ra
                        push(ra);
                        break;
                    case SVMParser.STORERV: // store top into rv
                        rv = pop();
                        break;
                    case SVMParser.LOADC: //mette sullo stack l'indirizzo del metodo all'interno di code
                        int indirizzoCodice = pop();
                        push(code[indirizzoCodice]);
                        break;
                    case SVMParser.LOADRV: // load from rv
                        push(rv);
                        break;
                    case SVMParser.LOADFP: // load frame pointer in the stack
                        push(fp);
                        break;
                    case SVMParser.LOADHP: // load heap pointer in the stack
                        push(hp);
                        break;
                    case SVMParser.STOREFP: // store top into frame pointer
                        fp = pop();
                        break;
                    case SVMParser.COPYFP: // copy stack pointer into frame pointer
                        fp = sp;
                        break;
                    case SVMParser.ADD:
                        v1 = pop(); //   register address
                        v2 = pop(); //   offset
                        push(v2 + v1);
                        break;
                    case SVMParser.TIMES:
                        v1 = pop();
                        v2 = pop();
                        push(v2 * v1);
                        break;
                    case SVMParser.DIV:
                        v1 = pop();
                        v2 = pop();
                        push(v2 / v1);
                        break;
                    case SVMParser.SUB:
                        v1 = pop();
                        v2 = pop();
                        push(v2 - v1);
                        break;
                    case SVMParser.AND:
                        v1 = pop();
                        v2 = pop();
                        if ((v1 == 1) && (v2 == 1)) {
                            push(1);
                        } else {
                            push(0);
                        }
                        break;
                    case SVMParser.OR:
                        v1 = pop();
                        v2 = pop();
                        if ((v1 == 0) && (v2 == 0)) {
                            push(0);
                        } else {
                            push(1);
                        }
                        break;
                    case SVMParser.COPY:    //duplica il valore in cima allo stack
                        push(getMemory(sp));
                        break;
                    case SVMParser.HEAPOFFSET:  //converte l'offset di un campo di un oggetto
                        // nell'offset reale tra l'indirizzo dell'oggetto nello heap e l'indirizzo del campo
                        int indirizzoOggetto = pop(); // indirizzo dell'oggetto del quale si richiede il valore del campo
                        int offsettOggetto = pop();  // offset dell'oggetto rispetto all'inizio del suo spazio nello heap
                        HeapCell heapCell = heapInUso
                                .stream()
                                .filter(cell -> cell.getIndex() == indirizzoOggetto)
                                .reduce(new HeapCell(0, null), (prev, curr) -> curr);
                        for (int i = 0; i < offsettOggetto; i++) {
                            heapCell = heapCell.next;
                        }
                        int indirizzoCampo = heapCell.getIndex();
                        int offsettReale = indirizzoCampo - indirizzoOggetto;
                        push(offsettReale);
                        push(indirizzoOggetto);
                        break;
                    case SVMParser.NEW:
                        // Sulla testa dello stack deve esserci l'indirizzo della propria dispatch table, il numero degli argomenti e il valore degli argomenti
                        // Prelevo queste informazioni dallo stack
                        int indirizzoDispatchTable = pop();
                        int numeroArgomenti = pop();
                        int[] argomenti = new int[numeroArgomenti];
                        for (int i = numeroArgomenti - 1; i >= 0; i--) {
                            argomenti[i] = pop();
                        }

                        HeapCell memoriaAllocata;
                        // Alloco memoria per gli argomenti e per l'indirizzo alla dispatch table
                        memoriaAllocata = heap.allocate(numeroArgomenti + 1);

                        //Considero la memoria appena allocata come in uso
                        heapInUso.add(memoriaAllocata);

                        int heapMemoryStart = memoriaAllocata.getIndex();
                        // Inserisco l'indirizzo della dispatch table ed avanzo nella memoria ottenuta
                        setMemory(memoriaAllocata.getIndex(), indirizzoDispatchTable);
                        memoriaAllocata = memoriaAllocata.next;

                        // Inserisco un argomento in ogni indirizzo di memoria
                        for (int i = 0; i < numeroArgomenti; i++) {
                            setMemory(memoriaAllocata.getIndex(), argomenti[i]);
                            memoriaAllocata = memoriaAllocata.next;
                        }
                        // Metto sullo stack l'indirizzo della prima cella dell'oggetto che ho istanziato
                        push(heapMemoryStart);

                        //gestisco il caso in cui l'heap superi lo stack, r
                        if (heap.getFreeIndex() > hp) {
                            hp = heap.getFreeIndex();
                        }
                        break;
                    case SVMParser.PRINT:
                        output.add((sp < START_ADDRESS + MEMSIZE) ? Integer.toString(getMemory(sp)) : "Lo stack è vuoto");
                        pop();
                        break;
                    case SVMParser.HALT:
                        for (int i = MEMSIZE - 1; i >= 0; i--) {
                            memoryFinalMap.put(i, memory[i]);
                        }
                        if (output.size() == 0) {
                            output.add((sp < START_ADDRESS + MEMSIZE) ? Integer.toString(getMemory(sp)) : "Lo stack è vuoto");
                        }
                        return output;
                }
            }
        } catch (HeapOverflowError | SegmentationFaultError | StackOverflowError e) {
            output.add("Errore: " + e.getMessage());
            return output;
        }
    }

    public void print() {
        int i;
        System.out.println("START_ADDRESS: " + START_ADDRESS + "\t\t" + "MEMSIZE: " + MEMSIZE + "\t\t" + "Memoria: " + (START_ADDRESS + MEMSIZE));
        System.out.println("SP init: " + (START_ADDRESS + MEMSIZE) + "\t\t\tFP init: " + (START_ADDRESS + MEMSIZE) + "\t\tHEAP init: " + START_ADDRESS + "\n");
        for (i = START_ADDRESS + MEMSIZE - 1; i >= sp; i--) {
            System.out.print("addr: " + i + " location: " + (i - START_ADDRESS) + " -> val: " + getMemory(i) + "\n");
        }

    }

    public void getMemoryFinalMap() {
        Iterator it = memoryFinalMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if ((Integer) pair.getValue() != 0) {
                System.out.println(pair.getKey() + " = " + pair.getValue());
            }
        }
    }

}
