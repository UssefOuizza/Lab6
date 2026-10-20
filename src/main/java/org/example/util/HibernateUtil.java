package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Lecture automatique du fichier hibernate.cfg.xml
            Configuration hibernateConfig = new Configuration();
            hibernateConfig.configure();
            return hibernateConfig.buildSessionFactory();
        } catch (Throwable initException) {
            System.err.println("Échec de la création initiale de la SessionFactory : " + initException);
            throw new ExceptionInInitializerError(initException);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
