/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facades;

import Entidades.Premio;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author propietario
 */
@Stateless
public class PremioFacade extends AbstractFacade<Premio> {

    @PersistenceContext(unitName = "libreriaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PremioFacade() {
        super(Premio.class);
    }
    
    public List<Premio> findPremiosL(){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("Premio.findPremiosL");
        return q.getResultList();
    }
    
    public List<Premio> findPremiosA(){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("Premio.findPremiosA");
        return q.getResultList();
    }
    
}
