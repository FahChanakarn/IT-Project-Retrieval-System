<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายการโครงงานของนักศึกษา</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/listStudentProject.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold text-danger">โครงงาน /
			รายการโครงงานของนักศึกษา</h5>
		<hr>

		<div class="mb-3">
			<label class="form-label fw-bold">ภาคเรียน :</label> <select
				class="form-select w-auto d-inline"
				onchange="location = '?semester=' + this.value;">
				<c:forEach var="sem" items="${semesterList}">
					<option value="${sem}" ${sem == selectedSemester ? 'selected' : ''}>${sem}</option>
				</c:forEach>
			</select>
		</div>

		<table class="table table-bordered align-middle text-center">
			<thead class="table-light">
				<tr>
					<th>รหัสนักศึกษา</th>
					<th>ชื่อ - สกุล</th>
					<th>หัวข้อโครงงาน</th>
					<th>รายละเอียด</th>
					<th>อนุมัติการอัปโหลด</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="project" items="${studentProjects}">
					<c:forEach var="student" items="${project.students}">
						<tr>
							<td>${student.stuId}</td>
							<td>${student.stu_prefix}${student.stu_firstName}
								${student.stu_lastName}</td>
							<td>${project.project_nameTh}</td>
							<td><a class="btn btn-primary btn-sm"
								href="${pageContext.request.contextPath}/project/detail/${project.projectId}">
									รายละเอียด </a></td>
							<td>
								<form method="post"
									action="${pageContext.request.contextPath}/admin/approveUpload">
									<input type="hidden" name="projectId"
										value="${project.projectId}" />
									<button type="submit" class="btn btn-success btn-sm"
										${project.uploadApproved ? 'disabled' : ''}>
										${project.uploadApproved ? 'เผยแพร่แล้ว' : 'อนุมัติ'}</button>
								</form>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>

		<!-- Pagination (หากมี) -->
		<nav aria-label="Page navigation">
			<ul class="pagination justify-content-center">
				<c:forEach var="i" begin="1" end="${totalPages}">
					<li class="page-item ${i == currentPage ? 'active' : ''}"><a
						class="page-link" href="?page=${i}">${i}</a></li>
				</c:forEach>
			</ul>
		</nav>
	</div>

</body>
</html>
