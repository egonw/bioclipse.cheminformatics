/*******************************************************************************
 * Copyright (c) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint;

import net.bioclipse.cdk.jchempaint.business.IJavaJChemPaintGlobalPropertiesManager;
import net.bioclipse.cdk.jchempaint.business.IJavaJChemPaintManager;
import net.bioclipse.cdk.jchempaint.business.IJavaScriptJChemPaintGlobalPropertiesManager;
import net.bioclipse.cdk.jchempaint.business.IJavaScriptJChemPaintManager;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.cdk.jchempaint";

    // The shared instance
    private static Activator plugin;
    
    // tracks the JChemPaintManager
    private ServiceTracker jcpFinderTracker;
    private ServiceTracker jcpJsFinderTracker;
    // tracks the JChemPaintGlobalPropertiesManager
    private ServiceTracker jcpPropFinderTracker;
    private ServiceTracker jcpPropJsFinderTracker;
    
    /**
     * The constructor
     */
    public Activator() {}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        jcpFinderTracker = new ServiceTracker(
                context, 
                IJavaJChemPaintManager.class.getName(), 
                null
        );
        jcpFinderTracker.open();
        jcpJsFinderTracker = new ServiceTracker(
                context, 
                IJavaScriptJChemPaintManager.class.getName(), 
                null
        );
        jcpJsFinderTracker.open();
        jcpPropFinderTracker = new ServiceTracker(
                context, 
                IJavaJChemPaintGlobalPropertiesManager.class.getName(), 
                null
        );
        jcpPropFinderTracker.open();
        jcpPropJsFinderTracker = new ServiceTracker(
                context, 
                IJavaScriptJChemPaintGlobalPropertiesManager.class.getName(), 
                null
        );
        jcpPropJsFinderTracker.open();
    }

    public IJavaJChemPaintManager getJavaManager() {
        IJavaJChemPaintManager exampleManager = null;
        try {
            exampleManager = (IJavaJChemPaintManager) jcpFinderTracker.waitForService(1000*30);
        } catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the JavaJChemPaintManager", e);
        }
        if(exampleManager == null) {
            throw new IllegalStateException(
                          "Could not get the JavaJChemPaintManager" );
        }
        return exampleManager;
    }
    public IJavaScriptJChemPaintManager getJavaScriptManager() {
        IJavaScriptJChemPaintManager exampleManager = null;
        try {
            exampleManager = (IJavaScriptJChemPaintManager)jcpJsFinderTracker.waitForService(1000*30);
        } catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the JavaScriptJChemPaintManager", e);
        }
        if(exampleManager == null) {
            throw new IllegalStateException(
                          "Could not get the JavaScriptJChemPaintManager" );
        }
        return exampleManager;
    }

    public IJavaJChemPaintGlobalPropertiesManager getJCPPropManager() {
        IJavaJChemPaintGlobalPropertiesManager manager = null;
        try {
            manager = (IJavaJChemPaintGlobalPropertiesManager)
                jcpPropFinderTracker.waitForService(1000*30);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not get jcpglobal manager", e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get jcpglobal manager");
        }
        return manager;
    }
    public IJavaScriptJChemPaintGlobalPropertiesManager
        getJCPPropJavaScriptManager() {
        IJavaScriptJChemPaintGlobalPropertiesManager manager = null;
        try {
            manager = (IJavaScriptJChemPaintGlobalPropertiesManager)
                jcpPropJsFinderTracker.waitForService(1000*30);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not get jcpglobal manager", e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get jcpglobal manager");
        }
        return manager;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
}
