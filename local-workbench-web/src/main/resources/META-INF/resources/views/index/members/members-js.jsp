<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes"%>

<script>
	var BASE_URL = "${pageContext.request.contextPath}";
	var assignURL = BASE_URL + "<%= WebRoutes.assignMembers %>";
	var deleteUrl = BASE_URL + "<%= WebRoutes.deleteMember %>";
	var acceptUrl = BASE_URL + "<%= WebRoutes.acceptMember %>";
	var inviteURL = BASE_URL + "<%= WebRoutes.inviteUser %>";
	var recoverUrl = BASE_URL + "<%= WebRoutes.recoverMember %>";
	var reinviteUrl = BASE_URL + "<%= WebRoutes.reinviteUser %>";
</script>

<script type="text/javascript" src="<c:url value="/assets/index/js/members.js"/>"></script>