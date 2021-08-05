$(document).ready(function() {
	
	$(".user-roles").on("click", function() {
		var tableRow = $(this).closest("tr");
        var userRoles = getActiveUserRoles(tableRow);
		var roleDialog = $("#roleDialog");
		roleDialog.dialog({
			modal: true,
			resizable: false,
			width: 400,
			autoOpen: false,
			buttons: [{
					text: i18next.t("changeRoles.confirm"),
					click: function() {
						var userId = roleDialog.find(".role-user-id").val();
						var assignData = {
							'userId': userId,
							'roles': []
						};
						var activeRoles = [];
						roleDialog.find("li input.role-select").each(function() {
							if ($(this)[0].checked) {
								assignData.roles.push($(this).val());
								activeRoles.push($(this).parent().find(".role-type").val());
							}
						});
						$.ajax({
							'url': assignURL,
							'method': "POST",
							'data': JSON.stringify(assignData),
							'contentType': "application/json",
							'success': function() {
								var roleRow = $(".table-users tr .user-id[value="+userId+"]").closest("tr");
								roleRow.find(".active-user-roles").val("[" + activeRoles + "]");
								roleDialog.dialog("close");
							}
						});
					}
				},
				{
					text: i18next.t("common:Cancel"),
					click: function() {
						roleDialog.dialog("close");
					}
				}
			],
			open: function() {
				var roleDialogDiv = roleDialog.closest(".ui-dialog");
				var closeIcon = $("<a>").html("<i class='fa fa-fw fa-close'></i>")
				.on("click", function() {
					$( "#roleDialog" ).dialog("close");
				});
				
				var titlebar = roleDialogDiv.find(".ui-dialog-titlebar").attr("class", "primary title-bar").empty();
				$("<div>").html(i18next.t("changeRoles.title")).addClass("center title").appendTo(titlebar);
				$("<span>").addClass("right").append(closeIcon).appendTo(titlebar);
				
				roleDialogDiv.find("button").attr("class", "button")
				.addClass(function(index) {
					var className = "";
					if (index != 0) className += " cancel-changes secondary";
					else className += " save-changes";
					return className;
				});
				roleDialogDiv.find(".ui-dialog-buttonpane").attr("class", "padding-left");
				roleDialogDiv.find(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
				roleDialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
				roleDialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
			}
		});
		roleDialog.find("li").each(function() {
			var roleType = $(this).find(".role-type").val();
			if (userRoles.indexOf(roleType) != -1) {
				$(this).find("input.role-select").attr('checked', true);
			}
			else {
				$(this).find("input.role-select").attr('checked', false);
			}
		});
		roleDialog.find(".role-user-id").val(tableRow.find(".user-id").val());
		roleDialog.removeClass("hide");
		roleDialog.dialog("open");
	});
	$(".user-delete").on("click", function() {
		var tableRow = $(this).closest("tr");
		
		tableRow.find(".user-delete").addClass("hide");
		tableRow.find(".delete-confirm-cancel").removeClass("hide");
	});
	$(".delete-confirm-cancel .cancel-button").on("click", function() {
		var tableRow = $(this).closest("tr");
		
		tableRow.find(".user-delete").removeClass("hide");
		tableRow.find(".delete-confirm-cancel").addClass("hide");
	});
	$(".delete-confirm-cancel .confirm-button").on("click", function() {
		var tableRow = $(this).closest("tr");
		var userId = tableRow.find(".user-id").val();
		
		var deleteUsrUrl = deleteUrl + "?userId=" + userId;
		$.ajax({
			'url': deleteUsrUrl,
			'method': "POST",
			'success': function() {
				tableRow.remove();
			}
		});
	});
	$(".user-recover").on("click", function() {
		var tableRow = $(this).closest("tr");
		var userId = tableRow.find(".user-id").val();
		
		var resetUsrUrl = recoverUrl + "?userId=" + userId;
		$.ajax({
			'url': resetUsrUrl,
			'method': "POST",
			'success': function() {
			    showSuccessDialog("User recovered successfully");
				tableRow.find(".user-status").html("<small>Recovering</small>");
			},
			'error': function() {
			    showErrorDialog("Something went wrong during the user recovery");
			},
			'ignoreGlobalError': true
		});
	});
	$(".user-reinvite").on("click", reinviteUser);

	$(".sidebar-tabs li").on("click", function() {
		$(".sidebar-tabs li.is-active").removeClass("is-active");
		$(this).addClass("is-active");
		filterUsers();
	});
	
	$(".account-status").on("click", "a", function() {
		var tableRow = $(this).closest("tr");
		tableRow.find(".account-status").addClass("hide");
		tableRow.find(".user-accept-decline").removeClass("hide");
		$(this).closest("td").css("padding-top", "1em")
	});
	
	$(".user-accept-decline").on("click", ".user-decline", function() {
		var tableRow = $(this).closest("tr");
		
		tableRow.find(".account-status").removeClass("hide");
		tableRow.find(".user-accept-decline").addClass("hide");
		$(this).closest("td").css("padding-top", "")
	})
	.on("click", ".user-accept", function() {
		var tableRow = $(this).closest("tr");
		var tableCell = $(this).closest("td");
		
		tableRow.find(".account-status").removeClass("hide");
		tableRow.find(".user-accept-decline").addClass("hide");
		tableCell.css("padding-top", "");
		
		var userId = tableRow.find(".user-id").val();
		var usrAcceptUrl = acceptUrl + "?userId=" + userId;
		$.ajax({
			'url': usrAcceptUrl,
			'method': "POST",
			'success': function() {
				tableCell.html("<small>Invited</small>")
			}
		})
	});
	
	$(".search-user").on("keydown change", function() {
		setTimeout(filterUsers, 10);
	});
	
	$(".invite-user").on("click", showInvitePane);
	$(".invite-menu")
	.on("click", ".close-button", hideInvitePane)
	.on("click", ".invite-cancel", hideInvitePane)
	.on("click", ".invite-submit", doInviteUser);
});

function showInvitePane() {
	resetInviteValidation();
	
	$(".invite-username").val("");
	$(".invite-mail").val("");
	
	$(".filter-users").addClass("hide");
	$(".invite-menu").removeClass("hide");
}

function hideInvitePane() {
	$(".filter-users").removeClass("hide");
	$(".invite-menu").addClass("hide");
	
}

function doInviteUser() {
	resetInviteValidation();
	var userName = $(".invite-username").val();
	var mail = $(".invite-mail").val();
	isValid = true;
	
	if (userName == '') {
		$(".invite-username").addClass("form-field-error");
		isValid = false;
	}
	if (!validateEmail(mail)) {
		$(".invite-mail").addClass("form-field-error");
		isValid = false;
	}
	
	if (isValid) {
		var inviteData = {
			'username': userName,
			'mail': mail.toLowerCase(),
			'roles': []
		}
		
		$(".invite-roles li input.invite-role").each(function() {
			if ($(this)[0].checked) {
				inviteData.roles.push($(this).val());
			}
		});
		
		$.ajax({
			'url': inviteURL,
			'type': "POST",
			'contentType': "application/json",
			'data': JSON.stringify(inviteData),
			'success': function() {
				location.reload();
			},
			'error': function(request, status, error) {
				var errorText = "Something went wrong during the invitation";
				if (request.status == 406) {
					errorText = "This user already exists"
				}
				
				$(".user-invited-error .error-message").text(errorText);
				$(".user-invited-error").addClass("is-active");
				setTimeout(function() {
					$(".user-invited-error").removeClass("is-active");
				}, 3000);
			},
			'ignoreGlobalError': true
		});
	}
}


function validateEmail(email) {
    var emailReg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return emailReg.test( email );
}

function resetInviteValidation() {
	$(".invite-username").removeClass("form-field-error");
	$(".invite-mail").removeClass("form-field-error");
}

function reinviteUser(event) {
    var tableRow = $(event.target).closest("tr");
    var userId = tableRow.find(".user-id").val();
    $.ajax({
        'url': reinviteUrl + "?userId=" + userId,
        'method': "POST",
        'success': function() {
            showSuccessDialog("User invitation message sent");
            tableRow.find(".user-status").html("<small>Invited</small>");
        },
        'error': function() {
            showErrorDialog("Something went wrong during the user recovery");
        },
        'ignoreGlobalError': true
    });
}

function filterUsers() {
	var selectedRoleType = $(".sidebar-tabs li.is-active").attr("role");
	var searchStr = $(".search-user").val().toLowerCase();
	
	if (selectedRoleType || searchStr) {
		$(".table-overview tbody tr").each(function() {
            var userRoles = getActiveUserRoles($(this));
			var userName = $(this).find(".user-name").text().toLowerCase();

			var valid = true;
			valid = valid && (!selectedRoleType || userRoles.indexOf(selectedRoleType) != -1);
			valid = valid && (!searchStr.length || userName.indexOf(searchStr) != -1);
			
			if (valid) {
				$(this).removeClass("hide");
			}
			else {
				$(this).addClass("hide");
			}
		});
	}
	else {
		$(".table-overview tbody tr").removeClass("hide");
	}
}

function getActiveUserRoles(tableRow) {
    var userRolesStr = tableRow.find(".active-user-roles").val();
    return userRolesStr.substr(1, userRolesStr.length - 2).replace(/\s/g, "").split(",");
}