/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author propietario
 */
@Entity
@Table(name = "autor_premio", catalog = "libros", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutorPremio.findAll", query = "SELECT a FROM AutorPremio a")
    , @NamedQuery(name = "AutorPremio.findByCodAutor", query = "SELECT a FROM AutorPremio a WHERE a.autorPremioPK.codAutor = :codAutor")
    , @NamedQuery(name = "AutorPremio.findByCodPremio", query = "SELECT a FROM AutorPremio a WHERE a.autorPremioPK.codPremio = :codPremio")
    , @NamedQuery(name = "AutorPremio.findByPremio", query = "SELECT a FROM AutorPremio a WHERE a.premio = :premio")
    , @NamedQuery(name = "AutorPremio.findByAnio", query = "SELECT a FROM AutorPremio a WHERE a.autorPremioPK.anio = :anio")})
public class AutorPremio implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AutorPremioPK autorPremioPK;
    @JoinColumn(name = "cod_autor", referencedColumnName = "cod_autor", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Autor autor;
    @JoinColumn(name = "cod_premio", referencedColumnName = "cod_premio", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Premio premio;

    public AutorPremio() {
    }

    public AutorPremio(AutorPremioPK autorPremioPK) {
        this.autorPremioPK = autorPremioPK;
    }

    public AutorPremio(int codAutor, int codPremio, int anio) {
        this.autorPremioPK = new AutorPremioPK(codAutor, codPremio, anio);
    }

    public AutorPremioPK getAutorPremioPK() {
        return autorPremioPK;
    }

    public void setAutorPremioPK(AutorPremioPK autorPremioPK) {
        this.autorPremioPK = autorPremioPK;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Premio getPremio() {
        return premio;
    }

    public void setPremio(Premio premio) {
        this.premio = premio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (autorPremioPK != null ? autorPremioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutorPremio)) {
            return false;
        }
        AutorPremio other = (AutorPremio) object;
        if ((this.autorPremioPK == null && other.autorPremioPK != null) || (this.autorPremioPK != null && !this.autorPremioPK.equals(other.autorPremioPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.AutorPremio[ autorPremioPK=" + autorPremioPK + " ]";
    }
    
}
