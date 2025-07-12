<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>เพิ่มข้อมูลอาจารย์</title>
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
	href="${pageContext.request.contextPath}/assets/css/addAdvisor.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold text-danger">อาจารย์ / เพิ่มข้อมูลอาจารย์</h5>
		<hr>

		<form action="${pageContext.request.contextPath}/admin/addAdvisor"
			method="post">
			<div class="mb-3">
				<label class="form-label">คำนำหน้า :</label> <input type="text"
					name="adv_prefix" class="form-control" required>
			</div>
			<div class="mb-3">
				<label class="form-label">ชื่อ :</label> <input type="text"
					name="adv_firstName" class="form-control" required>
			</div>
			<div class="mb-3">
				<label class="form-label">นามสกุล :</label> <input type="text"
					name="adv_lastName" class="form-control" required>
			</div>
			<div class="mb-3">
				<label class="form-label">ตำแหน่ง :</label> <select
					name="adv_position" class="form-select" required>
					<option value="">เลือกตำแหน่ง</option>
					<option value="อาจารย์ที่ปรึกษา">อาจารย์ที่ปรึกษา</option>
					<option value="อาจารย์ประสานงาน">อาจารย์ประสานงาน</option>
				</select>
			</div>
			<div class="mb-3">
				<label class="form-label">Email :</label> <input type="email"
					name="adv_email" class="form-control" required>
			</div>
			<div class="mb-3">
				<label class="form-label">รหัสผ่าน :</label> <input type="password"
					name="adv_password" class="form-control" required>
			</div>

			<div class="btn-group">
				<button type="submit" class="btn btn-success">บันทึก</button>
				<a href="${pageContext.request.contextPath}/admin/listAdvisors"
					class="btn btn-danger">ยกเลิก</a>
			</div>
		</form>
	</div>

</body>
</html>
