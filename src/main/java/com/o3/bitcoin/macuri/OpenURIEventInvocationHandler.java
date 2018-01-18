/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.macuri;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;


public class OpenURIEventInvocationHandler implements InvocationHandler {

    private OpenUriAppleEventHandler urlHandler;

    public OpenURIEventInvocationHandler(OpenUriAppleEventHandler urlHandler) {
        this.urlHandler = urlHandler;
    }

    @SuppressWarnings({ "rawtypes", "unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("openURI")) {
            try {
                Class openURIEventClass = Class.forName("com.apple.eawt.AppEvent$OpenURIEvent");
                Method getURLMethod = openURIEventClass.getMethod("getURI");
                //arg[0] should be an instance of OpenURIEvent
                URI uri =  (URI)getURLMethod.invoke(args[0]);
                urlHandler.handleURI(uri);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
