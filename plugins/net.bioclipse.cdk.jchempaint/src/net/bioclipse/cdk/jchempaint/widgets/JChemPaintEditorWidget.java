/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>.
 *
 * Contributors:
 *      Arvid Berg
 *
 *
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.widgets;

import static net.bioclipse.cdk.jchempaint.outline.StructureContentProvider.createCDKChemObject;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.bioclipse.cdk.domain.CDKChemObject;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.jchempaint.Activator;
import net.bioclipse.cdk.jchempaint.business.IJChemPaintGlobalPropertiesManager;
import net.bioclipse.cdk.jchempaint.editor.SWTMouseEventRelay;
import net.bioclipse.cdk.jchempaint.view.JChemPaintWidget;
import net.bioclipse.cdk.jchempaint.view.SWTRenderer;
import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.ControllerModel;
import org.openscience.cdk.controller.IChemModelEventRelayHandler;
import org.openscience.cdk.controller.IControllerModule;
import org.openscience.cdk.controller.IViewEventRelay;
import org.openscience.cdk.controller.MoveModule;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.ExternalHighlightGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.SelectionGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


public class JChemPaintEditorWidget extends JChemPaintWidget 
                                    implements ISelectionProvider {
    
    private final static StructureDiagramGenerator sdg = new
                                                    StructureDiagramGenerator();
    private Collection<ISelectionChangedListener> listeners =
                                    new ArrayList<ISelectionChangedListener>();

    private ISelection currentSelection = StructuredSelection.EMPTY;
    
    private IAtom prevHighlightedAtom;
    
    private IBond prevHighlightedBond;

    private ICDKMolecule cdkMolecule;

    private ControllerHub hub;
    
    private ControllerModel c2dm;
    
    private SWTMouseEventRelay relay;
    
    private boolean generated = false;

    private boolean isdirty = false;
    
    private final Point origin = new Point(0, 0); 
    
    private boolean isScrolling = false;


    public JChemPaintEditorWidget(Composite parent, int style) {
        super( parent, style | 
                SWT.H_SCROLL | 
                SWT.V_SCROLL | 
//                SWT.NO_BACKGROUND |
                SWT.DOUBLE_BUFFERED);
        
        final ScrollBar hBar = getHorizontalBar(); 
        hBar.setEnabled(true);
        hBar.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ScrollBar bar = (ScrollBar) event.widget;
                int d = bar.getSelection();
                int dx = -d - origin.x;
                Rectangle rect = getDiagramBounds();
                setIsScrolling(true);
                scroll(dx, 0, 0, 0, rect.width, rect.height, false);
                setIsScrolling(false);
                origin.x = -d;
                update();
            }
        });

        final ScrollBar vBar = getVerticalBar();
        vBar.setEnabled(true);
        vBar.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ScrollBar bar = (ScrollBar) event.widget;
                int d = bar.getSelection();
                int dy = -d - origin.y;
                Rectangle rect = getDiagramBounds();
                setIsScrolling(true);
                scroll(0, dy, 0, 0, rect.width, rect.height, false);
                setIsScrolling(false);
                origin.y = -d;
                update();
            }
        });
        
        addListener (SWT.Resize,  new Listener () {
            public void handleEvent (Event e) {
                Rectangle rect = getDiagramBounds();
                Rectangle client = getClientArea();
                hBar.setMaximum(rect.width);
                vBar.setMaximum(rect.height);
                hBar.setThumb(Math.min(rect.width, client.width));
                vBar.setThumb(Math.min(rect.height, client.height));
                
                int hPage = rect.width - client.width;
                int vPage = rect.height - client.height;
                int hSelection = hBar.getSelection();
                int vSelection = vBar.getSelection();
                if (hSelection >= hPage) {
                    if (hPage <= 0) hSelection = 0;
                    origin.x = -hSelection;
                }
                if (vSelection >= vPage) {
                    if (vPage <= 0) vSelection = 0;
                    origin.y = -vSelection;
                }
                redraw();
            }
        });

        
        RendererModel model = getRenderer().getRenderer2DModel(); 

        java.awt.Color color = 
            createFromSWT(
                    getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION )); 
        model.setSelectedPartColor(color);
        
        prevHighlightedAtom=null;
        prevHighlightedBond=null;
    }
    
    public void setIsScrolling(boolean isScrolling) {
        this.isScrolling = isScrolling;
    }
    
    public void setupPaintListener() {
        addPaintListener( new PaintListener() {
            
            public void paintControl( PaintEvent event ) {
                
                JChemPaintEditorWidget.this.paintControl( event );
            }
        } );

    }
    
    private void paintControl( PaintEvent event ) {
        drawBackground( event.gc, 0, 0, getSize().x, getSize().y );
        IChemModel chemModel = hub.getIChemModel();
        
        int atomCount = ChemModelManipulator.getAtomCount(chemModel);
        if ( chemModel == null || atomCount == 0) {
            setBackground( getParent().getBackground() );
            return;
        } else setBackground( getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
        
        Rectangle c = getClientArea();
        Rectangle2D clientArea =
            new Rectangle2D.Double(c.x, c.y, c.width, c.height); 
        SWTRenderer visitor = new SWTRenderer( event.gc );
        
        Renderer renderer = getRenderer();
        
        if (isScrolling) {
            renderer.repaint(visitor);
//            isScrolling = false;
            return;
        }
        
        if (isNew) {
            renderer.setScale(chemModel);
        }

        if (renderer.getRenderer2DModel().isFitToScreen()) {
            renderer.paintChemModel(chemModel, visitor, clientArea, isNew);
        } else {
            java.awt.Rectangle diagramBounds = 
                renderer.paintChemModel(chemModel, visitor);
        }
        
        isNew = false;
    }
    
    public void reset() {
        this.isNew = true;
    }
    
    private Rectangle getDiagramBounds() {
        java.awt.Rectangle r = 
            getRenderer().calculateDiagramBounds(hub.getIChemModel());
        return new Rectangle(r.x, r.y, r.width, r.height); 
    }

    private void setupControllerHub( IAtomContainer atomContainer ) {

        IChemModel chemModel = ChemModelManipulator.newChemModel(atomContainer);
        c2dm = new ControllerModel();
        hub = new ControllerHub(
                c2dm, getRenderer(), chemModel, 
                new IViewEventRelay() {
                    public void updateView() {
                        updateSelection();
                        JChemPaintEditorWidget.this.redraw();
                    }
                }
        );

        hub.setEventHandler(
                new IChemModelEventRelayHandler() {

                    public void coordinatesChanged() {
                        setDirty(true);
                    }

                    public void selectionChanged() {
                        setSelection(getSelection());
                    }

                    public void structureChanged() {
                        setDirty(true);
                    }

                    public void structurePropertiesChanged() {
                        setDirty(true);
                    }

                }
        );

        if (relay != null) {

            removeMouseListener(relay);
            removeMouseMoveListener(relay);
            removeListener(SWT.MouseEnter, (Listener) relay);
            removeListener(SWT.MouseExit, (Listener) relay);
        }
        
        relay = new SWTMouseEventRelay(hub);
        hub.setActiveDrawModule(new MoveModule(hub));

        // apply the global JCP properties
        IJChemPaintGlobalPropertiesManager jcpprop = 
            Activator.getDefault().getJCPPropManager();
        try {
            jcpprop.applyProperties(hub.getRenderer().getRenderer2DModel());
        } catch (BioclipseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        addMouseListener(relay);
        addMouseMoveListener(relay);
        addListener(SWT.MouseEnter, relay);
        addListener(SWT.MouseExit, relay);

    }

    @Override
    protected List<IGenerator> createGenerators() {
        List<IGenerator> generatorList = new ArrayList<IGenerator>();
        generatorList.add(new ExternalHighlightGenerator());
        generatorList.addAll( super.createGenerators() );
        generatorList.add(new SelectionGenerator());

        return generatorList;
    }

    @Override
    public void setAtomContainer( IAtomContainer atomContainer ) {

        if( atomContainer != null) {
            generated = false;
            if(atomContainer.getAtomCount() > 0 &&
               !GeometryTools.has2DCoordinates( atomContainer )) {
                atomContainer = generate2Dfrom3D( atomContainer );
                generated = true;
                setDirty( true );
            }
        } else {
        	atomContainer = NoNotificationChemObjectBuilder.getInstance()
        	    .newAtomContainer();
        }
        setupControllerHub( atomContainer );
        super.setAtomContainer( atomContainer );
        setDirty( false );
    }

    public void setInput( Object element ) {

        if(element instanceof IAdaptable) {
            ICDKMolecule molecule = 
                (ICDKMolecule)
                ((IAdaptable)element).getAdapter( ICDKMolecule.class );
            
            if (molecule != null) {
                cdkMolecule = molecule;
                setAtomContainer(molecule.getAtomContainer());
            }
            else setAtomContainer( null );
        }
    }
    
    /*
     * Utility method for copying 3D x,y to 2D coordinates
     */
    public static IAtomContainer generate2Dfrom3D( IAtomContainer atomContainer ) {

        IAtomContainer container = null;
        try {
            sdg.setMolecule( (IMolecule) atomContainer.clone() );
            sdg.generateCoordinates();
            container = sdg.getMolecule();
        } catch ( CloneNotSupportedException e ) {
        	System.out.println("Could not create 3D coordinates: " + e.getMessage());
            return atomContainer;
        } catch (Exception e) {
        	System.out.println("Could not create 3D coordinates: " + e.getMessage());
            return atomContainer;
		}
        return container;

    }

    public ControllerHub getControllerHub() {
        return hub;
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {

        listeners.add( listener );

    }

    public ISelection getSelection() {
        RendererModel rendererModel = getRenderer2DModel();
        if (rendererModel == null && cdkMolecule != null)
            return new StructuredSelection(cdkMolecule);
        
        List<CDKChemObject> selection = new LinkedList<CDKChemObject>();

        IAtom highlightedAtom = rendererModel.getHighlightedAtom();
        IBond highlightedBond = rendererModel.getHighlightedBond();
        
        if (highlightedBond != null)
            selection.add(createCDKChemObject(highlightedBond));
        
        if (highlightedAtom != null)
            selection.add(createCDKChemObject(highlightedAtom));

        IChemObjectSelection sel = rendererModel.getSelection();
        IAtomContainer modelSelection = sel.getConnectedAtomContainer();
        
        if (modelSelection != null) {
            for (IAtom atom : modelSelection.atoms()) {
                selection.add(createCDKChemObject(atom));
            }
            
            for (IBond bond : modelSelection.bonds()) {
                selection.add(createCDKChemObject(bond));
            }
        }
        
        if (selection.size() == 0 && cdkMolecule != null) {
            return new StructuredSelection(cdkMolecule);
        }
        
        return new StructuredSelection(selection);
    }

    public void removeSelectionChangedListener(
                                          ISelectionChangedListener listener ) {

        listeners.remove( listener );

    }

    public void setSelection( ISelection selection ) {
        currentSelection = selection;
        final SelectionChangedEvent e = 
            new SelectionChangedEvent(this, selection);
        Object[] listenersArray = listeners.toArray();

        for (int i = 0; i < listenersArray.length; i++) {
            final ISelectionChangedListener l = (ISelectionChangedListener)
                                                              listenersArray[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(e);
                }
            });
        }

    }

    private void updateSelection() {
        RendererModel rendererModel = getRenderer2DModel();
        IAtom atom = rendererModel.getHighlightedAtom();
        IBond bond = rendererModel.getHighlightedBond();
        
        if (atom != prevHighlightedAtom || bond != prevHighlightedBond) {
            prevHighlightedAtom = atom;
            prevHighlightedBond = bond;
            if (prevHighlightedAtom != null) {
                setToolTipText(
                        rendererModel.getToolTipText(prevHighlightedAtom));
            } else if (prevHighlightedBond != null) {
             // put getToolTipText(prevHighlightedBond) here
                setToolTipText( null ); 
            } else {
                setToolTipText( "" );
            }
            setSelection( getSelection() );
        }

    }

	public void setActiveDrawModule(IControllerModule activeDrawModule){
		hub.setActiveDrawModule(activeDrawModule);
	}

	public void setDirty( boolean dirty) {
	    this.isdirty = dirty;
	}

	public boolean getDirty() {
	    return isdirty;
	}

	java.awt.Color createFromSWT(org.eclipse.swt.graphics.Color color) {
	    return new java.awt.Color( color.getRed(),
	                               color.getGreen(),
	                               color.getBlue());
	}
}