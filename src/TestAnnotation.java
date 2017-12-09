import latentdragon.redisorm.annotation.Column;
import latentdragon.redisorm.annotation.Id;
import latentdragon.redisorm.annotation.Table;
import latentdragon.redisorm.annotation.Transient;

import java.util.Date;

/**
 * Created by chenshaojie on 2017/12/8,18:50.
 */

@Table
public class TestAnnotation {

    @Id
    @Column(name = "int")
    private int intData = 12345;

    @Column(name = "long")
    private long longData = 1234567890;

    @Column(name="string")
    private String stringData = "hello world";

    @Column(name="float")
    private float floatData = 1.234f;

    @Column(name = "double")
    private double doubleData = 1.23456;

    @Column(name = "date")
    private Date dateData = new Date();

    @Transient
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
