### 任务执行

大多数并发应用程序都是围绕”任务执行“来构建的，任务通常是咦嘻嘻额抽象且离散的工作单元。通过把任务程序的工作分解到多个任务，可以简化程序的组织结构，提供一种自然的事物边界来优化错误恢复过程。

#### 在线程中执行任务

围绕”任务执行“来设计应用程序结构时，第一步就是要找出清晰的任务边界。在理想的情况下，各个任务之间是相互独立的：任务不依赖于其他任务的状态，结果，边界。独立性有利于实现并发。

##### 串行地执行任务

最简单的任务调度策略是在单个线程中采用串行方式顺序执行各个任务。单线程的程序执行性能比较糟糕，每次只能处理一个请求，如果发生阻塞，那么会造成CPU一直处于闲置的状态。

##### 显式地创建线程

可以为每个请求创建一个线程用来提供服务，从而实现更好的响应性。

1. 将任务处理过程从主线程中分离，使得主循环能够更快地重新等待下一个任务，提高响应性
2. 任务可以并行处理，从而能够服务多个请求，当任务被阻塞时（如：I/O完成，获得锁等），程序吞吐量都可以提高
3. 任务处理代码必须是线性安全的，因为当有多个任务时会并发的调用代码

##### 无限创建线程的不足

1. 线程生命周期的开销非常高——创建线程需要时间，并延迟处理请求，且需要JVM以及操作系统提供辅助
2. 资源消耗——活跃的资源会消耗系统资源，尤其是内存，当可运行的线程数量多于可用处理器的数量，那么线程将闲置
3. 稳定性——可创建的线程数量存在一个限制

#### Executor框架

任务是一组逻辑工作单元，而线程则是任务异步执行的机制，执行任务的策略有两种，在单个线程中串行执行；将每个任务放在各自的线程执行。

java.util.concurrent提供了灵活的线程池实现作为Executor框架的一部分。在java库类中，任务执行需要的主要抽象不是Thread，而是Executor。Executor能够支持多种类型的任务执行策略，提供一种标准的方法将任务的提交过程与执行解耦开来，并采用runnable来表示任务。

executor基于生产者——消费者模式，提交任务的操作相当于生产者，而执行任务的线程为消费者，

```java
public class TaskExecutionWebServer {
    private static final int NTHREADS = 100;
    private static final Executor exec
            = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    handleRequest(connection);
                }
            };
            exec.execute(task);
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
```

一个线程池的创建实例

#### 执行策略

执行策略定义了任务执行的”what，where，when，how“，请用Executor来替代新建线程

```java
new Thread(runnable).start();
```

##### 线程池

指一组管理同构工作线程的资源池，线程池与工作队列密切相关，其中在工作队列中保存了所有等待执行的任务。工作者线程从工作丢列获得一个任务对象，执行，然后返回线程池并等待下个任务。
优势：

1. 重用线程，实现分摊创建线程的开销
2. 避免请求的延迟处理

类库提供了一部分：

可以通过调用Executors中的静态方法来创建一个线程：

1. newFixedThreadPool：创建一个固定长度的线程池，提交一个任务就创建一个线程，直到达到最大的数量。
2. newCachedThreadPool：创建一个可缓存的线程池，如果线程池的当前规模超过了处理需求就会回收空闲线程。当需求增加时，添加新的线程，线程池规模无限值。
3. newSingleThreadExecutor：一个单线程的Executor，创建单个工作者线程来执行任务，如果线程异常结束，就会创建新的线程以替代。
4. newScheduleThreadPool：创建一个固定长度的线程池，以定时或者延迟的方式执行任务。

##### Executor的生命周期

JVM只有在所有（非守护）线程全部结束之后才会退出

Executor以异步的方式执行任务，Executor会在关闭时将受影响的任务状态反映给应用程序。Executor扩展了ExecutorService接口，添加了生命周期管理的办法。

ExecutorService的生命周期有三种状态：运行，关闭，和已终止。

创建时处于运行状态。

shutdown将执行平缓的关闭过程：不再接受新的任务，同时等待已提交的任务执行完成。

shutdownNow执行粗暴的关闭：取消所有运行的任务，同时不再启动未执行的任务。

关闭之后提交的任务会交给”拒绝执行处理器“处理，待所有任务都完成后，转入终止状态。可利用awaitExecutionException，来等待终止状态，或者isTerminated来轮询是否终止。

##### 延迟任务和周期任务

timer来负责管理延迟任务以及周期任务，可以使用ScheduledThreadPoolExecutor来替代。

Timer执行所有定时任务时只会创建一个线程。如果某个任务执行过程过长，那么会破坏其他任务的定时精准性。另外timer不会捕获异常，当timerTask抛出定时异常时将会终止定时线程。此时已被调度的线程不会执行，新任务也不能调度，称之为”线程泄露“。

#### 找出可利用的并行性

如果需要使用executor，任务必须表述为一个runnable，runnable有一个局限性，不能够将结果返回或者抛出一个异常，其只能将结果写入日志或者将结果放入共享的数据结构中。

Executor中包含了一些辅助方法能将其他类型的任务封装为一个Callable。

Executor都描述的是抽象任务，其再执行任务是具有4个生命周期阶段：创建，提交，开始和完成。

Future表示一个任务的生命周期，并提供相应的方法来判断是否完成。

get方法取决于任务完成得状态,如果任务已经完成，那么get会返回一个结果，或者抛出一个异常，如果任务没有完成，那么get将会一直阻塞，直到任务完成。

##### 在异构任务并行化中存在局限

如果任务可以分解为一些独立的任务，就可以利用多个线程来实现并行化处理，但是并行化处理并不一定能保证提高任务的处理速度。

##### CompletionService

解决了向executor提交多任务之后，反复调用get的麻烦，其可以通过take和poll方法来获得已完成的结果，结果返回的是future

##### 任务时限

当任务执行太长时间时，就不需要其结果，可以放弃任务。future.get支持这样的时限任务，当超时时，任务抛出TimeOutException，方法invokeAll可以同时提交多个任务，并可以实现设计时限任务。

