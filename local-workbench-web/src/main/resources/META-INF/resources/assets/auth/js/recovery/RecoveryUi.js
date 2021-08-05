function RecoveryUi() {
    var $submitButton = $("#submitButton");
    var $loadingLabel = $("#loading");
    var $successAlert = $("#correct-answer-alert");
    var $errorAlert = $("#error-alert");

    this.getSecurityQuestionLabel = getSecurityQuestionLabel;
    this.getEmailField = getEmailField;
    this.getSecurityAnswerField = getSecurityAnswerField;
    this.getSubmitButton = getSubmitButton;
    this.getSecurityQuestion = getSecurityQuestion;
    this.resetValidation = resetValidation;
    this.getNoQuestionAlert = getNoQuestionAlert;
    this.addFieldError = addFieldError;
    this.hideAllAlerts = hideAllAlerts;
    this.onRecoveryLoading = onRecoveryLoading;
    this.onRecoverySuccess = onRecoverySuccess;
    this.onRecoveryError = onRecoveryError;

    function resetValidation() {
        $(".form-field-error").removeClass("form-field-error");
        $(".form-text-error").remove();
    }

    function getNoQuestionAlert() {
        return $("#no-question-alert");
    }

    function getSecurityQuestionLabel() {
        return $("#security-question-label");
    }

    function getSecurityQuestion() {
        return $(".question-security");
    }

    function getEmailField() {
        return $("#email");
    }

    function getSecurityAnswerField() {
        return $("#securityAnswer");
    }

    function getSubmitButton() {
        return $submitButton;
    }

    function hideAllAlerts() {
        $(".messages div").addClass("hide");
    }

    function addFieldError(text, field) {
        $("<p>").addClass("form-text-error").text(text).insertAfter(field);
    }

    function onRecoveryLoading() {
        $submitButton.addClass("disabled");
        $loadingLabel.removeClass("hide");
    }

    function onRecoverySuccess() {
        $successAlert.removeClass("hide");
        $loadingLabel.addClass("hide");
    }

    function onRecoveryError(errorText) {
        $errorAlert.text(errorText).removeClass("hide");
        $submitButton.removeClass("disabled");
        $loadingLabel.addClass("hide");
    }
}