<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="eu.ehr4cr.workbench.local.WebRoutes"%>

<c:url var="homeUrl" value="<%= WebRoutes.cohortStudies%>"></c:url>

<div class="grid-block vertical">
	<div class="grid-content small-12 align-center ng-scope">
		<div class="small-10 small-offset-1 medium-7 medium-offset-2 large-5 large-offset-3 padding-large-tb">
			<div class="grid-block header shrink">
				<div class="grid-block">
					<h4>Permission denied.</h4>
				</div>
			</div>
			<div>
				<p>You do not have permission for the requested action.</p>
				<p>
					Back to the <a href="${homeUrl}">homepage</a>
				</p>
			</div>
		</div>
	</div>
</div>