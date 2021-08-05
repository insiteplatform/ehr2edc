var pageLoadPopupFlag;
var previousNotificationsLength;
var firstPageLoad;

$(document).ready(function () {

    $(".header li.notifications-menu").on("click", function () {
        var notifyMenu = $("#notifications-menu .notifications-menu").toggleClass("is-active");

        if (notifyMenu.hasClass("is-active")) {
            notifyMenu.css("visibility", "visible");
        }
        else {
            notifyMenu.css("visibility", "hidden");
        }

    });
    $("body").on("click", function (event) {
        if (!$(event.target).closest(".notifications-menu").length) {
            $("#notifications-menu .notifications-menu").css("visibility", "hidden").removeClass("is-active");
        }
    });
    pageLoadPopupFlag = false;
    firstPageLoad = true;
    previousNotificationsLength = 0;
    getNotifications();
})

function getNotifications() {
    $.ajax({
        url: NEW_NOTIFICATIONS_URL,
        dataType: "json",
        type: "GET",
        timeout: notificationPollingInterval,
        success: function (data) {
            constructNotificationMenu(data);
        },
        error: function () {

        },
        ignoreGlobalError: true
    });
    setTimeout(getNotifications, notificationPollingInterval);
}

function constructNotificationMenu(notifications) {
    var notifications_ul = $("#notifications-menu .notifications-menu .notifications-list").empty();
    if (notifications.length > 0) {
        setPopupFlagForNotificationLength(notifications.length);

        for (var index in notifications) {
            if (notifications.hasOwnProperty(index)) {
                var notification_li = constructNotificationMenuItem(notifications[index]);
                notifications_ul.append(notification_li);
            }
        }
        $(".notifications-menu .notifications").addClass("notifications-new");
        popupNotifications(notificationPopupDuration);
    }
    else {
        previousNotificationsLength = 0;
        var emptyNotifications = $("<li>").html("You have no recent notifications.");
        notifications_ul.append(emptyNotifications);
        $(".notifications-menu .notifications").removeClass("notifications-new");
    }
}

function setPopupFlagForNotificationLength(notificationDataLength) {
    if (notificationDataLength > previousNotificationsLength) {
        if (firstPageLoad !== true) {
            pageLoadPopupFlag = true;
        } else {
            firstPageLoad = false;
        }
        previousNotificationsLength = notificationDataLength;
    }
}

function popupNotifications(timerInMs) {
    if (pageLoadPopupFlag) {
        var notifyMenu = $("#notifications-menu .notifications-menu").toggleClass("is-active");
        if (notifyMenu.hasClass("is-active")) {
            notifyMenu.css("visibility", "visible");
        }
        setTimeout(function () {
            $("#notifications-menu .notifications-menu").toggleClass("is-active");
            notifyMenu.css("visibility", "hidden");
        }, timerInMs);
        pageLoadPopupFlag = false;
    }
}

function constructNotificationMenuItem(notification) {
    var icon = constructNotificationIcon(notification.type);
    var subject = $("<div>").addClass("notification-subject").html(notification.contents[0].subject);
    var body = $("<div>").addClass("notification-body").html(notification.contents[0].body);
    var sendingDate = $("<div>").addClass("notification-date").html(formatDate(notification.sendingDate));
    var notification_li = $("<li>").append(icon).append(subject).append(body).append(sendingDate);
    notification_li.on("click", function () {
        notificationClicked(notification);
    });
    return notification_li;
}

function constructNotificationIcon(notificationType) {
    var iconTag = $("<i>").addClass("fa notification-type");
    switch (notificationType) {
        case "Feasibility":
            iconTag.addClass("fa-list-ul");
            break;
        case "Recruitment":
            iconTag.addClass("fa-users");
            break;
        case "Admin":
            iconTag.addClass("fa-wrench");
            break;
        default:
            iconTag.addClass("fa-star");
            break;
    }
    return iconTag;
}

function notificationClicked(notification) {
    var urlWithParameter = replacePathVariable(READ_NOTIFICATION, "notificationId", notification.id);
    $.ajax({
        url: urlWithParameter,
        dataType: "json",
        type: "POST",
        success: function () {
            getNotifications();
        },
        error: function () {

        }
    });
}




