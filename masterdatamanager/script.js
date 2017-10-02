/*
This is a tool for managing the static master data of AppTycoon.
*/

/*global console, $ */


var resources, // The dataset loaded from an external JSON goes here.
    UiUtils, // Declare UiUtils here, defined later on. Provides utilities for handling the UI.
    selectedDataType; // Content that's being editet.


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
 * getTypeIdDropdownForObject - Builds a dropdown menu as a HTML string that contains
 * the typeIds shown with their constant name instead of just a number. Also sets the
 * current ID as the default selection.
 *
 * @param  {object} typeIds   Object containing all typeIds
 * @param  {number} currentId ID of the row. This will be selected as default
 * @return {string}           A <select> dropdown menu as a string
 */
function getTypeIdDropdownForObject(typeIds, currentId) {
    "use strict";
    console.log("getTypeIdDropdownForObject() - currentId=" + currentId);

    var typeIdMenu = "<select>",
        key;

    for (key in typeIds) {
        if (typeIds.hasOwnProperty(key)) {
            typeIdMenu += "<option value=\"" + typeIds[key] + "\"";

            // Set the current ID as default selection.
            if (typeIds[key] === currentId) {
                typeIdMenu += " selected=\"selected\"";
            }

            typeIdMenu += ">" + key + "</option>";
        }
    }
    typeIdMenu += "</select>";

    return typeIdMenu;
}


/**
 * getBooleanSelect - Builds a HTML <select> with options 'true' and 'false'.
 * Values for those are "0" and "1" respectively.
 *
 * @param  {boolean} defaultValue Value to be selected as default.
 * @return {string}              String with HTML <select>
 */
function getBooleanSelect(defaultValue) {
    "use strict";
    console.log("getBooleanSelect() - defaultValue = " + defaultValue);

    var select = "<select><option value=\"0\"",
        selectedProp = " selected=\"selected\">";

    select += defaultValue ? ">" : selectedProp;
    select += "false" + "</option>";

    select += "<option value=\"1\"";
    select += defaultValue ? selectedProp : ">";
    select += "true" + "</option>";

    return select;
}


/**
 * addNewrow - Adds a new row with properties entered by the user.
 *
 * @return {boolean} True when added successfully
 */
function addNewRow() {
    "use strict";
    var key,
        value,
        elementTypes = ["input", "select"],
        elementType,
        $element,
        valueType,
        i,
        newDataItem = {};

    console.log("addNewrow()");

    $(".addRowBoxPropertyRow").each(function () {
        $(this).find("td").each(function (index) {
            if (index === 0) {
                // This is a "key" column.
                key = $(this).html();
            } else {
                // This is a "value" column.
                // Determine which kind of input element the column contains.
                for (i = 0; i < elementTypes.length; i += 1) {
                    elementType = elementTypes[i];
                    $element = $(this).find(elementType);
                    if ($element.length > 0) {
                        break;
                    }
                }

                // Dig the data from the input element.
                if (elementType === "input") {
                    value = $element.prop("value");
                } else if (elementType === "select") {
                    $element.find("option").each(function () {
                        if ($(this).prop("selected")) {
                            value = $(this).prop("value");
                        }
                    });
                }

                // The value is cast according to the type specified in the schema.
                valueType = typeof resources.schema[selectedDataType][key];
                console.log(valueType + " " + value);

                switch (valueType) {
                case "number":
                    value = Number(value);
                    break;
                case "boolean":
                    // "Booleans" are gotten as strings "0" or "1" from the table.
                    // So, first cast that string to number, then that number
                    // to boolean.
                    value = Boolean(Number(value));
                    break;
                default:
                    break;
                }
            }
        });

        // Finally store the property to the new data item.
        newDataItem[key] = value;
        console.log("key=" + key + ", value=" + value);
    });

    resources.data[selectedDataType].push(newDataItem);
    return true;
}

/**
 * buildAndShowAddRowBox - Constructs a "dilaog" for entering a new row of data
 * of the currently selected type.
 *
 * Then shows the box.
 *
 */
function buildAndShowAddRowBox() {
    "use strict";
    console.log("buildAndShowAddRowBox()");

    var schema = resources.schema[selectedDataType],
        table,
        key,
        propertyType;

    // Compile a nice table to enter the properties for the new item.
    table = "<table cellspacing=\"0\" cellpadding=\"0\">";
    for (key in schema) {
        if (schema.hasOwnProperty(key)) {
            // Title column.
            table += "<tr class=\"addRowBoxPropertyRow\"><td>" + key + "</td><td>";

            // Value input column. The type of the input element depends on
            // the type of the property in the schema.
            propertyType = typeof schema[key];

            // Type ID is always a dropdown
            if (key === "typeId") {
                table += getTypeIdDropdownForObject(resources.typeIds[selectedDataType], -1);
            } else if (propertyType === "string" || propertyType === "number") {
                table += "<input type=\"text\">";
            } else if (propertyType === "boolean") {
                table += getBooleanSelect(schema[key]);
            }

            table += "</td></tr>";
        }
    }

    table += "</table>";

    $("#addRowBoxFields").html(table);
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
        typeIds = resources.typeIds[dataType];

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
            table += "<tr class=\"dataRow\">";

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
                            table += getTypeIdDropdownForObject(typeIds, dataObject[key]);
                        } else if (typeof schema[key] === "boolean") {
                            // For booleans, show a dropdown menu with 'true' and 'false'.
                            table += getBooleanSelect(dataObject[key]);
                        } else {
                            // Else this was a 'normal' data property.
                            table += "<input class=\"dataValue\" value=\"" + dataObject[key] + "\">";
                        }
                        table += "</td>";
                    } else {
                        // The dataObject did not have a property that was
                        // in the schema!
                        table += "<td><b>none</b></td>";
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


/**
 * storeCurrentDataTable - Stores the data that's in the currently visible table
 * into the currently loaded resources.
 */
function storeCurrentDataTable() {
    "use strict";
    console.log("storeCurrentDataTable()");

    var schema = resources.schema[selectedDataType],
        properties = [],
        i = 0,
        key;

    for (key in schema) {
        if (schema.hasOwnProperty(key)) {
            properties[i] = key;
            i += 1;
        }
    }

    // Clear the current data for the data type
    resources.data[selectedDataType] = [];

    // Iterate through all the data rows
    $(".dataRow").each(function (index) {
        var rowIndex = index,
            newDataItem = {};

        console.log("storeCurrentDataTable() - rowIndex = " + rowIndex);

        // Then iterate through each column
        $(this).find("td").each(function (index) {
            // Index of the column matches the index of the property
            // in the properties[] array.
            //
            // Based on the element type, the value we want is stored in
            // differently named properties.
            var elementTypes = ["input", "select"],
                elementType,
                $element,
                value,
                valueType;

            // Determine which kind of input element the column contains.
            for (i = 0; i < elementTypes.length; i += 1) {
                elementType = elementTypes[i];
                $element = $(this).find(elementType);
                if ($element.length > 0) {
                    break;
                }
            }

            // Dig the data from the input element.
            if (elementType === "input") {
                value = $element.prop("value");
            } else if (elementType === "select") {
                $element.find("option").each(function () {
                    if ($(this).prop("selected")) {
                        value = $(this).prop("value");
                    }
                });
            }

            // The value is cast according to the type specified in the schema.
            valueType = typeof resources.schema[selectedDataType][properties[index]];
            console.log(valueType + " " + value);

            switch (valueType) {
            case "number":
                value = Number(value);
                break;
            case "boolean":
                // "Booleans" are gotten as strings "0" or "1" from the table.
                // So, first cast that string to number, then that number
                // to boolean.
                value = Boolean(Number(value));
                break;
            default:
                break;
            }

            // Finally add the value for the new data item.
            newDataItem[properties[index]] = value;

        });

        // Add the new data item into the data for the selected data type.
        resources.data[selectedDataType].push(newDataItem);

    });
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
            }
        }

        // Re-bind elements to click event handlers as the DOM has
        // now changed.
        UiUtils.bindOnClickEvents();
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
            selectedDataType = $(this).html();
            displayData(selectedDataType);
            console.log("resourceSelectorLink.click() - " + selectedDataType);
        });

        // Export button
        $("#buttonExport").click(function () {
            UiUtils.showExportBox();
        });

        // Close export box button
        $("#buttonCloseExportBox").click(function () {
            UiUtils.hideExportBox();
        });

        // Add row button
        $("#buttonAddRow").click(function () {
            UiUtils.showAddRowBox();
        });

        // Cancel button for adding a row
        $("#buttonAddRowBoxCancel").click(function () {
            UiUtils.hideAddRowBox();
        });

        // Done button for adding a row
        $("#buttonAddRowBoxDone").unbind("click");
        $("#buttonAddRowBoxDone").click(function () {
            var success = addNewRow();
            console.log("buttonAddRowBoxDone.click() - success=" + success);
            UiUtils.hideAddRowBox();

            // If new data was added, reload the data table.
            if (success) {
                displayData(selectedDataType);
            }
        });

        // Save button
        $("#buttonSave").unbind("click");
        $("#buttonSave").click(function () {
            console.log("buttonSave.click()");
            storeCurrentDataTable();
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

    showAddRowBox: function () {
        "use strict";
        $("#addRowBox").show();
        buildAndShowAddRowBox();
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
    },

    hideAddRowBox: function () {
        "use strict";
        $("#addRowBox").hide();
    },

    hideAllPopUps: function () {
        "use strict";
        UiUtils.hideExportBox();
        UiUtils.hideAddRowBox();
    }

};

// This is the jQuery entry point.
$(function () {
    "use strict";
    console.log("document ready callback");

    $("#contentDiv").html("Nothing to show...");

    loadResourcesFromFile("example_dataset.json");
    UiUtils.hideAllPopUps();
    UiUtils.setResourcesLoaded(false);
    UiUtils.bindOnClickEvents();
});
