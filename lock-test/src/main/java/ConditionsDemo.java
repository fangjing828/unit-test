import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fang_j on 2021/07/08.
 */
public class ConditionsDemo {
    private final Deque<Integer> stk = new LinkedList<>();
    private final int capacity = 5;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public void push(int val) {
        try {
            lock.lock();
            while (stk.size() == capacity) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            stk.push(val);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int pop() {
        int result;
        try {
            lock.lock();
            while (stk.isEmpty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            result = stk.pop();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
        return result;
    }
}
