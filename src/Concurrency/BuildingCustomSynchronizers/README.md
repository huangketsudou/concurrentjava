### 自定义同步工具

#### 状态依赖管理工具

传递异常

通过轮询与休眠来实现阻塞

条件队列：使一组线程能够通过某种方式来等待特定的条件为真

#### 条件队列

##### 条件谓语

使某个操作称为状态依赖操作的前提条件，

尽量使用notify而不是notifyAll，因为对于两个线程A，B假设线程正在等待条件PA，PB，B正在等待条件PB，此时C线程令条件PB为真，notify随机选择了A，而A的PA还没被置为真，导致A不能被唤醒，B也没有。但是通过notifyAll又会导致唤醒过多线程，导致上下文切换。

#### Condition对象

多线程的通信工具， Condition是一个多线程间协调通信的工具类，使得某个，或者某些线程一起等待某个条件（Condition）,只有当该条件具备( signal 或者 signalAll方法被带调用)时 ，这些等待线程才会被唤醒，从而重新争夺锁。 

```java
public interface Condition{
	void await() throws InterruptedException;
    boolean await(long time,TimeUnit unit) throws InterruptedException;
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    void awaitUninterruptibly();
    boolean awaitUntil(Date deadline) throws InterruptedException;
    void signal();
    void signalAll();
}
```

对于Condition，其于Object的wait，notify和notifyAll相对应的是方法await，signal和signalAll。

一个Condition与一个Lock关联在一起，要创建一个Condition，可以在相关联的Lock上调用Lock.newCondition.

当使用显式的Lock和Condition时，也必须满足锁，条件谓语和条件变量之间的三元关系。

#### AQS

AQS是大多数同步类的基类，基于AQS能极大地减少实现共工作，也不必处理多个位置发生的竞争问题，

AQS构造的容器包含一系列基本的操作：

获取：获得锁或者许可，且调用者可能会一直等待直到同步类器处于被获取的状态。

释放：所有在请求时被阻塞的线程会开始执行

AQS负责管理同步类器中的状态，它管理了一个整数状态信息，可以通过getState，setState，compareSetState等protected类型的方法来操作。

**关键点：**如果同步器支持独占的获取操作，那么需要实现一些保护方法，包括tryAcquire、tryRelease和isHeldExclusively等，而对于支持共享获取的同步器，需要实现tryAcquireShared和tryReleaseShared等方法。AQS中的acquire、acquireShared、release和releaseShared等方法。

**tryAcquireShared：**返回一个负值，表示获取操作失败，返回0值表示同步器通过独占方式获取，返回正值表示同步器通过非独占方式获得。

**tryRelease和tryReleaseShared:**如果释放操作使得所有在获取同步器时被阻塞的线程恢复执行，那么这两个方法应该返回true

#### java.util.concurrent同步类中的AQS

##### ReentrantLock

**只支持**独占的方式获取操作，因此它实现了tryAcquire,tryRelease和isHeldExclusively

```java
protected boolean tryAcquire(int ignored){
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0){
		if (compareAndSetState(0,1)){
            owner = current;
            return true;
        }
    }else if (current == owner){
        setState(c+1);
        return true;
    }
    return false;
}
```

其获取锁的方式如下：tryAcquire将首先检查锁的状态，如果锁未被持有，那么它将尝试更新锁的状态以表示锁已经被持有，由于**状态可能在检查之后被立即修改**，因此tryAcquire使用compareAndSetState来原子地更新状态，（关于compareAndSetState地描述在15章中，该函数与unsafe类有关（一个饿汉加载的单例类），为一个native函数），表明该锁已经被占有了，如果该锁已经被占有，并且当前线程是锁地持有者，那么获得计数会增加，如果当前线程不是锁的持有者，那么获取操作将失败。

##### Semaphore与CountDownLatch

Semaphore将AQS的状态用于保存当前可用许可的数量，tryAcquireShared首先计算剩余许可的数量，如果没有足够的许可，那么会返回一个值表示获取操作失败。如果还有剩余的许可，那么通过compareAndSetState以原子方式来降低许可的计数。如果该操作成功，那么返回一个值来表示获取操作成功。

```java
protected int tryAcquireShared(int acquires){
    while (true){
        int available = getState();
        int remaining = available - acquires;
        if (remaining < 0||compareAndSetState(availble,remaining))
            return remaining;
    }
}
protected boolean tryReleaseShared(int releases){
    while (true){
        int p = getState();
        if (compareAndSetState(p,p+releases))
            return true;
    }
}
```

当没有足够的许可，或者当tryAcquireShared可以通过原子方式来更新许可的计数以响应获取操作，while循环终止，在调用compareAndSetState时，可能与其他的线程发生竞争而导致重新尝试。在经过一定的尝试之后，有一个条件会变为真，因此可以退出。

##### FutureTask

利用get来实现类似闭锁的语义，如果某些事件发生就继续执行

##### ReentrantReadWriteLock

ReadWriteLock接口中存在两个锁，一个读锁，一个写锁，但是在基于AQS实现的锁中，由单个AQS类同时管理读锁和写锁。其使用一个16位的状态来表示写入锁的计数，并使用另一个16位的状态来表示读锁的计数。读锁上使用共享的获取方法与释放方法，在写入锁上使用独占的获取方法和释放方法

