<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="eu.ehr4cr.workbench.local.WebRoutes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
	<sec:csrfMetaTags />
	<title>Accept invitation</title>
	<link rel="stylesheet" media="screen" href="<c:url value="/assets/index/css/app.css"/>">
	<link rel="stylesheet" media="screen" href="<c:url value="/assets/index/css/fira-sans/font.css"/>">
	<link rel="stylesheet" media="screen" href="<c:url value="/assets/index/css/fontawesome/font-awesome.min.css"/>">

	<link rel="shortcut icon" href="<c:url value="/assets/global/img/favicon/favicon.ico"/>">
	<link rel="icon" sizes="16x16 32x32 64x64" href="<c:url value="/assets/global/img/favicon/favicon.ico"/>">
	<link rel="icon" type="image/png" sizes="196x196" href="<c:url value="/assets/global/img/favicon/favicon-192.png"/>">
	<link rel="icon" type="image/png" sizes="160x160" href="<c:url value="/assets/global/img/favicon/favicon-160.png"/>">
	<link rel="icon" type="image/png" sizes="96x96" href="<c:url value="/assets/global/img/favicon/favicon-96.png"/>">
	<link rel="icon" type="image/png" sizes="64x64" href="<c:url value="/assets/global/img/favicon/favicon-64.png"/>">
	<link rel="icon" type="image/png" sizes="32x32" href="<c:url value="/assets/global/img/favicon/favicon-32.png"/>">
	<link rel="icon" type="image/png" sizes="16x16" href="<c:url value="/assets/global/img/favicon/favicon-16.png"/>">
	<link rel="apple-touch-icon" href="<c:url value="/assets/global/img/favicon/favicon-57.png"/>">
	<link rel="apple-touch-icon" sizes="114x114" href="<c:url value="/assets/global/img/favicon/favicon-114.png"/>">
	<link rel="apple-touch-icon" sizes="72x72" href="<c:url value="/assets/global/img/favicon/favicon-72.png"/>">
	<link rel="apple-touch-icon" sizes="144x144" href="<c:url value="/assets/global/img/favicon/favicon-144.png"/>">
	<link rel="apple-touch-icon" sizes="60x60" href="<c:url value="/assets/global/img/favicon/favicon-60.png"/>">
	<link rel="apple-touch-icon" sizes="120x120" href="<c:url value="/assets/global/img/favicon/favicon-120.png"/>">
	<link rel="apple-touch-icon" sizes="76x76" href="<c:url value="/assets/global/img/favicon/favicon-76.png"/>">
	<link rel="apple-touch-icon" sizes="152x152" href="<c:url value="/assets/global/img/favicon/favicon-152.png"/>">
	<link rel="apple-touch-icon" sizes="180x180" href="<c:url value="/assets/global/img/favicon/favicon-180.png"/>">
	<meta name="msapplication-TileColor" content="#FFFFFF">
	<meta name="msapplication-TileImage" content="/assets/global/img/favicon/favicon-144.png">
	<meta name="msapplication-config" content="/browserconfig.xml">
</head>
<body>
	<div class="grid-frame vertical">
		<div class="header grid-content shrink" style="padding: 0;">
			<div class="header-content menu-group primary">
				<div class="menu-group left">
					<ul class="menu-bar condense">
						<li class="logo">
                            <a href="<c:url value='<%=WebRoutes.home%>'/>">
                                <img src="<spring:url value="/assets/index/img/insite-trinetx-clinical-workbench-logo-white.svg"/>" alt="" style="border: 0;"/>
                            </a>
                        </li>
					</ul>
				</div>
				<div class="menu-group right">
					<ul class="menu-bar condense"></ul>
				</div>
			</div>
		</div>
		<div class="grid-block">
			<section class="grid-block vertical">
				<article class="grid-block vertical log-in padding-medium shrink">
					<h3>Accept invitation</h3>
					<c:url var="loginURL" value="${loginRoute}"></c:url>
					<c:choose>
						<c:when test="${requestScope.expired}">
							<p class="alert-box info">
								<i class="fa fa-exclamation-circle"></i> Your invitation has expired. Contact an administrator for a new invitation.
							</p>
						</c:when>
						<c:when test="${!empty requestScope.error}">
							<p class="alert-box info"><i class="fa fa-exclamation-circle"></i> ${requestScope.error}</p>
						</c:when>
						<c:otherwise>
							<input type="hidden" id="user-id" name="user-id" value="${requestScope.userId}" />
							<p class="margin-bottom">Please enter a password to complete the invitation.</p>
							<div class="grid-block vertical">
								<div class="icon-user">
								    <input type="text" placeholder="user name" value="${requestScope.userEmail}" disabled="disabled">
								</div>
								<div class="icon-password">
								    <input type="password" placeholder="password" id="password">
								</div>
								<div class="icon-password">
								    <input type="password" placeholder="repeat password"  id="password2">
								</div>
							</div>
                            <p class="margin-bottom">Please set your security question and answer.</p>
                            <div class="grid-block vertical">
                                <div class="security-question">
                                    <select placeholder="Your security question" id="secQuestion">
                                        <c:forEach var="question" items="${questions}">
                                            <option value="${question.id}">${question.text}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="security-answer">
                                    <input type="text" placeholder="Answer to your security question"  id="secAnswer">
                                </div>
                            </div>
							<div class="grid-content collapse shrink">
								<a id="inviteAccept" class="button accent">Accept Invitation</a>
							</div>
							<p class="alert-box accent hide accept-success">
								<i class="fa fa-info"></i> Your invitation has been accepted. Please <a href="${loginURL}">log in</a> to start.
							</p>
							<div class="alert-box warning hide accept-error" style="white-space: pre-wrap;"></div>
						</c:otherwise>
					</c:choose>
				</article>
			</section>
		</div>
	</div>
	
	<script type="text/javascript" src="<c:url value="/assets/global/js/json.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/assets/global/js/jquery-1.11.3.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/assets/auth/js/completeInvitation.js"/>"></script>

</body>