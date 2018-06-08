# Compilatori-e-Interpreti

# FOOL - Functional Object Oriented Language

> A project for *Compilers and Interpreters* course, Computer Science Master @University of Bologna, academic year 2018

Our implementation of FOOL language features:

- ANTLR 4 for grammar definition, lexical and syntacic analysis
- Intermediate code generation
- Fool Virtual Machine
- Dynamic dispatch
- Garbage collection

## Usage

```
$ java -jar fool.jar -h

usage: fool
 -a,--ast                   show the AST
 -b,--bytecode <filepath>   outputs the generated bytecode to the given file
 -c,--no-colors             disable the output colors
 -d,--debug                 show debug logs
 -h,--help                  print this message
 -i,--input <filepath>      REQUIRED input .fool file
 -s,--svm <filepath>        outputs the generated SVM code to the given file
 -t,--test                  the input file is treatead as a .yml test suite file
```
You can test it using `examples/sort.fool` as input. This file implements a variable-size list sorting.
## Developer setup

1. Add `libs/antlr-4.7-complete.jar` as library
2. Add `libs/snakeyaml-1.18.jar` as library
3. Add `libs/commons-cli-1.4.jar` as library

## Project directives (Italian only)

### Progetto compilatori e interpreti AA 2016/2017

Il progetto consiste nella realizzazione di un compilatore per il linguaggio la cui sintassi è definita nel file FOOLPLUS.g. 

### Il linguaggio

Questo linguaggio è una estensione object-oriented del linguaggio funzionale visto a lezione. In particolare 

* E' possibile dichiarare classi e sottoclassi. 
* Gli oggetti, che nascono come istanza di classi, contengono campi (dichiarati nella classe o ereditati dalla super-classe) e metodi (esplicitamente dichiarati nella classe o ereditati dalla super-classe). Se in una sottoclasse viene dichiarato un campo o un metodo con il medesimo nome di un campo della super-classe, tale campo o metodo sovrascrive quello della super-classe. 
* I campi non sono modificabili ed il loro valore viene definito quando l'oggetto è creato.
* E' inoltre possibile dichiarare funzioni annidate. Le funzioni NON possono essere passate come parametri.

### Type Checker

Il compilatore deve comprendere un type-checker che controlli il corretto uso dei tipi. Si deve considerare una nozione di subtyping fra classi e tipi di funzioni. 

* Il tipo di una funzione f1 è sottotipo del tipo di una funzione f2 se il tipo ritornato da f1 è sottotipo del tipo ritornato da f2, se hanno il medesimo numero di parametri, e se ogni tipo di paramentro di f1 è sopratipo del corrisponde tipo di parametro di f2. 
* Una classe C1 è sottotipo di una classe C2 se C1 estende C2 e se i campi e metodi che vengono sovrascritti sono sottotipi rispetto ai campi e metodi corrispondenti di C2.  Inoltre, C1 è sottotipo di C2 se esiste una classe C3 sottotipo di C2 di cui C1 è sottotipo.

### Il codice oggetto

Il compilatore deve generare codice per un esecutore virtuale chiamato SVM (stack virtual machine) la cui sintassi è definita nel file SVM.g. Tale esecutore ha una memoria in cui gli indirizzi alti sono usati per uno stack. Uno stack pointer punta alla locazione successiva alla prossima locazione libera per lo stack (se la memoria ha indirizzi da 0 a MEMSIZE-1, lo stack pointer inizialmente punta a MEMSIZE). In questo modo, quando lo stack non è vuoto, lo stack pointer punta al top dello stack. 

Il programma è collocato in una memoria separata puntata dall' instruction pointer (che punta alla prossima istruzione da eseguire). Gli altri registri della macchina virtuale sono: HP (heap pointer), RA (return address), RV (return value) e FP (frame pointer). 
In particolare, HP serve per puntare alla prossima locazione disponibile dello heap; assumendo di usare gli indirizzi bassi per lo heap, HP contiene inizialmente il valore 0.

### Opzionali

Le seguenti richieste non sono obbligatorie:

* La deallocazione degli oggetti nello heap (garbage collection) NON è OBBLIGATORIA. Chi è interessato può scrivere il modulo relativo.

