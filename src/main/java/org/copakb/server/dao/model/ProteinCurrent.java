    package org.copakb.server.dao.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin on 4/30/2015.
 */

@Entity
@Table(name = "Protein_Current")
public class ProteinCurrent {
    private String protein_acc;
    private String sequence;
    private String protein_name;
    private String chromosome;
    private double molecular_weight;
    private String transmembrane_domain;
    private String cytoplasmatic_domain;
    private String noncytoplasmatic_domain;
    private String signal_peptide;
    private String ref_kb_id;
    private String keywords;
    private String feature_table;
    private String ENSG_ID;
    private Species species;
    private String wiki_link;
    private Set<ProteinGene> protein_genes;
    private Set<GOProtein> goProteins;
    private Set<PTM> PTMs;
    private Set<HPA> HPAs;

    public ProteinCurrent(String sequence, String protein_name, String chromosome, double molecular_weight, String transmembrane_domain, String cytoplasmatic_domain, String noncytoplasmatic_domain, String signal_peptide, String ref_kb_id, String keywords, String feature_table, String ENSG_ID, Species species, String wiki_link, Set<ProteinGene> protein_genes, Set<PTM> PTMs, Set<HPA> HPAs) {
        this.sequence = sequence;
        this.protein_name = protein_name;
        this.chromosome = chromosome;
        this.molecular_weight = molecular_weight;
        this.transmembrane_domain = transmembrane_domain;
        this.cytoplasmatic_domain = cytoplasmatic_domain;
        this.noncytoplasmatic_domain = noncytoplasmatic_domain;
        this.signal_peptide = signal_peptide;
        this.ref_kb_id = ref_kb_id;
        this.keywords = keywords;
        this.feature_table = feature_table;
        this.ENSG_ID = ENSG_ID;
        this.species = species;
        this.wiki_link = wiki_link;
        this.protein_genes = protein_genes;
        this.PTMs = PTMs;
        this.HPAs = HPAs;
    }

    public ProteinCurrent() {

    }

    @Id
    @Column(name = "protein_acc")
    public String getProtein_acc() {
        return protein_acc;
    }
    public void setProtein_acc(String protein_acc) {
        this.protein_acc = protein_acc;
    }

    @Column(name = "sequence")
    public String getSequence() {
        return sequence;
    }
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Column(name = "protein_name")
    public String getProtein_name() {
        return protein_name;
    }
    public void setProtein_name(String protein_name) {
        this.protein_name = protein_name;
    }

    @Column(name = "chromosome")
    public String getChromosome() {
        return chromosome;
    }
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    @Column(name = "molecular_weight")
    public double getMolecular_weight() {
        return molecular_weight;
    }
    public void setMolecular_weight(double molecular_weight) {
        this.molecular_weight = molecular_weight;
    }

    @Column(name = "transmembrane_domain")
    public String getTransmembrane_domain() {
        return transmembrane_domain;
    }
    public void setTransmembrane_domain(String transmembrane_domain) {
        this.transmembrane_domain = transmembrane_domain;
    }

    @Column(name = "cytoplasmatic_domain")
    public String getCytoplasmatic_domain() {
        return cytoplasmatic_domain;
    }
    public void setCytoplasmatic_domain(String cytoplasmatic_domain) {
        this.cytoplasmatic_domain = cytoplasmatic_domain;
    }

    @Column(name = "noncytoplasmatic_domain")
    public String getNoncytoplasmatic_domain() {
        return noncytoplasmatic_domain;
    }
    public void setNoncytoplasmatic_domain(String noncytoplasmatic_domain) {
        this.noncytoplasmatic_domain = noncytoplasmatic_domain;
    }

    @Column(name = "signal_peptide")
    public String getSignal_peptide() {
        return signal_peptide;
    }
    public void setSignal_peptide(String signal_peptide) {
        this.signal_peptide = signal_peptide;
    }

    @Column(name = "ref_kb_id")
    public String getRef_kb_id() {
        return ref_kb_id;
    }
    public void setRef_kb_id(String ref_kb_id) {
        this.ref_kb_id = ref_kb_id;
    }

    @Column(name = "keywords")
    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Column(name = "feature_table")
    public String getFeature_table() {
        return feature_table;
    }
    public void setFeature_table(String feature_table) {
        this.feature_table = feature_table;
    }

    @Column(name = "ENSG_ID")
    public String getENSG_ID() {
        return ENSG_ID;
    }
    public void setENSG_ID(String ENSG_ID) {
        this.ENSG_ID = ENSG_ID;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id",nullable = false)
    public Species getSpecies() {
        return species;
    }
    public void setSpecies(Species species) {
        this.species = species;
    }

    @Column(name = "wiki_link")
    public String getWiki_link() {
        return wiki_link;
    }
    public void setWiki_link(String wiki_link) {
        this.wiki_link = wiki_link;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "proteinCurrent")
    public Set<ProteinGene> getProtein_genes() {
        return protein_genes;
    }
    public void setProtein_genes(Set<ProteinGene> protein_genes) {
        this.protein_genes = protein_genes;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "proteinCurrent")
    public Set<PTM> getPTMs() {
        return PTMs;
    }
    public void setPTMs(Set<PTM> PTMs) {
        this.PTMs = PTMs;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "proteinCurrent")
    public Set<HPA> getHPAs() {
        return HPAs;
    }
    public void setHPAs(Set<HPA> HPAs) {
        this.HPAs = HPAs;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "proteinCurrent")
    public Set<GOProtein> getGoProteins() {
        return goProteins;
    }

    public void setGoProteins(Set<GOProtein> goProteins) {
        this.goProteins = goProteins;
    }
}
