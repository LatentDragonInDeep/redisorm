package latentdragon.redisorm.core;

import latentdragon.redisorm.RedisormException;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by chenshaojie on 2017/12/6,20:29.
 */
public class Session implements Closeable{

    public Session(Jedis jedis) {
        this.jedis = jedis;
    }

    private Jedis jedis;
    private Queue<Operation> operationQueue = new ArrayBlockingQueue<Operation>(5);
    private SessionFactory sessionFactory;

    public void setSessionFactory (SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private boolean isInTransaction;

    private Transaction current;

    public Transaction beginTransaction() {
         isInTransaction = true;
         if(current!=null) {
             throw new RedisormException("the last transaction have not be commited!");
         }
         current = new Transaction(this);
         return current;
    }

    private void checkTransaction() {
        if(current == null) {
            throw new RedisormException("have not start a transaction!");
        }
    }

    @Override
    public void close() throws IOException {
        SessionFactory.closeJedis(jedis);
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void save(Object o) {
        checkTransaction();
        Operation operation = new Operation();
        String className = o.getClass().getName();
        operation.setClassName(className);
        operation.setType(Operation.OperationType.SAVE);
        operation.setMapping(sessionFactory.getMapping(o.getClass()));
        operation.setOperateObject(o);

        current.addOperation(operation);
    }

    public void saveOrUpdate(Object o) {
        checkTransaction();
        Operation operation = new Operation();
        String className = o.getClass().getName();
        operation.setClassName(className);
        operation.setType(Operation.OperationType.SAVE_OR_UPDATE);
        operation.setMapping(sessionFactory.getMapping(o.getClass()));
        operation.setOperateObject(o);

        current.addOperation(operation);
    }

    public void update(Object o) {
        checkTransaction();
        Operation operation = new Operation();
        String className = o.getClass().getName();
        operation.setClassName(className);
        operation.setType(Operation.OperationType.UPDATE);
        operation.setMapping(sessionFactory.getMapping(o.getClass()));
        operation.setOperateObject(o);

        current.addOperation(operation);
    }

    public void delete(String key,Class clazz) {
        checkTransaction();
        Operation operation = new Operation();
        String className = clazz.getName();
        operation.setClassName(className);
        operation.setType(Operation.OperationType.DELETE);
        operation.setMapping(sessionFactory.getMapping(clazz));
        operation.setKey(key.toString());

        current.addOperation(operation);

    }

    public Object get(Object key,Class clazz) {
        Operation operation = new Operation();
        String className = clazz.getName();
        operation.setClassName(className);
        operation.setType(Operation.OperationType.QUERY);
        operation.setMapping(sessionFactory.getMapping(clazz));
        operation.setKey(key.toString());

        return operation.query(getJedis());
    }

    public void reset() {
        current = null;
    }
}
