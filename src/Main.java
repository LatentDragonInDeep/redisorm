import latentdragon.redisorm.configure.Configuration;
import latentdragon.redisorm.core.Session;
import latentdragon.redisorm.core.SessionFactory;
import latentdragon.redisorm.core.Transaction;

/**
 * Created by chenshaojie on 2017/12/8,19:00.
 */
public class Main {

    public static void main (String[] args) {
        Configuration configuration = Configuration.configure();
        SessionFactory factory = SessionFactory.buildSessionFactory(configuration);
        Session session = factory.getSession();
        Transaction transaction = session.beginTransaction();
        TestXML testXML = new TestXML();
        TestAnnotation testAnnotation = new TestAnnotation();
        session.save(testXML);
        session.save(testAnnotation);
        transaction.commit();

        TestXML newTestXML = (TestXML) session.get(12345,TestXML.class);
        TestAnnotation newTestAnnotation = (TestAnnotation)session.get(12345,TestAnnotation.class);

        String noUse = "";
    }
}
