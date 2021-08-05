<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="springMsg" uri="http://www.springframework.org/tags" %>
<%@page import="eu.ehr4cr.workbench.local.WebRoutes" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" media="screen" href="<c:url value="/assets/index/css/app.css"/>">
    <link rel="stylesheet" media="screen" href="<c:url value="/assets/index/css/fira-sans/font.css"/>">
    <link rel="stylesheet" media="screen" href="<c:url value="/assets/index/css/fontawesome/font-awesome.min.css"/>">

    <link rel="shortcut icon" href="<c:url value="/assets/global/img/favicon/favicon.ico"/>">
    <link rel="icon" sizes="16x16 32x32 64x64" href="<c:url value="/assets/global/img/favicon/favicon.ico"/>">
    <link rel="icon" type="image/png" sizes="196x196"
          href="<c:url value="/assets/global/img/favicon/favicon-192.png"/>">
    <link rel="icon" type="image/png" sizes="160x160"
          href="<c:url value="/assets/global/img/favicon/favicon-160.png"/>">
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
        <nav class="grid-block top-bar shrink">
            <ul class="title-area grid-block margin-small-left">
                <li class="title-logo">
                    <a href="#">
                        <img src="<c:url value="/assets/index/img/insite-trinetx-clinical-workbench-logo-green.svg"/>">
                    </a>
                </li>
            </ul>
            <div class="grid-block shrink">
                <ul class="menu-bar condense"></ul>
            </div>
        </nav>

    </div>

    <div class="grid-block">
        <section class="grid-block vertical logging">
            <article class="grid-block vertical log-in padding-medium shrink">
                <form method="post" onsubmit="getLoginAction(this);">
                    <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
                        <div class="alertmsg">
                            <span class="closebtn">&times;</span>
                            <strong>${SPRING_SECURITY_LAST_EXCEPTION.message}</strong>
                        </div>
                    </c:if>

                    <c:if test="${not empty param.register}">
                        <div class="alertmsg success">
                            <span class="closebtn">&times;</span>
                            <strong><springMsg:message code="register.success"/></strong>
                        </div>
                    </c:if>
                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}"/>
                    <div class="grid-block header margin-bottom">
                        <h4 class="grid-block"><springMsg:message code="login.title"/></h4>
                        <a href="<c:url value="<%= WebRoutes.registerAccount%>" />"
                           class="grid-block shrink accent"><springMsg:message code="login.requestAccount"/></a>
                    </div>
                    <div class="icon-user">
                        <springMsg:message code="login.mailTitle" var="mailTitle"/>
                        <input type="email" name="username" placeholder="${mailTitle}">
                    </div>
                    <div class="icon-password">
                        <springMsg:message code="login.passwordTitle" var="passwordTitle"/>
                        <input type="password" name="password" placeholder="${passwordTitle}">
                    </div>
                    <p>
                        <springMsg:message code="login.submitTitle" var="submitTitle"/>
                        <input type="submit" class="button accent button-login" value="${submitTitle}"/>
                    </p>
                    <p>
                        <c:url var="recoverUrl" value="<%= WebRoutes.recoverAccount %>"/>
                        <springMsg:message code="login.recoverPasswordTitle"/><br/><a href="${recoverUrl}"
                                                                                      class="accent"><springMsg:message
                            code="login.recoverPasswordLink"/></a>.
                    </p>
                </form>
            </article>
            <div class="grid-block login-footer shrink">
                <img src="<c:url value='/assets/index/img/logo-custodix-white.svg'/>"/>
            </div>
        </section>

    </div>
</div>
<script>
    var close = document.getElementsByClassName("closebtn");
    var i;

    for (i = 0; i < close.length; i++) {
        close[i].onclick = function () {
            var div = this.parentElement;
            div.style.opacity = "0";
            setTimeout(function () {
                div.style.display = "none";
            }, 100);
        }
    }

    function getLoginAction(form) {
        form.action = "login";
        var hash = document.location.hash;
        if (hash) {
            form.action = form.action + hash;
        }
        return true;
    }
</script>
</body>
