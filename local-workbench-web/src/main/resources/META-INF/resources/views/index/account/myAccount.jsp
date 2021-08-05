<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes"%>

<div id="leftPanel" class="grid-content collapse menu-left vertical shrink ng-scope">
	<ul class="sidebar-tabs">
	    <li class="sidebar-tab is-active" tab="profile"><a><spring:message code="myAccount.sidebar.profile"/></a></li>
	    <li class="sidebar-tab" tab="password"><a><spring:message code="myAccount.sidebar.changePassword"/></a></li>
	    <li class="sidebar-tab" tab="security"><a><spring:message code="myAccount.sidebar.securityQuestion"/></a></li>
	    <li class="sidebar-tab" tab="physician"><a><spring:message code="myAccount.sidebar.treatingPhysician"/></a></li>
	</ul>
</div>
<div id="rightPanel" class="grid-block vertical">
	<form>
		<div id="profile" class="grid-block small-10 medium-8 large-4 vertical padding-large-left ng-pristine ng-valid ng-scope tab-item">
			<h3 class="padding-medium-tb"><spring:message code="myAccount.profile.title"/></h3>
			<label>
				<spring:message code="myAccount.profile.username"/>*
				<input type="text" name="username" id="username" value="${account.userName}" />
			</label>
			<label>
				<spring:message code="myAccount.profile.email"/>*
				<c:choose>
				    <c:when test="${canManage}">
				        <input type="email" name="email" id="email" value="${account.email}" />
				    </c:when>
				    <c:otherwise>
                        <input type="email" name="email" id="email" value="${account.email}" disabled="disabled" class="disabled" />
                        <p><spring:message code="myAccount.profile.email.admin" arguments="${fn:join(adminMails,';')}"/></p>
				    </c:otherwise>
				</c:choose>
			</label>
			<div class="grid-content collapse shrink">
				<a id="profSubmit" class="button"><spring:message code="common.Save"/></a>
				<a id="profReset" class="button secondary"><spring:message code="common.discardChanges"/></a>
			</div>
			<div class="profile-success alert-box success hide margin-tb">Your profile has been updated successfully.</div>
			<div class="profile-error alert-box warning hide margin-tb">Unexpected error while updating your profile.</div>
		</div>
		<div id="password" class="grid-block small-10 medium-8 large-4 vertical padding-large-left ng-pristine ng-valid ng-scope tab-item hide">
			<h3 class="padding-medium-tb"><spring:message code="myAccount.changePassword.title"/></h3>
			<c:if test="${not empty account.passwordExpiryDate}">
			    <p><i class="fa fa-info-circle"></i> Your current password expires on <fmt:formatDate type="date" pattern="d MMMM yyyy" value="${account.passwordExpiryDate}"/>.</p>
			</c:if>
			<label>
				<spring:message code="myAccount.changePassword.current"/>*
				<spring:message code="myAccount.changePassword.currentPlaceholder" var="changePasswordCurrentPlaceholder"/>
				<input id="pwdOld" type="password" placeholder="${changePasswordCurrentPlaceholder}">
			</label>
			<label>
				<spring:message code="myAccount.changePassword.enterNew"/>*
				<spring:message code="myAccount.changePassword.enterNewPlaceholder" var="changePasswordEnterNewPlaceholder"/>
				<input id="pwdNew1" type="password" placeholder="${changePasswordEnterNewPlaceholder}">
			</label>
			<label>
				<spring:message code="myAccount.changePassword.repeatNew"/>*
				<spring:message code="myAccount.changePassword.repeatNewPlaceholder" var="changePasswordRepeatNewPlaceholder"/>
				<input id="pwdNew2" type="password" placeholder="${changePasswordRepeatNewPlaceholder}">
			</label>
			<div class="grid-content collapse shrink">
				<a id="pwdSubmit" class="button"><spring:message code="myAccount.changePassword.confirm"/></a>
				<a id="pwdReset" class="button secondary"><spring:message code="common.discardChanges"/></a>
			</div>
			<div class="password-success alert-box success hide margin-tb">Your password has been updated successfully.</div>
			<div class="password-error alert-box warning hide margin-tb" style="white-space: pre-wrap;"></div>
		</div>
		<div id="security" class="grid-block small-10 medium-8 large-4 vertical padding-large-left ng-pristine ng-valid ng-scope tab-item hide">
			<h3 class="padding-medium-tb"><spring:message code="myAccount.securityQuestion.title"/></h3>
			<label>
				<spring:message code="myAccount.securityQuestion.securityQuestion"/>*
				<select id="secQuestion" placeholder="Your security question">

					<c:forEach var="question" items="${account.securityQuestions}">
						<c:choose>
							<c:when test="${account.securityQuestionId != question.id}"><option value="${question.id}">${question.text}</option></c:when>
							<c:otherwise><option selected="selected" value="${question.id}">${question.text}</option></c:otherwise>
						</c:choose>
						
					</c:forEach>
				</select>
			</label>
			<label>
				<spring:message code="myAccount.securityQuestion.answer"/>*
				<spring:message code="myAccount.securityQuestion.answerPlaceholder" var="answerPlaceholder"/>
				<input id="secAnswer" type="text" placeholder="${answerPlaceholder}" value="${account.securityAnswer}">
			</label>
			<div class="grid-content collapse shrink">
				<a id="secSubmit" class="button"><spring:message code="myAccount.securityQuestion.submit"/></a>
				<a id="secReset" class="button secondary"><spring:message code="common.discardChanges"/></a>
			</div>
			<div class="alert-box success hide secSuccess margin-tb">Your security question has been updated successfully.</div>
			<div class="alert-box warning hide secError margin-tb">Something went wrong while updating your security question.</div>
		</div>
		<div id="physician" class="grid-block small-10 medium-8 large-4 vertical padding-large-left ng-pristine ng-valid ng-scope tab-item hide">
			<h3 class="padding-medium-tb"><spring:message code="myAccount.treatingPhysician.title"/></h3>
			<label>
				<spring:message code="myAccount.treatingPhysician.providerId"/>*
				<spring:message code="myAccount.treatingPhysician.providerId" var="treatingPhysicianProviderId"/>
				<input type="text" placeholder="${treatingPhysicianProviderId}" id="phyProvider" value="${account.treatingPhysician.providerId}">
			</label>
			<c:choose>
				<c:when test="${empty account.treatingPhysician}">
					<input type="checkbox" placeholder="Is default physician" id="phyDefault">
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${account.treatingPhysician.defaultPhysician}">
							<input type="checkbox" placeholder="Is default physician" id="phyDefault" checked="checked">
						</c:when>
						<c:otherwise>
							<input type="checkbox" placeholder="Is default physician" id="phyDefault">
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			<label for="phyDefault" <c:if test="${not canManage}"><c:out value="class=disabled"/></c:if>>
				<spring:message code="myAccount.treatingPhysician.defaultPhysician"/>
			</label>
			<div class="grid-content margin-top collapse shrink">
				<a id="phySubmit" class="button"><spring:message code="common.Save"/></a>
				<a id="phyReset" class="button secondary"><spring:message code="common.discardChanges"/></a>
			</div>
			<div class="physician-success alert-box success hide margin-tb">Your provider ID has been updated successfully.</div>
			<div class="physician-error alert-box warning hide margin-tb">Unexpected error while updating your provider ID.</div>
		</div>
	</form>
</div>
