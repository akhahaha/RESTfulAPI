package org.copakb.server.dao;

import org.copakb.server.dao.model.*;
import org.copakb.server.dao.model.Version;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * ProteinDAO implementation.
 * Created by vincekyi on 5/2/15.
 */
@SuppressWarnings("unchecked")
public class ProteinDAOImpl implements ProteinDAO {

    private SessionFactory sessionFactory;

    /**
     * Default sets the session factory.
     *
     * @param sessionFactory Session factory to use.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String addProteinCurrent(ProteinCurrent protein) throws HibernateException {
        ProteinCurrent existingProtein = searchByID(protein.getProtein_acc());
        if (existingProtein != null) {
            return existingProtein.getProtein_acc();
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        String uniprot = "";
        try {
            uniprot = (String) session.save(protein);
            tx.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            session.close();
            String temp = "";
            if (e instanceof org.hibernate.exception.LockTimeoutException) {
                temp = addProteinCurrentLast(protein);
            }
            if (temp.equals("")) {
                return "Failed";
            }
        }

        return uniprot;
    }

    /**
     * Add ProteinCurrent object as a last resort in case of Hibernate's LockTimeoutException.
     *
     * @param protein object to be added
     * @return Uniprot ID of protein if successful, empty string otherwise
     */
    public String addProteinCurrentLast(ProteinCurrent protein) {
        if (searchByID(protein.getProtein_acc()) != null) {
            return "Existed";
        }

        try {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            String uniprot = (String) session.save(protein);
            tx.commit();
            session.close();
            return uniprot;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }

    @Override
    public void updateProteinCurrent(String protein_acc, ProteinCurrent p) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ProteinCurrent proteinCurrent = (ProteinCurrent) session.get(ProteinCurrent.class, protein_acc);

            // Update protein current values
            proteinCurrent.setProtein_name(p.getProtein_name());
            proteinCurrent.setSequence(p.getSequence());
            proteinCurrent.setMolecular_weight(p.getMolecular_weight());
            //proteinCurrent.setChromosome(p.getChromosome());

            session.update(proteinCurrent);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deleteProteinCurrent(String protein_acc) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        ProteinCurrent proteinCurrent = (ProteinCurrent) session.load(ProteinCurrent.class, protein_acc);

        try {
            if (proteinCurrent != null) {
                session.delete(proteinCurrent);
                tx.commit();
                session.close();
                return true;
            }
        } catch (Exception e) {
            tx.rollback();
            session.close();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean compareProteinCurrent(ProteinCurrent a, ProteinCurrent b) {
        // todo: add more checks without null pointer exception; check if null?
        return !(!a.getSequence().equals(b.getSequence()) ||
                !a.getProtein_name().equals(b.getProtein_name()) ||
                a.getMolecular_weight() != b.getMolecular_weight() ||
                a.getSpecies().getSpecies_id() != b.getSpecies().getSpecies_id());
    }

    @Override
    public String addProteinHistory(ProteinHistory p) {
        //ProteinHistory existingProteinHistory = searchProteinHistory(p.getProtein_acc()); // add param
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(p);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            session.close();
            return "Failed";
        }
        session.close();
        return "Success";
    }

    @Override
    public ProteinHistory searchProteinHistory(String protein_acc) {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(ProteinHistory.class);

        Transaction tx = session.beginTransaction();
        try {
            Criterion c = Restrictions.eq("protein_acc", protein_acc);
            criteria.add(c);
            ProteinHistory result = (ProteinHistory) criteria.addOrder(Order.desc("version")).setMaxResults(1).uniqueResult();
            tx.commit();
            if (result == null)
                return null;
            return result; //return latest one
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public String addSpectrumProteinHistory(SpectrumProteinHistory p) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(p);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            session.close();
            return "Failed";
        }

        session.close();
        return "Success";
    }

    @Override
    public SpectrumProteinHistory searchSpectrumProteinHistory(String protein_acc, int spec_id) {
        Session session = sessionFactory.openSession();

        SpectrumProteinHistory result = (SpectrumProteinHistory) session
                .createCriteria(SpectrumProteinHistory.class)
                .add(Restrictions.and(
                        Restrictions.eq("protein_acc", protein_acc),
                        Restrictions.eq("spectrum_id", spec_id)))
                .addOrder(Order.desc("version"))
                .setMaxResults(1)
                .uniqueResult();

        session.close();
        return result;
    }

    @Override
    public int addVersion(Version version) {
        int result = -1;

        Version existingVersion = searchVersion(version.getVersion()); // add param
        if (existingVersion != null)
            return existingVersion.getVersion();

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            result = (int) session.save(version);
            tx.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Version searchVersion(int version) {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Version.class);

        Transaction tx = session.beginTransaction();
        try {
            Criterion nameRestriction = Restrictions.eq("version", version);

            criteria.add(Restrictions.and(nameRestriction));
            List<Version> results = criteria.list();
            tx.commit();
            if (results.isEmpty())
                return null;
            return results.get(0);
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public Version searchLatestVersion() {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Version.class);

        Transaction tx = session.beginTransaction();
        try {
            Version latestVersion = (Version) criteria.addOrder(Order.desc("version")).setMaxResults(1).uniqueResult();
            tx.commit();
            return latestVersion;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public ProteinCurrent searchByID(String uniprotID) {
        Session session = sessionFactory.openSession();

        ProteinCurrent protein = (ProteinCurrent) session.get(ProteinCurrent.class, uniprotID);

        session.close();
        return protein;
    }

    @Override
    public List<ProteinCurrent> searchByLikeID(String idPrefix) {
        Session session = sessionFactory.openSession();

        // Create query
        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class)
                .add(Restrictions.like("protein_acc", idPrefix + "%"))
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins;
    }

    @Override
    public List<ProteinCurrent> searchByPartialID(String idFragment) {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class)
                .add(Restrictions.like("protein_acc", "%" + idFragment + "%"))
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins;
    }

    @Override
    public ProteinCurrent searchByName(String protein_name) {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class)
                .add(Restrictions.eq("protein_name", protein_name))
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins.get(0);
    }

    @Override
    public List<ProteinCurrent> searchByPartialSequence(String sequence) {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class)
                .add(Restrictions.like("sequence", "%" + sequence + "%"))
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins;
    }

    @Override
    public List<ProteinCurrent> list() throws HibernateException {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class)
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins;
    }

    @Override
    public List<ProteinCurrent> limitedList(int start, int length) {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class)
                .setFirstResult(start)
                .setMaxResults(length)
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins;
    }

    @Override
    public String addDbRef(DBRef dbRef) {
        if (searchDbRefByID(dbRef.getProtein_acc()) != null) {
            return "Existed";
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        String result = (String) session.save(dbRef);

        tx.commit();
        session.close();
        return result;
    }

    @Override
    public DBRef searchDbRefByID(String uniprotID) {
        Session session = sessionFactory.openSession();

        DBRef dbRef = (DBRef) session.get(DBRef.class, uniprotID);

        session.close();
        return dbRef;
    }

    public DBRef searchDbRefByPDB(String pdbID) {
        Session session = sessionFactory.openSession();

        List<DBRef> dbRefs = session
                .createCriteria(DBRef.class)
                .add(Restrictions.like("pdb", "%" + pdbID + "%"))
                .list();

        session.close();

        if (dbRefs == null || dbRefs.isEmpty()) {
            return null;
        }

        return dbRefs.get(0);
    }

    @Override
    public ProteinCurrent searchByPDB(String pdbID) {
        DBRef dbRef = searchDbRefByPDB(pdbID);
        if (dbRef == null) {
            return null;
        }

        return searchByID(dbRef.getProtein_acc());
    }

    @Override
    public String addGene(Gene gene) throws HibernateException {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Gene existingGene = searchByGeneName(gene.getGene_name());
        if (existingGene != null) {
            session.close();
            return existingGene.getGene_name();
        }

        String result = (String) session.save(gene);
        tx.commit();

        session.close();
        return result;
    }

    @Override
    public Gene searchByGeneName(String name) {
        Session session = sessionFactory.openSession();

        Gene gene = (Gene) session.get(Gene.class, name);

        session.close();
        return gene;
    }

    @Override
    public ProteinCurrent searchByEnsg(String ensemblID) {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class, "protein")
                .createAlias("protein.genes", "genes")
                .add(Restrictions.like("genes.ensembl_id", "%" + ensemblID + "%"))
                .list();

        session.close();

        if (proteins.isEmpty()) {
            return null;
        }

        return proteins.get(0);
    }

    @Override
    public ProteinCurrent getProteinWithGenes(String uniprotID) {
        Session session = sessionFactory.openSession();

        ProteinCurrent protein = (ProteinCurrent) session.get(ProteinCurrent.class, uniprotID);
        if (protein != null) {
            Hibernate.initialize(protein.getGenes());
        }

        session.close();
        return protein;
    }

    @Override
    public int addGoTerms(GoTerms goTerms) {
        GoTerms existingGoTerms = searchByGOAccession(goTerms.getGO_accession());
        if (existingGoTerms != null) {
            return existingGoTerms.getGO_accession();
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        int result = (int) session.save(goTerms);

        tx.commit();
        session.close();
        return result;
    }

    @Override
    public GoTerms searchByGOAccession(int GO_accession) {
        Session session = sessionFactory.openSession();

        GoTerms go = (GoTerms) session.get(GoTerms.class, GO_accession);

        session.close();
        return go;
    }

    @Override
    public ProteinCurrent getProteinWithGoTerms(String uniprotID) {
        Session session = sessionFactory.openSession();

        ProteinCurrent protein = (ProteinCurrent) session.get(ProteinCurrent.class, uniprotID);
        if (protein != null) {
            Hibernate.initialize(protein.getGoTerms());
        }

        session.close();
        return protein;
    }

    @Override
    public int addSpecies(Species sp) throws HibernateException {
        Species existingSpecies = searchSpecies(sp.getSpecies_name()); // add param
        if (existingSpecies != null) {
            return existingSpecies.getSpecies_id();
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            int result = (int) session.save(sp);
            tx.commit();
            session.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            session.close();
            return -1;
        }
    }

    @Override
    public Species searchSpecies(String name) {
        Session session = sessionFactory.openSession();

        List<Species> species = session
                .createCriteria(Species.class)
                .add(Restrictions.eq("species_name", name))
                .list();

        session.close();

        if (species == null || species.isEmpty()) {
            return null;
        }

        return species.get(0);
    }

    @Override
    public int addSpectrumProtein(SpectrumProtein p) throws HibernateException {
        int result = -1;

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            result = (int) session.save(p);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.close();
        return result;
    }

    @Override
    public boolean deleteSpectrumProtein(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        SpectrumProtein spectrumProtein = (SpectrumProtein) session.load(SpectrumProtein.class, id);

        try {
            if (spectrumProtein != null) {
                session.delete(spectrumProtein);
                tx.commit();
                session.close();
                return true;
            }
        } catch (Exception e) {
            tx.rollback();
            session.close();
        }
        return false;
    }

    @Override
    public ProteinCurrent getProteinWithSpectra(String uniprotID) {
        Session session = sessionFactory.openSession();

        ProteinCurrent protein = (ProteinCurrent) session.get(ProteinCurrent.class, uniprotID);
        if (protein != null) {
            Hibernate.initialize(protein.getSpectra());
            for (SpectrumProtein s : protein.getSpectra()) {
                Hibernate.initialize(s.getSpectrum().getPeptide());
            }
        }

        session.close();
        return protein;
    }

    public SpectrumProtein searchSpectrumProtein(Spectrum spectrum, ProteinCurrent protein) {
        Session session = this.sessionFactory.openSession();

        Criteria criteria = session.createCriteria(SpectrumProtein.class);

        Transaction tx = session.beginTransaction();
        try {
            Criterion specIdRestriction = Restrictions.eq("spectrum", spectrum);
            Criterion proteinAccRestriction = Restrictions.eq("protein", protein);

            criteria.add(Restrictions.and(specIdRestriction, proteinAccRestriction));
            List<SpectrumProtein> results = criteria.list();

            session.flush();
            tx.commit();
            if (results.isEmpty())
                return null;
            return results.get(0);
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public List<SpectrumProtein> searchSpectrumProteins(ProteinCurrent proteinCurrent) {
        Session session = sessionFactory.openSession();

        List<SpectrumProtein> spectrumProteins = session
                .createCriteria(SpectrumProtein.class)
                .add(Restrictions.eq("protein", proteinCurrent))
                .list();

        session.close();

        if (spectrumProteins == null || spectrumProteins.isEmpty()) {
            return null;
        }

        return spectrumProteins;
    }

    @Override
    public List<ProteinCurrent> searchProteinsByPeptide(Peptide peptide) {
        Session session = sessionFactory.openSession();

        List<ProteinCurrent> proteins = session
                .createCriteria(ProteinCurrent.class, "protein")
                .createAlias("protein.spectra", "spectra")
                .add(Restrictions.eq("spectra.peptide", peptide))
                .list();

        session.close();

        if (proteins == null || proteins.isEmpty()) {
            return null;
        }

        return proteins;
    }

    @Override
    public List<SpectrumProtein> searchSpectrumProteins(Spectrum spectrum) {
        Session session = sessionFactory.openSession();
        //session.save(spectrum);

        List<SpectrumProtein> spectrumProteins = session
                .createCriteria(SpectrumProtein.class)
                .add(Restrictions.eq("spectrum", spectrum))
                .list();

        session.flush();

        session.close();

        if (spectrumProteins.isEmpty()) {
            return null;
        }

        return spectrumProteins;
    }

    @Override
    public String addHPAProtein(HPAProtein protein) {
        if (searchHPAByID(protein.getEnsemblID()) != null) {
            return "Existed";
        }

        Session session = sessionFactory.openSession();

        String result = (String) session.save(protein);

        session.close();
        return result;
    }

    @Override
    public HPAProtein searchHPAByID(String ensemblID) {
        Session session = sessionFactory.openSession();

        HPAProtein protein = (HPAProtein) session.get(HPAProtein.class, ensemblID);

        session.close();
        return protein;
    }

    @Override
    public String addAntibody(Antibody antibody) {
        if (searchAntibodyByID(antibody.getAntibodyID()) != null) {
            return "Existed";
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        String result = (String) session.save(antibody);

        tx.commit();
        session.close();
        return result;
    }

    @Override
    public Antibody searchAntibodyByID(String antibodyID) {
        Session session = sessionFactory.openSession();

        Antibody antibody = (Antibody) session.get(Antibody.class, antibodyID);

        session.close();
        return antibody;
    }

    @Override
    public ProteinCurrent getProteinWithPTMs(String uniprotID) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        ProteinCurrent protein = null;

        try {
            protein = (ProteinCurrent) session.get(ProteinCurrent.class, uniprotID);
            if (protein != null) {
                Hibernate.initialize(protein.getPTMs());
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }

        return protein;
    }
}
