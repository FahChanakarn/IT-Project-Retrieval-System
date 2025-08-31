<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Import Student</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">

<!-- Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/importStudent.css">
</head>

<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-4 pt-4 fix-import-student-content">
		<div class="mx-auto">
			<h5>
				<strong>ข้อมูลนักศึกษา / Import Excel File</strong>
			</h5>
			<hr class="mb-4">

			<form method="post" enctype="multipart/form-data"
				action="${pageContext.request.contextPath}/admin/importStudentFile">

				<div class="d-flex align-items-center gap-3 flex-wrap mb-3">
					<!-- เลือกภาคเรียน -->
					<div class="d-flex align-items-center gap-2">
						<label for="semester" class="form-label fw-bold mb-0">ภาคเรียน:</label>
						<select name="semester" id="semester" class="form-select w-auto"
							onchange="this.form.submit()">
							<c:forEach var="sem" items="${semesterList}">
								<option value="${sem}"
									<c:if test="${sem == selectedSemester}">selected</c:if>>
									${sem}</option>
							</c:forEach>
						</select>
					</div>

				<!-- ✅ ชื่อไฟล์ -->
				<div class="mb-3">
					<label for="fileName" class="form-label fw-bold">ชื่อไฟล์ :</label>
					<input type="text" id="fileName" class="form-control"
						value="${not empty param.file ? param.file : ''}">
				</div>

				<!-- ✅ เลือกไฟล์ Excel -->
				<div class="mb-4">
					<label class="form-label fw-bold">ไฟล์ที่ต้องการนำเข้า :</label> <input
						type="file" class="form-control" name="file" id="fileInput"
						required>
				</div>

				<!-- ✅ ปุ่ม -->
				<div class="text-center">
					<button type="submit" class="btn btn-success me-2">นำเข้า</button>
					<button type="button"
						onclick="location.href='${pageContext.request.contextPath}/'"
						class="btn btn-danger">ยกเลิก</button>
				</div>

				<!-- ✅ แสดงผลลัพธ์ -->
				<c:if test="${not empty error}">
					<div class="alert alert-danger mt-4">${error}</div>
				</c:if>
				<c:if test="${not empty success}">
					<div class="alert alert-success mt-4">${success}</div>
				</c:if>
			</form>
		</div>
	</div>
</body>
</html>
