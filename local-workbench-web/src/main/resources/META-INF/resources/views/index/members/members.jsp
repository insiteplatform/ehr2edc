<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="grid-content collapse menu-left vertical shrink ng-scope">
	<div class="sidebarTools slideInRight filter-users">
		<div class="sidebar-search-form padding">
			<span class="inline-label margin-none">
				<spring:message code="members.sidebar.searchPlaceholder" var="searchPlaceholder"/>
				<input type="search" placeholder="${searchPlaceholder}" class="search-user">
			</span>
		</div>
		<ul class="sidebar-tabs">
			<li class="is-active"><a><spring:message code="members.sidebar.all"/></a></li>
			<c:forEach var="role" items="${roles}">
				<li role="${role.name()}">
				    <a>${role.label}</a>
				</li>
			</c:forEach>
		</ul>
		<div class="sidebar-create padding">
			<a class="invite-user button success expand"> <i class="fa fa-user-plus"></i> <spring:message code="members.sidebar.inviteNewUser"/></a>
		</div>
	</div>
	<div class="invite-menu vertical shrink padding slideInLeft hide">
	    <a class="close-button"><i class="fa fa-close"></i></a>
	    <h3 class="margin-bottom"><spring:message code="members.sidebar.invite.title"/></h3>
	    <form class="ng-pristine ng-valid">
	    	<label> <spring:message code="members.sidebar.invite.username"/>*
	        	<input type="text" placeholder="user name" class="invite-username">
	      	</label>
	      	<label> <spring:message code="members.sidebar.invite.emailAddress"/>*
	        	<input type="email" placeholder="email@address.com" class="invite-mail">
	      	</label>
	      	<label> <spring:message code="members.sidebar.invite.roles"/>
				<ul class="invite-roles no-bullet margin-tb">
	 				<c:forEach var="role" items="${roles}">
						<li>
							<input type="checkbox" value="${role.name()}" id="invite-role-${role.name()}" class="invite-role" />
							<label for="invite-role-${role.name()}">${role.label}</label>
						</li>
					</c:forEach>
				</ul>
	     	</label>
	    </form>
	    <a class="button invite-submit"><spring:message code="members.sidebar.invite.invite"/></a>
	    <a class="button secondary invite-cancel"><spring:message code="common.Cancel"/></a>
	 </div>
</div>
<div id="rightPanel" class="grid-block ng-scope">
	<table class="table-overview table-users">
		<thead>
			<tr>
				<th><spring:message code="members.table.name"/></th>
				<th><spring:message code="members.table.email"/></th>
				<th class="small-2"><spring:message code="members.table.accountStatus"/></th>
				<th class="small-1 text-right"><spring:message code="members.table.roles"/></th>
				<th class="small-1 text-right"><spring:message code="members.table.actions"/></th>
				<th class="small-1 text-right"><spring:message code="members.table.delete"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="userInfo" items="${users}">
				<tr>
					<td>
						<input type="hidden" name="user-${userInfo.id}-id" class="user-id" value="${userInfo.id}" />
						<input type="hidden" name="user-${userInfo.id}-roles" class="active-user-roles" value="${userInfo.roles}" />
						<div class="user-name">${userInfo.username}
							<c:if test="${userInfo.id eq user.id}"> <small>(you)</small></c:if>
						</div>
					</td>
					<td>${userInfo.email}</td>
					<td class="user-status">
                        <c:choose>
                            <c:when test="${userInfo.status == 'PENDING'}">
                                <small class="account-status"><a><spring:message code="members.table.${userInfo.status}"/></a></small>
                                <div class="user-accept-decline hide">
                                    <span class="user-accept button warning tiny margin-right-none">
                                        <i class="fa fa-check"></i><spring:message code="members.table.accept"/>
                                    </span>
                                    <span class="user-decline button tiny secondary">
                                        <i class="fa fa-close"></i><spring:message code="common.cancel"/>
                                    </span>
                                </div>
                            </c:when>
                            <c:otherwise><small><spring:message code="members.table.${userInfo.status}"/></small></c:otherwise>
                        </c:choose>
					</td>
					<td class="text-right">
						<a class="group-icon user-roles"><i class="fa fa-users"></i></a>
					</td>
					<td class="text-right">
						<c:if test="${userInfo.status.recoverable && userInfo.id ne user.id}">
							<a class="user-recover" title="Send recovery mail"><i class="fa fa-key"></i></a>
						</c:if>
						<c:if test="${userInfo.status.reinvitable}">
                            <a class="user-reinvite" title="Resend invitation mail"><i class="fa fa-user-plus"></i></a>
                        </c:if>
					</td>
					<td class="text-right">
						<c:if test="${userInfo.id ne user.id}">
							<a class="delete-icon user-delete"><i class="fa fa-trash-o"></i></a>
							<ul class="button-group segmented tiny delete-confirm-cancel confirm-cancel hide">
								<li class="confirm confirm-button">
									<a href="#"><spring:message code="common.confirm"/></a>
								</li>
								<li class="cancel cancel-button">
									<a href="#"><spring:message code="common.cancel"/></a>
								</li>
							</ul>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<div id="roleDialog" class="hide">
		<input type="hidden" class="role-user-id" />
		<ul class="no-bullet margin-tb">
			<c:forEach var="role" items="${roles}">
				<li>
				    <input class="role-type" type="hidden" value="${role.name()}"/>
					<input type="checkbox" value="${role.name()}" id="role-${role.name()}" class="role-select" />
					<label for="role-${role.name()}">${role.label}</label>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>

<div class="static-notification top-right user-invited-message">
	<a href="#" class="close-button">&times;</a>
	<div class="notification-content">
		<p class="ng-scope padding-rl"><i class="fa fa-info-circle"></i> Invitation sent successfully</p>
	</div>
</div>

<div class="static-notification top-right warning user-invited-error">
	<a href="#" class="close-button">&times;</a>
	<div class="notification-content">
		<p class="ng-scope padding-rl"><i class="fa fa-info-circle"></i> <span class="error-message"></span></p>
	</div>
</div>