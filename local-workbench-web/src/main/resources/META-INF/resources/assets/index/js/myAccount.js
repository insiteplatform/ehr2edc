$(document).ready(function () {
    initTabs();
    $("#profSubmit").on("click", saveProfile);
    $("#profReset").on("click", resetForm);
    $("#pwdSubmit").on("click", savePassword);
    $("#pwdReset").on("click", resetForm);
    $("#phySubmit").on("click", savePhysician);
    $("#phyReset").on("click", resetForm);
    $("#secSubmit").on("click", saveSecurityQuestion);
    $("#secReset").on("click", resetForm);
});

function initTabs() {
    openInitialTab();
    $(".sidebar-tabs").on("click", ".sidebar-tab", sidebarTabClickHandler);
}

function openInitialTab() {
    if (location.hash) {
        openTab(location.hash.substr(1));
    }
}

function sidebarTabClickHandler() {
	var tabClicked = $(this).attr("tab");
    openTab(tabClicked);
    history.replaceState(undefined, undefined, '#' + tabClicked);
}

function openTab(tabId) {
    hideCurrentTab();
    showTab(tabId);
}

function hideCurrentTab() {
    $(".tab-item").addClass("hide");
    $(".sidebar-tab.is-active").removeClass("is-active");
}

function showTab(tabId) {
	$("#" + tabId).removeClass("hide");
	$(".sidebar-tab[tab=" + tabId + "]").addClass("is-active");
}

function saveProfile() {
	resetValidation();
	var username = $("#username").val();
	var email = $("#email").val();
	if (isValid(username, email)) {
		var saveData = {
			'username': username,
			'email': email.toLowerCase()
		};
		saveUser(updateProfileURL, saveData, successHandler, errorHandler);
	}

	function isValid(username, email) {
		var isValid = true;
		if (!username.length) {
			$("#username").addClass("form-field-error");
			$("<p>Field required.</p>").addClass("form-text-error").insertAfter($("#username"));
			isValid = false;
		}
		if (!isValidEmail(email)) {
			$("#email").addClass("form-field-error");
			$("<p>Invalid E-mail.</p>").addClass("form-text-error").insertAfter($("#email"));
			isValid = false;
		}
		return isValid;
	}

	function isValidEmail(email) {
        var emailReg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return emailReg.test(email);
    }

	function successHandler() {
		showMessage($(".profile-success"));
	}

	function errorHandler() {
		showMessage($(".profile-error"));
	}
}

function savePassword() {
	resetValidation();
	var pwdOld = $("#pwdOld").val();
	var pwdNew1 = $("#pwdNew1").val();
	var pwdNew2 = $("#pwdNew2").val();
	if (isValid(pwdOld, pwdNew1, pwdNew2)) {
		var saveData = {
			'oldPasswd': pwdOld,
			'newPasswd': pwdNew1
		};
		saveUser(updatePasswordURL, saveData, successHandler, errorHandler);
	}

	function isValid(pwdOld, pwdNew1, pwdNew2) {
		var isValid = true;
		if (!pwdOld.length) {
			$("#pwdOld").addClass("form-field-error");
			$("<p>Field required.</p>").addClass("form-text-error").insertAfter($("#pwdOld"));
			isValid = false;
		}
		if (!pwdNew1.length || !pwdNew2.length || pwdNew1 != pwdNew2) {
			$("#pwdNew1").addClass("form-field-error");
			$("#pwdNew2").addClass("form-field-error");
			$("<p>New passwords do not match</p>").addClass("form-text-error").insertAfter($("#pwdNew2"));
			isValid = false;
		}
		return isValid;
	}

	function successHandler() {
		showMessage($(".password-success"));
	}

	function errorHandler(jqXHR) {
		if (jqXHR.responseText !== "") {
			showMessage($(".password-error"), jqXHR.responseText);
		} else {
			showMessage($(".password-error"), "Unexpected error while updating your password.");
		}
	}
}

function savePhysician() {
	resetValidation();
	var provId = $("#phyProvider").val();
	if (isValid(provId)) {
		var saveData = {
			'providerId': provId,
			'isDefault': $("#phyDefault")[0].checked
		};
		saveUser(updatePhysicianURL, saveData, successHandler, errorHandler);
	}

	function isValid(provId) {
		var isValid = true;
		if (!provId.length) {
			$("#phyProvider").addClass("form-field-error");
			$("<p>Field required.</p>").addClass("form-text-error").insertAfter($("#phyProvider"));
			isValid = false;
		}
		return isValid;
	}

	function successHandler() {
		showMessage($(".physician-success"));
	}

	function errorHandler() {
		showMessage($(".physician-error"));
	}
}

function saveUser(url, saveData, successHandler, errorHandler) {
    $.ajax({
        'url': url,
        'method': "POST",
        'data': JSON.stringify(saveData),
        'contentType': "application/json",
        'success': successHandler,
        'error': errorHandler,
		'ignoreGlobalError': true
    })
}

function saveSecurityQuestion() {
    resetValidation();
    var secQuestionId = $("#secQuestion").val();
    var secAnswer = $("#secAnswer").val();
    if (isValid(secQuestionId, secAnswer)) {
        var secData = {
            'securityQuestionId': secQuestionId,
            'securityAnswer': secAnswer
        }
        doSaveSecurityQuestion(secData);
    }

    function isValid(secQuestionId, secAnswer) {
		var isValid = true;
		if (!secQuestionId.length) {
			$("#secQuestion").addClass("form-field-error");
			$("<p>Field required.</p>").addClass("form-text-error").insertAfter($("#secQuestion"));
			isValid = false;
		}
		if (!secAnswer.length) {
			$("#secAnswer").addClass("form-field-error");
			$("<p>Field required.</p>").addClass("form-text-error").insertAfter($("#secAnswer"));
			isValid = false;
		}
		return isValid;
    }

    function doSaveSecurityQuestion(secData) {
		$.ajax({
			'url': securityURL,
			'method': "POST",
			'data': JSON.stringify(secData),
			'contentType': "application/json",
			'success': successHandler,
			'error': errorHandler,
			'ignoreGlobalError': true
		});
    }

    function successHandler() {
		showMessage($(".secSuccess"));
	}

	function errorHandler(jqXHR) {
		if (jqXHR.responseText !== "") {
			showMessage($(".secError"), jqXHR.responseText);
		} else {
			showMessage($(".secError"), "Unexpected error while updating your security question.");
		}
	}
}

function showMessage(element, message) {
	setMessage(element, message);
	show(element);
	hideAfterDelayInMillis(element, 5000);
}

function setMessage(element, message) {
	if (typeof message !== 'undefined') {
		element.text(message);
	}
}

function show(element) {
	element.removeClass("hide");
}

function hideAfterDelayInMillis(element, delay) {
	setTimeout(function () {
		element.addClass("hide");
	}, delay)
}

function resetForm() {
    $("form")[0].reset();
    resetValidation();
}

function resetValidation() {
    $(".form-field-error").removeClass("form-field-error margin-bottom");
    $(".form-text-error").remove();
}