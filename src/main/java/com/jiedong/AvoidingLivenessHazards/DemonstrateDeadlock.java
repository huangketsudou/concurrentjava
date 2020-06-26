package com.jiedong.AvoidingLivenessHazards;

import java.lang.reflect.Constructor;
import java.util.Random;

/**
 * @author 19411
 * @date 2020/06/26 15:20
 **/
public class DemonstrateDeadlock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS =100000000;



    public static void main(String[] args) {
        Class t = DemonstrateDeadlock.class;
        Constructor[] cs = t.getConstructors();
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];

        for (int i = 0; i < accounts.length; i++)
            accounts[i] = new Account();

        class TransferThread extends Thread {


            public void run() {
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                    try {
                        DynamicOrderDeadlock.transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (DynamicOrderDeadlock.InsufficientFundsException ignored) {
                    }
                }
            }
        }
        for (int i = 0; i < NUM_THREADS; i++)
            new TransferThread().start();
    }





}
class Account{}

class DynamicOrderDeadlock{

    static class InsufficientFundsException extends Exception{}


    DynamicOrderDeadlock(){

    }
    static void transferMoney(Account a, Account b,DollarAmount c) throws InsufficientFundsException {

    }
}


class DollarAmount{
    DollarAmount(int i){

    }
}