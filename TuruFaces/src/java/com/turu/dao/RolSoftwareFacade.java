/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turu.dao;

import com.turu.entidad.RolSoftware;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mateo
 */
@Stateless
public class RolSoftwareFacade extends AbstractFacade<RolSoftware> {
    @PersistenceContext(unitName = "TuruFacesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RolSoftwareFacade() {
        super(RolSoftware.class);
    }
    
    public void editarRolSoftware(RolSoftware rol){
       Query cq=getEntityManager().createNativeQuery("delete from rol_soft_menu where id_rol=?");
       cq.setParameter(1,rol.getIdRol());
       cq.executeUpdate();
       getEntityManager().merge(rol);
    }
    
}
