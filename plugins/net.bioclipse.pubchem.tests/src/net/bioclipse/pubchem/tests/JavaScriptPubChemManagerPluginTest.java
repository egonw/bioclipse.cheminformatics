/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.pubchem.tests;

import org.junit.BeforeClass;

public class JavaScriptPubChemManagerPluginTest
    extends AbstractPubChemManagerPluginTest {

    @BeforeClass public static void setup() {
        pubchem = net.bioclipse.pubchem.Activator.getDefault().
            getJavaScriptManager();
    }

}