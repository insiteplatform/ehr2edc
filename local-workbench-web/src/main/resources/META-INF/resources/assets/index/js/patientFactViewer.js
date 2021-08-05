function hasGroupsToRender(data) {
	return data != null && data.hasOwnProperty("factGroups") && data.factGroups.length != 0;
}

function renderFactsPane(data, element, patientId) {
    $.each(data.factGroups, function(groupIndex, groupItem) {
		var groupDiv = $("<div>").addClass("fact-group border-bottom padding-rl margin-small-bottom").appendTo(element);
		$("<h5>").addClass("border-bottom-none margin-small-bottom").html(groupItem.header).appendTo(groupDiv);
		var tableDiv = $("<div>").addClass("site-statuses padding-rl").appendTo(groupDiv);
		renderFactsGroupItem(groupItem, tableDiv);
	});
}

function renderFactsGroupItem(groupItem, groupDiv) {
	var groupTable = $("<table>").addClass("border-none").appendTo(groupDiv);
    renderFactsGroupItemHeader(groupTable);
    renderFactsGroupItemBody(groupTable, groupItem);
}

function renderFactsGroupItemHeader(groupTable) {
    var groupHeader = $("<thead>").appendTo(groupTable)
	var groupHeaderRow = $("<tr>").appendTo(groupHeader);
	$("<th>").addClass("small-6").html("Observation").appendTo(groupHeaderRow);
	$("<th>").addClass("small-3").html("Date").appendTo(groupHeaderRow);
	$("<th>").addClass("small-3").html("Source").appendTo(groupHeaderRow);
}

function renderFactsGroupItemBody(groupTable, groupItem) {
    var groupBody = $("<tbody>").appendTo(groupTable);
    $.each(groupItem.observations, function(observationIndex, observation) {
        var observationRow = $("<tr>").appendTo(groupBody);
        addObservationToRow(observationRow, observation);
    });
}

function addObservationToRow(observationRow, observation) {
    var observationCell = $("<td>").appendTo(observationRow);
    var observationList = $("<ul>").addClass("margin-left no-bullet");
    $.each(observation.facts, function(factIndex, fact) {
        if (factIndex == 0) {
            observationCell.html(getObservationStringFromFact(fact));
            $("<td>").html(fact.startDate).appendTo(observationRow);
            $("<td>").html(fact.sourceName).appendTo(observationRow);
        } else {
            $("<li>").html(getObservationAttributeStringFromFact(fact)).appendTo(observationList);
        }
    });
    if (observationList.children().length) {
        observationList.appendTo(observationCell);
    }
}

function getObservationStringFromFact(fact) {
	var observationString = fact.observationName;
	if (fact.observationAttribute) {
		observationString += " (" + fact.observationAttribute + ")";
	}
	if (fact.observationValue && fact.observationValue != "") {
		observationString += ": <strong>" + fact.observationValue + "</strong>";
	}
	return observationString;
}

function getObservationAttributeStringFromFact(fact) {
	var observationString = "";
	if (fact.observationAttribute) {
		observationString += fact.observationAttribute;
	}
	if (fact.observationAttribute && fact.observationValue && fact.observationValue !== "") {
		observationString += ":"
	}
	if (fact.observationValue && fact.observationValue !== "") {
		observationString += " <strong>" + fact.observationValue + "</strong>";
	}
	return observationString;
}
