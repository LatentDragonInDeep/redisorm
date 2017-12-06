package latentdragon.redisorm.configure;

import latentdragon.redisorm.RedisormException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshaojie on 2017/12/6,20:37.
 */
public class Mapping {

    private final String fullQualifiedClassName;
    private String idName;

    public Mapping (String fullQualifiedClassName) {
        this.fullQualifiedClassName = fullQualifiedClassName;
    }

    public String getIdName () {
        return idName;
    }

    public void setIdName (String idName) {
        this.idName = idName;
    }

    public enum ValueType {
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        DATE,
        STRING
    }
    private HashMap<String,ValueType> valueMap = new HashMap<>();
    private HashMap<String,String> fieldToColumn = new HashMap<>();
    private HashMap<String,String> columnToField = new HashMap<>();

    public ValueType getValueType(String field) {
        return valueMap.get(field);
    }

    private void addValueField(String field,ValueType type) {
        valueMap.put(field,type);
    }

    public void addNameFiled(String field,String column) {
        fieldToColumn.put(field,column);
        columnToField.put(column,field);
    }

    public String getNameFiled(String column) {
        return columnToField.get(column);
    }

    public String getNameColumn(String field) {
        return fieldToColumn.get(field);
    }

    public void build() {
        try {
            Class clazz = Class.forName(fullQualifiedClassName);
            for (Map.Entry<String,String> entry: fieldToColumn.entrySet()) {
                String field = entry.getKey();
                Field realField = clazz.getField(field);
                realField.setAccessible(true);
                Class fieldType = realField.getType();
                String fieldTypeName = fieldType.getTypeName();
                switch (fieldTypeName) {
                    case "java.lang.Integer":
                        addValueField(field,ValueType.INTEGER);
                        break;
                    case "java.lang.Long":
                        addValueField(field,ValueType.LONG);
                        break;
                    case "java.lang.Float":
                        addValueField(field,ValueType.FLOAT);
                        break;
                    case "java.lang.Double":
                        addValueField(field,ValueType.DOUBLE);
                        break;
                    case "java.lang.String":
                        addValueField(field,ValueType.STRING);
                        break;
                    case "java.util.Date":
                        addValueField(field,ValueType.DATE);
                        break;
                    default:
                        throw new RedisormException("Invalid data type!");
                }
            }
        }
        catch (ClassNotFoundException e) {
        e.printStackTrace();
        }
        catch (NoSuchFieldException e) {

        }
    }
}
