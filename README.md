# Thread Pool implementation
##### Contributor: Dumitrascu Filip-Teodor 333CA

## Content
1.[Thread Pool](#thread-pool)

2.[Operation](#operation)

3.[Priority](#priority)

## Thread Pool
A custom thread pool implementation with the `submit` and `shutdown` methods.
In the `submit`, a new task is added in a BlockingQueue and in the `shutdown`
all the threads started in the class constructor are cleaned. The intern class,
`Worker`, implements the threads action while the thread pool is running.

## Operation
This is a class representing how the worker handles the task from the thread
pool's task list. After operation, the result is applied to the database.

## Priority
Depending on who has priority (the readers or the writers), the abstract class
priority becomes the implementation for ReaderPriority or WriterPriority
(with 2 implementation options). The 4 common methods, before and after
read/write are implemented depending on the priority. 

## Conclusion
Din punct de vedere al performantei, Thread Poolul este mai eficient intrucat 
porneste un numar limitat de threaduri, ales astfel incat sa nu dureze mai mult
crearea si curatarea lor decat ar dura executarea seriala a intregilor taskuri.
Threadurile sunt worker pentru mai multe taskuri si se opresc dupa apelul
de shutdown si dupa ce au terminat toate taskurile din coada.
Cat despre teste, sunt acoperite mai multe cazuri pentru a fi asigurata o
implementare completa. Numarul de threaduri nu depaseste 12 iar numarul de taskuri
nu depaseste 500. Raportul cititori-scriitori favorizeaza scriitorii iar zonele
de memorie ale bazei de date variaza intre 255 si 3000 de zone. Astfel, trebuie
alocate o multitudine de semafoare pentru a asigura prioritatea dorita.
