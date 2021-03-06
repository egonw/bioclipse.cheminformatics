/*******************************************************************************
 * Copyright (c) 2010  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.opsin.business;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="Manager for the OPSIN functionality to convert IUPAC names into " +
    		"chemical structures.",
    doi="10.1021/ci100384d"
)
@TestClasses("net.bioclipse.opsin.test.APITest," +
    "net.bioclipse.opsin.test.AbstractOpsinManagerPluginTest")
public interface IOpsinManager extends IBioclipseManager {

	@PublishedMethod(
		params="String iupacName",
		methodSummary="Converts an IUPAC name into a chemical structure"
	)
	@TestMethods("testParseIUPACName")
	public ICDKMolecule parseIUPACName(String iupacName) 
	                    throws BioclipseException;

	@PublishedMethod(
		params="String iupacName",
		methodSummary="Converts an IUPAC name into a CML document"
	)
	@TestMethods("testParseIUPACNameAsCML")
	public String parseIUPACNameAsCML(String iupacName) 
	              throws BioclipseException;

    @PublishedMethod(
        params="String iupacName",
        methodSummary="Converts an IUPAC name into a SMILES string"
    )
    public String parseIUPACNameAsSMILES(String iupacName) 
                  throws BioclipseException;

}
