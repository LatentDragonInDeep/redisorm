package latentdragon.redisorm.core;

import latentdragon.redisorm.configure.Configuration;
import latentdragon.redisorm.configure.Mapping;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by chenshaojie on 2017/12/6,20:28.
 */
public class SessionFactory {

    private Configuration configuration;
    private static SessionFactory sessionFactory = new SessionFactory();

    private static JedisPool pool;

    public static void closeJedis(Jedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }

    public synchronized static Jedis getJedis() {
        try {
            if (pool != null) {
                Jedis resource = pool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JedisPool getPool(Configuration configuration) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxWaitMillis(3000);
        config.setMaxIdle(20);
        config.setMaxTotal(500);
        config.setMinIdle(10);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        pool = new JedisPool(config, configuration.getHost(),configuration.getPort());

        return pool;
    }

    public static SessionFactory buildSessionFactory(Configuration configuration) {
        sessionFactory.configuration = configuration;
        pool = getPool(configuration);

        return sessionFactory;
    }

    public Session getSession() {
        Session session = new Session(getJedis());
        session.setSessionFactory(this);

        return session;
    }

    public Mapping getMapping(Class clazz) {
        return configuration.getMapping(clazz);
    }
}
