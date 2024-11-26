/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.language;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class Language {

    public static final Language DEFAULT = new Language();
    public static Language CURRENT = DEFAULT;

    public final String[] aboutTxt;

    public final String windowTitleKnockoutViewer;

    public final String vocError, vocExport, vocView, vocProperties, vocHelp;

    public final String menuHelp, menuHelpDocumentation, menuHelpAbout, matrixViewerMenuView, matrixViewerMenuViewProperties, matrixViewerMenuExport, matrixViewerMenuExportCSV, matrixViewerMenuExportSVG, matrixViewerMenuExportPNG;

    public final String lblConfigTitle, lblConfigPlaceListTitle, lblConfigTransitionListTitle,
            lblFileInfoFilename, lblFileInfoTransitionCount, lblFileInfoPlaceCount;

    public final String rbtnKnockOutMatrixBasedOnTI, rbtnKnockOutMatrixBasedOnMI, rbtnKnockOutMatrixBasedOnMIFastSearch;

    public final String chBxSelectAllTransitions, chBxSelectAllInputTransitions, chBxSelectAllPlaces, chBxIntegrateOutputTransitions, chBxSaveInvariants, chBxIncludePInvariants;

    public final String btnCreateKnockOut, btnLoadFile;

    public final String titleInfoKnockOutMatrixBasedOnMI, titleInfoKnockOutMatrixBasedOnTI, titleInfoKnockOutMatrixBasedOnMIFastSearch, titleInfoKnockOutMatrixInputTransitions;
    
    public final String msgParseFormatTitle, msgParseFormat, msgFileContainsNoDataTitle, msgFileContainsNoData,
            msgWrongFileFormatTitle, msgWrongFileFormat, msgNoTransitionsSelectedTitle, msgNoTransitionsSelected, msgNoPlacesSelectedTitle, msgNoPlacesSelected,
            msgCalculationMayTakeAWhileTitle, msgCalculationMayTakeAWhile, msgCalculationFailedTitle,
            msgCalculationFailed, msgCalculationFailedErrorTitle, msgCalculationFailedError, matrixViewerMsgExportFailedTitle,
            matrixViewerMsgExportFailed;

    public final String errorInvariantCalculationFailedNoResult, errorInvariantCalculationInputFile, errorInvariantCalculationOutputFile,
            errorInvariantCalculationResultFile;

    public final String booleanMatrixVocAffected, booleanMatrixVocUnaffected;
    
    public final String booleanMatrixWindowTitleMatrixProperties;
    
    public final String booleanMatrixViewerMenuViewSortPlaces, booleanMatrixViewerMenuViewSortTransitions, booleanMatrixViewerMenuViewSortClusterOrder, booleanMatrixViewerMenuViewSortDecendingOrder, booleanMatrixViewerMenuViewSortAcendingOrder, booleanMatrixViewerMenuViewDisplayMultipleKnockout, booleanMatrixViewerMenuViewSortStandardOrder;

    public final String booleanMatrixMatrixPropertiesLblAffected, booleanMatrixMatrixPropertiesLblUnaffected;
    
    public final String booleanMatrixViewerInfoTextUnaffected, booleanMatrixViewerInfoTextAffected;

    public Language() {

        windowTitleKnockoutViewer = "Knockout Viewer";

        vocError = "Error";
        vocHelp = "Help";
        vocView = "View";
        vocExport = "Export";
        vocProperties = "Properties";
        

        menuHelp = vocHelp;
        menuHelpDocumentation = "View Documentation";
        menuHelpAbout = "About";

        matrixViewerMenuExport = vocExport;
        matrixViewerMenuExportCSV = "Export as csv";
        matrixViewerMenuExportPNG = "Export as png";
        matrixViewerMenuExportSVG = "Export as svg";

        matrixViewerMenuView = vocView;
        matrixViewerMenuViewProperties = vocProperties;

        
        booleanMatrixVocAffected = "Affected";
        booleanMatrixVocUnaffected = "Unaffected";
        
        booleanMatrixWindowTitleMatrixProperties = vocProperties;
        booleanMatrixMatrixPropertiesLblAffected = booleanMatrixVocAffected;
        booleanMatrixMatrixPropertiesLblUnaffected = booleanMatrixVocUnaffected;
        
        booleanMatrixViewerMenuViewSortTransitions = "Sort reactions:";
        booleanMatrixViewerMenuViewSortPlaces = "Sort species:";

        booleanMatrixViewerMenuViewDisplayMultipleKnockout = "Display multipe knockout row";
        booleanMatrixViewerMenuViewSortStandardOrder = "Standard";
        booleanMatrixViewerMenuViewSortAcendingOrder = "A-Z";
        booleanMatrixViewerMenuViewSortDecendingOrder = "Z-A";
        booleanMatrixViewerMenuViewSortClusterOrder = "Cluster";

        booleanMatrixViewerInfoTextAffected = "Species \"#columnLabel#\" is affected by the knockout of reaction \"#rowLabel#\"";
        booleanMatrixViewerInfoTextUnaffected = booleanMatrixViewerInfoTextAffected.replace("affected", "unaffected");

        lblConfigTitle = "Options:"; //In silico knockout based on:";
        lblConfigPlaceListTitle = "Species";
        lblConfigTransitionListTitle = "Knocked out reactions";

        rbtnKnockOutMatrixBasedOnTI = "Transition invariants";
        rbtnKnockOutMatrixBasedOnMI = "Manatee invariants (exhaustive search)";
        rbtnKnockOutMatrixBasedOnMIFastSearch = "Manatee invariants (fast search)";
        
        titleInfoKnockOutMatrixBasedOnMI = rbtnKnockOutMatrixBasedOnMI;
        titleInfoKnockOutMatrixBasedOnMIFastSearch = rbtnKnockOutMatrixBasedOnMIFastSearch;
        titleInfoKnockOutMatrixBasedOnTI = rbtnKnockOutMatrixBasedOnTI;
        titleInfoKnockOutMatrixInputTransitions = "Output reactions";

        chBxSelectAllTransitions = "Select all reactions";
        chBxSelectAllInputTransitions = "Select all syntheses/productions";
        chBxSelectAllPlaces = "Select all species";
        chBxIntegrateOutputTransitions = "Integrate output reactions";
        chBxSaveInvariants = "Save invariants";
        chBxIncludePInvariants = "Include species of place invariants";

        btnCreateKnockOut = "Create knockout matrix";
        btnLoadFile = "Load file";

        lblFileInfoFilename = "Filename:";
        lblFileInfoTransitionCount = "No. of reactions:";
        lblFileInfoPlaceCount = "No. of species:";

        msgParseFormatTitle = "Wrong file format";
        msgParseFormat = "Couldn't parse the format of the given file.";

        msgFileContainsNoDataTitle = "No valid data";
        msgFileContainsNoData = "The file contains no valid data.";

        msgWrongFileFormatTitle = "Wrong file format";
        msgWrongFileFormat = "The file format is not supported.";

        msgNoTransitionsSelectedTitle = "No reactions selected";
        msgNoTransitionsSelected = "Can't perform knockout: You've not selected any reactions.";

        msgNoPlacesSelectedTitle = "No species selected";
        msgNoPlacesSelected = "Can't perform knockout: You've not selected any species.";

        msgCalculationMayTakeAWhileTitle = "Calculation may take a while";
        msgCalculationMayTakeAWhile = "You've chosen a high number of species. This may take a while. Do you want to continue?";

        msgCalculationFailedTitle = "Invariant calculation failed";
        msgCalculationFailed = "Invariant calculation failed.";

        msgCalculationFailedErrorTitle = msgCalculationFailedTitle;
        msgCalculationFailedError = msgCalculationFailed + " " + vocError + ": ";

        matrixViewerMsgExportFailedTitle = "Export failed";
        matrixViewerMsgExportFailed = "Export failed: Couldn't write to: ";

        errorInvariantCalculationFailedNoResult = "Calculation returned no result.";
        errorInvariantCalculationInputFile = "Can't write input file for invariant calculation.";
        errorInvariantCalculationOutputFile = "Couldn't write invariants to file: ";
        errorInvariantCalculationResultFile = "Can't access invariant calculation result file: ";

        aboutTxt = new String[]{
            "version: " + isiknock.IsiKnock.APP_VERSION,
            " ",
            "\u00A9 Molekulare Bioinformatik, Goethe-University Frankfurt, Frankfurt am Main, Germany",
            " ",
            "isiKnock is licensed under the Artistic License 2.0 and depence on non-free software.",
            "This is a free software license which is compatible with the GPL according to the FSF.",
            "It is the same license that is used by the Perl programming language.",
            " ",
            "Project members in alphabetical order (recent and former):",
            "Börje Schweizer, Heiko Giese, Ina Koch, Jennifer Scheidel, Jörg Ackermann, Leonie Amstein",};
    }

}
