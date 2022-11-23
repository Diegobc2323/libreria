/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facades;

import Entidades.Autor;
import Entidades.AutorPremio;
import Entidades.Pais;
import Entidades.Premio;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author propietario
 * AQUI SE HACEN LAS LLAMADAS A CONSULTAS, LAS CUALES SE ENCUENTRAN EN LA CLASE AUTOR.JAVA
 */
@Stateless
public class AutorFacade extends AbstractFacade<Autor> {

    @PersistenceContext(unitName = "libreriaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AutorFacade() {
        super(Autor.class);
    }
    
    public List<Autor> autores_ordenados(){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("Autor.findAllOrder");
        return q.getResultList();
    }
    
    public List<Autor> autoresDeUnPais(Pais pais){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("Autor.findByPais").setParameter("pais", pais);
        return q.getResultList();
    }
    
    public List<AutorPremio> autoresDeUnPremio(Premio premio){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("AutorPremio.findByPremio").setParameter("premio", premio);
        return q.getResultList();
    }
    
    
}
