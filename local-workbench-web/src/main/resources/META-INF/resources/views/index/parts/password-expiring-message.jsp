<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes" %>

<div class="static-notification top-right warning" id="password-expiring-message">
    <a href="#" class="close-button">&times;</a>
    <div class="notification-content">
        <p class="ng-scope padding-rl">
            <i class="fa fa-exclamation-circle"></i>
            <spring:message code="password-expiring-message.prefix"/>
            <a class="action-link" href="<c:url value='<%=WebRoutes.MY_ACCOUNT_PASSWORD%>'/>" target="_blank">
                <spring:message code="password-expiring-message.click"/></a>
            <spring:message code="password-expiring-message.suffix"/>
        </p>
    </div>
</div>