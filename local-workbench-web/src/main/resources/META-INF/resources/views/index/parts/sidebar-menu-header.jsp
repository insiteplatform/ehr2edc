<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="grid-block shrink border-bottom sidebar-menu-header">
    <div class="grid-content margin-none padding-tl sidebar-menu-expand">
        <i class="fa fa-list"></i>
        <span class="margin-small-rl sidebar-menu-item-text">Terminologies</span>
        <i class="fa fa-angle-down"></i>
    </div>
    <div class="grid-content shrink margin-none padding-none sidebar-collapse">
        <a class="button info icon margin-none"><i class="fa fa-angle-left"></i></a>
    </div>
</div>
<div class="sidebar-menu tether-enabled popup terminology-restrictions action-sheet-container">
    <div>
		<ul class="cohort-tabs">
            <li class="tab-item is-active" tab="tab-termino"><spring:message code="cohortbuilder.sidebar.terminologies"/></li>
            <li class="tab-item" tab="tab-blocks"><spring:message code="cohortbuilder.sidebar.filters"/></li>
		</ul>
	</div>
</div>