package virtualMachine;

public class HeapCell {

    private int index; //l'indice di questa cella

    HeapCell next; //contiene la prossima cella di memoria libera

    public HeapCell(int index, HeapCell next) {
        this.index = index;
        this.next = next;
    }

    public int getIndex() {
        return index;
    }
}
