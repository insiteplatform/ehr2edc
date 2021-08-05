<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes" %>

<spring:eval expression="@environment.getProperty('support.mail')" var="supportMail" />
<spring:eval expression="@environment.getProperty('support.installId')" var="supportInstallId" />

<div class="grid-frame vertical">
	<div class="grid-block">
		<section class="logging ng-scope">
			<article class="error-page padding-medium">
				<h3 class="border-bottom"><spring:message code="error.title"/></h3>
				<c:if test="${not empty trackingId}">
					<p class="margin-tb"><spring:message code="error.trackingId"/>: ${trackingId}</p>
                </c:if>
				<p class="margin-tb"><spring:message code="error.contact.begin"/>
					<a href="mailto:${supportMail}?subject=InSite clinical workbench issue report ${trackingId} (${supportInstallId})"
					   class="no-unload" target="_parent">${supportMail}</a>
					<spring:message code="error.contact.end"/>
				</p>
				<p>
					<spring:message code="error.homepage.begin"/>
					<a href="<c:url value='<%=WebRoutes.home%>'/>"><spring:message code="error.homepage"/>.</a>
				</p>
			</article>
		</section>
	</div>
</div>