/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix;

import isiknock.view.MainWindow;
import isiknock.view.matrix.knockout.bool.BooleanKnockoutMatrixPanel;
import isiknock.view.matrix.knockout.bool.BooleanMatrixIllustrationSettings;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import isiknock.entities.matrix.knockout.BooleanKnockoutMatrix;
import isiknock.language.Language;
import isiknock.view.matrix.knockout.IKnockoutMatrixIllustrator;
import isiknock.view.matrix.knockout.bool.BooleanKnockoutMatrixIllustrator;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JSeparator;
import tools.Files;
import tools.extendedswing.EFileFilter;
import tools.General;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of value stored in the displayed matrix.
 */
public final class MatrixViewer<E> extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private MatrixPanel matrixPanel;

    private JScrollPane jScrollPane;

    private JMenu jMenuView;

    private JMenuItem jMitmPNG;
    private JMenuItem jMitmSVG;
    private JMenuItem jMitmCSV;
    private JMenuItem jMitmProperties;

    public MatrixViewer(MatrixPanel matrixPanel) {
        this(matrixPanel, "");
    }

    public MatrixViewer(MatrixPanel matrixPanel, String string) throws HeadlessException {
        super(string);
        General.setWindowIcon(MatrixViewer.this, getClass().getResource("/resources/images/icon.png"));

        this.initComponents();
        this.createLayout();
        this.registerListener();

        this.setMatrix(matrixPanel);
    }

    private void initComponents() {
        this.jScrollPane = new JScrollPane() {
            @Override
            protected void processMouseWheelEvent(MouseWheelEvent e) {

                if (matrixPanel != null && e.isControlDown()) {
                    matrixPanel.mouseWheelMoved(e);
                } else {
                    super.processMouseWheelEvent(e);
                }
            }

        };

        //export meun
        JMenu jMenuExport = new JMenu(Language.CURRENT.matrixViewerMenuExport);
        this.jMitmPNG = new JMenuItem(Language.CURRENT.matrixViewerMenuExportPNG);
        this.jMitmSVG = new JMenuItem(Language.CURRENT.matrixViewerMenuExportSVG);
        this.jMitmCSV = new JMenuItem(Language.CURRENT.matrixViewerMenuExportCSV);
        jMenuExport.add(jMitmCSV);
        jMenuExport.add(jMitmPNG);
        jMenuExport.add(jMitmSVG);

        //view menu
        this.jMenuView = new JMenu(Language.CURRENT.matrixViewerMenuView);
        this.jMitmProperties = new JMenuItem(Language.CURRENT.matrixViewerMenuViewProperties);
        jMenuView.add(jMitmProperties);

        //help menu
        JMenu jMenuHelp = MainWindow.getHelpSection(this);

        //build menu bar
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(jMenuExport);
        jMenuBar.add(jMenuView);
        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);
    }

    private void createLayout() {
        this.add(this.jScrollPane);
    }

    private File last = new File("");

    private void registerListener() {
        //actions
        this.jMitmPNG.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (last == null) {
                    last = new File("");
                }
                File file = General.OpenFileDialog(Files.extractFileNameWithPath(last), General.FileChooserTyp.Save, new EFileFilter[]{EFileFilter.FILTER_PNG}, 0, true, true, MatrixViewer.this);
                if (file != null && (!file.exists() || file.canWrite())) {
                    last = file;
                    try {
                        matrixPanel.saveAsPNG(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(MatrixViewer.this, Language.CURRENT.matrixViewerMsgExportFailed + file.getAbsolutePath(), Language.CURRENT.matrixViewerMsgExportFailedTitle, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        this.jMitmSVG.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (last == null) {
                    last = new File("");
                }
                File file = General.OpenFileDialog(Files.extractFileNameWithPath(last), General.FileChooserTyp.Save, new EFileFilter[]{EFileFilter.FILTER_SVG}, 0, true, true, MatrixViewer.this);
                if (file != null && (!file.exists() || file.canWrite())) {
                    last = file;
                    try {
                        matrixPanel.saveAsSVG(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(MatrixViewer.this, Language.CURRENT.matrixViewerMsgExportFailed + file.getAbsolutePath(), Language.CURRENT.matrixViewerMsgExportFailedTitle, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        this.jMitmCSV.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (last == null) {
                    last = new File("");
                }
                File file = General.OpenFileDialog(Files.extractFileNameWithPath(last), General.FileChooserTyp.Save, new EFileFilter[]{EFileFilter.FILTER_CSV}, 0, true, true, MatrixViewer.this);
                if (file != null && (!file.exists() || file.canWrite())) {
                    last = file;
                    try {
                        matrixPanel.saveAsCSV(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(MatrixViewer.this, Language.CURRENT.matrixViewerMsgExportFailed + file.getAbsolutePath(), Language.CURRENT.matrixViewerMsgExportFailedTitle, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        this.jMitmProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                matrixPanel.openSettingsDialog(MatrixViewer.this);
                settingsChanged();
            }
        });
    }

    private void setMatrix(MatrixPanel matrixPanel) {
        if (this.matrixPanel == null || this.matrixPanel != matrixPanel) {
            this.matrixPanel = matrixPanel;
            this.matrixPanel.addMouseListener(MatrixViewer.this);
            this.matrixPanel.addMouseMotionListener(MatrixViewer.this);
//            this.matrixPanel.addMouseWheelListener(MatrixViewer.this); //adding a MouseWheelListener prevents the scroll pane from receiving scroll events and thus scrolling with mouse wheel is not working.
            this.addKeyListener(this);

            this.jScrollPane.setViewportView(this.matrixPanel);
            Component[] viewMenuExtensions = this.matrixPanel.getViewMenuExtensions();
            jMenuView.add(new JSeparator());
            if (viewMenuExtensions != null) {
                for (Component viewMenuExtension : viewMenuExtensions) {
                    jMenuView.add(viewMenuExtension);
                }
            }
        }

        this.matrixChanged();
        this.settingsChanged();
    }

    protected void matrixChanged() {
    }

    protected void settingsChanged() {
        MatrixIllustrationSettings settings = this.matrixPanel.getSettings();
        //adjust scroll speed
        this.jScrollPane.getVerticalScrollBar().setUnitIncrement((settings.getCellHeight() + settings.getCellOffsetY()) / 2);
        this.jScrollPane.getHorizontalScrollBar().setUnitIncrement((settings.getCellWidth() + settings.getCellOffsetX()) / 2);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        matrixPanel.mouseClicked(me);
    }

    @Override
    public void mousePressed(MouseEvent me) {
        matrixPanel.mousePressed(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        matrixPanel.mouseReleased(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        matrixPanel.mouseEntered(me);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        matrixPanel.mouseExited(me);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        matrixPanel.mouseDragged(me);
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        matrixPanel.mouseMoved(me);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        matrixPanel.mouseWheelMoved(mwe);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        matrixPanel.keyTyped(ke);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        matrixPanel.keyPressed(ke);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        matrixPanel.keyReleased(ke);
    }

    public static void main(String[] args) {

        int columnCount = 1500;
        int rowCount = 1000;
        Boolean[][] data = new Boolean[rowCount][columnCount];

        String[] rowLabel = new String[rowCount];
        String[] columnLabel = new String[columnCount];

        for (int i = 0; i < rowCount; i++) {
            rowLabel[i] = "t" + (i + 1);
        }
        for (int i = 0; i < columnCount; i++) {
            columnLabel[i] = "p" + (i + 1);
        }

        //generate random matrix
        Random random = new Random(1500); //use seed to ensure that we always get the same matrix in each run
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                data[row][col] = random.nextBoolean();
            }
        }

        BooleanKnockoutMatrix matrix = new BooleanKnockoutMatrix(rowLabel, columnLabel, data);
        IKnockoutMatrixIllustrator illustrator = new BooleanKnockoutMatrixIllustrator(matrix, new BooleanMatrixIllustrationSettings());
        MatrixViewer window = new MatrixViewer(new BooleanKnockoutMatrixPanel(illustrator), "viewer");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();

        window.setVisible(true);
    }

}
