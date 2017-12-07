package latentdragon.redisorm.core;

import latentdragon.redisorm.RedisormException;
import latentdragon.redisorm.configure.Mapping;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshaojie on 2017/12/7,15:17.
 */
public class Operation {

    private static final String SET="set";
    private static final String GET="get";

    public enum OperationType {
        SAVE,
        SAVE_OR_UPDATE,
        UPDATE,
        DELETE,
        QUERY,
    }

    public void operate(Jedis jedis) {
        switch (type) {
            case SAVE:
            case SAVE_OR_UPDATE:
            case UPDATE:
                MiddleState state = objectToMap(operateObject,mapping);
                jedis.hmset(state.key,state.map);
                break;
            case DELETE:
                jedis.del(key);
                break;
        }
    }

    private static String firstAlphaToUpperCase(String name) {
        char firstAlpha = name.charAt(0);
        if(firstAlpha<'a'||firstAlpha>'z') {
            throw new RedisormException("the field must be named by English and CamelCase");
        }
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }

    public Object query(Jedis jedis) {
        Map<String,String> map = jedis.hgetAll(key);
        return mapToObject(map,mapping,className);
    }

    private static MiddleState objectToMap(Object o,Mapping mapping) {
        Map<String,String> map = new HashMap<>();
        MiddleState middleState = new MiddleState();
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String idName = mapping.getIdName();
        try {
            middleState.key = clazz.getField(idName).get(o).toString();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if(!mapping.existField(fieldName)) {
                    continue;
                }
                String columnName = mapping.getNameColumn(fieldName);
                String methodName = GET+firstAlphaToUpperCase(fieldName);
                Method method = clazz.getDeclaredMethod(methodName);
                map.put(columnName,method.invoke(o).toString());
            }
        }
        catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {

        }
        catch (NoSuchMethodException e) {
            throw new RedisormException(e.getMessage()+"the field must have setter!");
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return middleState;
    }

    private static Object mapToObject(Map<String,String> map,Mapping mapping,String className) {
        try {
            Class clazz = Class.forName(className);
            Object o = clazz.newInstance();
            for (Map.Entry<String,String> entry:map.entrySet()) {
                String columnName = entry.getKey();
                String value = entry.getValue();
                String fieldName = mapping.getNameFiled(columnName);
                Field field = clazz.getField(fieldName);
                field.setAccessible(true);
                String fieldType = field.getType().getName();
                String methodName = SET+fieldName;
                Method method = clazz.getDeclaredMethod(methodName);
                switch (fieldType) {
                    case "java.lang.Integer":
                        method.invoke(o,Integer.parseInt(value));
                        break;
                    case "java.lang.Long":
                        method.invoke(o,Long.parseLong(value));
                        break;
                    case "java.lang.Float":
                        method.invoke(o,Float.parseFloat(value));
                        break;
                    case "java.lang.Double":
                        method.invoke(o,Double.parseDouble(value));
                        break;
                    case "java.lang.String":
                        method.invoke(o,value);
                        break;
                    case "java.util.Date":
                        method.invoke(o,Date.parse(value));
                        break;
                    default:
                        throw new RedisormException("Invalid data type!");
                }
            }

            return o;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            throw new RedisormException("can not found a default constructor");
        }
        catch (IllegalAccessException e) {
            throw new RedisormException("the constructor may be private");
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            throw new RedisormException(e.getMessage()+"the field must have getter");
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private OperationType type;
    private String className;
    private String key;
    private Mapping mapping;
    private Object operateObject;

    public void setOperateObject (Object operateObject) {
        this.operateObject = operateObject;
    }

    public OperationType getType () {
        return type;
    }

    public void setType (OperationType type) {
        this.type = type;
    }

    public String getClassName () {
        return className;
    }

    public void setClassName (String className) {
        this.className = className;
    }

    public String getKey () {
        return key;
    }

    public void setKey (String key) {
        this.key = key;
    }

    public Mapping getMapping () {
        return mapping;
    }

    public void setMapping (Mapping mapping) {
        this.mapping = mapping;
    }
}
