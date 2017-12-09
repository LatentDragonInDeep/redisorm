package latentdragon.redisorm.configure;

import latentdragon.redisorm.RedisormException;
import latentdragon.redisorm.annotation.Column;
import latentdragon.redisorm.annotation.Id;
import latentdragon.redisorm.annotation.Table;
import latentdragon.redisorm.annotation.Transient;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshaojie on 2017/12/6,19:59.
 */
public class ConfigurationXMLParser {
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder;

    private static final String ROOT_TAG="redisorm-configuration";
    private static final String SESSION_FACTORY="session-factory";
    private static final String PROPERTY = "property";
    private static final String PROPERTY_ATTR = "name";
    private static final String RESOURCE = "resource";
    private static final String ROOT_TAG_MAPPING = "redisorm-mapping";
    private static final String CLASS = "class";
    private static final String ID = "id";
    private static final String RAW_TYPE = "raw_type";
    private static final String REDIS_NAME = "redis-name";

    private static final String MAPPING_XML = "mapping-xml";
    private static final String MAPPING_ANNOTATION = "mapping-annotation";

    private static final String COLUMN="column";

    private enum ConfigurationType {
        HOST,
        PORT,
        USERNAME,
        PASSWORD,
        POOL_SIZE,
        TIMEOUT,
        CHARACTER_ENCODING,
        AUTO_COMMIT,
        DEAMONIZE,
        LOG_LEVEL,
        DATABASE_NUM
    }

    static {
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Configuration parseXMLConfiguration(String filePath,Configuration configuration) {
        try {
            String confFile = ConfigurationXMLParser.class.getClassLoader().getResource(filePath).getFile();
            Document document = builder.parse(new File(confFile));
            Element root = document.getDocumentElement();
            if(!root.getTagName().equals(ROOT_TAG)) {
                throw new RedisormException("root element invalid!");
            }
            Node sessionFactory = root.getElementsByTagName(SESSION_FACTORY).item(0);
            NodeList properties = sessionFactory.getChildNodes().item(1).getChildNodes();
            for (int i = 0; i < properties.getLength(); i++) {
                Node property = properties.item(i);
                if(!property.getNodeName().equals(PROPERTY)) {
                    continue;
                }
                String content=property.getTextContent();
                switch (ConfigurationType.valueOf(property.getAttributes().getNamedItem(PROPERTY_ATTR).getNodeValue().toUpperCase())) {
                    case HOST:
                        configuration.setHost(content);
                        break;
                    case PORT:
                        configuration.setPort(Integer.parseInt(content));
                        break;
                    case TIMEOUT:
                        configuration.setTimeout(Integer.parseInt(content));
                        break;
                    case PASSWORD:
                        configuration.setPassword(content);
                        break;
                    case USERNAME:
                        configuration.setUsername(content);
                        break;
                    case DEAMONIZE:
                        configuration.setDaemonize(Boolean.parseBoolean(content));
                        break;
                    case LOG_LEVEL:
                        configuration.setLogLevel(content);
                        break;
                    case POOL_SIZE:
                        configuration.setPoolSize(Integer.parseInt(content));
                        break;
                    case AUTO_COMMIT:
                        configuration.setAutoCommit(Boolean.parseBoolean(content));
                        break;
                    case DATABASE_NUM:
                        configuration.setDatabaseNum(Integer.parseInt(content));
                        break;
                    case CHARACTER_ENCODING:
                        configuration.setCharacterEncoding(content);
                        break;
                    default:
                        throw new RedisormException("Invalid property name!");
                }
            }
            configuration.setMappingMap(getMappings(confFile));
            return configuration;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
            throw new RedisormException("the number must be correct format!");
        }
        return null;
    }

    @Nullable
    public static Map<String,Mapping> getMappings(String filePath) {
        Map<String,Mapping> mappingMap = new HashMap<>();
        try {
            Document document = builder.parse(filePath);
            Element root = document.getDocumentElement();
            Node sessionFactory = root.getElementsByTagName(SESSION_FACTORY).item(0);
            NodeList mappings = sessionFactory.getChildNodes().item(3).getChildNodes();
            for (int i = 0; i < mappings.getLength(); i++) {
                Node mapping = mappings.item(i);
                if(mapping.getNodeName().equals(MAPPING_XML)) {
                    String mappingFile = mapping.getAttributes().getNamedItem(RESOURCE).getNodeValue();
                    parseMappingByXML(mappingFile, mappingMap);
                }
                else if(mapping.getNodeName().equals(MAPPING_ANNOTATION)) {
                    String className = mapping.getAttributes().getNamedItem(CLASS).getNodeValue();
                    parseMappingByAnnotation(className,mappingMap);
                }
            }
            return mappingMap;
        }
        catch (IOException e) {

        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void parseMappingByXML(String filePath, Map<String,Mapping> result){
        try {
            String confFile = ConfigurationXMLParser.class.getClassLoader().getResource(filePath).getFile();
            Document document = builder.parse(new File(confFile));
            Element root = document.getDocumentElement();
            if(!root.getTagName().equals(ROOT_TAG_MAPPING)) {
                throw new RedisormException("Invalid root element!");
            }
            NodeList classes = root.getElementsByTagName(CLASS);
            for (int i = 0; i < classes.getLength(); i++) {

                Node clazz = classes.item(i);
                if(!clazz.getNodeName().equals(CLASS)) {
                    continue;
                }
                String fullQualifiedName = clazz.getAttributes().getNamedItem(PROPERTY_ATTR).getNodeValue();
                Mapping mapping = new Mapping(fullQualifiedName);
                NodeList columns = clazz.getChildNodes();
                if(!columns.item(1).getNodeName().equals(ID)) {
                    throw new RedisormException("must have a id tag and the tag must be first!");
                }
                NamedNodeMap idAttrs = columns.item(1).getAttributes();
                String idName = idAttrs.getNamedItem(PROPERTY_ATTR).getNodeValue();
                String idRedisName = idAttrs.getNamedItem(REDIS_NAME).getNodeValue();
                mapping.setIdName(idName);
                mapping.addNameField(idName,idRedisName);
                for (int j =0; j < columns.getLength(); j++) {
                    Node column = columns.item(j);
                    if(!column.getNodeName().equals(COLUMN)) {
                        continue;
                    }
                    NamedNodeMap columnAttrs = column.getAttributes();
                    String columnName = columnAttrs.getNamedItem(PROPERTY_ATTR).getNodeValue();
                    String columnRedisName = columnAttrs.getNamedItem(REDIS_NAME).getNodeValue();
                    mapping.addNameField(columnName,columnRedisName);
                }
                mapping.build();
                result.put(fullQualifiedName,mapping);
            }
        }
        catch (IOException e) {

        }
        catch (SAXException e) {

        }
    }

    private static void parseMappingByAnnotation(String className,Map<String,Mapping> result) {
        Mapping mapping = new Mapping(className);
        try {
            Class clazz = Class.forName(className);
            if(!clazz.isAnnotationPresent(Table.class)) {
                throw new RedisormException("Entity type must have @Table annotation!");
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field:fields) {
                if(field.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if(field.isAnnotationPresent(Id.class)) {
                    mapping.setIdName(field.getName());
                    if(!field.isAnnotationPresent(Column.class)) {
                        throw new RedisormException("field must have @Column Annotation!");
                    }
                    mapping.addNameField(field.getName(),field.getAnnotation(Column.class).name());
                    continue;
                }
                if(!field.isAnnotationPresent(Column.class)) {
                    throw new RedisormException("field must have @Column Annotation!");
                }
                mapping.addNameField(field.getName(),field.getAnnotation(Column.class).name());
            }
            mapping.build();
            result.put(className,mapping);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
