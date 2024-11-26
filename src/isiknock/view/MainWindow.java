/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view;

import isiknock.view.matrix.knockout.bool.BooleanKnockoutMatrixPanel;
import isiknock.IsiKnock;
import isiknock.view.matrix.knockout.bool.BooleanMatrixIllustrationSettings;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.LineBorder;
import knockoutmatrix.PntCreator;
import isiknock.entities.matrix.IMatrix;
import isiknock.entities.matrix.knockout.BooleanKnockoutMatrix;
import isiknock.entities.matrix.knockout.IntegerKnockoutMatrix;
import isiknock.language.Language;
import isiknock.view.matrix.MatrixPanel;
import isiknock.view.matrix.MatrixViewer;
import isiknock.view.matrix.knockout.IKnockoutMatrixIllustrator;
import isiknock.view.matrix.knockout.bool.BooleanKnockoutMatrixIllustrator;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import knockoutmatrix.invariants.InvariantCalculator;
import petrinet.IPetrinet;
import petrinet.io.IPetrinetParser;
import petrinet.io.PntParser;
import petrinet.io.SBMLParser;
import tools.Files;
import tools.General;
import tools.Mathematics;
import tools.extendedswing.JEPanel;
import tools.OS;
import tools.extendedswing.EFileFilter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class MainWindow extends JFrame {

    private File fileInput;
    private File fileSaveInvariantsTo;
    private List<List<Integer>> adjacencyMatrix;
    private List<String> transitions;
    private List<String> inputTransitions;
    private List<String> places;
    private List<List<Integer>> multiplierMatrix;

    private Set<Integer> idsOfPlacesInvolvedInPInvariants;
    private Set<Integer> idsOfTransitionsInvolvedInTInvariants;

    private boolean[] availablePlaces, availableTransitions;

    private JButton jBtnLoadFile;
    private JButton jBtnCreateKnockOut;

    private JEPanel jPnlContent, jPnlFile, jPnlFileInfo, jPnlConfig;
    private JPanel jPaneTransitions;
    private JPanel jPanePlaces;
    private JScrollPane jScrPaneTransitions;
    private JScrollPane jScrPanePlaces;

    private JLabel jLblLogo, jLblConfigTitle, jLblConfigPlaceListTitle, jLblConfigTransitionListTitle;
    private JLabel jLblFileInfoFileNameTitle, jLblFileInfoTransitionCountTitle, jLblFileInfoPlaceCountTitle, jLblFileInfoFileName, jLblFileInfoTransitions, jLblFileInfoPlaces;

    private JRadioButton jRbtnKnockOutMatrixBasedOnTI;
    private JRadioButton jRbtnKnockOutMatrixBasedOnMI;
    private JRadioButton jRbtnKnockOutMatrixBasedOnMIFastSearch;

    private JCheckBox jChBxSelectAllTransitions;
    private JCheckBox jChBxSelectAllInputTransitions;
    private JCheckBox jChBxSelectAllPlaces;
    private JCheckBox jChBxIntegrateOutputTransitions;
    private JCheckBox jChBxSaveInvariants;
    private JCheckBox jChBxIncludePInvariants;

    List<JCheckBox> boxesT = new ArrayList<>();
    List<JCheckBox> boxesIT = new ArrayList<>();
    List<JCheckBox> boxesP = new ArrayList<>();

    public MainWindow() {
        super(IsiKnock.APP_NAME + " v" + IsiKnock.APP_VERSION);

        this.fileInput = new File("");
        this.fileSaveInvariantsTo = null;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        General.setWindowIcon(MainWindow.this, getClass().getResource("/resources/images/icon.png"));

        this.initComponents();
        this.createLayout();
        this.registerListener();

        this.clear();

        setPreferredSize(new Dimension(460, 740));
        setResizable(false);
        pack();
    }

    private void initComponents() {

        this.jPnlContent = new JEPanel();
        this.jPnlFile = new JEPanel();
        this.jPnlConfig = new JEPanel();
        this.jPnlFileInfo = new JEPanel();

        this.jLblLogo = new JLabel(new ImageIcon(getClass().getResource("/resources/images/logo.png")));

        this.jLblFileInfoFileNameTitle = new JLabel(Language.CURRENT.lblFileInfoFilename);
        this.jLblFileInfoTransitionCountTitle = new JLabel(Language.CURRENT.lblFileInfoTransitionCount);
        this.jLblFileInfoPlaceCountTitle = new JLabel(Language.CURRENT.lblFileInfoPlaceCount);
        this.jLblFileInfoFileName = new JLabel();
        this.jLblFileInfoTransitions = new JLabel();
        this.jLblFileInfoPlaces = new JLabel();

        this.jLblConfigTitle = new JLabel(Language.CURRENT.lblConfigTitle);
        this.jLblConfigPlaceListTitle = new JLabel(Language.CURRENT.lblConfigPlaceListTitle);
        this.jLblConfigTransitionListTitle = new JLabel(Language.CURRENT.lblConfigTransitionListTitle);

        this.jPaneTransitions = new JPanel();
        this.jPanePlaces = new JPanel();
        this.jScrPaneTransitions = new JScrollPane();
        this.jScrPanePlaces = new JScrollPane();
        this.jScrPanePlaces.getVerticalScrollBar().setUnitIncrement(15);
        this.jScrPaneTransitions.getVerticalScrollBar().setUnitIncrement(15);
        this.jRbtnKnockOutMatrixBasedOnTI = new JRadioButton(Language.CURRENT.rbtnKnockOutMatrixBasedOnTI);
        this.jRbtnKnockOutMatrixBasedOnMI = new JRadioButton(Language.CURRENT.rbtnKnockOutMatrixBasedOnMI);
        this.jRbtnKnockOutMatrixBasedOnMIFastSearch = new JRadioButton(Language.CURRENT.rbtnKnockOutMatrixBasedOnMIFastSearch);

        this.jChBxSelectAllTransitions = new JCheckBox(Language.CURRENT.chBxSelectAllTransitions);
        this.jChBxSelectAllInputTransitions = new JCheckBox(Language.CURRENT.chBxSelectAllInputTransitions);
        this.jChBxSelectAllPlaces = new JCheckBox(Language.CURRENT.chBxSelectAllPlaces);
        this.jChBxIntegrateOutputTransitions = new JCheckBox(Language.CURRENT.chBxIntegrateOutputTransitions);
        this.jChBxSaveInvariants = new JCheckBox(Language.CURRENT.chBxSaveInvariants);
        this.jChBxIncludePInvariants = new JCheckBox(Language.CURRENT.chBxIncludePInvariants);

        this.jBtnCreateKnockOut = new JButton(Language.CURRENT.btnCreateKnockOut);// new JButton("Create single knockout");

        this.jBtnLoadFile = new JButton(Language.CURRENT.btnLoadFile);
        this.jBtnLoadFile.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.jBtnLoadFile.setHorizontalTextPosition(SwingConstants.CENTER);
        this.jBtnLoadFile.setIcon(new ImageIcon(getClass().getResource("/resources/images/open.png")));
        this.jBtnLoadFile.setSize(new Dimension(70, 40));
        this.jBtnLoadFile.setFocusPainted(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(this.jRbtnKnockOutMatrixBasedOnTI);
        bg.add(this.jRbtnKnockOutMatrixBasedOnMI);
        bg.add(this.jRbtnKnockOutMatrixBasedOnMIFastSearch);

        this.jPaneTransitions.setLayout(new BoxLayout(jPaneTransitions, BoxLayout.Y_AXIS));//new GridBagLayout()); 
        this.jPanePlaces.setLayout(new BoxLayout(jPanePlaces, BoxLayout.Y_AXIS));//new GridBagLayout()); 
        this.jScrPaneTransitions.setViewportView(jPaneTransitions);
        this.jScrPanePlaces.setViewportView(jPanePlaces);

        //default settings
        this.jRbtnKnockOutMatrixBasedOnMIFastSearch.setSelected(true);
        this.jChBxIntegrateOutputTransitions.setSelected(true);

        //menu
        JMenuBar menu = new JMenuBar();
        JMenu jMenuHelp = MainWindow.getHelpSection(this);
        menu.add(jMenuHelp);

        this.setJMenuBar(menu);

    }

    private void clear() {

        this.jLblFileInfoFileName.setText("-");
        this.jLblFileInfoTransitions.setText("-");
        this.jLblFileInfoPlaces.setText("-");

        this.fileSaveInvariantsTo = null;

        this.jPanePlaces.removeAll();
        this.jPaneTransitions.removeAll();

        this.boxesP.clear();
        this.boxesT.clear();
        this.boxesIT.clear();

        this.adjacencyMatrix = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.inputTransitions = new ArrayList<>();
        this.places = new ArrayList<>();
        this.multiplierMatrix = new ArrayList<>();

        this.idsOfPlacesInvolvedInPInvariants = new HashSet<>();
        this.idsOfTransitionsInvolvedInTInvariants = new HashSet<>();

        this.jChBxSelectAllTransitions.setSelected(false);
        this.jChBxSelectAllInputTransitions.setSelected(false);
        this.jChBxSelectAllPlaces.setSelected(false);

        this.enableConfigPanel(false);

    }

    private void lock(boolean lock) {
        this.jBtnLoadFile.setEnabled(!lock);
        this.enableConfigPanel(!lock);
        if (!lock) {
            this.updateUIElementsStatus();
        }
    }

    private void enableConfigPanel(boolean enable) {

        this.jLblFileInfoFileNameTitle.setEnabled(enable);
        this.jLblFileInfoTransitionCountTitle.setEnabled(enable);
        this.jLblFileInfoPlaceCountTitle.setEnabled(enable);
        this.jLblFileInfoFileName.setEnabled(enable);
        this.jLblFileInfoTransitions.setEnabled(enable);
        this.jLblFileInfoPlaces.setEnabled(enable);

        this.jLblConfigTitle.setEnabled(enable);
        this.jLblConfigPlaceListTitle.setEnabled(enable);
        this.jLblConfigTransitionListTitle.setEnabled(enable);

        this.jChBxIntegrateOutputTransitions.setEnabled(enable);
        this.jChBxSelectAllPlaces.setEnabled(enable);
        this.jChBxSelectAllTransitions.setEnabled(enable);
        this.jChBxSelectAllInputTransitions.setEnabled(enable);

        this.jRbtnKnockOutMatrixBasedOnMI.setEnabled(enable);
        this.jRbtnKnockOutMatrixBasedOnMIFastSearch.setEnabled(enable);
        this.jRbtnKnockOutMatrixBasedOnTI.setEnabled(enable);

        this.jBtnCreateKnockOut.setEnabled(enable);

        this.jScrPanePlaces.setEnabled(enable);
        this.jScrPanePlaces.getVerticalScrollBar().setEnabled(enable);
        this.jScrPanePlaces.getHorizontalScrollBar().setEnabled(enable);
        this.jScrPaneTransitions.setEnabled(enable);
        this.jScrPaneTransitions.getVerticalScrollBar().setEnabled(enable);
        this.jScrPaneTransitions.getHorizontalScrollBar().setEnabled(enable);

        this.jPanePlaces.setEnabled(enable);
        this.jPaneTransitions.setEnabled(enable);

        this.jChBxSaveInvariants.setEnabled(enable);
        this.jChBxIncludePInvariants.setEnabled(enable);

        for (int i = 0; i < boxesP.size(); i++) {
            JCheckBox box = boxesP.get(i);
            box.setEnabled(availablePlaces[i] && enable);
        }
        for (int i = 0; i < boxesT.size(); i++) {
            JCheckBox box = boxesT.get(i);
            box.setEnabled(availableTransitions[i] && enable);
        }
    }

    private void createLayout() {

        final int defaultOffset = 10;
        final int defaultInnerOffset = 5;

        //File panel
        jPnlFile.addComponent(jBtnLoadFile, 0, 0, 1, 1, 1.0, 0.0, 5, 30, 0, 30);
        jPnlFile.addComponent(jPnlFileInfo, 0, 1, 1, 1, 1.0, 0.0, 0, 0, 0, 0);
        jPnlFile.addComponent(jLblLogo, 1, 0, 1, 2, 0.0, 0.0, 0, 0, 0, 0);
        //file panel - file info
        jPnlFileInfo.addComponent(this.jLblFileInfoFileNameTitle, 0, 0, 1, 1, 0.0, 0.0, 0, 0, 0, 0);
        jPnlFileInfo.addComponent(this.jLblFileInfoTransitionCountTitle, 0, 1, 1, 1, 0.0, 0.0, 0, 0, 0, 0);
        jPnlFileInfo.addComponent(this.jLblFileInfoPlaceCountTitle, 0, 2, 1, 1, 0.0, 0.0, 0, 0, 0, 0);
        jPnlFileInfo.addComponent(jLblFileInfoFileName, 1, 0, 1, 1, 1.0, 0.0, 0, 0, 0, defaultOffset);
        jPnlFileInfo.addComponent(jLblFileInfoTransitions, 1, 1, 1, 1, 1.0, 0.0, 0, 0, 0, defaultOffset);
        jPnlFileInfo.addComponent(jLblFileInfoPlaces, 1, 2, 1, 1, 1.0, 0.0, 0, 0, 0, defaultOffset);

        //Config panel
        int row = 0;
        jPnlConfig.addComponent(jLblConfigTitle, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jRbtnKnockOutMatrixBasedOnMIFastSearch, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jRbtnKnockOutMatrixBasedOnMI, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jRbtnKnockOutMatrixBasedOnTI, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jChBxIntegrateOutputTransitions, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jChBxIncludePInvariants, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 20);
        row++;
        jPnlConfig.addComponent(jChBxSaveInvariants, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jLblConfigTransitionListTitle, 0, row, 1, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        jPnlConfig.addComponent(jLblConfigPlaceListTitle, 1, row, 1, 1, 1.0, 0.0, 0, 0, 0, 0);
        row++;
        jPnlConfig.addComponent(jScrPaneTransitions, 0, row, 1, 1, 1.0, 1.0, 0, defaultInnerOffset, 0, 0);
        jPnlConfig.addComponent(jScrPanePlaces, 1, row, 1, 1, 1.0, 1.0, 0, 0, 0, 0);
        row++;
        jPnlConfig.addComponent(jChBxSelectAllTransitions, 0, row, 1, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        jPnlConfig.addComponent(jChBxSelectAllPlaces, 1, row, 1, 1, 1.0, 0.0, 0, 0, 0, 0);
        row++;
        jPnlConfig.addComponent(jChBxSelectAllInputTransitions, 0, row, 2, 1, 1.0, 0.0, 0, defaultInnerOffset, 0, 0);
        row++;
        jPnlConfig.addComponent(jBtnCreateKnockOut, 0, row, 2, 1, 1.0, 0.0, defaultOffset, 0, 0, 0);

        JEPanel content = new JEPanel();
        content.setBorder(new LineBorder(Color.GRAY, 1));
        content.addComponent(jPnlConfig, 0, 0, 1, 1, 1.0, 1.0, defaultOffset, defaultOffset, defaultOffset, defaultOffset);

        jPnlContent.addComponent(jPnlFile, 0, 0, 1, 1, 1.0, 0.0, defaultOffset, defaultOffset, defaultOffset, defaultOffset);
        jPnlContent.addComponent(content, 0, 1, 1, 1, 1.0, 1.0, 0, defaultOffset, defaultOffset, defaultOffset);

        this.add(jPnlContent);

    }

    private void registerListener() {

        jBtnLoadFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                File newFile = General.OpenFileDialog(fileInput.getAbsolutePath(), General.FileChooserTyp.OpenFile, EFileFilter.addAllSupportedFormatsFileFilter(new EFileFilter[]{EFileFilter.FILTER_PNT, EFileFilter.FILTER_SBML}), 0, true, false, MainWindow.this);
                if (newFile != null && (fileInput == null || !newFile.getAbsolutePath().equals(fileInput.getAbsolutePath()))) {
                    fileInput = newFile;
                    fileSaveInvariantsTo = null;
                    int fTyp = 0;
                    if (fileInput.getName().endsWith(".pnt")) {
                        fTyp = 1;
                    } else if (fileInput.getName().endsWith(".xml")) {
                        fTyp = 2;
                    }
                    if (fTyp > 0) {
                        MainWindow.this.clear();
                        String filepath = fileInput.getAbsolutePath();
                        IPetrinetParser parser;
                        try {
                            parser = fTyp == 1 ? new PntParser() : new SBMLParser();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MainWindow.this, Language.CURRENT.msgParseFormat, Language.CURRENT.msgParseFormatTitle, JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        //parse file
                        IPetrinet net;
                        try {
                            net = parser.parse(newFile);
                        } catch (IOException ex) {
                            //todo error message
                            MainWindow.this.clear();
                            return;
                        }

                        if (net == null) {
                            //net couldn't be parsed
                            JOptionPane.showMessageDialog(MainWindow.this, Language.CURRENT.msgParseFormat, Language.CURRENT.msgParseFormatTitle, JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        //extract needed values from petri net
                        List<String> t = new ArrayList<>(Arrays.asList(net.getTrasitions()));
                        List<String> p = new ArrayList<>(Arrays.asList(net.getPlaces()));

                        int[][] intMatrix = net.getAdjacencyMatrix();
                        List<List<Integer>> matrix = new ArrayList<>(intMatrix.length);
                        for (int[] row : intMatrix) {
                            List<Integer> listRow = new ArrayList<>(row.length);
                            for (int val : row) {
                                listRow.add(val);
                            }
                            matrix.add(listRow);
                        }

                        int[][] intMulti = net.getMultiplierMatrix();
                        List<List<Integer>> multiMatrix = new ArrayList<>(intMulti.length);
                        for (int[] row : intMulti) {
                            List<Integer> listRow = new ArrayList<>(row.length);
                            for (int val : row) {
                                listRow.add(val);
                            }
                            multiMatrix.add(listRow);
                        }

                        Set<Integer> cachePlaceInvariants = null;
                        Set<Integer> cacheTransitionInvariants = null;
                        try {
                            //get a list of all places involved in place invariants - exclude such places from the calculation of manatees
                            List<List<Integer>> pInvs = calculatePInvariantsForPntFile(createTempPntFile(matrix, p, t, multiMatrix));
                            if (pInvs != null) {
                                Set<Integer> cachePI = new HashSet<>();
                                for (List<Integer> pInv : pInvs) {
                                    for (Integer pID : pInv) {
                                        cachePI.add(pID);
                                    }
                                }
                                cachePlaceInvariants = cachePI;
                            }

                            //get a list of all transitions involved in transition invariants - exclude connected places from the calculation
                            List<List<Integer>> tInvs = calculateTInvariantsForPntFile(createTempPntFile(matrix, p, t, multiMatrix));
                            if (tInvs != null) {
                                Set<Integer> cacheTI = new HashSet<>();
                                for (List<Integer> tInv : tInvs) {
                                    for (Integer pID : tInv) {
                                        cacheTI.add(pID);
                                    }
                                }
                                cacheTransitionInvariants = cacheTI;
                            }
                        } catch (NoSuchFileException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //check if all needed data was extracted
                        if (t.isEmpty() || p.isEmpty() || multiMatrix.isEmpty() || multiMatrix.isEmpty()) {
                            JOptionPane.showMessageDialog(MainWindow.this, Language.CURRENT.msgFileContainsNoData, Language.CURRENT.msgFileContainsNoDataTitle, JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        if (cachePlaceInvariants == null || cacheTransitionInvariants == null) {
                            JOptionPane.showMessageDialog(MainWindow.this, Language.CURRENT.msgCalculationFailed, Language.CURRENT.msgCalculationFailedTitle, JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        adjacencyMatrix = matrix;
                        transitions = t;
                        places = p;
                        multiplierMatrix = multiMatrix;

                        idsOfPlacesInvolvedInPInvariants = cachePlaceInvariants;
                        idsOfTransitionsInvolvedInTInvariants = cacheTransitionInvariants;

                        //calculate input transitions
                        List<String> inTransitions = new ArrayList<>();
                        for (int i = 0; i < transitions.size(); i++) {
                            boolean isInput = false;
                            for (int j = 0; j < adjacencyMatrix.size(); j++) {
                                if (adjacencyMatrix.get(j).get(i) == -1) {
                                    isInput = true;
                                } else if (adjacencyMatrix.get(j).get(i) == 1) {
                                    isInput = false;
                                    break;
                                }
                            }
                            if (isInput == true) {
                                inTransitions.add(transitions.get(i));
                            }
                        }
                        inputTransitions = inTransitions;

                        //determine the available places and transitions 
                        //- only transitions that are part of a t-invariant are active
                        //- only places that have at least one output of an active transition are active
                        availablePlaces = new boolean[places.size()];
                        availableTransitions = new boolean[transitions.size()];
                        //active transitions
                        for (Integer tID : idsOfTransitionsInvolvedInTInvariants) {
                            availableTransitions[tID] = true;
                        }
                        //active places
                        int pID = 0;
                        for (List<Integer> list : adjacencyMatrix) {
                            for (Integer tID : idsOfTransitionsInvolvedInTInvariants) {
                                if (list.get(tID) == -1) {
                                    availablePlaces[pID] = true;
                                    break;
                                }
                            }
                            pID++;
                        }

                        createCheckBoxes();
                        jLblFileInfoFileName.setText(fileInput.getName());
                        jLblFileInfoTransitions.setText("" + transitions.size());
                        jLblFileInfoPlaces.setText("" + places.size());

                        selectAll(true, boxesT);
                        selectAll(true, boxesP);
                        jChBxSelectAllTransitions.setSelected(true);
                        jChBxSelectAllInputTransitions.setSelected(true);
                        jChBxSelectAllPlaces.setSelected(true);

                        enableConfigPanel(true);
                        updateUIElementsStatus();

                    } else {
                        JOptionPane.showMessageDialog(MainWindow.this, Language.CURRENT.msgWrongFileFormat, Language.CURRENT.msgWrongFileFormatTitle, JOptionPane.INFORMATION_MESSAGE);
                        MainWindow.this.clear();
                    }
                }
            }
        }
        );

        jBtnCreateKnockOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                MainWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                new Thread() {

                    @Override
                    public void run() {
                        showKnockoutMatrix(jChBxSaveInvariants.isSelected());
                        lock(false);
                        MainWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }

                }.start();
            }
        });

        jChBxSelectAllTransitions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                selectAll(jChBxSelectAllTransitions.isSelected(), boxesT);
                jChBxSelectAllInputTransitions.setSelected(jChBxSelectAllTransitions.isSelected());
            }
        });

        jChBxSelectAllPlaces.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                selectAll(jChBxSelectAllPlaces.isSelected(), boxesP);
            }
        });

        jChBxSelectAllInputTransitions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                selectAll(jChBxSelectAllInputTransitions.isSelected(), boxesIT);
                doAllCheck(boxesT, jChBxSelectAllTransitions);
            }
        });

        jRbtnKnockOutMatrixBasedOnMI.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateUIElementsStatus();
            }
        });

        jRbtnKnockOutMatrixBasedOnMIFastSearch.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateUIElementsStatus();
            }
        });

        jChBxIntegrateOutputTransitions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUIElementsStatus();
            }
        });

        jChBxIncludePInvariants.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUIElementsStatus();
            }
        });

    }

    private void selectAll(boolean selected, Iterable<JCheckBox> boxes) {
        for (JCheckBox jCheckBox : boxes) {
            jCheckBox.setSelected(selected);
        }
    }

    private void updateUIElementsStatus() {
        jChBxIncludePInvariants.setVisible(!this.idsOfPlacesInvolvedInPInvariants.isEmpty());
        jChBxIncludePInvariants.setEnabled(jChBxIntegrateOutputTransitions.isEnabled() && jChBxIntegrateOutputTransitions.isSelected());
        for (Integer placeID : idsOfPlacesInvolvedInPInvariants) {
            if (availablePlaces[placeID]) {
                boxesP.get(placeID).setEnabled((jChBxIncludePInvariants.isVisible() && jChBxIncludePInvariants.isEnabled() && jChBxIncludePInvariants.isSelected()) || !(jChBxIntegrateOutputTransitions.isEnabled() && jChBxIntegrateOutputTransitions.isSelected()));
            }
        }
    }

    private void createCheckBoxes() {
        //remove old content
        jPaneTransitions.removeAll();
        jPanePlaces.removeAll();
        boxesT.clear();
        boxesIT.clear();
        boxesP.clear();

        //sort transitions by name
        List<String> sortedTransitions = new ArrayList<>(transitions);
        Collections.sort(sortedTransitions);

        //sort places by name
        List<String> sortedPlaces = new ArrayList<>(places);
        Collections.sort(sortedPlaces);

        ActionListener alTransitions = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doAllCheck(boxesT, jChBxSelectAllTransitions);
                doAllCheck(boxesIT, jChBxSelectAllInputTransitions);
            }
        };
        ActionListener alPlaces = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doAllCheck(boxesP, jChBxSelectAllPlaces);
            }
        };

        //add transition checkboxes
        for (int i = 0; i < transitions.size(); i++) {
            JCheckBox box = new JCheckBox(transitions.get(i));
            box.setEnabled(true);
            box.addActionListener(alTransitions);
            boxesT.add(box);
            if (inputTransitions.contains(transitions.get(i))) {
                boxesIT.add(box);
            }
        }
        List<JCheckBox> sortedBoxes = new ArrayList<>(boxesT);
        Collections.sort(sortedBoxes, new Comparator<JCheckBox>() {
            @Override
            public int compare(JCheckBox t, JCheckBox t1) {
                return t.getText().compareTo(t1.getText());
            }
        });
        for (JCheckBox box : sortedBoxes) {
            jPaneTransitions.add(box);
        }

        //add place checkboxes
        for (int i = 0; i < places.size(); i++) {
            JCheckBox box = new JCheckBox(places.get(i));
            box.setEnabled(true);
            box.addActionListener(alPlaces);
            boxesP.add(box);
        }
        List<JCheckBox> sortedPlaceBoxes = new ArrayList<>(boxesP);
        Collections.sort(sortedPlaceBoxes, new Comparator<JCheckBox>() {
            @Override
            public int compare(JCheckBox t, JCheckBox t1) {
                return t.getText().compareTo(t1.getText());
            }
        });
        for (JCheckBox box : sortedPlaceBoxes) {
            jPanePlaces.add(box);
        }
    }

    private void doAllCheck(List<JCheckBox> boxes, JCheckBox... cbxAll) {
        boolean status = true;
        for (JCheckBox box : boxes) {
            if (!box.isSelected()) {
                status = false;
                break;
            }
        }
        for (JCheckBox jCheckBox : cbxAll) {
            jCheckBox.setSelected(status);
        }
    }

    private static File createTempPntFile(List<List<Integer>> matrix, List<String> places, List<String> transitions, List<List<Integer>> multiplierMatrix) throws NoSuchFileException {
        File tempFolder;
        try {
            tempFolder = tools.Files.createTempFolder("isiknock");
            tempFolder.deleteOnExit();

            File pntFile = tools.Files.createTempFile("petrinet", "pnt", tempFolder);
            PntCreator.createPntFile(pntFile, matrix, places, transitions, multiplierMatrix);
            pntFile.deleteOnExit();

            return pntFile;
        } catch (IOException ex) {
            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationInputFile);
        }
    }

    private List<List<Integer>> calculatePInvariants() throws NoSuchFileException {
        File pntFile = createTempPntFile(adjacencyMatrix, places, transitions, multiplierMatrix);
        List<List<Integer>> pInvariants = calculatePInvariantsForPntFile(pntFile);
        pntFile.delete();
        return pInvariants;
    }

    private List<List<Integer>> calculatePInvariantsForPntFile(File pntFile) throws NoSuchFileException {
        try {
            File resFile = InvariantCalculator.calcPInvariants(pntFile);
            List<List<String>> pinvs = InvariantCalculator.extractInvariants(resFile);
            List<List<Integer>> transform = transformInvariantStringNameToID(pinvs, 'P');
            return transform;
        } catch (IOException ex) {
            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationInputFile);
        }
    }
    
    private List<List<Integer>> calculateTInvariants() throws NoSuchFileException {
        File pntFile = createTempPntFile(adjacencyMatrix, places, transitions, multiplierMatrix);
        List<List<Integer>> tInvariants = calculatePInvariantsForPntFile(pntFile);
        pntFile.delete();
        return tInvariants;
    }

    private List<List<Integer>> calculateTInvariantsForPntFile(File pntFile) throws NoSuchFileException {
        try {
            File resFile = InvariantCalculator.calcTInvariants(pntFile);
            List<List<String>> tinvs = InvariantCalculator.extractInvariants(resFile);
            List<List<Integer>> transform = transformInvariantStringNameToID(tinvs, 'T');
            return transform;
        } catch (IOException ex) {
            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationInputFile);
        }
    }

    AtomicInteger done = new AtomicInteger(0);
    private IMatrix<Integer> buildMatrix(List<Integer> chosenP, List<Integer> chosenT, final INVARIANT_ALGORITHM algo, boolean isIntegratingOutputTransitions, boolean saveInvariants) throws IOException {

        //copy multimatrix
        List<List<Integer>> multiplierMatrixCopy = new ArrayList<>(multiplierMatrix.size());
        for (int i = 0; i < multiplierMatrix.size(); i++) {
            List<Integer> sublist = new ArrayList<>(multiplierMatrix.get(i));
            multiplierMatrixCopy.add(sublist);
        }

        final File tempBaseFolder;
        tempBaseFolder = tools.Files.createTempFolder("isiknock");
        tempBaseFolder.deleteOnExit();

        Integer[][] knockoutMatrix = null;

        List<String> outputInvariantLabels = new ArrayList<>();
        List<List<List<String>>> allInvariantsToSave = new ArrayList<>();
        if (isIntegratingOutputTransitions) {

            //add artificial output to multiplier matrix
            for (List<Integer> row : multiplierMatrixCopy) {
                row.add(1);
            }

            Map<Integer, List<List<Integer>>> resMap = new ConcurrentHashMap<>(chosenP.size());

            
            
            done.set(0);
            final int todo = chosenP.size();
            AtomicBoolean failed = new AtomicBoolean(false);
            System.out.println("Calculating...");
            printProgress(0, todo);
            chosenP.parallelStream().forEach((pID) -> {

                //checkpoint
                if (failed.get()) {
                    return;
                }

                //copy transitions
                List<String> transitionsCopy = new ArrayList<>(transitions);

                //copy matrix
                List<List<Integer>> adjacencyMatrixCopy = new ArrayList<>(adjacencyMatrix.size());
                for (int i = 0; i < adjacencyMatrix.size(); i++) {
                    List<Integer> sublist = new ArrayList<>(adjacencyMatrix.get(i));
                    adjacencyMatrixCopy.add(sublist);
                }

                //add artificial output transition
                String otP = "OR_S" + pID + "_" + places.get(pID);
                transitionsCopy.add(otP);
                final int outTransitionID = transitionsCopy.size() - 1;

                File tempFolder = null;
                try {
                    tempFolder = tools.Files.createTempFolder(tempBaseFolder.toPath(), "isiknocktemp" + pID);
                    tempFolder.deleteOnExit();
                } catch (IOException ex) {
                    //todo throw and catch error
                    if (IsiKnock.DEBUG) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //add artificial output to adjacency matrix
                for (int i = 0; i < adjacencyMatrixCopy.size(); i++) {
                    if (pID == i) {
                        adjacencyMatrixCopy.get(i).add(1);
                    } else {
                        adjacencyMatrixCopy.get(i).add(0);
                    }
                }

                try {
                    //checkpoint
                    if (failed.get()) {
                        return;
                    }

                    List<List<String>> inv = calcInvariants(adjacencyMatrixCopy, transitionsCopy, multiplierMatrixCopy, algo, tempFolder);
                    if (!IsiKnock.DEBUG) {
                        Files.deleteFolder(tempFolder);
                    }
                    if (inv.isEmpty()) {
                        if (IsiKnock.DEBUG) {
                            System.out.println("Invariant calculation returned no results for: " + otP);
                        }
                        failed.set(true);
                        return;
                    }

                    if (saveInvariants) {
                        List<List<String>> invariantsToSave = translateInvariantsTransitionIDsToTrivialName(inv, transitionsCopy);

                        outputInvariantLabels.add("ADDITIONAL INVARIANTS FOR OUTPUT-REACTION " + otP + ":");
                        allInvariantsToSave.add(invariantsToSave);
                    }

                    //remove output transition
                    String oIDLabel = "T" + outTransitionID;
                    for (List<String> partialInvariant : inv) {
                        partialInvariant.removeIf(i -> (i.equalsIgnoreCase(oIDLabel)));
                    }

                    List<List<Integer>> tinvariants = getTinvs(inv, transitions.size());

                    if (tinvariants == null || tinvariants.isEmpty()) {
                        failed.set(true);
                        return;
                    }

                    resMap.put(pID, tinvariants);

                    //this is synchronized - it slows the parallel execution if multiple threads try to print progress at the same moment
                    //todo: implement none blocking solution and display progress in gui
                    incrementAndPrintProgress(todo);

                } catch (IOException ex) {
                    failed.set(true);
                }
            });
            System.out.println("Done" + OS.getLineSeperator());

            //last check if we got all results or if something got wrong
            if (resMap.size() != todo) {
                failed.set(true);
            }

            if (!failed.get()) {
                Integer[][] matrix = new Integer[chosenT.size()][chosenP.size()];
                for (int i = 0; i < chosenP.size(); i++) {
                    Integer knockedOutPlaceID = chosenP.get(i);
                    List<List<Integer>> tinvariants = resMap.get(knockedOutPlaceID);

                    Integer[][] partialKnockoutMatrix = getKnockoutMatrix(adjacencyMatrix, tinvariants, transitions, places, chosenT, Arrays.asList(knockedOutPlaceID));

                    for (int j = 0; j < partialKnockoutMatrix.length; j++) {
                        matrix[j][i] = partialKnockoutMatrix[j][0];
                    }
                }
                knockoutMatrix = matrix;

                if (saveInvariants) {

                    if (!allInvariantsToSave.isEmpty()) {
                        List<List<String>> incommen = new ArrayList<>(allInvariantsToSave.get(0));

                        //get the set of incommen invariants
                        for (List<List<String>> invariants : allInvariantsToSave) {
                            incommen.retainAll(invariants);
                        }

                        //remove the incommen invariants from each invariant resultset - retain only invariants that contain the added output transition
                        for (List<List<String>> invariants : allInvariantsToSave) {
                            invariants.removeAll(incommen);
                        }

                        //add incommen invariants to save list
                        outputInvariantLabels.add(0, "SHARED INVARIANTS:");
                        allInvariantsToSave.add(0, incommen);
                    }

                }
            }

        } else {
            //copy transitions
            List<String> transitionsCopy = new ArrayList<>(transitions);

            //copy matrix
            List<List<Integer>> adjacencyMatrixCopy = new ArrayList<>(adjacencyMatrix.size());
            for (int i = 0; i < adjacencyMatrix.size(); i++) {
                List<Integer> sublist = new ArrayList<>(adjacencyMatrix.get(i));
                adjacencyMatrixCopy.add(sublist);
            }

            boolean failed = false;
            List<List<String>> inv = null;
            try {
                System.out.println("Calculating...");
                inv = calcInvariants(adjacencyMatrixCopy, transitionsCopy, multiplierMatrixCopy, algo, tempBaseFolder);
                System.out.println("Done" + OS.getLineSeperator());
            } catch (IOException ex) {
                failed = true;
            }
            if (inv == null || inv.isEmpty()) {
                failed = true;
                if (IsiKnock.DEBUG) {
                    System.out.println("Invariant calculation returned no results");
                }
            }

            if (!failed) {
                List<List<Integer>> tinvariants = getTinvs(inv, transitions.size());
                knockoutMatrix = getKnockoutMatrix(adjacencyMatrix, tinvariants, transitions, places, chosenT, chosenP);

                if (saveInvariants) {
                    List<List<String>> invariantsToSave = translateInvariantsTransitionIDsToTrivialName(inv, transitions);

                    outputInvariantLabels.add("INVARIANTS:");
                    allInvariantsToSave.add(invariantsToSave);
                }
            }
        }

        if (!IsiKnock.DEBUG) {
            Files.deleteFolder(tempBaseFolder);
        }

        if (knockoutMatrix == null) {
            return null;
        }

        if (saveInvariants) {
            EFileFilter filter = (algo == INVARIANT_ALGORITHM.INV) ? EFileFilter.FILTER_TINVARIANT : EFileFilter.FILTER_MANATEE;
            File fStart;
            if (fileSaveInvariantsTo != null) {
                fStart = new File(Files.extractFileNameWithPath(fileSaveInvariantsTo) + "." + filter.getExtension());;
            } else if (fileInput != null) {
                fStart = new File(Files.extractFileNameWithPath(fileInput) + "." + filter.getExtension());
            } else {
                fStart = new File("");
            }
            File saveTo = General.OpenFileDialog(fStart.getAbsolutePath(), General.FileChooserTyp.Save, new EFileFilter[]{filter}, 0, true, true, MainWindow.this);

            if (saveTo != null) {
                fileSaveInvariantsTo = saveTo;
                boolean failed = false;
                if (saveTo.exists()) {
                    if (!saveTo.canWrite()) { //check if we can access the file
                        failed = true;
                    }
                }
                if (!failed) {
                    InvariantCalculator.saveInvariants(saveTo, outputInvariantLabels, allInvariantsToSave);
                    if (!saveTo.exists()) {
                        failed = true;
                    }
                }
                if (failed) {
                    JOptionPane.showMessageDialog(this, Language.CURRENT.errorInvariantCalculationOutputFile + saveTo.getAbsolutePath(), "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        String[] rowLabel = new String[chosenT.size()];
        for (int i = 0; i < chosenT.size(); i++) {
            rowLabel[i] = transitions.get(chosenT.get(i));
        }
        String[] columnLabel = new String[chosenP.size()];
        for (int i = 0; i < chosenP.size(); i++) {
            columnLabel[i] = places.get(chosenP.get(i));
        }

        return new IntegerKnockoutMatrix(rowLabel, columnLabel, knockoutMatrix);
    }

    private synchronized void incrementAndPrintProgress(int jobsTotal) {
        printProgress(done.incrementAndGet(), jobsTotal);
    }
    
    private synchronized void printProgress(int jobsDone, int jobsTotal) {
        if (jobsTotal > 0) {
            System.out.println("Progress: " + Mathematics.round(((float) jobsDone / (float) jobsTotal) * 100f, 1) + "% (" + jobsDone + "/" + jobsTotal + ")");
        }
    }

    private enum INVARIANT_ALGORITHM {

        INV, MANATEE, MANATEE_FAST
    };

    private List<List<String>> calcInvariants(List<List<Integer>> adjacencyMatrixCopy, List<String> transitionsCopy, List<List<Integer>> multiplierMatrixCopy, INVARIANT_ALGORITHM algo, File tempFolder) throws NoSuchFileException, IOException {

        File resFile;

        try {

            File pntFile = tools.Files.createTempFile("petrinet", "pnt", tempFolder);
            PntCreator.createPntFile(pntFile, adjacencyMatrixCopy, places, transitionsCopy, multiplierMatrixCopy);
            pntFile.deleteOnExit();

            switch (algo) {
                default:
                case INV:
                    resFile = InvariantCalculator.calcTInvariants(pntFile);
                    break;
                case MANATEE:
                    resFile = InvariantCalculator.calcManateeInvariants(pntFile, false);
                    break;
                case MANATEE_FAST:
                    resFile = InvariantCalculator.calcManateeInvariants(pntFile, true);
                    break;
            }

            pntFile.delete();
            pntFile = null;
        } catch (IOException ex) {
            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationInputFile);
        }

        if (resFile == null) {
            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationFailedNoResult);
        }

        if (resFile.exists() && resFile.canRead()) {
            List<List<String>> inv = InvariantCalculator.extractInvariants(resFile);

            //clean up
            resFile.delete();
            resFile = null;

            return inv;
        } else {
            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationResultFile + resFile.getAbsolutePath());
        }
    }

//    private IMatrix<Integer> buildMatrix(List<String> chosenP, List<String> chosenT, INVARIANT_ALGORITHM algo, boolean isIntegratingOutputTransitions, boolean saveInvariants) throws IOException {
//
//        //copy transitions
//        List<String> transitionsCopy = new ArrayList<>(transitions);
//
//        //copy matrix
//        List<List<Integer>> adjacencyMatrixCopy = new ArrayList<>(adjacencyMatrix.size());
//        for (int i = 0; i < adjacencyMatrix.size(); i++) {
//            List<Integer> sublist = new ArrayList<>(adjacencyMatrix.get(i));
//            adjacencyMatrixCopy.add(sublist);
//        }
//
//        //copy multimatrix
//        List<List<Integer>> multiplierMatrixCopy = new ArrayList<>(multiplierMatrix.size());
//        for (int i = 0; i < multiplierMatrix.size(); i++) {
//            List<Integer> sublist = new ArrayList<>(multiplierMatrix.get(i));
//            multiplierMatrixCopy.add(sublist);
//        }
//
//        if (isIntegratingOutputTransitions) {
//            for (String pName : chosenP) {
//                transitionsCopy.add("OT_" + pName);
//
//                //add artificial output to adjacency matrix
//                final int pID = places.indexOf(pName);
//                for (int i = 0; i < adjacencyMatrixCopy.size(); i++) {
//                    if (pID == i) {
//                        adjacencyMatrixCopy.get(i).add(1);
//                    } else {
//                        adjacencyMatrixCopy.get(i).add(0);
//                    }
//                }
//
//                //add artificial output to multiplier matrix
//                for (List<Integer> row : multiplierMatrixCopy) {
//                    row.add(1);
//                }
//            }
//        }
//
//        File resFile;
//        File tempFolder;
//        try {
//            tempFolder = tools.Files.createTempFolder("isiknock");
//            tempFolder.deleteOnExit();
//            File pntFile = tools.Files.createTempFile("petrinet", "pnt", tempFolder);
//            PntCreator.createPntFile(pntFile, adjacencyMatrixCopy, places, transitionsCopy, multiplierMatrixCopy);
//            pntFile.deleteOnExit();
//
//            switch (algo) {
//                default:
//                case INV:
//                    resFile = InvariantCalculator.calcTInvariants(pntFile);
//                    break;
//                case MANATEE:
//                    resFile = InvariantCalculator.calcManateeInvariants(pntFile, false);
//                    break;
//                case MANATEE_FAST:
//                    resFile = InvariantCalculator.calcManateeInvariants(pntFile, true);
//                    break;
//            }
//
//            pntFile.delete();
//            pntFile = null;
//        } catch (IOException ex) {
//            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationInputFile);
//        }
//
//        List<List<Integer>> tinvariants;
//        if (resFile == null) {
//            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationFailedNoResult);
//        }
//
//        if (resFile.exists() && resFile.canRead()) {
//            List<List<String>> inv = InvariantCalculator.extractInvariants(resFile);
//            tinvariants = getTinvs(inv, transitions);
//
//            if (saveInvariants) {
//                EFileFilter filter = (algo == INVARIANT_ALGORITHM.INV) ? EFileFilter.FILTER_TINVARIANT : EFileFilter.FILTER_MANATEE;
//                File fStart;
//                if (fileSaveInvariantsTo != null) {
//                    fStart = new File(Files.extractFileNameWithPath(fileSaveInvariantsTo) + "." + filter.getExtension());;
//                } else if (fileInput != null) {
//                    fStart = new File(Files.extractFileNameWithPath(fileInput) + "." + filter.getExtension());
//                } else {
//                    fStart = new File("");
//                }
//                File saveTo = General.OpenFileDialog(fStart.getAbsolutePath(), General.FileChooserTyp.Save, new EFileFilter[]{filter}, 0, true, true, MainWindow.this);
//
//                if (saveTo != null) {
//                    fileSaveInvariantsTo = saveTo;
//                    boolean failed = false;
//                    if (saveTo.exists()) {
//                        if (!saveTo.canWrite()) { //check if we can access the file
//                            failed = true;
//                        }
//                    }
//                    if (!failed) {
//                        List<List<String>> invariantsToSave = translateInvariantsTransitionIDsToTrivialName(inv, transitionsCopy);
//                        InvariantCalculator.saveInvariants(saveTo, invariantsToSave);
//                        if (!saveTo.exists()) {
//                            failed = true;
//                        }
//                    }
//                    if (failed) {
//                        JOptionPane.showMessageDialog(this, Language.CURRENT.errorInvariantCalculationOutputFile + saveTo.getAbsolutePath(), "", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                }
//            }
//            //clean up
//            resFile.delete();
//            resFile = null;
//            Files.deleteFolder(tempFolder);
//            tempFolder = null;
//        } else {
//            throw new NoSuchFileException(Language.CURRENT.errorInvariantCalculationResultFile + resFile.getAbsolutePath());
//        }
//
//        if (tinvariants == null) {
//            return null;
//        }
//
//        Integer[][] knockoutMatrix = getKnockoutMatrix(adjacencyMatrix, tinvariants, transitions, places, chosenT, chosenP);
//        String[] rowLabel = chosenT.toArray(new String[chosenT.size()]);
//        String[] columnLabel = chosenP.toArray(new String[chosenP.size()]);
//
//        return new IntegerKnockoutMatrix(rowLabel, columnLabel, knockoutMatrix);
//    }
    private static List<List<Integer>> transformInvariantStringNameToID(List<List<String>> invs, char typ) {
        if (invs == null) {
            return null;
        }

        //step to turn all Strings to ints
        List<List<Integer>> transform = new ArrayList<>();
        for (List<String> s : invs) {
            List<Integer> intList = new ArrayList<>();
            for (String str : s) {
                int indexID = str.indexOf(typ);
                if (indexID > -1) {
                    intList.add(Integer.parseInt(str.substring(indexID + 1)));
                }
            }
            transform.add(intList);
        }
        return transform;
    }

    public static List<List<Integer>> getTinvs(List<List<String>> tinvs, int numberOfTransitions) {

        if (tinvs == null) {
            return null;
        }

        //step to turn all Strings to ints
        List<List<Integer>> transform = transformInvariantStringNameToID(tinvs, 'T');

        List<List<Integer>> tinvsAsInt = new ArrayList<>();
        //step to write 0s or 1s in the tinvs-matrix
        for (List<Integer> s : transform) {
            List<Integer> intList = new ArrayList<>();
            for (int i = 0; i < numberOfTransitions; i++) {
                if (s.contains(i)) {
                    intList.add(1);
                } else {
                    intList.add(0);
                }
            }
            tinvsAsInt.add(intList);
        }

        return tinvsAsInt;
    }

    public static List<List<String>> translateInvariantsTransitionIDsToTrivialName(List<List<String>> invariantsWithTransitionIDs, List<String> transitions) {
        List<List<String>> translated = new ArrayList<>(invariantsWithTransitionIDs.size());
        for (List<String> invariant : invariantsWithTransitionIDs) {
            List<String> translatedInvariant = new ArrayList<>(invariant.size());
            for (String transitionID : invariant) {
                int idIndex = transitionID.indexOf("T");
                if (idIndex > -1) {
                    int id = Integer.parseInt(transitionID.substring(idIndex + 1));
                    if (id < transitions.size()) {
                        if (idIndex > 0) {
                            //add multiplier tag
                            translatedInvariant.add(transitionID.substring(0, idIndex) + transitions.get(id));
                        } else {
                            translatedInvariant.add(transitions.get(id));
                        }
                    } else {
                        //no transition with the given id
                        return null;
                    }
                }
            }
            translated.add(translatedInvariant);
        }
        return translated;
    }

    private void showKnockoutMatrix(boolean saveInvariants) {

        INVARIANT_ALGORITHM algo;
        if (this.jRbtnKnockOutMatrixBasedOnMI.isSelected()) {
            algo = INVARIANT_ALGORITHM.MANATEE;
        } else if (this.jRbtnKnockOutMatrixBasedOnMIFastSearch.isSelected()) {
            algo = INVARIANT_ALGORITHM.MANATEE_FAST;
        } else {
            algo = INVARIANT_ALGORITHM.INV;
        }

        boolean isIntegratingOutputTransitions = this.jChBxIntegrateOutputTransitions.isSelected();

        List<Integer> choosenPlaces = new ArrayList<>();
        for (int i = 0; i < boxesP.size(); i++) {
            if (boxesP.get(i).isEnabled() && boxesP.get(i).isSelected()) {
                choosenPlaces.add(i);
            }
        }

        List<Integer> choosenTransitions = new ArrayList<>();
        for (int i = 0; i < boxesT.size(); i++) {
            if (boxesT.get(i).isEnabled() && boxesT.get(i).isSelected()) {
                choosenTransitions.add(i);
            }
        }

        this.lock(true); //don't lock the ui before we got all enabled + selected placdes and transitions

        if (choosenTransitions.isEmpty()) {
            JOptionPane.showMessageDialog(this, Language.CURRENT.msgNoTransitionsSelected, Language.CURRENT.msgNoTransitionsSelectedTitle, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (choosenPlaces.isEmpty()) {
            JOptionPane.showMessageDialog(this, Language.CURRENT.msgNoPlacesSelected, Language.CURRENT.msgNoPlacesSelectedTitle, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!(algo == INVARIANT_ALGORITHM.INV) && choosenPlaces.size() >= 30) {
            int showConfirmDialog = JOptionPane.showConfirmDialog(this, Language.CURRENT.msgCalculationMayTakeAWhile, Language.CURRENT.msgCalculationMayTakeAWhileTitle, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (showConfirmDialog != JOptionPane.YES_OPTION) {
                return;
            }
        }

        IMatrix<Integer> matrix;
        try {
            matrix = buildMatrix(choosenPlaces, choosenTransitions, algo, isIntegratingOutputTransitions, saveInvariants);

            if (matrix == null) {
                JOptionPane.showMessageDialog(this, Language.CURRENT.msgCalculationFailed, Language.CURRENT.msgCalculationFailedTitle, JOptionPane.ERROR_MESSAGE);
                return;
            }

            IMatrix<Boolean> boolMatrix = matrix.translate(new BooleanKnockoutMatrix(new Boolean[matrix.getRowCount()][matrix.getColumnCount()]), (Integer toTransform) -> toTransform > 0);

            IKnockoutMatrixIllustrator illustrator = new BooleanKnockoutMatrixIllustrator(boolMatrix, new BooleanMatrixIllustrationSettings());
            MatrixPanel panel = new BooleanKnockoutMatrixPanel(illustrator);
            String info = Files.extractFileName(fileInput) + " (";
            switch (algo) {
                default:
                case INV:
                    info += Language.CURRENT.titleInfoKnockOutMatrixBasedOnTI;
                    break;
                case MANATEE:
                    info += Language.CURRENT.titleInfoKnockOutMatrixBasedOnMI;
                    break;
                case MANATEE_FAST:
                    info += Language.CURRENT.titleInfoKnockOutMatrixBasedOnMIFastSearch;
                    break;
            }
            info += (isIntegratingOutputTransitions ? ", " + Language.CURRENT.titleInfoKnockOutMatrixInputTransitions : "") + ")";
            MatrixViewer viewer = new MatrixViewer(panel, Language.CURRENT.windowTitleKnockoutViewer + (info.isEmpty() ? "" : " - " + info));
            viewer.pack();

            //refit for scrollbars
            Dimension screenSize = OS.getWindowsGraphicsDeviceResoluion(this);
            Dimension size = viewer.getSize();
            int scrollBarHeight = 15;
            int desiredWidth = size.width;
            int desiredHeight = size.height;
            if (screenSize.width < size.width) {
                desiredHeight += scrollBarHeight;
            }
            if (screenSize.height < size.height) {
                desiredWidth += scrollBarHeight;
            }
            desiredWidth = Math.max(400, desiredWidth); //min width is needed because for maps with few columns the window decoration is crunched on some operation systems requiring the user to resize the window before he can drag the window  
            viewer.setSize(desiredWidth, desiredHeight);
            if (desiredWidth > (screenSize.width - 50) && desiredHeight > (screenSize.height - 50)) {
                //if the window is larger than the screen resolution - 50 for tolerance (horizontal or vertical taskbar) go full screen
                viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            viewer.setLocationRelativeTo(null);
            viewer.setVisible(true);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, Language.CURRENT.msgCalculationFailedError + ex.getMessage(), Language.CURRENT.msgCalculationFailedErrorTitle, JOptionPane.ERROR_MESSAGE);
        }

    }

    public static final JMenu getHelpSection(Frame frame) {

        JMenu help = new JMenu(Language.CURRENT.menuHelp);
        ImageIcon helpImg = new ImageIcon(MainWindow.class.getResource("/resources/images/help.png"));
        help.setIcon(helpImg);

        JMenuItem jMitmDoc = new JMenuItem(Language.CURRENT.menuHelpDocumentation);
        JMenuItem jMitmReadme = new JMenuItem(Language.CURRENT.menuHelpAbout);

        jMitmDoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                OS.openURLinBrowser(IsiKnock.HELP_URL);
            }
        });
        jMitmReadme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DialogAbout about = new DialogAbout(frame);
                about.setVisible(true);
            }
        });

        help.add(jMitmDoc);
        help.add(jMitmReadme);

        return help;
    }

    public static synchronized Integer[][] getKnockoutMatrix(List<List<Integer>> adjacencyMatrix, List<List<Integer>> tinvariants, List<String> transitions, List<String> places, List<Integer> chosenT, List<Integer> chosenP) {

        Integer[][] affected = new Integer[chosenT.size()][chosenP.size()]; //a new matrix to save 0/1 values to know if the place is knocked out or not  

        for (int i = 0; i < chosenT.size(); i++) {
            for (int j = 0; j < chosenP.size(); j++) {
                affected[i][j] = 0;
            }
        }

        List<Integer> tPositions = new ArrayList<>(chosenT.size());
        for (int i = 0; i < transitions.size(); i++) { //they are not given as positions but as names (Strings)
            for (Integer tID : chosenT) {
                tPositions.add(tID);
            }
        }

        List<Integer> pPositions = new ArrayList<>(chosenP.size());
        for (int i = 0; i < places.size(); i++) { //they are not given as positions but as names (Strings)
            for (Integer pID : chosenP) {
                pPositions.add(pID);
            }
        }

        //tests for every transition what would happen if it is knocked out        
        for (int i = 0; i < chosenT.size(); i++) { //the positions of the chosen transitions
            Set<Integer> aliveTransitions = new HashSet<>();
            for (int j = 0; j < tinvariants.size(); j++) { //iterates the t-invariants
                if (tinvariants.get(j).get(tPositions.get(i)) == 0) { //tinvs doesn't contain this transition
                    //->if one tinv doesn't contain it, then all transisiotns 
                    //of this tinv are NOT affected
                    for (int k = 0; k < tinvariants.get(j).size(); k++) {
                        if (tinvariants.get(j).get(k) > 0) {
                            aliveTransitions.add(k);
                        }
                    }
                }
            }

            for (int j = 0; j < chosenP.size(); j++) {
                if (aliveTransitions.isEmpty()) { //no transitions are alive 
                    affected[i][j] = 1; //affected
                } else {
                    for (int alive : aliveTransitions) {
                        if (adjacencyMatrix.get(pPositions.get(j)).get(alive) == -1) { //there is an input from an alive transition
                            affected[i][j] = 0; //unaffected
                            break;
                        } else {
                            affected[i][j] = 1; //affected
                        }
                    }
                }
            }
        }

        return affected;
    }

    public static void main(String[] args) {
        IsiKnock.main(args);
    }
}
