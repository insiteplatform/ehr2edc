<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="eu.ehr4cr.workbench.local.WebRoutes" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
		<sec:csrfMetaTags />
        <title>Recover account</title>
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
            PATH_RECOVER = "<%= WebRoutes.recoverAccount %>";
            PATH_LOGIN = "<%= WebRoutes.registerAccount %>";
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
				    	<h3 class="padding-medium-tb">Recover your account</h3>
		                <label>
							E-mail
							<input type="email" name="email" placeholder="mail@address.com" id="email">
		                </label>
		                <label id="security-question-label" class="label-security hide">
		                	Security question: <strong id="security-question" class="question-security"></strong>
		                	<input type="text" name="securityAnswer" placeholder="your answer" id="securityAnswer">
		                </label>
		                <div class="messages">
		                	<div id="correct-answer-alert" class="alert-box success accent margin-tb hide answer-correct">An e-mail has been sent. Please follow the link to complete the recovery of your account.</div>
		                	<div id="no-question-alert" class="alert-box alert warning margin-tb hide error-noquestion">Could not find a security question for this email.</div>
		                	<div id="error-alert" class="alert-box alert warning margin-tb hide"></div>
		                </div>
		                <div class="grid-content collapse shrink">
                        	<a id="submitButton" class="button accent">Recover</a>
            				<a href="<c:url value="<%= WebRoutes.home%>"/>" class="button secondary">Cancel</a>
		                </div>
		                <div id="loading" class="hide"><i class="fa fa-circle-o-notch fa-spin"></i></div>
				  	</div>
				</div>
            </div>
        </div>
    </body>
    <script type="text/javascript" src="<c:url value="/assets/global/js/jquery-1.8.2.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/assets/auth/js/recovery/RecoveryUi.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/assets/auth/js/recovery/RecoveryService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/assets/auth/js/recovery/RecoveryController.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/assets/global/js/CsrfService.js"/>"></script>

	<script type="text/javascript">
        var recoveryController = new RecoveryController();
		var csrfService = new CsrfService();

        $(document)
            .ajaxSend(sefCsrfHeader)
            .ready(onReady);

        function onReady() {
            recoveryController.initHandlers();
        }

        function sefCsrfHeader(e, xhr) {
            csrfService.sefCsrfHeader(e, xhr)
		}
	</script>
</html>
