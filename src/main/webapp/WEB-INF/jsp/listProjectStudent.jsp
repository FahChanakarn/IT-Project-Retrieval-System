<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="isAdmin" value="${userRole == 'admin'}" />
<c:set var="baseUrl" value="${isAdmin ? '/admin' : '/advisor'}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายการโครงงานของนักศึกษา</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<!-- CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/listProjectStudent.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body style="font-family: 'Kanit', sans-serif;">
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-4">
		<h5>รายการโครงงานนักศึกษา</h5>
		<hr>

		<form method="get"
			action="${pageContext.request.contextPath}${baseUrl}/myAdviseeProjects"
			class="mb-4">
			<div class="d-flex align-items-center gap-2">
				<label for="semester" class="form-label mb-0">ภาคเรียน:</label> <select
					name="semester" id="semester"
					class="form-select custom-semester-select"
					onchange="this.form.submit()">
					<option value="">เลือกภาคเรียน</option>
					<c:forEach var="sem" items="${semesterList}">
						<option value="${sem}"
							<c:if test="${sem == selectedSemester}">selected</c:if>>${sem}</option>
					</c:forEach>
				</select>
			</div>
		</form>

		<c:choose>
			<c:when test="${empty projects}">
				<div class="alert alert-warning text-center mt-4">
					ไม่พบข้อมูลโครงงานสำหรับภาคเรียนที่เลือก</div>
			</c:when>
			<c:otherwise>
				<!-- ตารางข้อมูลนักศึกษา -->
				<table class="table table-bordered">
					<thead class="table-primary">
						<tr>
							<th>รหัสนักศึกษา</th>
							<th>ชื่อ - สกุล</th>
							<th>หัวข้อโครงงาน</th>
							<th>รายละเอียด</th>
							<th>อนุมัติการอัปโหลด</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="row" items="${projects}">
							<tr>
								<td>${row[0]}</td>
								<td>${row[1]} ${row[2]}</td>
								<td>${row[3]}</td>
								<td><a
									href="${pageContext.request.contextPath}/viewAbstract?projectId=${row[4]}"
									class="btn btn-primary btn-sm">รายละเอียด</a></td>
								<td><c:choose>
										<c:when test="${row[5] == 'approved'}">
											<button class="btn btn-warning btn-sm" disabled>อนุมัติแล้ว</button>
										</c:when>
										<c:otherwise>
											<button type="button"
												class="btn btn-success btn-sm approve-btn"
												data-project-id="${row[4]}">รอดำเนินการอนุมัติ</button>
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>

				<!-- Pagination -->
				<div class="d-flex justify-content-center mt-3">
					<ul class="pagination">
						<c:forEach var="i" begin="1" end="${totalPages}">
							<li class="page-item ${i == currentPage ? 'active' : ''}"><a
								class="page-link" href="?page=${i}&semester=${selectedSemester}">${i}</a>
							</li>
						</c:forEach>
					</ul>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script>
		const baseUrl = '${baseUrl}';
		
		$(document).ready(function() {
			$(".approve-btn").click(function() {
				var btn = $(this);
				var projectId = btn.data("project-id");

				$.ajax({
					url : "${pageContext.request.contextPath}" + baseUrl + "/approveUploadAjax",
					method : "POST",
					data : {
						projectId : projectId},
					success : function(response) {
						if (response === "success") {
							btn.text("อนุมัติแล้ว");
							btn.prop("disabled",true);} 
						else {
							alert("ไม่สามารถอนุมัติได้ กรุณาลองใหม่");}
					},
					error : function() {
					alert("เกิดข้อผิดพลาดในการส่งข้อมูล");}
				});
			});
		});
	</script>
</body>
</html>