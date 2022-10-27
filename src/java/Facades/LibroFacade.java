/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facades;

import Entidades.Autor;
import Entidades.Libro;
import Entidades.Pais;
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
public class LibroFacade extends AbstractFacade<Libro> {

    @PersistenceContext(unitName = "libreriaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LibroFacade() {
        super(Libro.class);
    }
    
    
    public List<Libro> libros_autor(Autor autor){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("Libro.findByAutor").setParameter("autor", autor);
        return q.getResultList();
    }
    
    public List<Pais> paises_ordenados(){
        em = getEntityManager();
        Query q;
        q = em.createNamedQuery("Pais.findAllOrder");
        return q.getResultList();
    }
    
}
