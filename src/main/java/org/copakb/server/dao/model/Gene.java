package org.copakb.server.dao.model;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Kevin on 4/30/2015.
 */
@Entity
@Table(name = "Gene")
public class Gene {
    private String gene_name;
    private String ensembl_id;
    private String chromosome;
    private Set<HPAProtein> hpaProteins;
    private Set<Disease> diseases;
    private Set<ProteinCurrent> proteins;

    public Gene(String gene_name, String ensembl_id, Set<HPAProtein> hpaProteins, Set<Disease> diseases,
                Set<ProteinCurrent> proteins) {
        this.gene_name = gene_name;
        this.ensembl_id = ensembl_id;
        this.hpaProteins = hpaProteins;
        this.diseases = diseases;
        this.proteins = proteins;
    }

    public Gene() {
        //empty
    }

    @Id
    @Column(name = "gene_name")
    public String getGene_name() { return gene_name; }
    public void setGene_name(String gene_name) { this.gene_name = gene_name; }

    @Column(name = "ensembl_id")
    public String getEnsembl_id() { return ensembl_id; }
    public void setEnsembl_id(String ensembl_id) { this.ensembl_id = ensembl_id; }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ensemblID")
    public Set<HPAProtein> getHpaProteins() { return hpaProteins; }
    public void setHpaProteins(Set<HPAProtein> hpas1) { this.hpaProteins = hpas1; }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "Disease_Gene", joinColumns = {
            @JoinColumn(name = "gene_name", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "DOID",
                    nullable = false, updatable = false) })
    public Set<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(Set<Disease> diseases) {
        this.diseases = diseases;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "Protein_Gene", joinColumns = {
            @JoinColumn(name = "gene_name", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "protein_acc",
                    nullable = false, updatable = false) })
    public Set<ProteinCurrent> getProteins() {
        return proteins;
    }

    public void setProteins(Set<ProteinCurrent> proteins) {
        this.proteins = proteins;
    }

    @Override
    public String toString() {
        String ans = "gene_name: " + gene_name + "\n" +
                "ensembl_id: " + ensembl_id + "\n" + "disease: ";

        //ans += diseases.size() + "";
        for(Disease disease : diseases) {
            ans += disease.toString() + "\n";
        }
        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gene gene = (Gene) o;

        return !(ensembl_id != null ? !ensembl_id.equals(gene.ensembl_id) : gene.ensembl_id != null);

    }
}
