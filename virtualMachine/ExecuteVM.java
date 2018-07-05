package virtualMachine;

import exceptions.HeapOverflowError;
import exceptions.SegmentationFaultError;
import parser.SVMParser;

import java.util.ArrayList;
import java.util.HashSet;

public class ExecuteVM {

    // TODO codegen() for all nodes

    public static final int START_ADDRESS = 1234;   //indirizzo di partenza
    private static final int MEMSIZE = 135;        //dimensione totale della memoria
    //private static final int GARBAGE_THRESHOLD = Math.max((MEMSIZE / 100) * 10, 10);    //limite superato il quale agisce il gargabe collector

    private ArrayList<String> output = new ArrayList<>();   //contiene l'esito della print o gli errori

    private int[] memory = new int[MEMSIZE];    //memoria
    private int[] code;                         //codice da eseguire

    private int hp = START_ADDRESS;             //heap pointer
    private int ip = 0;                         //istruction pointer
    private int sp = START_ADDRESS + MEMSIZE;   //stack pointer
    private int fp = START_ADDRESS + MEMSIZE;   //frame pointerall'inizio punta alla stessa locazione dello stack pointer
    private int ra;
    private int rv;

    private Heap heap = new Heap(MEMSIZE);
    private HashSet<HeapCell> heapInUso = new HashSet<>();

    public ExecuteVM(int[] c) {
        code = c;
    }

    //prende il valore contenuto all'indirizzo passato
    private int getMemory(int address) throws SegmentationFaultError {
        int location = address - START_ADDRESS;
        if (location < 0 || location >= MEMSIZE) {
            throw new SegmentationFaultError();
        }
        return memory[location];
    }

    //setta il valore passato all'indirizzo passato
    private void setMemory(int address, int value) throws SegmentationFaultError {
        int location = address - START_ADDRESS;
        if (location < 0 || location >= MEMSIZE) {
            throw new SegmentationFaultError();
        }
        memory[location] = value;
    }


    //TODO Garbage Collector

    /*// Garbage collector con tecnica mark and sweep
    private void garbageCollector() throws SegmentationFaultError {
        HashMap<Integer, Boolean> hashMap = new HashMap<>(); //(indice cella, è usata true/false)
        // Inizializzo a false tutti gli oggetti
        for (HeapCell m : heapInUso) {
            hashMap.put(m.getIndex(), false);
        }
        // Se viene trovato sullo stack l'indirizzo di un oggetto, setto la hashMap a true
        for (int i = MEMSIZE + START_ADDRESS - 1; i >= sp; i--) {
            if (hashMap.containsKey(getMemory(i))) {
                hashMap.put(getMemory(i), true);
            }
        }
        //Se viene trovato nel registro rv, setto la hashMap a true
        if (hashMap.containsKey(rv)) {
            hashMap.put(rv, true);
        }
        for (HeapCell heapCell : heapInUso) {
            //se hashMap.get(heapCell.getIndex()) ritorna true, vuol dire che l'oggetto è in uso;
            //quindi con ! non entro dentro al ciclo che lo deallocherebbe
            if (!hashMap.get(heapCell.getIndex())) {
                heap.deallocate(heapCell);
            }
        }
        //se hashMap.get(m.getIndex()) è true, vuol dire che tale elemento è in uso
        //quindi con ! non lo vado a rimuovere dal hashset
        heapInUso.removeIf(m -> !hashMap.get(m.getIndex()));

    }*/

    //ritorno il valore presente sullo stack
    private int pop() throws SegmentationFaultError {
        int res = getMemory(sp);
        setMemory(sp++, 0);
        return res;
    }

    //metto il valore passato sullo stack
    private void push(int v) throws StackOverflowError, SegmentationFaultError {
        if (sp - 1 < hp) {
            throw new StackOverflowError();
        }
        setMemory(--sp, v);
    }

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
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v2 == v1) ip = address;
                        break;
                    case SVMParser.BRANCHLESS:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v2 < v1) ip = address;
                        break;
                    case SVMParser.BRANCHLESSEQ:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v2 <= v1) ip = address;
                        break;
                    case SVMParser.BRANCHGREATHER:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v2 > v1) ip = address;
                        break;
                    case SVMParser.BRANCHGREATHEREQ:
                        address = code[ip++];
                        v1 = pop();
                        v2 = pop();
                        if (v2 >= v1) ip = address;
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
                    case SVMParser.LOADRV: // load from rv
                        push(rv);
                        break;
                    case SVMParser.LOADFP: // load frame pointer in the stack
                        push(fp);
                        break;
                    case SVMParser.STOREFP: // store top into frame pointer
                        fp = pop();
                        break;
                    case SVMParser.COPYFP: // copy stack pointer into frame pointer
                        fp = sp;
                        break;
                    case SVMParser.ADD:
                        v1 = pop();
                        v2 = pop();
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
                    case SVMParser.NOT:
                        v1 = pop();
                        if (v1 == 1) {
                            push(0);
                        }else {
                            push(1);
                        }
                        break;
                    case SVMParser.LOADC: //mette sullo stack l'indirizzo del metodo all'interno di code
                        int indirizzoCodice = pop();
                        push(code[indirizzoCodice]);
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

                        //TODO garbage
                        /*//Se il valore assoluto della differenza tra sp e hp supera il valore
                        //della soglia massima, viene eseguito il garbage collector
                        if (Math.abs(sp - hp) <= GARBAGE_THRESHOLD) {
                            garbageCollector();
                        }
*/
                        HeapCell memoriaAllocata;
                        // Alloco memoria per i gli argomenti e per l'indirizzo alla dispatch table
                        memoriaAllocata = heap.allocate(numeroArgomenti + 1);

                        //Considero la memoria appena allocata come in uso,
                        // così che il garbage collector possa controllarla
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

                        //TODO garbage
                       /* //se la memoria termina, eseguo il garbageCollector
                        if (hp == -1) {
                            garbageCollector();
                            if(heap.ottienIndirizzoLibero() > hp) {
                                hp=heap.ottienIndirizzoLibero();
                            }
                        }*/
                        break;
                    case SVMParser.HALT:
                        output.add((sp < START_ADDRESS + MEMSIZE) ? Integer.toString(getMemory(sp)) : "Lo stack è vuoto");
                        return output;
                }
            }
        } catch (HeapOverflowError | SegmentationFaultError | StackOverflowError e) {
            output.add("Errore: " + e.getMessage());
            return output;
        }
    }


}
