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
From a performance point of view, the Thread Pool is more efficient because
it starts a limited number of threads, chosen so that it takes no more time
to create and clean them than it would take to serially execute the entire
tasks. Also, the size of the database affects the run time. Threads are
workers for multiple tasks and stop after the shutdown call and after they
have finished all tasks in the queue. As for tests, several cases are covered
to ensure a complete implementation. The type of priority used is influenced
by the ratio of readers to writers.
