/*******************************************************************************
 * Copyright (c) 2008-2009  Ola Spjuth
 *               2008-2009  Jonathan Alvarsson
 *               2008-2009  Stefan Kuhn
 *               2008-2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.cdk.business;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.domain.MoleculesInfo;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.ui.jobs.BioclipseUIJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.formats.IChemFormat;

@PublishedClass( "Contains CDK related methods")
@TestClasses(
    "net.bioclipse.cdk.business.test.CDKManagerTest," +
		"net.bioclipse.cdk.business.test.CDKManagerPluginTest"
)
public interface ICDKManager extends IBioclipseManager {

    /**
     * Create a CDKMolecule from SMILES
     * @param SMILES
     * @return
     * @throws BioclipseException
     */
    @Recorded
    @PublishedMethod( params = "String SMILES",
                      methodSummary = "Creates a cdk molecule from " +
                      		          "SMILES")
    @TestMethods(
       "testLoadMoleculeFromSMILESFile,testCreateMoleculeFromSMILES," +
       "testFingerPrintMatch,testSubStructureMatch,testSMARTSMatching," +
       "testSave")
    public ICDKMolecule fromSMILES(String SMILES)
        throws BioclipseException;


    /**
     * Perceives aromaticity on an IMolecule
     * @param mol
     * @return
     * @throws BioclipseException
     */
    @Recorded
    @PublishedMethod( params = "IMolecule mol",
                      methodSummary = "Perceives aromaticity on an IMolecule. The molecule will be unchanged " +
                                    "and a new molecule created. Uses the org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector class from CDK.")
    @TestMethods(
       "testPerceiveAromaticity,")
    public IMolecule perceiveAromaticity(IMolecule mol) throws BioclipseException;

    /**
     * Loads a molecule from file using CDK.
     * If many molecules, just return first.
     * To return a list of molecules, use loadMolecules(...)
     *
     * @param path The path to the file
     * @return a BioJavaSequence object
     * @throws IOException
     * @throws BioclipseException
     * @throws CoreException
     */
    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Loads a molecule from file. Does not do aromaticity detection (use perceiveAromaticity). " +
                                      "Returns the first if multiple " +
                      		          "molecules exists in the file ")
    @TestMethods("testLoadMoleculeFromCMLFile,testLoadCMLFromFile2")
    public ICDKMolecule loadMolecule( String path )
        throws IOException, BioclipseException, CoreException;

    public ICDKMolecule loadMolecule( IFile file,
                                      BioclipseUIJob<ICDKMolecule> uiJob)
                                             throws IOException,
                                                    BioclipseException,
                                                    CoreException;
    /**
     * Load molecule from an <code>IFile</code> using CDK.
     * If many molecules, just return first.
     * To return a list of molecules, use loadMolecules(...)
     *
     * @param file to be loaded
     * @return loaded sequence
     * @throws IOException
     * @throws BioclipseException
     * @throws CoreException
     */
    @Recorded
    public ICDKMolecule loadMolecule( IFile file,
                                      IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

    /**
     * @param file
     * @return
     * @throws IOException
     * @throws BioclipseException
     * @throws CoreException
     */
    @Recorded
    public ICDKMolecule loadMolecule( IFile file )
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Determines the file format if the file, if chemical")
    @TestMethods("testDetermineFormat")
    public String determineFormat(String path) throws IOException, CoreException;

    /**
     * Loads molecules from a file at a given path.
     *
     * @param path
     * @return a list of molecules
     * @throws IOException
     * @throws BioclipseException on parsing trouble of the molecules
     * @throws CoreException
     */
    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Loads molecules from a file at " +
                      		          "a given path into a list of " +
                      		          "molecules")
    @TestMethods("testLoadMolecules")
    public List<ICDKMolecule> loadMolecules(String path)
        throws IOException, BioclipseException, CoreException;

    /**
     * Loads molecules from an IFile.
     *
     * @param file
     * @return a list of molecules
     * @throws IOException
     * @throws BioclipseException on parsing trouble of the molecules
     * @throws CoreException
     */
    @Recorded
    public List<ICDKMolecule> loadMolecules( IFile file,
                                             IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

    @Recorded
    public List<ICDKMolecule> loadMolecules( IFile file )
        throws IOException, BioclipseException, CoreException;

    public List<ICDKMolecule> loadMolecules( IFile file,
                                       BioclipseUIJob<List<ICDKMolecule>> uiJob);

    public List<ICDKMolecule> loadMolecules( IFile file,
                                             IChemFormat format,
                                             IProgressMonitor monitor)
        throws IOException, BioclipseException, CoreException;

    /**
     * Save a molecule in same format as loaded to same filename, if exists
     * @param mol The molecule to save
     * @throws IllegalStateException
     */
    @Recorded
    @PublishedMethod(params = "IMolecule mol",
            methodSummary="Saves molecule to file, if read from file.")
    @TestMethods("testSaveMolecule_IMolecule")
    public void saveMolecule(IMolecule mol)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Save a molecule in same format as loaded to same filename, if exists
     * @param mol The molecule to save
     * @param overwrite If set to true, overwrite if file exists
     * @throws IllegalStateException
     */
    @Recorded
    @PublishedMethod(params = "IMolecule mol, boolean overwrite",
            methodSummary="saves mol to a file, if previously read from file. " +
            		"Overwrite determines if existing file shall be overwritten.")
    @TestMethods("testSaveMolecule_IMolecule_boolean")
    public void saveMolecule(IMolecule mol, boolean overwrite)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Save a molecule in same format as loaded
     * @param mol The molecule to save
     * @param filename Where to save, relative to workspace root
     * @throws IllegalStateException
     */
    @Recorded
    @PublishedMethod(params = "IMolecule mol, String filename",
            methodSummary="saves mol to a file (filename must be a relative to workspace root and "+
            "folder must exist), filetype must be one of the constants given by getPossibleFiletypes")
    @TestMethods("testSaveMolecule_IMolecule_String")
    public void saveMolecule(IMolecule mol, String filename)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Save a molecule in same format as loaded
     * @param mol The molecule to save
     * @param filename Where to save, relative to workspace root
     * @param overwrite If set to true, overwrite if file exists
     * @throws IllegalStateException
     */
    @Recorded
    @PublishedMethod(params = "IMolecule mol, String filename, boolean overwrite",
            methodSummary="saves mol to a file (filename must be a relative to workspace root and "+
            "folder must exist), with a given filename and " +
            "overwrites determined by the given boolean. File type is taken" +
            "from the mol object, if available. If not, then the file extension " +
            "is used to make a somewhat educated guess.")
    @TestMethods("testSaveMolecule_IMolecule_String_boolean")
    public void saveMolecule(IMolecule mol, String filename, boolean overwrite)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Save a molecule in same format as loaded
     * @param mol The molecule to save
     * @param file Where to save
     * @param overwrite If set to true, overwrite if file exists
     * @throws IllegalStateException
     */
    @Recorded
    @TestMethods("testSaveMolecule_IMolecule_IFile_boolean")
    public void saveMolecule(IMolecule mol, IFile file, boolean overwrite)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * @param mol The molecule to save
     * @param filename Where to save, relative to workspace root
     * @param filetype Which format to save (for formats, see constants)
     * @throws IllegalStateException
     */
    @Recorded
    @PublishedMethod(params = "IMolecule mol, String filename, IChemFormat filetype",
            methodSummary="saves mol to a file (filename must be a relative to workspace root and "+
            "folder must exist), filetype must be a IChemFormat")
    @TestMethods("testSaveMolecule_IMolecule_String_String")
    public void saveMolecule(IMolecule mol, String filename, IChemFormat filetype)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * @param mol The molecule to save
     * @param target Where to save
     * @param filetype Which format to save (for formats, see constants)
     * @throws IllegalStateException
     */
    @Recorded
    public void saveMolecule(IMolecule mol, IFile target, IChemFormat filetype)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * @param mol The molecule to save
     * @param filename Where to save, relative to workspace root
     * @param filetype Which format to save (for formats, see constants)
     * @param overwrite if true and file exists, overwrite
     * @throws IllegalStateException
     */
    @Recorded
    @PublishedMethod(params = "IMolecule mol, String filename, IChemFormat filetype, boolean overwrite",
            methodSummary="saves mol to a file (filename must be a relative to workspace root and "+
            "folder must exist), filetype must be a IChemFormat. " +
            "If overwrite=true then file will be overwritten if exists.")
    @TestMethods("testSaveMolecule_IMolecule_String_String")
    public void saveMolecule(IMolecule mol, String filename, IChemFormat filetype, boolean overwrite)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * @param mol The molecule to save
     * @param target Where to save
     * @param filetype Which format to save (for formats, see constants)
     * @param overwrite if true and file exists, overwrite
     * @throws IllegalStateException
     */
    @Recorded
    public void saveMolecule(IMolecule mol, IFile target, IChemFormat filetype, boolean overwrite)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Save a list of molecules to file
     * @param molecules The molecules to save
     * @param target The IFile to save to
     * @param filetype either CML or SDF
     * @throws BioclipseException
     * @throws CDKException
     * @throws CoreException
     */
    @Recorded
    public void saveMolecules(List<? extends IMolecule> molecules, IFile target, IChemFormat filetype)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Save a list of molecules to file
     * @param molecules The molecules to save
     * @param path Path to file to save to
     * @param filetype either CML or SDF
     * @throws BioclipseException
     * @throws CDKException
     * @throws CoreException
     */
    @Recorded
    public void saveMolecules(List<? extends IMolecule> molecules, String path, IChemFormat filetype)
    	throws BioclipseException, CDKException, CoreException;


    /**
     * @param model The ChemModel to save
     * @param target Where to save
     * @param filetype Which format to save (for formats, see constants)
     * @throws IllegalStateException
     */
    @Recorded
    public void save(IChemModel model, String target, IChemFormat filetype)
    	throws BioclipseException, CDKException, CoreException;

    /**
     * Calculate SMILES string for an IMolecule
     * @param molecule
     * @return
     * @throws BioclipseException
     */
    @Recorded
    @PublishedMethod ( params = "IMolecule molecule",
                       methodSummary = "Returns the SMILES for a " +
                       		           "molecule" )
    @TestMethods("testSaveMoleculesSDF,testSaveMoleculesCML,testSaveMoleculesCMLwithProps")
    public String calculateSMILES (IMolecule molecule)
                  throws BioclipseException;

    /**
     * @param path
     * @return
     * @throws CoreException
     */
    @PublishedMethod (params = "String path",
                      methodSummary = "Creates and iterator to the " +
                      		          "molecules in the file at the " +
                      		          "path")
    @TestMethods("testCreatingMoleculeIterator")
    public Iterator<ICDKMolecule> createMoleculeIterator(String path)
                                  throws CoreException;

    /**
     * @param file
     * @return
     * @throws CoreException
     */
    public Iterator<ICDKMolecule> createMoleculeIterator(
        IFile file,
        IProgressMonitor monitor ) throws CoreException;

    public Iterator<ICDKMolecule> createMoleculeIterator(IFile file)
                                  throws CoreException;

    /**
     * True if the fingerprint of the subStructure is a subset of the
     * fingerprint for the molecule
     *
     * @param molecule
     * @param subStructure
     * @return
     * @throws BioclipseException
     */
    @PublishedMethod (params = "ICDKMolecule molecule, " +
    		                   "ICDKMolecule subStructure",
                      methodSummary = "Returns true if the " +
                      		          "fingerprint of the " +
                      		          "subStructure is a subset of the" +
                      		          "fingerprint for the molecule")
    @Recorded
    @TestMethods("testLoadMoleculeFromSMILESFile,testFingerPrintMatch")
    public boolean fingerPrintMatches( ICDKMolecule molecule,
                                       ICDKMolecule subStructure )
                   throws BioclipseException;

    /**
     * True if the paramater molecule1 and the
     * paramater molecule2 are isomorph.
     *
     * (Performs an isomophism test without checking fingerprints first)
     *
     * @param molecule
     * @param subStructure
     * @return
     */
    @PublishedMethod (params = "ICDKMolecule molecule1, " +
    		                       "ICDKMolecule molecule2",
    		       methodSummary = "Returns true if the paramater named " +
    		                       "molecule1 and the " +
    		                       "paramater named molecule2 are isomorph. \n" +
    		                       "(Performs an isomophism test without " +
    		                       "checking fingerprints)")
    @Recorded
    @TestMethods("testStructureMatches")
    public boolean areIsomorphic( ICDKMolecule molecule1,
                                  ICDKMolecule molecule2 );


    /**
     * True if the paramater substructure is a substructure to the
     * paramater molecule.
     *
     * (Performs an isomophism test without checking fingerprints first)
     *
     * @param molecule
     * @param subStructure
     * @return
     */
    @PublishedMethod (params = "ICDKMolecule molecule, " +
    		                       "ICDKMolecule subStructure",
    		       methodSummary = "Returns true if the paramater named " +
    		                       "subStructure is a substructure of the " +
    		                       "paramater named molecule. \n" +
    		                       "(Performs an isomophism test without " +
    		                       "checking fingerprints)")
    @Recorded
    @TestMethods("testSubStructureMatch")
    public boolean subStructureMatches( ICDKMolecule molecule,
                                        ICDKMolecule subStructure );

    /**
     * Creates a cdk molecule from an IMolecule
     *
     * @param m
     * @return
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "IMolecule m",
                       methodSummary = "Creates a cdk molecule from a" +
                                       " molecule" )
    @Recorded
    @TestMethods("testCDKMoleculeFromIMolecule")
    public ICDKMolecule create( IMolecule m ) throws BioclipseException;

    /**
     * Creates a cdk molecule from a CML String
     *
     * @param m
     * @return
     * @throws BioclipseException if input is null or parse fails
     * @throws IOException if file cannot be read
     */
    @PublishedMethod ( params = "String cml",
                       methodSummary = "Creates a cdk molecule from a " +
                                       "CML String" )
    @Recorded
    @TestMethods("testFromCML")
    public ICDKMolecule fromCml( String cml )
                        throws BioclipseException, IOException;

    /**
     * Creates a cdk molecule from any String.
     */
    @PublishedMethod ( params = "String input file",
                       methodSummary = "Creates a cdk molecule from a " +
                                       "String." )
    @Recorded
    @TestMethods("testFromString")
    public ICDKMolecule fromString( String cml ) throws
        BioclipseException, IOException;

    /**
     * Returns true if the given molecule matches the given SMARTS
     *
     * @param molecule
     * @param smarts
     * @return whether the given SMARTS matches the given molecule
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "ICDKMolecule molecule, String smarts",
                       methodSummary = "Returns true if the given " +
                                       "SMARTS matches the given " +
                                       "molecule" )
    @Recorded
    @TestMethods("testSMARTSMatching")
    public boolean smartsMatches( ICDKMolecule molecule, String smarts )
                   throws BioclipseException;

    /**
     * @param filePath
     * @return the number of entries in the sdf file at the given path or
     *         0 if failed to read somehow.
     */
    @PublishedMethod ( params = "String filePath",
                       methodSummary = "Counts the number of entries " +
                                       "in an SDF file at the given " +
                                       "file path. Returns 0 in case " +
                                       "of problem.")
    @Recorded
    @TestMethods("testNumberOfEntriesInSDF")
    public int numberOfEntriesInSDF( String filePath );

    /**
     * @param filePath
     */
    @PublishedMethod ( params = "String filePath",
                       methodSummary = "Creates a index file for the SD-file." )
    public void createSDFileIndex( String filePath);

    public void createSDFileIndex(IFile file, IProgressMonitor monitor);

    public void createSDFileIndex(IFile file, BioclipseUIJob<?> uiJob);
    /**
     * Reads files and extracts conformers if available.
     * @param path the full path to the file
     * @return a list of molecules that may have multiple conformers
     */
    @Recorded
    @PublishedMethod ( params = "String path",
                       methodSummary = "Loads the molecules at the " +
                          "path into a list, and take conformers into " +
                          "account. Currently only reads SDFiles.")
    @TestMethods("testLoadConformers")
    public List<ICDKMolecule> loadConformers( String path );

    /**
     * Reads files and extracts conformers if available.
     * @param file
     * @return a list of molecules that may have multiple conformers
     */
    @Recorded
    public List<ICDKMolecule> loadConformers( IFile file,
                                              IProgressMonitor monitor );

    /**
     * @param file
     * @return
     */
    public List<ICDKMolecule> loadConformers( IFile file);

    /**
     * Returns an iterator to the molecules in an IFile that might
     * contain conformers.
     *
     * @param instream
     * @return
     */
    @Recorded
    public Iterator<ICDKMolecule> createConformerIterator(
        IFile file, IProgressMonitor monitor );

    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Returns an iterator to the molecules" +
                          " in an IFile that might contain conformers." )
    public Iterator<ICDKMolecule> createConformerIterator( String path );

    @PublishedMethod ( params = "IMolecule molecule",
                       methodSummary = "Calculate and return the " +
                                       "molecular weight for the " +
                                       "molecule.")
    @Recorded
    @TestMethods("testLoadMoleculeFromSMILESFile")
    public double calculateMass( IMolecule molecule )
                  throws BioclipseException;


    public SDFileIndex createSDFIndex(IFile file, IProgressMonitor monitor);
    public SDFileIndex createSDFIndex(IFile file);
    public SDFileIndex createSDFIndex(IFile file, BioclipseUIJob<SDFileIndex> uiJob);
    @Recorded
    @PublishedMethod( params = "String file",
                      methodSummary = "Creates a index of the molecules positons in a SDFile")
    public SDFileIndex createSDFIndex(String file);
    /**
     * @param file
     * @param subProgressMonitor
     * @return
     */
    @Recorded
    public int numberOfEntriesInSDF( IFile file, IProgressMonitor monitor );
    public int numberOfEntriesInSDF( IFile file, BioclipseUIJob<Integer> uiJob);

    @Recorded
    @PublishedMethod(params = "List<IMolecule> molecule",
                     methodSummary="Create 2D coordinate for the given molecules")
    @TestMethods("testGenerate2DCoordinates")
    public List<ICDKMolecule>  generate2dCoordinates(List<? extends IMolecule> molecules) throws Exception;

    @Recorded
    @PublishedMethod(params = "IMolecule molecule",
                     methodSummary="Create 2D coordinate for the given molecule")
    @TestMethods("testGenerate2DCoordinatesSingle")
    public ICDKMolecule generate2dCoordinates(IMolecule molecule) throws Exception;

    @Recorded
    @PublishedMethod(params = "List<IMolecule> molecule",
                     methodSummary="Create 3D coordinate for the given molecules")
    @TestMethods("testGenerate3DCoordinates")
    public List<IMolecule> generate3dCoordinates(List<IMolecule> molecule) throws Exception;


    @Recorded
    @PublishedMethod(params = "IMolecule molecule",
                     methodSummary="Create 3D coordinate for the given molecule")
    @TestMethods("testGenerate3DCoordinatesSingle")
    public IMolecule generate3dCoordinates(IMolecule molecule) throws Exception;

    @Recorded
	  public void saveMol2(ICDKMolecule mol2, String filename) throws InvocationTargetException, BioclipseException, CDKException, CoreException;

    @Recorded
    @PublishedMethod(params = "ICDKMolecule molecule, String filename",
                     methodSummary = "Saves a molecule in the MDL molfile V2000 format (filename must be a relative to workspace root and "+
    								 "folder must exist)")
    @TestMethods("testSaveMDLMolfile")
    public void saveMDLMolfile(ICDKMolecule mol, String filename) throws InvocationTargetException, BioclipseException, CDKException, CoreException;

    @Recorded
    @PublishedMethod(params = "ICDKMolecule molecule, String filename",
                     methodSummary = "Saves a molecule in the Chemical Markup Language format (filename must be a relative to workspace root and "+
    								 "folder must exist). Example of file String: \"/Virtual/bla.cml\"")
    @TestMethods("testSaveCML")
    public void saveCML(ICDKMolecule cml, String filename) throws InvocationTargetException, BioclipseException, CDKException, CoreException;

    /**
     * Loads molecules from a SMILES file.
     *
     * @param path String with the path to the file
     * @return a list of molecules
     * @throws CoreException
     * @throws IOException
     */
    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Loads molecules from a SMILES file at " +
                      		          "a given path into a list of " +
                      		          "molecules")
    @TestMethods("testLoadMoleculeFromSMILESFileDirectly,testLoadMoleculeFromSMILESFile")
    public List<ICDKMolecule> loadSMILESFile(String path) throws CoreException, IOException;

	public List<ICDKMolecule> loadSMILESFile(IFile file) throws CoreException,
	                                                            IOException;
	public List<ICDKMolecule> loadSMILESFile(IFile file,IProgressMonitor monitor)
	                                    throws CoreException, IOException;
	public List<ICDKMolecule> loadSMILESFile(IFile file,
	                               BioclipseUIJob<List<ICDKMolecule>> uiJob)
	                               throws CoreException, IOException;

	/**
	 * Return number of molecules in file
	 * @param file
	 * @return
	 */
    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Returns number of molecules in file.")
    @TestMethods("testNoMoleucles_String")
	public int getNoMolecules(String path);

	/**
	 * Return number of molecules in file
	 * @param file
	 * @return
	 */
    @Recorded
    @PublishedMethod( params = "String path",
                      methodSummary = "Returns number of molecules in file.")
	@TestMethods("testGetInfo")
    public MoleculesInfo getInfo(String path);

    /**
     * Depict if molecule has 2D coordinates available.
     * @param mol IMolecule to depict 2D for
     * @return
     * @throws BioclipseException if calculation failed
     */
    @Recorded
    @PublishedMethod( params = "IMolecule mol",
                      methodSummary = "Returns true if molecule has 2D coordinates, " +
                      		"false otherwise.")
    @TestMethods("testHas2d")
	boolean has2d(IMolecule mol) throws BioclipseException;

    /**
     * Depict if molecule has 3D coordinates available.
     * @param mol IMolecule to depict 3D for
     * @return
     * @throws BioclipseException if calculation failed
     */
    @Recorded
    @PublishedMethod( params = "IMolecule mol",
                      methodSummary = "Returns true if molecule has 3D coordinates, " +
                      		"false otherwise.")
    @TestMethods("testHas3d")
	boolean has3d(IMolecule mol) throws BioclipseException;

    @Recorded
    @PublishedMethod(params = "IMolecule mol",
                     methodSummary="Adds explicit hydrogens to this molecule")
    @TestMethods("testAddExplicitHydrogens")
    public IMolecule addExplicitHydrogens(IMolecule molecule) throws Exception;

    @Recorded
    @PublishedMethod(params = "IMolecule mol",
                     methodSummary="Adds implicit hydrogens to this molecule")
    @TestMethods("testAddImplicitHydrogens")
   	public IMolecule addImplicitHydrogens(IMolecule molecule) throws BioclipseException, InvocationTargetException;

    @Recorded
    @PublishedMethod(methodSummary="Creates an empty molecule list.")
    public List<IMolecule> createMoleculeList()
        throws BioclipseException, InvocationTargetException;

    @Recorded
    @TestMethods("testCreateSDFile_IFile_IMoleculeArray")
   	public void saveSDFile(IFile file, List<IMolecule> entries, IProgressMonitor monitor) throws BioclipseException, InvocationTargetException;

    @Recorded
    @PublishedMethod(params = "String file, List<IMolecule> entries",
                     methodSummary="Creates an sd file from a number of molecules")
    @TestMethods("testCreateSDFile_String_IMoleculeArray")
    public void saveSDFile(String file, List<IMolecule> entries) throws BioclipseException, InvocationTargetException;

    @Recorded
    @TestMethods("testExtractFromSDFile_IFile_int_int")
    public List<IMolecule> extractFromSDFile(IFile file, int startentry, int endentry) throws BioclipseException, InvocationTargetException;

    @Recorded
    @PublishedMethod(params = "String file, int startentry, int endentry",
                     methodSummary="Extracts a number of entries from an sd file. Does not read the complete file for this.")
    @TestMethods("testExtractFromSDFile_String_int_int")
    public List<IMolecule> extractFromSDFile(String file, int startentry, int endentry) throws BioclipseException, InvocationTargetException;

    @PublishedMethod(params = "String file, String property, Collection values",
                     methodSummary= "Extracts moleculs indicated by index in values to a list")
    public List<ICDKMolecule> extractFromSDFile( String file,
                                                 String property,
                                                 Collection<String> value);
    public List<ICDKMolecule> extractFromSDFile( IFile file,
                                                 String property,
                                                 Collection<String> value,
                                                 IProgressMonitor monitor);

    public Map<Integer,String> createSDFPropertyMap( IFile file,
                                                     String property)
                                                     throws CoreException,
                                                     IOException;
    @PublishedMethod(params = "String file, String property",
                     methodSummary="Create a index for molecules that contains a certain property")

    public Map<Integer,String> createSDFPropertyMap( String file,
                                                     String property)
                                                     throws CoreException,
                                                     IOException;

    @Recorded
    @PublishedMethod(params = "ICDKMolecule m",
                     methodSummary = "Gives the molecularformula as a String")
    public String molecularFormula( ICDKMolecule m );

    @Recorded
    @PublishedMethod(
         params = "IContentType type",
         methodSummary = "Determines the IChemFormat equivalent of the given" +
             "content type")
    public IChemFormat determineFormat(IContentType type);

    @Recorded
    @PublishedMethod(
         params = "String type",
         methodSummary = "Returns the IChemFormat for the given type")
    @TestMethods("testGetFormat")
    public IChemFormat getFormat(String type);

    @Recorded
    @PublishedMethod(
         methodSummary = "Returns all available IChemFormats")
    public String getFormats();

    @Recorded
    @PublishedMethod(
         params = "String path",
         methodSummary = "Makes an educated guess if the file format based" +
              "on the file extension alone.")
    @TestMethods("testGuessFormatFromExtension")
    public IChemFormat guessFormatFromExtension(String type);

    @Recorded
    @PublishedMethod(
         params = "String SMARTS",
         methodSummary = "Determines if a SMARTS string cane be interpreted by CDK.")
    @TestMethods("testSMARTSonFile")
    boolean isValidSmarts( String SMARTS );

    @Recorded
    @PublishedMethod(
         params = "IMolecule molecule: Molecule to query, String SMARTS",
         methodSummary = "Query a molecule for a SMARTS string and return a list " +
         		"of IAtomCOntainers with the matches.")
    @TestMethods("testSMARTSonFile")
    List<IAtomContainer> getSmartsMatches( ICDKMolecule molecule, String SMARTS )
    throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params = "IMolecule molecule: atom container to fragmentate",
         methodSummary = "Splits up an atom container into fully connected molecules")
    @TestMethods("testSMARTSonFile")
    public List<IAtomContainer> partition(IMolecule molecule) throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params = "IMolecule molecule: molecule to calculate the total formal" +
         		" charge for.",
         methodSummary = "Calculates the total formal charge.")
    @TestMethods("testSMARTSonFile")
    public int totalFormalCharge(IMolecule molecule)
        throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params = "IMolecule calculateFor, IMolecule reference",
         methodSummary = "Calculate tanimoto similarity of calculateFor and reference via CDK fingerprint.")
    @TestMethods("testSingleTanimoto")
    public float calculateTanimoto(IMolecule calculateFor, IMolecule reference)
        throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params = "BitSet fingerprint1, BitSet fingerprint2",
         methodSummary = "Calculate tanimoto similarity between two " +
             "fingerprints")
    @TestMethods("testCalculateTanimoto_BitSet_BitSet")
    public float calculateTanimoto(BitSet fingerprint1,
            BitSet fingerprint2) throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params = "BioList<IMolecule> calculateFor, BitSet reference",
         methodSummary = "Calculate tanimoto similarity of a molecule " +
            "(calculateFor) to a BitSet (reference) via CDK fingerprint.")
    @TestMethods("testCalculateTanimoto_IMolecule_BitSet")
    public float calculateTanimoto(IMolecule calculateFor,
            BitSet reference) throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params = "BioList<IMolecule> calculateFor, IMolecule reference",
         methodSummary = "Calculate tanimoto similarity of a list of molecules (calculateFor) to another molecule (reference) via CDK fingerprint.")
    @TestMethods("testMultipleTanimoto")
    public List<Float> calculateTanimoto(BioList<IMolecule> calculateFor, IMolecule reference)
        throws BioclipseException;

    @Recorded
    @PublishedMethod(
         params="IMolecule molecule",
         methodSummary="Returns a MDL V2000 molfile serialization")
    @TestMethods("testGetMDLMolfileString()")
    public String getMDLMolfileString(ICDKMolecule molecule);

    @Recorded
    @PublishedMethod(
         params="ICDKMolecule molecule, Object propertyName",
         methodSummary="Returns the property value for the given property name")
    @TestMethods("testGetSetProperty()")
    public Object getProperty(ICDKMolecule molecule, Object propertyName);

    @Recorded
    @PublishedMethod(
         params="ICDKMolecule molecule, Object propertyName, Object" +
         		" propertyValue",
         methodSummary="Sets thesproperty value for the given property name." +
         		" Returns the old value, or null if it was yet unset.")
    @TestMethods("testGetSetProperty()")
    public Object setProperty(ICDKMolecule molecule, Object propertyName,
            Object propertyValue);

}