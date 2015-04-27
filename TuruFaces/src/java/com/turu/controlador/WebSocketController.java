/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turu.controlador;

import com.turu.dao.UsuarioFacade;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 *
 * @author mateo
 */
@ManagedBean
@ApplicationScoped
public class WebSocketController implements Serializable{

    /**
     * Creates a new instance of WebSocketController
     */
    private volatile int count;
    @EJB
    private UsuarioFacade usuarioFacade;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
  //  @PostConstruct
    public void increment() {
       // count=usuarioFacade.count();  
        count++;
        System.out.println(count+" count");
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
      
        System.out.println("si va");
        eventBus.publish("/counter", String.valueOf(count));
    }

    public WebSocketController() {
    }
}
