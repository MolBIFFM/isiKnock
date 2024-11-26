/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.knockout.bool;

import isiknock.language.Language;
import isiknock.view.matrix.MatrixPanel;
import isiknock.view.matrix.knockout.IKnockoutMatrixIllustrator;
import isiknock.view.matrix.listener.SortActionListener;
import isiknock.view.matrix.sorter.ClusterSorter;
import isiknock.view.matrix.sorter.MatrixColumnSorter;
import isiknock.view.matrix.sorter.MatrixRowSorter;
import java.awt.Component;
import java.awt.Cursor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import tools.IValueTranslater;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class BooleanKnockoutMatrixPanel extends MatrixPanel<IKnockoutMatrixIllustrator<Boolean>, Boolean> {

    private final Component[] menu;

    public BooleanKnockoutMatrixPanel(IKnockoutMatrixIllustrator<Boolean> matrixIllustrator) {
        super(matrixIllustrator);
        
        //row sorter
        JCheckBoxMenuItem jMItmShowMultipleKnockout = new JCheckBoxMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewDisplayMultipleKnockout);
        jMItmShowMultipleKnockout.addActionListener(new SortActionListener(null){
            @Override
            public void trigger(Object arg) {
                matrixIllustrator.setShowMultipleKnockout(jMItmShowMultipleKnockout.isSelected());
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.revalidate();
                BooleanKnockoutMatrixPanel.this.repaint();
            }
        });
        
        //row sorter
        JRadioButtonMenuItem jMItmRowNaturalOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortStandardOrder);
        jMItmRowNaturalOrder.addActionListener(new SortActionListener(null){
            @Override
            public void trigger(Object arg) {
                matrixIllustrator.setRowSorter(null);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
            }
        });
        
        JRadioButtonMenuItem jMItmRowAlphabetaicallyOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortAcendingOrder);
        jMItmRowAlphabetaicallyOrder.addActionListener(new SortActionListener<MatrixRowSorter<Boolean>, Boolean>(new MatrixRowSorter<>(true)) {

            @Override
            public void trigger(MatrixRowSorter<Boolean> arg) {
                matrixIllustrator.setRowSorter(arg);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
            }

        });
        
        JRadioButtonMenuItem jMItmRowAlphabetaicallyDecendingOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortDecendingOrder);
        jMItmRowAlphabetaicallyDecendingOrder.addActionListener(new SortActionListener<MatrixRowSorter<Boolean>, Boolean>(new MatrixRowSorter<>(false)) {

            @Override
            public void trigger(MatrixRowSorter<Boolean> arg) {
                matrixIllustrator.setRowSorter(arg);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
            }

        });

        JRadioButtonMenuItem jMItmRowClusterOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortClusterOrder);
        IValueTranslater<Boolean, Double> translater = (Boolean toTranslate) -> toTranslate == null || toTranslate ? 0 : 1d;
        jMItmRowClusterOrder.addActionListener(new SortActionListener<ClusterSorter<Boolean>, Boolean>(new ClusterSorter<>(translater, false)) {

            @Override
            public void trigger(ClusterSorter<Boolean> arg) {
                BooleanKnockoutMatrixPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                matrixIllustrator.setRowSorter(arg);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
                BooleanKnockoutMatrixPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

        });
        
        ButtonGroup groupRow = new ButtonGroup();
        groupRow.add(jMItmRowNaturalOrder);
        groupRow.add(jMItmRowAlphabetaicallyOrder);
        groupRow.add(jMItmRowAlphabetaicallyDecendingOrder);
        groupRow.add(jMItmRowClusterOrder);

        //column sorter
        JRadioButtonMenuItem jMItmColumnNaturalOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortStandardOrder);
        jMItmColumnNaturalOrder.addActionListener(new SortActionListener(null){
            @Override
            public void trigger(Object arg) {
                matrixIllustrator.setColumnSorter(null);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
            }
        });
        
        JRadioButtonMenuItem jMItmSortColumnAlphabetaicallyOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortAcendingOrder);
        jMItmSortColumnAlphabetaicallyOrder.addActionListener(new SortActionListener<MatrixColumnSorter<Boolean>, Boolean>(new MatrixColumnSorter<>(true)) {

            @Override
            public void trigger(MatrixColumnSorter<Boolean> arg) {
                matrixIllustrator.setColumnSorter(arg);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
            }

        });
        JRadioButtonMenuItem jMItmSortColumnAlphabetaicallyDescendingOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortDecendingOrder);
        jMItmSortColumnAlphabetaicallyDescendingOrder.addActionListener(new SortActionListener<MatrixColumnSorter<Boolean>, Boolean>(new MatrixColumnSorter<>(false)) {

            @Override
            public void trigger(MatrixColumnSorter<Boolean> arg) {
                matrixIllustrator.setColumnSorter(arg);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
            }

        });
        
        JRadioButtonMenuItem jMItmColumnClusterOrder = new JRadioButtonMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortClusterOrder);
        jMItmColumnClusterOrder.addActionListener(new SortActionListener<ClusterSorter<Boolean>, Boolean>(new ClusterSorter<>(translater, true)) {

            @Override
            public void trigger(ClusterSorter<Boolean> arg) {
                BooleanKnockoutMatrixPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                matrixIllustrator.setColumnSorter(arg);
                BooleanKnockoutMatrixPanel.this.clearImageCache();
                BooleanKnockoutMatrixPanel.this.repaint();
                BooleanKnockoutMatrixPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

        });
        
        ButtonGroup groupColumn = new ButtonGroup();
        groupColumn.add(jMItmColumnNaturalOrder);
        groupColumn.add(jMItmSortColumnAlphabetaicallyOrder);
        groupColumn.add(jMItmSortColumnAlphabetaicallyDescendingOrder);
        groupColumn.add(jMItmColumnClusterOrder);

        JMenuItem jMnuItemRow = new JMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortTransitions);
        jMnuItemRow.setEnabled(false);
        JMenuItem jMnuItemColumn = new JMenuItem(Language.CURRENT.booleanMatrixViewerMenuViewSortPlaces);
        jMnuItemColumn.setEnabled(false);
        
        jMItmColumnNaturalOrder.setSelected(true);
        jMItmRowNaturalOrder.setSelected(true);
        
        this.menu = new Component[]{
            
            jMnuItemColumn, 
            jMItmColumnNaturalOrder, 
            jMItmSortColumnAlphabetaicallyOrder, 
            jMItmSortColumnAlphabetaicallyDescendingOrder,
            jMItmColumnClusterOrder,
            
            jMnuItemRow, 
            jMItmRowNaturalOrder, 
            jMItmRowAlphabetaicallyOrder, 
            jMItmRowAlphabetaicallyDecendingOrder,
            jMItmRowClusterOrder,
        
            new JSeparator(), 
            jMItmShowMultipleKnockout,
        };
    }

    @Override
    public Component[] getViewMenuExtensions() {
        return menu;
    }

    @Override
    protected String[][] getDefaultInfoText(int row, int column) {
        Boolean value = this.matrixIllustrator.getValue(row, column);
        return new String[][]{
            new String[]{ (value ? Language.CURRENT.booleanMatrixViewerInfoTextAffected : Language.CURRENT.booleanMatrixViewerInfoTextUnaffected) }
        };
    }

}
