/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turu.sockets;

import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.annotation.Singleton;
import org.primefaces.push.impl.JSONEncoder;

/**
 *
 * @author mateo
 */
@PushEndpoint("/counter")
@Singleton
public class SocketResource {

    @OnMessage(encoders = {JSONEncoder.class})
    public String onMessage(String mensaje) {
        System.out.println(mensaje+" socket");
        return mensaje;
    }
}
