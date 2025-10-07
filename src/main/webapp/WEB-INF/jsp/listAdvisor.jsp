<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายชื่ออาจารย์</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<!-- Bootstrap JS -->
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/listAdvisor.css">
<!-- Font -->
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold">ข้อมูลอาจารย์ / รายชื่ออาจารย์</h5>
		<hr>
		<a href="${pageContext.request.contextPath}/admin/addAdvisorForm"
			class="btn btn-primary mb-3">เพิ่มข้อมูลอาจารย์</a>

		<table class="table table-bordered text-center">
			<thead class="table-info">
				<tr>
					<th>รหัสอาจารย์</th>
					<th>ชื่อ - สกุล</th>
					<th>Email</th>
					<th>ตำแหน่ง</th>
					<th>สถานะ</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="advisor" items="${advisorList}">
					<tr>
						<td>${advisor.advisorId}</td>
						<td>${advisor.adv_prefix}${advisor.adv_firstName}
							${advisor.adv_lastName}</td>
						<td>${advisor.adv_email}</td>
						<td><form method="post"
								action="${pageContext.request.contextPath}/admin/togglePosition">
								<input type="hidden" name="adv_id" value="${advisor.advisorId}" />
								<button type="submit"
									class="status-btn text-white ${advisor.adv_position == 'อาจารย์ที่ปรึกษา' ? 'bg-primary' : 'bg-warning'}">
									${advisor.adv_position}</button>
							</form></td>
						<td>
							<form method="post"
								action="${pageContext.request.contextPath}/admin/toggleStatus">
								<input type="hidden" name="adv_id" value="${advisor.advisorId}" />
								<button type="submit"
									class="status-btn text-white ${advisor.adv_status == 'ปฏิบัติราชการ' ? 'bg-success' : 'bg-danger'}">
									${advisor.adv_status}</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

</body>
</html>
