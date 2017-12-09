package latentdragon.redisorm.core;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chenshaojie on 2017/12/6,20:29.
 */
public class Transaction {
    private Session session;

    public Transaction(Session session) {
        this.session = session;
    }

    private Queue<Operation> operationQueue = new LinkedBlockingQueue<>();

    public void addOperation(Operation operation) {
        operationQueue.add(operation);
    }

    public void commit() {
        while (!operationQueue.isEmpty()) {
            synchronized (operationQueue) {
                Operation operation = operationQueue.poll();
                operation.operate(session.getJedis());
            }
        }
        session.reset();
    }
}
