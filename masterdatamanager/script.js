/*
This is a tool for managing the static master data of AppTycoon.
*/

/*global console, $ */


var resources, // The dataset loaded from an external JSON goes here.
    UiUtils; // Declare UiUtils here, defined later on. Provides utilities for handling the UI.


/**
 * loadResourcesFromFile - Loads the static master data from a JSON file.
 *
 * @param  {string} fileName Path to the file to open.
 */
function loadResourcesFromFile(fileName) {
    "use strict";
    console.log("loadResources() - fileName = '" + fileName + "'");

    // Configure MIME Type for the following getJSON to prevent an ugly error.
    $.ajaxSetup({
        beforeSend: function (xhr) {
            if (xhr.overrideMimeType) {
                xhr.overrideMimeType("application/json");
            }
        }
    });
    $.getJSON(fileName, function (response) {
        resources = response;
        UiUtils.setResourcesLoaded(true);
        console.log("loadResources() - file loaded");
        console.log(resources);

        UiUtils.buildContentSelectors();
    });
}


/**
 * displayData - Builds and then shows a table that displays the selected data.
 *
 * @param  {string} dataType Data type name (as written in the data JSON).
 */
function displayData(dataType) {
    "use strict";
    console.log("displayData() - dataType = '" + dataType + "'");

    // The schema for the selected data type.
    var schema = resources.schema[dataType],
        data = resources.data[dataType],

        // The table with the data for selected data type.
        table = "<table cellspacing=0><tr class=\"titleRow\">",

        // Used for iterating through objects. Keys of members are stored into this variable.
        key,

        // One instance of data. I.e. one row in the table.
        dataObject,
        i,

        // Possible typeId values for this data type.
        typeIds = resources.typeIds[dataType],

        // The dropdown menu as a HTML string with possible type IDs.
        typeIdMenu;

    // Build the type ID dropdown menu.
    typeIdMenu = "<select>";
    for (key in typeIds) {
        if (typeIds.hasOwnProperty(key)) {
            typeIdMenu += "<option value=\"" + typeIds[key] + "\">" + key
                + "</option>";
        }
    }
    typeIdMenu += "</select>";

    // Build the header row using the schema.
    for (key in schema) {
        if (schema.hasOwnProperty(key)) {
            table += "<td>" + key + "</td>";
        }
    }
    table += "</tr>";

    // ...and then the data rows from the actual data.
    for (i in data) {
        if (data.hasOwnProperty(i)) {
            dataObject = data[i];
            table += "<tr>";

            // Now add the columns for the object based on the schema.
            // This because the columns might not be in the same order as in
            // the schema, and thus just iterating could result the columns
            // being printed in wrong order.
            for (key in schema) {
                if (schema.hasOwnProperty(key)) {
                    if (dataObject.hasOwnProperty(key)) {
                        table += "<td>";
                        if (key === "typeId") {
                            // TypeId is always a dropdown menu of possible options.
                            table += typeIdMenu;
                        } else {
                            // Else this was a 'normal' data property.
                            table += "<input class=\"dataValue\" value=\"" + dataObject[key] + "\">";
                        }
                        table += "</td>";
                    } else {
                        // The dataObject did not have a property that was
                        // in the schema!
                        table += "<td> !!!MISSING!!! </td>";
                    }
                }
            }

            table += "</tr>";

            console.log(dataObject);
        }
    }

    table += "</table>";
    //console.log(table);

    UiUtils.showDataTable(table);
}

// UI modifications wrapped into its own namespace.
UiUtils = {

    /**
     * Toggles the text that indicates whether the resources are loaded or not.
     */
    setResourcesLoaded: function (isLoaded) {
        "use strict";
        $("#spanResLoaded").html(isLoaded ? "loaded" : "not loaded");
    },


    /**
     * Displays selector links for which data to edit based on the schema
     * of the loaded resources JSON file.
     */
    buildContentSelectors: function () {
        "use strict";
        console.log("UiUtils.buildContent()");

        var key;
        for (key in resources.schema) {
            if (typeof resources.schema[key] === "object") {
                console.log(key + ": " + typeof resources.schema[key]);
                $("#selectorDiv").append("<span class=\"resourceSelectorLink\">"
                    + key + "</span> ");

                // Re-bind elements to click event handlers as the DOM has
                // now changed.
                UiUtils.bindOnClickEvents();
            }
        }
    },

    clearContentDiv: function () {
        "use strict";
        $("#contentDiv").empty();
    },

    /**
     * Binds click events of elements to actions.
     *
     * Call this when the DOM changes to include more stuff that needs
     * handling of click events.
     */
    bindOnClickEvents: function () {
        "use strict";
        console.log("bindOnClickEvents()");

        // Handler for clicks on content type selector links.
        $(".resourceSelectorLink").click(function () {
            var key = $(this).html();
            $(this).click(function () {
                displayData(key);
            });
            console.log(key);
        });

        // Export button
        $("#buttonExport").click(function () {
            UiUtils.showExportBox();
        });

        // Close export box button
        $("#buttonCloseExportBox").click(function () {
            UiUtils.hideExportBox();
        });
    },

    /**
     * Shows a table with the data that is selected for edit.
     *
     * @param {string} table The <table> element as a string.
     */
    showDataTable: function (table) {
        "use strict";
        $("#contentDiv").html(table);
    },

    /**
     * Show the exported data in a box that pops over the page.
     */
    showExportBox: function () {
        "use strict";
        console.log("showExportBox()");
        var exportBox = $("#exportBox"),
            textArea = $("#textareaExportedJson");
        exportBox.show();
        textArea.html(JSON.stringify(resources));
    },

    hideExportBox: function () {
        "use strict";
        $("#exportBox").hide();
    }

};

// This is the jQuery entry point.
$(function () {
    "use strict";
    console.log("document ready callback");

    $("#contentDiv").html("Nothing to show...");

    loadResourcesFromFile("example_dataset.json");
    UiUtils.hideExportBox();
    UiUtils.setResourcesLoaded(false);
    UiUtils.bindOnClickEvents();
});
