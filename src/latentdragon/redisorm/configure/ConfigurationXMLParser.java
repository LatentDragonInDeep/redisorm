package latentdragon.redisorm.configure;

import latentdragon.redisorm.RedisormException;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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

    public static Configuration parseXMLConfiguration(String filePath) {
        Configuration configuration = new Configuration();
        try {
            Document document = builder.parse(new File(filePath));
            Element root = document.getDocumentElement();
            if(!root.getTagName().equals(ROOT_TAG)) {
                throw new RedisormException("root element invalid!");
            }
            Node sessionFactory = root.getElementsByTagName(SESSION_FACTORY).item(0);
            NodeList properties = sessionFactory.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < properties.getLength(); i++) {
                Node property = properties.item(i);
                String content=property.getTextContent();
                switch (ConfigurationType.valueOf(property.getNodeName())) {
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
                configuration.setMappingMap(getMappings(filePath));
                return configuration;
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            NodeList mappings = sessionFactory.getChildNodes().item(1).getChildNodes();
            for (int i = 0; i < mappings.getLength(); i++) {
                String mappingFile = mappings.item(i).getAttributes().getNamedItem(RESOURCE).getNodeValue();
                parseMapping(mappingFile,mappingMap);
            }
            return mappingMap;
        }
        catch (IOException e) {

        }
        catch (SAXException e) {

        }
        return null;
    }

    private static void parseMapping(String filePath,Map<String,Mapping> result){
        try {
            Document document = builder.parse(new File(filePath));
            Element root = document.getDocumentElement();
            if(!root.getTagName().equals(ROOT_TAG_MAPPING)) {
                throw new RedisormException("Invalid root element!");
            }
            NodeList classes = root.getElementsByTagName(CLASS);
            for (int i = 0; i < classes.getLength(); i++) {

                Node clazz = classes.item(i);
                String fullQualifiedName = clazz.getAttributes().getNamedItem(PROPERTY_ATTR).getNodeValue();
                Mapping mapping = new Mapping(fullQualifiedName);
                NodeList columns = clazz.getChildNodes();
                if(!columns.item(0).getNodeName().equals(ID)) {
                    throw new RedisormException("must have a id tag and the tag must be first!");
                }
                NamedNodeMap idAttrs = columns.item(0).getAttributes();
                String idName = idAttrs.getNamedItem(PROPERTY_ATTR).getNodeValue();
                String idRedisName = idAttrs.getNamedItem(REDIS_NAME).getNodeValue();
                mapping.setIdName(idName);
                mapping.addNameFiled(idName,idRedisName);
                for (int j = 1; j < columns.getLength(); j++) {
                    NamedNodeMap columnAttrs = columns.item(i).getAttributes();
                    String columnName = columnAttrs.getNamedItem(PROPERTY_ATTR).getNodeValue();
                    String columnRedisName = columnAttrs.getNamedItem(REDIS_NAME).getNodeValue();
                    mapping.addNameFiled(columnName,columnRedisName);
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

}
