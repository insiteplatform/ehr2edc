$(document).ready(function() {
    var $message = $("#password-expiring-message");
    var $actionLink = $message.find(".action-link");

    init();

    function init() {
        if (!hasAlreadyBeenShownThisSession() && !isCurrentPagePasswordPage()) {
            showMessage();
            hideMessageOnAction();
        }
    }

    function isCurrentPagePasswordPage() {
        var passwordPageUrl = $actionLink.attr("href");
        return location.href.indexOf(passwordPageUrl) !== -1
    }

    function hasAlreadyBeenShownThisSession() {
        return sessionStorage.getItem('passwordExpiringShown');
    }

    function setAlreadyShownThisSession() {
        sessionStorage.setItem('passwordExpiringShown', true);
    }

    function showMessage() {
    	$message.addClass("is-active");
    	setAlreadyShownThisSession();
    }

    function hideMessageOnAction() {
        $actionLink.on("click", function() {
            $message.removeClass("is-active");
        });
    }
});