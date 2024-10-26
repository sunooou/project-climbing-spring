<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="member_id" %>

<!-- FIXME 네비게이션바 코드 시작 -->
<div class="sidebar col-md-2 p-4 position-fixed z-3">
	<div class="main-header-logo text-center">
		<a href="adminMain.do"><img src="images/logo.png" alt="Main.jsp"></a>
	</div>
	<div class="sidebar-wrapper position-relative" data-background-color="wheat" style="position: relative;">
		<div class="sidebar-wrapper h-auto">
			<div class="sidebar-content">
				<ul class="nav nav-secondary list-group">
					<li class="nav-item list-group-item-action"><a href="boardManagement.do"><i class="fas fa-home"></i> 메인</a></li>
					<li class="nav-item list-group-item-action"><a href="userManagement.do"><i class="fas fa-user-edit"></i> 회원 관리</a></li>
					<li class="nav-item list-group-item-action"><a href="gymManagement.do"><i class="fas fa-map-marked-alt"></i> 암벽장 관리</a></li>
					<li class="nav-item list-group-item-action"><a href="boardManagement.do"><i class="fas fa-clipboard-list"></i> 게시판 관리</a></li>
					<li class="nav-item list-group-item-action"><a href="reservationManagement.do"><i class="fas fa-wallet"></i> 예약 관리</a></li>
					<li class="nav-item list-group-item-action"><a href="crewManagement.do"><i class="fas fa-flag-checkered"></i> 크루전 관리</a></li>
				</ul>
			</div>
		</div>
	</div>
	<div class="text-bg-dark"><a href="logout.do">로그아웃</a></div>
</div>
<!-- 네비게이션바 코드 종료 -->
