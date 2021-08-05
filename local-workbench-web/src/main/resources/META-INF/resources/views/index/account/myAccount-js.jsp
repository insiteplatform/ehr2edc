<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes"%>

<script type="text/javascript">
	var BASE_URL = "${pageContext.request.contextPath}";
	var updateProfileURL = BASE_URL + "<%= WebRoutes.updateProfile %>";
	var updatePasswordURL = BASE_URL + "<%= WebRoutes.updatePassword %>";
	var updatePhysicianURL = BASE_URL + "<%= WebRoutes.updatePhysician %>";
	var securityURL = BASE_URL + "<%= WebRoutes.accountQuestion %>"
</script>

<script type="text/javascript" src="<c:url value="/assets/index/js/myAccount.js"/>"></script>
<!-- 
<script type="text/javascript" src="http://localhost:8080/cwb-dev/js/myAccount.js"></script>
-->