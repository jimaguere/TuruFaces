/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turu.dao;

import com.turu.entidad.EstadosPotrero;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mateo
 */
@Stateless
public class EstadosPotreroFacade extends AbstractFacade<EstadosPotrero> {
    @PersistenceContext(unitName = "TuruFacesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstadosPotreroFacade() {
        super(EstadosPotrero.class);
    }
    
}
