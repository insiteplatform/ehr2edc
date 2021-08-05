<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div
	class="small-10 small-offset-1 medium-7 medium-offset-2 large-5 large-offset-3 padding-large-top">
	<h3><spring:message code="about.title"/></h3>
	<h5 class="subtitle">
		<spring:message code="about.version"/> <c:out value="${requestScope.lwbVersion}" /> &copy;2016 <a
			href="http://www.custodix.com">custodix.com</a>
	</h5>

	<div class="padding-top"><spring:message code="about.body"/></div>

	<hr>

	<p>
		<spring:message code="about.footnotes"/>
	</p>
</div>