package latentdragon.redisorm.configure;

/**
 * Created by chenshaojie on 2017/12/6,19:50.
 */
public class Configuration {
    private static final String DEFAULT_CONFIGURATION_FILE="redisorm.cfg.xml";

    private String host = null;
    private int port = 6379;
    private String username = null;
    private String password = null;
    private int poolSize = 20;
    private int timeout = 1000;
    private String characterEncoding = "utf-8";
    private boolean autoCommit = false;
    private boolean daemonize = false;
    private String logLevel;
    private int databaseNum = 16;

    public static Configuration configure(String filePath) {
        return ConfigurationXMLParser.parseXMLConfiguration(filePath);
    }

    public static Configuration configure() {
        return configure(DEFAULT_CONFIGURATION_FILE);
    }

    public static String getDefaultConfigurationFile () {
        return DEFAULT_CONFIGURATION_FILE;
    }

    public String getHost () {
        return host;
    }

    public void setHost (String host) {
        this.host = host;
    }

    public int getPort () {
        return port;
    }

    public void setPort (int port) {
        this.port = port;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public int getPoolSize () {
        return poolSize;
    }

    public void setPoolSize (int poolSize) {
        this.poolSize = poolSize;
    }

    public int getTimeout () {
        return timeout;
    }

    public void setTimeout (int timeout) {
        this.timeout = timeout;
    }

    public String getCharacterEncoding () {
        return characterEncoding;
    }

    public void setCharacterEncoding (String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public boolean isAutoCommit () {
        return autoCommit;
    }

    public void setAutoCommit (boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public boolean isDaemonize () {
        return daemonize;
    }

    public void setDaemonize (boolean daemonize) {
        this.daemonize = daemonize;
    }

    public String getLogLevel () {
        return logLevel;
    }

    public void setLogLevel (String logLevel) {
        this.logLevel = logLevel;
    }

    public int getDatabaseNum () {
        return databaseNum;
    }

    public void setDatabaseNum (int databaseNum) {
        this.databaseNum = databaseNum;
    }
}
