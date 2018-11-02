package com.nemesis.mathserver;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.addBootstrapListener(response -> {
            response.getDocument().head().append("<script src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/MathJax.js?config=TeX-MML-AM_CHTML' async></script>");
        });

//        event.addDependencyFilter((dependencies, filterContext) -> {
//            // DependencyFilter to add/remove/change dependencies sent to
//            // the client
//            return dependencies;
//        });
//
//        event.addRequestHandler((session, request, response) -> {
//            // RequestHandler to change how responses are handled
//            return false;
//        });
    }
}