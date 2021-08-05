function RecoveryController() {
    var ui = new RecoveryUi();
    var recoveryService = new RecoveryService(ui);

    this.initHandlers = initHandlers;

    function initHandlers() {
        setSubmitClickHandler();
        setEmailChangeHandler();
    }

    function setSubmitClickHandler() {
        ui.getSubmitButton().on("click", function () {
            ui.resetValidation();
            if (ui.getSecurityQuestionLabel().hasClass("hide")) {
                recoveryService.checkMail();
            }
            else {
                recoveryService.checkAnswer();
            }
        });
    }

    function setEmailChangeHandler() {
        ui.getEmailField().on("change", function () {
            ui.getSecurityQuestionLabel().addClass("hide");
        });
    }
}