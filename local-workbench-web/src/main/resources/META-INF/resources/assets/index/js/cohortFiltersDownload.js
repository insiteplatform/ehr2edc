function downloadCohortFilters(cohortId) {
    var url = replacePathVariable(lwbSettings.downloadFiltersURL, "cohortId", cohortId);
    $.ajax({
        'url': url,
        'type': "GET",
        'success': function() {
            location.replace(url);
        },
        'error': function() {
            showErrorDialog("Failed downloading cohort filters");
        }
    });
}