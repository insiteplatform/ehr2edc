$(document).ready(function () {
    $("#patientIdeSource").on("change", updatePatientTable);
    $(".button-edit").on("click", show_edit_dialog);
    $(".button-delete").on("click", show_delete_dialog);
    $(".button-recalculate").on("click", recalculate_cohort);
    $(".button-download").on("click", downloadCohort);
    $(".patient-details").on("click", ".toggle-factspane", open_factspane);
    $(".patient-details").on("click", ".toggle-registrationPatientPane", openRegistrationPatientPane);
    $(".page-facts .page-fact-item-numbers").on("click", "li", update_facts_size);

    $(".facts-pane").on("click", ".close-icon", close_factspane);
    $(".ehr2edc-pane").on("click", ".close-icon", close_ehr2edcpane);

    setPatientTable();
    setFactsFiltering();
});

function setPatientTable() {
    var patientTable = $(".patient-details");
    if (patientTable.length) {
        $(".page-items .page-item-numbers").on("click", "li", update_pager_size);
        $("#leftPanel").on("keydown", ".patient-search", filter_patients_live);

        var cohortId = $("#cohort-id").val();
        var infoTooltip = $("#action-info-tooltip").val();

        var ehr2edcEnabled = $("#ehr2edc-enabled").val() === "true";
        var patientRegistrationTooltip = $("#action-patient-registration-tooltip").val();

        patientTable.on("click", "th", function () {
            setTimeout(update_sort_icons, 100);
        });
        setTimeout(update_sort_icons, 100);

        $.tablesorter.addParser({
            id: 'gender',
            is: function (s) {
                return false;
            },
            format: function (val, table, cell) {
                var letter = "";

                if ($(cell).find(".fa-venus").length) {
                    letter = "F";
                } else if ($(cell).find(".fa-mars").length) {
                    letter = "M";
                } else {
                    letter = "U";
                }
                return letter;
            },
            type: 'text'
        });
        patientTable.tablesorter({
            sortList: [[0, 0]],
            headers: {
                0: {sorter: true, filter: true},
                1: {sorter: 'gender', filter: false},
                2: {sorter: true, filter: false},
                3: {sorter: false, filter: false},
            },
            cssAsc: "asc",
            cssDesc: "desc",
            widgets: ["filter"],
            widgetOptions: {
                filter_cssFilter: 'filter-item',
                filter_childRows: false,
                filter_hideFilters: true,
                filter_ignoreCase: true,
                filter_saveFilters: false,
                filter_searchDelay: 300,
                filter_startsWith: false,
                filter_filteredRow: 'hide'
            },
        }).tablesorterPager({
            container: $(".footer"),
            size: 10,
            processAjaxOnInit: true,
            ajaxUrl: lwbSettings.patientsURL + "?cohortId=" + cohortId + "&size={size}&page={page}&{sortList:sort}&{filterList:filter}",
            customAjaxUrl: function (table, url) {
                url += "&patientIdeSource=" + $("#patientIdeSource").val();
                return url.replace(/\[/g, "").replace(/\]/g, "");
            },
            ajaxProcessing: function (data) {
                var resultData = [];

                if (data && data.totalSize) {
                    var totalSize = data.totalSize;
                } else {
                    var totalSize = 0;
                }

                if (data != null && data.hasOwnProperty("patients") && data.patients.length > 0) {
                    $.each(data.patients, function (index, patientJSON) {
                        switch (getGender(patientJSON)) {
                            case "male":
                            case "m":
                                var genderStr = "<i class='fa fa-mars'></i> " + i18next.t("sidebar.male");
                                var genderValue = "male";
                                break;
                            case "female":
                            case "f":
                                var genderStr = "<i class='fa fa-venus'></i> " + i18next.t("sidebar.female");
                                var genderValue = "female";
                                break;
                            default:
                                var genderStr = "-";
                                var genderValue = "unknown";
                                break;
                        }

                        var patientInput = $("<input>").attr({
                            'type': 'hidden',
                            'class': "patient-id",
                            'value': patientJSON.id,
                        });
                        var displayId = "N/A";
                        if (patientJSON.globalId != null) {
                            displayId = patientJSON.globalId;
                        }
                        var actions = "<div class='actions'><a class='toggle-factspane' title='" + infoTooltip + "'><i class='fa fa fa-file-text-o'></i></a>";
                        if (ehr2edcEnabled) {
                            actions += "<a class='toggle-registrationPatientPane' title='" + patientRegistrationTooltip + "'><i class='fa fa fa-user'></i></a>"
                        }
                        actions += "</div>";
                        var resJSON = ["<input type='hidden' name='patient-" + patientJSON.id + "-id' class='patient-globalId' value='" + patientJSON.globalId + "' /> "
                        + "<input type='hidden' name='patient-" + patientJSON.id + "-id' class='patient-id' value='" + patientJSON.id + "' /> "
                        + "<input type='hidden' name='patient-" + patientJSON.id + "-gender' class='patient-gender' value='" + genderValue + "' /> "
                        + "<input type='hidden' name='patient-" + patientJSON.id + "-age' class='patient-age' value='" + patientJSON.age + "' /> "
                        + displayId,
                            genderStr, patientJSON.age,
                            actions];
                        resultData.push(resJSON);
                    });
                    $(".patient-details").removeClass("hide");
                    $(".no-patients").addClass("hide");
                } else {
                    $(".patient-details").addClass("hide");
                    $(".no-patients").removeClass("hide");
                }
                return [totalSize, resultData];
            },
            ajaxObject: {
                method: "POST",
                dataType: "json",
                ignoreGlobalError: true,
                error: function () {
                    //The ajaxError from jQuery tablesorter doesn't work properly, so this is used instead
                    $(".patient-details").addClass("hide");
                    $(".no-patients").removeClass("hide");
                },
            },

            updateArrows: true,
            page: 0,
            output: "{page}/{totalPages}",
            removeRows: true,
            savePages: false,

            cssNext: '.next',
            cssPrev: '.prev',
            cssFirst: '.first',
            cssLast: '.last',
            cssDisabled: 'disabled',
            cssPageSize: '.pager-size',
            cssGoto: '.pager-nr',
            positionFixed: false
        });
        patientTable.bind("pagerComplete", function () {
            update_pager();
        });

        $(".tablesorter-filter-row").hide();
        filter_patients_reset();
    }
}

function updatePatientTable() {
    $(".patient-details").trigger('pagerUpdate');
}

function setFactsFiltering() {
    $(".toggle-search-pane")
        .on("click", ".button-expand", function () {
            $(".toggle-search-pane .button-expand").addClass("hide");
            $(".toggle-search-pane .button-collapse").removeClass("hide");
            $(".fact-search-pane").removeClass("hide");
        })
        .on("click", ".button-collapse", function () {
            $(".toggle-search-pane .button-expand").removeClass("hide");
            $(".toggle-search-pane .button-collapse").addClass("hide");
            $(".fact-search-pane").addClass("hide");
        });
    $(".datepicker").fdatepicker({
        format: 'd/mm/yyyy',
        disableDblClickSelection: true,
        showButtonPanel: true,
        closeText: 'Clear'

    }).on("changeDate", function (ev) {
        var picker = $(this);
        picker.find(".text").text(picker.data('date'));
    });
    $(".fact-search-pane").on("click", ".search-facts", function () {
        searchFacts();
    })
        .on("click", ".reset-search", resetSearch);
    $(".fact-header").on("click", ".reset-search", resetSearch);
}

function resetSearch() {
    var searchPane = $(".fact-search-pane");

    searchPane.find(".observation").val("");
    searchPane.find(".start-date .text").text("");
    searchPane.find(".end-date .text").text("");
    searchPane.find(".source").val("");
    searchFacts();
}

function isSearchDateValid(searchPane) {
    var startDate = searchPane.find(".start-date .text").text();
    var endDate = searchPane.find(".end-date .text").text();

    if (startDate !== "" && endDate !== "") {
        var startPicker = searchPane.find(".start-date").data('datepicker');
        var endPicker = searchPane.find(".end-date").data('datepicker');
        return startPicker.date.valueOf() <= endPicker.date.valueOf();
    } else {
        return true;
    }
}

function getGender(patient) {
    if (!patient.hasOwnProperty("gender") || patient.gender == null) {
        return "";
    } else {
        return patient.gender.toLowerCase();
    }
}

function open_factspane() {
    $("#leftPanel").addClass("hide");
    $(".ehr2edc-pane").addClass("hide");

    var tableRow = $(this).closest("tr");
    var patientId = tableRow.find(".patient-id").val();
    var patientGender = tableRow.find(".patient-gender").val();
    var patientAge = tableRow.find(".patient-age").val();

    $(".facts-pane .patient-id").val(patientId);
    $(".facts-pane .facts-title").html(i18next.t("sidebar.title") + " <small>"
        + i18next.t("sidebar.age") + ": " + patientAge + ", " + i18next.t("sidebar." + patientGender) + "</small>");
    $(".patient-details tr.is-active").removeClass("is-active");
    tableRow.addClass("is-active");

    $(".facts-pane").removeClass("hide");

    loadPatientData(patientId);
}

function openRegistrationPatientPane() {
    $("#leftPanel").addClass("hide");
    $(".facts-pane").addClass("hide");

    var tableRow = $(this).closest("tr");
    var patientId = tableRow.find(".patient-id").val();
    $(".patient-details tr.is-active").removeClass("is-active");
    tableRow.addClass("is-active");

    renderRegistrationPaneTitle(tableRow, patientId);
    loadPatientIdentifiers(patientId, renderPatientRegistrationPane);

    $(".ehr2edc-pane").removeClass("hide");
}

function renderRegistrationPaneTitle(tableRow, patientId) {
    var patientGender = tableRow.find(".patient-gender").val();
    var patientAge = tableRow.find(".patient-age").val();
    $(".ehr2edc-pane .patient-id").val(patientId);

    $(".ehr2edc-pane .ehr2edc-title").html(i18next.t("sidebar.patientRegistration.title") + " <small>"
        + i18next.t("sidebar.age") + ": " + patientAge + ", " + i18next.t("sidebar." + patientGender) + "</small>");
}

function renderPatientRegistrationPane(patientIdentifiers) {
    const patientIdentifier = patientIdentifiers[0];
    const data = {
        patientStudyListUrl: "ehr2edc/patient/" + patientIdentifier.patientIdeSource + "/" + patientIdentifier.patientIde + "/studies",
        patientRegistrationUrl: "ehr2edc/studies/${studyId}/subjects",
        patientSource: patientIdentifier.patientIdeSource,
        baseUrl: lwbSettings.ehr2edcBase,
        closeFunction: close_ehr2edcpane,
    };
    appendEhr2EdcRegisterSubject($("#ehr2edc-patient-registration")[0], data, patientIdentifier.patientIde);
}

function loadPatientData(patientId) {
    searchFacts();
    loadPatientIdentifiers(patientId, function () {
    });
    loadSources(patientId);
}

function loadPatientIdentifiers(patientId, onPatientIdentifiersLoaded) {
    renderPatientIdentifiersLoading();
    var url = replacePathVariable(lwbSettings.patientIdentifiersURL, "patientId", patientId);
    $.ajax({
        url: url,
        method: "GET",
        dataType: "json",
        ignoreGlobalError: true,
        success: function (data) {
            renderPatientIdentifiers(data.patientIdentifiers);
            onPatientIdentifiersLoaded(data.patientIdentifiers);
        },
        error: renderPatientIdentifiersError
    });
}

function loadSources(patientId) {
    var url = lwbSettings.patientSourcesURL + "?patientId=" + patientId;
    $.ajax({
        url: url,
        method: "GET",
        dataType: "json",
        ignoreGlobalError: true,
        success: function (data) {
            if (data.sources.length) {
                renderSourcesAsList(data);
            } else {
                $('.fact-sources').addClass("hide");
                var sourceList = $(".fact-sources select").empty();
                $("<option>").appendTo(sourceList);
            }
        }
    });
}

function renderPatientIdentifiers(patientIdentifiers) {
    var identifiers = $(".patient-identifiers").empty();
    $.each(patientIdentifiers, function (i, identifier) {
        identifiers.append($("<p>").text(identifier.patientIdeSource + ": " + identifier.patientIde).addClass("margin-none"));
    });
    renderPatientIdentifiersSuccess();
}

function renderPatientIdentifiersSuccess() {
    $(".patient-identifiers").removeClass("hide");
    $(".patient-identifiers-error").addClass("hide");
    $(".patient-identifiers-loading").addClass("hide");
}

function renderPatientIdentifiersLoading() {
    $(".patient-identifiers").addClass("hide");
    $(".patient-identifiers-error").addClass("hide");
    $(".patient-identifiers-loading").removeClass("hide");
}

function renderPatientIdentifiersError() {
    $(".patient-identifiers").addClass("hide");
    $(".patient-identifiers-error").removeClass("hide");
    $(".patient-identifiers-loading").addClass("hide");
}

function renderSourcesAsList(data) {
    $('.fact-sources').removeClass("hide");
    var sourceList = $(".fact-sources select").empty();
    var sourceSelected = $(".fact-search-pane .source").val();
    $("<option>").appendTo(sourceList);
    $.each(data.sources, function (index, item) {
        var option = $("<option>").text(item).appendTo(sourceList);
        if (item === sourceSelected) {
            option.attr("selected", "selected")
        }
    });
}

function searchFacts() {
    var searchPane = $(".fact-search-pane");
    if (isSearchDateValid(searchPane)) {
        var patientId = $(".facts-pane .patient-id").val();
        var observation = searchPane.find(".observation").val();
        var startDate = searchPane.find(".start-date .text").text();
        var endDate = searchPane.find(".end-date .text").text();
        var sourceSelected = searchPane.find(".source").val();
        var pageSize = $(".page-facts li.is-active").text();
        doFactSearch(patientId, startDate, endDate, sourceSelected, observation, pageSize);
    } else {
        searchPane.find(".end-date").addClass("form-field-error");
    }
}

function doFactSearch(patientId, startDate, endDate, source, textSearch, size) {
    disableFactsActions();
    renderFactsPaneLoading();
    var patientFactsUrlBase = getSearchUrl(patientId, startDate, endDate, source, textSearch, size);
    var filtersUsed = isFiltered(startDate, endDate, source, textSearch);
    $.ajax({
        url: patientFactsUrlBase,
        method: "POST",
        dataType: "json",
        ignoreGlobalError: true,
        complete: function () {
            enableFactsActions();
        },
        success: function (data) {
            if (filtersUsed) {
                $(".facts-pane .facts-filtered-status").removeClass("hide");
            } else {
                $(".facts-pane .facts-filtered-status").addClass("hide");
            }
            $(".display-summary").html(i18next.t("sidebar.displaySummary", {
                observations: data.observationsSize,
                facts: data.factsSize
            }));

            if (hasGroupsToRender(data)) {
                renderFactsPaneWithData(data);
            } else {
                renderEmptyFactsPane();
            }
        },
        error: function () {
            renderFactsPaneError();
        }
    });
}

function disableFactsActions() {
    $(".toggle-factspane").addClass("disabled dark");
    $("#factsPager li").addClass("disabled");
    $(".facts-filter-actions .button").addClass("disabled");
}

function enableFactsActions() {
    $(".toggle-factspane").removeClass("disabled dark");
    $("#factsPager li").removeClass("disabled");
    $(".facts-filter-actions .button").removeClass("disabled");
}

function getSearchUrl(patientId, startDate, endDate, source, textSearch, size) {
    var searchUrl = lwbSettings.factsURL + "?patientId=" + patientId + "&pageNr=0&pageSize=" + size;

    if (startDate !== "") {
        searchUrl += "&startDateAfter=" + startDate;
    }
    if (endDate !== "") {
        searchUrl += "&startDateBefore=" + endDate;
    }
    if (source !== "") {
        searchUrl += "&source=" + source;
    }
    if (textSearch !== "") {
        searchUrl += "&textSearch=" + textSearch;
    }
    return searchUrl;
}

function isFiltered(startDate, endDate, source, textSearch) {
    return startDate !== "" || endDate !== "" || source !== "" || textSearch !== "";
}

function renderFactsPaneWithData(data, patientId) {
    $(".facts-viewer").removeClass("hide");
    $(".fact-results").removeClass("hide");
    $("#facts-error").addClass("hide");
    $(".facts-search-status .search-results-loading-icon").addClass("hide");
    $(".facts-pane .facts-patient-id").val(patientId);
    $(".facts-pane .facts-pager").removeClass("disabled");
    var resultDiv = $(".fact-results").empty();
    renderFactsPane(data, resultDiv, patientId);
}

function renderFactsPaneLoading() {
    $(".facts-viewer").addClass("hide");
    $(".fact-results").addClass("hide");
    $(".facts-pane .pager-text").html(i18next.t("sidebar.loadingFacts"));
    $(".facts-pane .facts-pager").addClass("disabled");
    $(".facts-search-status .search-results-none-icon").addClass("hide");
    $(".facts-search-status .search-results-none-icon .empty-text").remove();
    $(".facts-search-status .search-results-loading-icon").removeClass("hide");
}

function renderFactsPaneError() {
    $(".facts-pane .facts-filtered-status").addClass("hide");
    $(".fact-results").addClass("hide");
    $(".facts-viewer").addClass("hide");
    $(".facts-search-status .search-results-loading-icon").addClass("hide");
    $(".facts-search-status .search-results-none-icon").removeClass("hide");
    $(".facts-search-status .facts-pane .pager-text").html(+i18next.t("sidebar.failedLoad"));
}

function renderEmptyFactsPane() {
    $(".facts-viewer").addClass("hide");
    $(".fact-results").addClass("hide");
    $(".facts-search-status .search-results-loading-icon").addClass("hide");
    var emptyIcon = $(".facts-search-status .search-results-none-icon").removeClass("hide");
    emptyIcon.append("<div class='empty-text align-center'><p class='text-center'>" + i18next.t("sidebar.noFactsAvailable") + "</p></div>");
    $(".facts-pane .pager-text").html(+i18next.t("sidebar.failedLoad"));
}

function close_factspane() {
    $(".facts-pane").addClass("hide");
    $("#leftPanel").removeClass("hide");
}

function close_ehr2edcpane() {
    $(".ehr2edc-pane").addClass("hide");
    $("#leftPanel").removeClass("hide");
}


function update_sort_icons() {
    $(".patient-details th").each(function () {
        if ($(this).hasClass("asc")) {
            $(this).find("i").attr("class", "fa fa-sort-asc")
        } else if ($(this).hasClass("desc")) {
            $(this).find("i").attr("class", "fa fa-sort-desc")
        } else {
            $(this).find("i").attr("class", "fa fa-sort")
        }
    });
}

function make_pager_number(number) {
    close_factspane();
    var result = $("<li>").addClass("move-page update-pager");
    $("<a>").attr("href", "#").html(number).appendTo(result);
    result.on("click", function () {
        $(".pager-nr").val(number);
        $(".pager-nr").trigger("change");
    });
    return result;
}

function update_pager_size(event) {
    var pager_size = $(event.target).text();
    $(".page-items .pager-size").val(pager_size).trigger("change");

    $(".page-items li.is-active").removeClass("is-active");
    $(this).addClass("is-active");
}

function filter_patients_live() {
    setTimeout(filter_patients, 10);
}

function filter_patients() {
    var filterValue = $("#leftPanel .patient-search").val();
    var filters = [];
    filters[0] = filterValue;
    filters[1] = filterValue;
    filters[2] = filterValue;
    $.tablesorter.setFilters($(".patient-details"), filters, true);
}

function filter_patients_reset() {
    var filters = [];
    $.tablesorter.setFilters($(".patient-details"), filters, true);

    $("#leftPanel .patient-search").val("");
}

function show_edit_dialog() {
    var editDialog = $("#editDialog").removeClass("hide");
    editDialog.dialog({
        title: i18next.t("editDialog.title"),
        modal: true,
        width: 600,
        resizable: false,
        autoOpen: false,
        buttons: [{
            text: i18next.t("common:Save"),
            click: function () {
                var title = editDialog.find(".cohort-title").val();
                var description = editDialog.find(".cohort-descr").val();
                var cohortId = $("#cohort-id").val();

                if (cohortId && title) {
                    var editURL = lwbSettings.updateURL;
                    var editData = {
                        'cohortId': cohortId,
                        'title': title,
                        'description': description
                    };
                    $.ajax({
                        'url': editURL,
                        'type': "POST",
                        'data': JSON.stringify(editData),
                        'contentType': "application/json",
                        'success': function () {
                            $("#editDialog").dialog("close");
                            location.reload();
                        },
                    });
                } else {
                    editDialog.find(".cohort-title").addClass("form-field-error");
                    editDialog.find(".form-text-error").remove();
                    $("<div class='form-text-error margin-medium-bottom'>Cohort title is required</div>")
                        .insertAfter(editDialog.find(".cohort-title"));
                }

            }
        },
            {
                text: i18next.t('common:discardChanges'),
                click: function () {
                    $("#editDialog").dialog("close");
                }
            }
        ],
        open: function () {
            editDialog.find(".form-text-error").remove();
            editDialog.find(".form-field-error").removeClass("form-field-error");

            var saveDialogDiv = editDialog.closest(".ui-dialog");
            var closeIcon = $("<a>").html("<i class='fa fa-fw fa-close'></i>")
                .on("click", function () {
                    $("#editDialog").dialog("close");
                });

            var titlebar = saveDialogDiv.find(".ui-dialog-titlebar").attr("class", "primary title-bar").empty();
            $("<div>").html(i18next.t("editDialog.title")).addClass("center title").appendTo(titlebar);
            $("<span>").addClass("right").append(closeIcon).appendTo(titlebar);

            saveDialogDiv.find("button").attr("class", "button").addClass(classNameBasedOfIndex);
            saveDialogDiv.find(".ui-dialog-buttonpane").attr("class", "padding-left");
            saveDialogDiv.find(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
            saveDialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
            saveDialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
        }
    });
    $("#editDialog").css("min-height", "").dialog("open");
}

function classNameBasedOfIndex(index) {
    if (index != 0) {
        return "secondary"
    } else {
        return "";
    }
}

function show_delete_dialog() {
    $("#deleteDialog").removeClass("hide").dialog({
        'modal': true,
        'width': 500,
        'resizable': false,
        'autoOpen': false,
        'buttons': [{
            text: i18next.t("common:Confirm"),
            click: function () {
                var cohortId = $("#cohort-id").val();
                var deleteURL = lwbSettings.deleteURL + "?cohortId=" + cohortId;

                $.ajax({
                    'url': deleteURL,
                    'type': "POST",
                    'success': function () {
                        var studyId = $("#study-id").val();
                        var studyURL = lwbSettings.studyURL + "?studyId=" + studyId;
                        window.location.href = studyURL;
                    }
                })
            }
        },
            {
                text: i18next.t("common:Cancel"),
                click: function () {
                    $("#deleteDialog").dialog("close");
                }
            }
        ],
        'open': function () {
            var deleteDialog = $("#deleteDialog");
            deleteDialog.find(".form-text-error").remove();
            deleteDialog.find(".form-field-error").removeClass("form-field-error");

            var saveDialogDiv = deleteDialog.closest(".ui-dialog");
            var titlebar = saveDialogDiv.find(".ui-dialog-titlebar").attr("class", "primary title-bar").remove();

            saveDialogDiv.find("button").attr("class", "button")
                .addClass(function (index) {
                    var className = "";
                    if (index == 0) className += " alert";
                    else className += " secondary";
                    return className;
                });
            saveDialogDiv.find(".ui-dialog-buttonpane").attr("class", "padding-left");
            saveDialogDiv.find(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
            saveDialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
            saveDialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
        }
    });
    $("#deleteDialog").css("min-height", "").dialog("open");
}

function recalculate_cohort() {
    var cohortId = $("#cohort-id").val();
    open_recalculate_dialog(cohortId);
}

function downloadCohort() {
    var cohortId = $("#cohort-id").val();
    downloadCohortFilters(cohortId);
}

function update_facts_size(event) {
    $(".page-facts li.is-active").removeClass("is-active");
    $(this).addClass("is-active");
    searchFacts();
}