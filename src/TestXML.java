import java.util.Date;

/**
 * Created by chenshaojie on 2017/12/8,18:47.
 */

public class TestXML {

    private int intData = 12345;

    private long longData = 1234567890;

    private String stringData = "hello world";

    private float floatData = 1.234f;

    private double doubleData = 1.2345678;

    private Date dateData = new Date();

    private String transientData = "world hello";

    public int getIntData () {
        return intData;
    }

    public void setIntData (int intData) {
        this.intData = intData;
    }

    public long getLongData () {
        return longData;
    }

    public void setLongData (long longData) {
        this.longData = longData;
    }

    public String getStringData () {
        return stringData;
    }

    public void setStringData (String stringData) {
        this.stringData = stringData;
    }

    public float getFloatData () {
        return floatData;
    }

    public void setFloatData (float floatData) {
        this.floatData = floatData;
    }

    public double getDoubleData () {
        return doubleData;
    }

    public void setDoubleData (double doubleData) {
        this.doubleData = doubleData;
    }

    public Date getDateData () {
        return dateData;
    }

    public void setDateData (Date dateData) {
        this.dateData = dateData;
    }

    public String getTransientData () {
        return transientData;
    }

    public void setTransientData (String transientData) {
        this.transientData = transientData;
    }
}
