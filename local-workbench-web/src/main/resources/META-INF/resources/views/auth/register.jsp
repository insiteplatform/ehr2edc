<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="eu.ehr4cr.workbench.local.WebRoutes" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="registerForm" class="eu.ehr4cr.workbench.local.auth.RegisterForm" scope="request"/>   

<!DOCTYPE html>
<html>
    <head>
		<sec:csrfMetaTags />
        <title>Request account</title>
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

        <script type="text/javascript">
            BASE_URL = "${pageContext.request.contextPath}";
            PATH_REGISTER = "<%= WebRoutes.registerAccount %>";
            PATH_LOGIN = "<%= WebRoutes.home %>";
        </script>
    </head>
    <body>
        <div class="grid-frame vertical">
            <div class="header grid-content shrink padding-none">
                <div class="header-content menu-group primary">
                    <div class="menu-group left">
                    	<ul class="menu-bar condense">
							<li class="logo">
								<a href="<c:url value="<%= WebRoutes.home%>"/>">
									<img src="<c:url value="/assets/index/img/insite-trinetx-clinical-workbench-logo-white.svg"/>" alt="" style="border: 0;" />
								</a>
							</li>
                    	</ul>
                    </div>                    
                </div>
            </div>
            
            <div class="grid-block ng-scope">
				<div class="grid-block small-12 align-center ng-scope">
				  	<div class="grid-content small-10 medium-5 large-3 padding-large-top">
				    	
				    	<h3 class="padding-medium-tb">Request an account</h3>
				    	<label class="name">
		                	User Name
							<input type="text" name="name" placeholder="Username" value="${registerForm.username}" id="name">
						</label>
		                <label>
							E-mail
							<input type="email" name="email" placeholder="mail@address.com" value="${registerForm.email}" id="email">
		                </label>
		                <div class="errors"></div>
		                
		                <div class="grid-content collapse shrink">
                        	<a id="btnSubmit" class="button accent">Register</a>
            				<a href="<c:url value="<%= WebRoutes.home%>"/>" class="button secondary">Cancel</a>
		                </div>
				  	</div>
				</div>
            </div>
        </div>
    </body>
    <script type="text/javascript" src="<c:url value="/assets/global/js/jquery-1.8.2.js"/>"></script>    
    <script type="text/javascript" src="<c:url value="/assets/auth/js/register.js"/>"></script>
</html>
