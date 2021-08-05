<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<zf-list-footer class="grid-block shrink footer">
	<div class="page-items grid-block">
		<span><spring:message code="common.itemsPerPage"/>:</span>
		<ul class="page-item-numbers">
			<li class="is-active"><a href="#">10</a></li>
			<li><a href="#">50</a></li>
			<li><a href="#">100</a></li>
		</ul>
		<input type="hidden" value="10" class="pager-size" />
	</div>
	<div class="grid-block shrink pager">
		<ul class="pagination">
			<li class="first update-pager"><a href="#"><i class="fa fa-angle-double-left"></i></a></li>
			<li class="prev update-pager"><a href="#"><i class="fa fa-angle-left"></i></a></li>
			<li class="next update-pager"><a href="#"><i class="fa fa-angle-right"></i></a></li>
			<li class="last update-pager"><a href="#"><i class="fa fa-angle-double-right"></i></a></li>
		</ul>
		<input type="hidden" value="10" class="pagesize" />
		<input type="hidden" value="0" class="pager-nr" />
		<input type="hidden" class="pagedisplay" value="0/0" />
	</div>
</zf-list-footer>