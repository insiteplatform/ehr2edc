<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes" %>
<%@ page import="eu.ehr4cr.workbench.local.controllers.NavigationItem" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>


<!DOCTYPE html>
<html>
<head>
    <sec:csrfMetaTags/>
    <title>${title}</title>

    <%@ include file="../../assets/index/html/header.html" %>

    <c:if test="${hasCSSSegment}">
        <jsp:include page="../..${CSSSegment}" flush="true"/>
    </c:if>
</head>
<body>
<div class="grid-frame vertical">
    <div class="header grid-content shrink padding-none">
        <div class="header-content menu-group primary">
            <div class="menu-group left">
                <ul class="menu-bar condense">
                    <li class="logo">
                        <a href="<c:url value='<%=WebRoutes.home%>'/>">
                            <img src="<spring:url value="/assets/index/img/insite-trinetx-clinical-workbench-logo-white.svg"/>" alt="" style="border: 0;"/>
                        </a>
                    </li>
                    <c:forEach var="navigationItem" items="${requestScope.sections}">
                        <c:choose>
                            <c:when test="${requestScope.section == navigationItem}">
                                <li class="is-active"><a href="<spring:url value="${navigationItem.route}"/>">
                                    <spring:message code="common.menu.${navigationItem.name}"/></a></li>
                            </c:when>
                            <c:otherwise>
                                <li><a href="<spring:url value="${navigationItem.route}"/>"> <spring:message
                                        code="common.menu.${navigationItem.name}"/></a></li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </ul>
            </div>


            <div class="menu-group right">
                <ul class="menu-bar condense">
                    <li class="user-menu"><a href="#" class="menu-right__trigger">${requestScope.user.username}<i
                            class="fa fa-angle-down"></i></a></li>
                    <li class="notifications-menu"><a class="notifications"><i class="fa fa-bell"></i> Notifications</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div id="notifications-menu" class="notifications action-sheet-container">
        <div class="action-sheet left notifications-menu" position="left" style="visibility: hidden">
            <ul class="notifications-list"></ul>
        </div>
    </div>
    <div id="user-menu" class="action-sheet-container">
        <div position="bottom" class="user-menu text-left action-sheet bottom">
            <ul>
                <c:forEach var="navigationItem" items="${requestScope.userMenu}">
                    <c:choose>
                        <c:when test="${requestScope.section == navigationItem}">
                            <li class="is-active"><a href="<spring:url value="${navigationItem.route}"/>">
                                <i class="fa ${navigationItem.cssClass}"></i><spring:message
                                    code="common.menu.${navigationItem.name}"/></a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="<spring:url value="${navigationItem.route}"/>">
                                <i class="fa ${navigationItem.cssClass}"></i><spring:message
                                    code="common.menu.${navigationItem.name}"/></a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </ul>
        </div>
    </div>

    <div class="grid-block">
        <jsp:include page="../..${requestScope.bodySegment}" flush="true"/>
    </div>
    <div class="static-notification top-middle warning" id="error-dialog">
        <a href="#" class="close-button">&times;</a>
        <div class="notification-content">
            <p class="ng-scope padding-rl"><i class="fa fa-exclamation-circle"></i><span id="error-dialog-text">An error occurred</span>
            </p>
        </div>
    </div>
    <div class="static-notification top-middle success" id="success-dialog">
        <a href="#" class="close-button">&times;</a>
        <div class="notification-content">
            <p class="ng-scope padding-rl"><i class="fa fa-info-circle"></i><span id="success-dialog-text">Action was successful</span>
            </p>
        </div>
    </div>
    <div id="sessionExpired" class="modal hide">
        <spring:url var="loginUrl" value="${loginRoute}"/>
        <aside class="modal is-active">
            <h3 class="border-bottom">Session Expired</h3>
            <p class="margin-tb">Please <strong><a href="${loginUrl}" target="_blank">sign in</a></strong> again first.
            </p>
            <p>A new window will open. Afterwards, return to this window and continue.</p>
            <a href="${loginUrl}" class="button primary no-unload" target="_blank">Sign in again</a>
        </aside>
    </div>
    <div id="siteDown" class="modal hide">
        <spring:eval expression="@environment.getProperty('support.mail')" var="supportMail"/>
        <spring:eval expression="@environment.getProperty('support.installId')" var="supportInstallId"/>
        <aside class="modal is-active">
            <h3 class="border-bottom">Connection lost</h3>
            <p class="margin-tb">Please check your internet connection or come back in a few moments.</p>
            <p>Please contact
                <a href="mailto:${supportMail}?subject=InSite clinical workbench (install id: ${supportInstallId}) is not reachable"
                   class="no-unload" target="_parent">${supportMail}</a>
                if this problem persists.</p>
        </aside>
    </div>
    <c:if test="${user.passwordImminentlyExpiring}">
        <jsp:include page="parts/password-expiring-message.jsp"/>
    </c:if>
</div>
</body>
<%@ include file="../../assets/index/html/libs.html" %>
<spring:eval expression="@environment.getProperty('notification.pooling.interval')" var="interval"/>
<spring:eval expression="@environment.getProperty('notification.popup.duration')" var="popupDuration"/>
<spring:eval expression="@environment.getProperty('jms.password')" var="cvetko"/>
<spring:eval expression="@environment.getProperty('ajax.retryLimit')" var="ajaxRetryLimit"/>
<spring:eval expression="@environment.getProperty('ajax.retryPeriod')" var="ajaxRetryPeriod"/>
<script type="text/javascript">
    var notificationPollingInterval = 30000;
    var notificationPopupDuration = 5000;
    <c:if test="${interval != null}" >
    notificationPollingInterval = ${interval};
    </c:if>
    <c:if test="${popupDuration != null}" >
    notificationPopupDuration = ${popupDuration};
    </c:if>

    var BASE_URL = "${pageContext.request.contextPath}";
    var NEW_NOTIFICATIONS_URL = BASE_URL + "<%= WebRoutes.notificationsUnread %>";
    var READ_NOTIFICATION = BASE_URL + "<%= WebRoutes.notificationsReadSingle%>";
    var page_namespace = "${requestScope.bodySegment}";
    page_namespace = page_namespace.substring(page_namespace.lastIndexOf('/') + 1, page_namespace.lastIndexOf("."));
    var loadPath;
    if (page_namespace.indexOf("cohortbuilder") != -1 || page_namespace.indexOf("patient-screening-manual") != -1 || page_namespace.indexOf("edit-screening-filters") != -1) {
        page_namespace = "querybuilder";
        loadPath = BASE_URL + '/public/locales/{{lng}}/{{ns}}.json';
    } else {
        var loadPath = BASE_URL + '/locales/{{lng}}/{{ns}}.json';
    }
    var enableInternationalization = ${enableInternationalization};
</script>
<%@ include file="../../assets/index/html/config.html" %>
<c:if test="${user.passwordImminentlyExpiring}">
    <script type="text/javascript" src="<c:url value="/assets/global/js/password-expiring-message.js"/>"></script>
</c:if>
<c:if test="${hasJSSegment}">
    <jsp:include page="../..${JSSegment}" flush="true"/>
</c:if>
<script>
    initAjaxHandling({
        retryLimit: ${ajaxRetryLimit},
        retryPeriod: ${ajaxRetryPeriod}
    });
</script>
<script type="text/javascript" src="<spring:url value="/public/javascripts/bundle.js"/>"></script>
</html>