### 死锁
过度加锁会导致锁顺序死锁，使用线程池以及信号量会导致资源死锁

#### 死锁
数据库在发生死锁时，会选择一个牺牲者放开资源使其他的事物可以顺序进行，而JVM没有这种能力

##### 死锁发生的条件

（1） 互斥条件：一个资源每次只能被一个进程使用。
（2） 占有且等待：一个进程因请求资源而阻塞时，对已获得的资源保持不放。
（3）不可强行占有: 进程已获得的资源，在末使用完之前，不能强行剥夺。
（4） 循环等待条件:若干进程之间形成一种头尾相接的循环等待资源关系。 


##### 锁顺序死锁
不同的线程以不同的顺序获得相同的锁

##### 动态的锁顺序死锁
虽然锁的顺序是相同的，但是上锁的顺序取决于传递的参数的顺序，此时也会造成死锁。

为了解决该问题，需要制定锁的顺序，制定顺序可以使用Systems.identityHashCode，该方法返回Objects.hascode的返回值。比较哈希值的大小决定锁的顺序。

可能两个值存在相同的哈希值，可以采用加时赛方式在获得两个锁之前，先取得加时赛锁

#### 协作对象之间的死锁

##### 开放调用

如果调用某个方法不需要持有锁，那么这种调用称之为开放调用，也就是synchronized不是修饰在方法上的。

##### 资源死锁

多个线程在相同的资源池中等待等待，也有死锁发生的可能。

线程依赖多种资源，且不同线程对资源的获取顺序不同。

##### 线程饥饿死锁

一个线程提交任务，并等待另一个任务的提交，而另一任务一直被阻塞。例如单线程executor

#### 死锁的避免与等待

###### 支持定时的锁

显式使用Lock类中的定时tryLock功能。但该方法自有在程序需要同时获得多个锁时才可以使用，而在嵌套的方法中，这样的技术无法释放外部的锁。

##### 线程转储信息来分析死锁

JVM通过线程转储来帮助识别死锁发生。线程转储包括各运行栈的栈追踪信息，以及加锁信息。ide具有集成的方式。

#### 其他活跃性危险

##### 饥饿

定义：线程无法访问满足需求的资源而不能继续执行，

在Thread API中定义好了线程的优先级，但是大部分线程优先级都是NORMPRIORITY,一般不要直接修改线程的优先级，作用较小。

##### 糟糕的响应性

例如后台线程，CPU密集的后台任务可能竞争时钟周期，导致性能降低。

##### 活锁

另一种活跃性问题：该问题不会导致线程阻塞，但是线程会一直执行相同的操作，且一直失败。活锁一般发生在处理事物消息的应用程序中。

