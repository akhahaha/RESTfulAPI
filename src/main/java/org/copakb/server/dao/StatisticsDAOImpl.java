package org.copakb.server.dao;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.copakb.server.dao.model.ModuleStatistics;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigInteger;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("unchecked")
public class StatisticsDAOImpl extends DAOImpl implements StatisticsDAO {

    public ModuleStatistics getModuleStatistics(int mod_id) throws HibernateException {

        update();
        Session session = sessionFactory.openSession();

        ModuleStatistics moduleStatistics = (ModuleStatistics) session.get(ModuleStatistics.class, mod_id);
        session.close();

        return moduleStatistics;
    }

    public List<ModuleStatistics> list() {
        Session session = sessionFactory.openSession();
        List<ModuleStatistics> list = session.createCriteria(ModuleStatistics.class).list();

        session.close();
        return list;


    }



    private void update() {
        Session session = sessionFactory.openSession();

        ModuleStatistics moduleStatistics = (ModuleStatistics) session.get(ModuleStatistics.class, 1);

        Calendar last = Calendar.getInstance();
        last.setTime(moduleStatistics.getLast_modified());


        Calendar current = Calendar.getInstance();
//        System.out.println(current.get(current.DAY_OF_WEEK));
//        System.out.println(last.get(current.DAY_OF_WEEK));
//        System.out.println(current.get(current.DAY_OF_MONTH));
//        System.out.println(last.get(current.DAY_OF_MONTH));
        if (current.get(current.DAY_OF_WEEK) == 1 && current.get(current.DAY_OF_MONTH) != last.get(last.DAY_OF_MONTH)) {


            int[] mod_id_indexes = {1,2,3,4,5,6,8,9};
            for (int i = 0; i < mod_id_indexes.length; i++) {
                Transaction tx1 = session.beginTransaction();
                String s = "SELECT COUNT(DISTINCT (protein_acc)) FROM COPADB.spectrum_protein WHERE mod_id = " + mod_id_indexes[i] +";";
                SQLQuery query = session.createSQLQuery(s);
                BigInteger big = (BigInteger) query.list().get(0);
                int nProteins = big.intValue();
//                System.out.println(nProteins);
                tx1.commit();

                Transaction tx2 = session.beginTransaction();
                s = "SELECT COUNT(DISTINCT (peptide_id)) FROM COPADB.spectrum WHERE mod_id = " + mod_id_indexes[i] + ";";
                query = session.createSQLQuery(s);
                big = (BigInteger) query.list().get(0);
                int nPeptides = big.intValue();
//                System.out.println(nPeptides);
                tx2.commit();

                Transaction tx3 = session.beginTransaction();
                s = "SELECT COUNT(DISTINCT (spectrum_id)) FROM COPADB.spectrum WHERE mod_id = " + mod_id_indexes[i] + ";";
                query = session.createSQLQuery(s);
                big = (BigInteger) query.list().get(0);
                int nSpectrum = big.intValue();
//                System.out.println(nSpectrum);
                tx3.commit();


                Transaction tx = session.beginTransaction();
                ModuleStatistics ms = (ModuleStatistics) session.get(ModuleStatistics.class, mod_id_indexes[i]);
                ms.setNum_of_proteins(nProteins);
                ms.setNum_of_peptides(nPeptides);
                ms.setNum_of_spectra(nSpectrum);
                ms.setLast_modified(new Date());
                session.merge(ms);
                tx.commit();
            }

        }

        session.close();

        return;
    }

}
