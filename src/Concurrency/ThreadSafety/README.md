### 线程安全性

要编写线程安全的代码，核心在于要对状态访问操作进行管理，特别是共享的以及可变状态的访问。

当多个线程访问同一个可变的状态变量没有使用合适的同步，程序会出现错误，通过以下的方式解决：

1. 不在线程之间共享该状态变量
2. 将状态变量修改为不可变的变量
3. 在访问状态变量时使用同步

状态变量：实例或者静态域中的数据

定义：当多个线程访问某个类时，这个类都能表现出正确的行为，那么这个类就是线程安全的。

原子性：对于非原子性的操作，多线程调用时，就不会将该操作视为不可分割的操作进行。例如文件[UnsafeCountingFactorizer.java](../../../src/main/java/com/jiedong/ThreadSafety/UnsafeCountingFactorizer.java)，其中多线程对count++这个操作进行的就不是原子性的操作，操作分为读取count，对count+1，将改变后的值写入count变量中，此时多个线程在执行count+1之前就读取了count的值，那么count就会被重复写入count+1，导致出错。

#### 竞态条件

当某个计算的正确性取决于多个线程的交替执行时序时，就会发生竞态条件。

类型：

1. 先检查后执行：通过一个可能失效的观测结果来决定下一步的动作

复合操作：将先检查后执行以及“读取-修改-写入”等操作都称为复合操作。

java.util.concurrent.atomic中存在有很多原子变量类，用于实现在数值和对象引用上的原子状态转换。

#### 加锁机制

1. 采用原子类来保证对单一状态变量的同步安全性问题
2. 对于多个状态变量，即使他们各自采用线程安全的方式进行各自的修改，但是如果各变量之间不是相互独立的，而是某个变量的值会对其他值产生约束，就需要在一个原子操作之中对这些不是互相独立的变量进行修改
3. 内置锁：同步代码块：一个作为锁的对象引用，一个作为由这个这个所保护的代码块，利用synchronized修饰的方法就是一种横跨整个方法体的同步代码块。**每个java对象都可以作为同步锁**。线程在进入同步代码块之前会自动获得锁，并且在退出同步代码块时自动释放锁，无论是通过正常的控制路径退出，还是抛出异常退出，获得内置锁的唯一途径就是进入由这个锁保护的代码块或方法。
4. 重入：某个线程请求由其他线程持有的锁，线程就会阻塞，而线程试图获得一个由它自己持有的锁，请求就会成功，这称之为重入。重入锁又被称之为递归锁，可以参考文件[LoggingWidget.java](../../../src/main/java/com/jiedong/ThreadSafety/LoggingWidget.java)和[Widget.java](../../../src/main/java/com/jiedong/ThreadSafety/Widget.java)，其中父类和子类的dosomething方法都是同步的，执行子类方法时，先获取了一次Widget锁，之后有执行super时，又需要获取一次，特别是在递归调用时，如果不能重入，递归就无法进行。这会造成死锁。
5. 对于每个包含多个变量的不变性条件，其中涉及的所有变量都需要由同一个锁来保护。
6. 活跃性与性能：直接对整个方法进行加锁，会导致在同一时间只能有一个线程能使用该方法，降低程序的性能，可以通过缩小同步代码块的作用范围实现性能的提高


第4点中对于同步代码块，子类与父类的synchronize关键字理解，可以参考下面的代码
```java
public class Test {
  public static void main(String[] args) throws InterruptedException {
    final TestChild t = new TestChild();

    new Thread(new Runnable() {
      @Override
      public void run() {
        t.doSomething();
      }
    }).start();
    Thread.sleep(100);
    t.doSomethingElse();
  }

  public synchronized void doSomething() {
    System.out.println("something sleepy!");
    try {
      Thread.sleep(1000);
      System.out.println("woke up!");
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static class TestChild extends Test {
    public void doSomething() {
      super.doSomething();
    }

    public synchronized void doSomethingElse() {
      System.out.println("something else");
    }
  }
}
```
对于上面的代码，main方法中有一个thread线程和main方法主线程，如果认为子类与父类的锁不是一个锁，那么代码的最终输出应该是：
```
something sleepy!
something else//因为thread线程被sleep了，
woke up!
```
但是，最终的输出结果是：
```
something sleepy!
woke up!
something else
```
可以看出实例的this锁被thread持有，主方法无法获得dosomethingelse的锁，而同时java又支持重入锁，因此得到结果。