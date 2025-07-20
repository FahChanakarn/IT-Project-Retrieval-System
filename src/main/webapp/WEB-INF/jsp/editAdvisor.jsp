<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>แก้ไขข้อมูลอาจารย์</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/editAdvisor.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold text-danger">อาจารย์ / แก้ไขข้อมูลอาจารย์</h5>
		<hr>

		<form action="${pageContext.request.contextPath}/admin/updateAdvisor"
			method="post">
			<!-- ชื่อและนามสกุล -->
			<div class="mb-3">
				<label class="form-label">คำนำหน้า :</label> <input type="text"
					name="adv_prefix" class="form-control mb-2"
					value="${advisor.adv_prefix}" required>

				<div class="row">
					<div class="col-md-5 mb-2">
						<label class="form-label">ชื่อ :</label> <input type="text"
							name="adv_firstName" class="form-control"
							value="${advisor.adv_firstName}" required>
					</div>
					<div class="col-md-5 mb-2">
						<label class="form-label">นามสกุล :</label> <input type="text"
							name="adv_lastName" class="form-control"
							value="${advisor.adv_lastName}" required>
					</div>
				</div>
			</div>


			<!-- Email -->
			<div class="mb-3">
				<label class="form-label">Email :</label> <input type="email"
					name="adv_email" class="form-control" value="${advisor.adv_email}"
					required>
			</div>

			<div class="row">
				<!-- รหัสผ่าน -->
				<div class="col-md-5 mb-2 pe-md-2">
					<label class="form-label">รหัสผ่าน :</label>
					<div class="input-group">
						<input type="password" name="adv_password" id="adv_password"
							class="form-control" value="${advisor.adv_password}" required>
						<span class="input-group-text"> <i class="bi bi-eye-slash"
							onclick="togglePassword(this, 'adv_password')"></i>
						</span>
					</div>
				</div>

				<!-- ยืนยันรหัสผ่าน -->
				<div class="col-md-5 mb-2 ps-md-2">
					<label class="form-label">ยืนยันรหัสผ่าน :</label>
					<div class="input-group">
						<input type="password" name="confirm_password"
							id="confirm_password" class="form-control"
							value="${advisor.adv_password}" required> <span
							class="input-group-text"> <i class="bi bi-eye-slash"
							onclick="togglePassword(this, 'confirm_password')"></i>
						</span>
					</div>
				</div>
			</div>

			<!-- ปุ่ม -->
			<div class="btn-group">
				<button type="submit" class="btn btn-success rounded">แก้ไข</button>
				<a href="${pageContext.request.contextPath}/admin/listAdvisors"
					class="btn btn-danger rounded">ยกเลิก</a>
			</div>


			<!-- รหัสอาจารย์ (ซ่อน) -->
			<input type="hidden" name="advisorId" value="${advisor.advisorId}">
		</form>
	</div>

	<script>
		function togglePassword(icon, fieldId) {
			const input = document.getElementById(fieldId);
			if (input.type === "password") {
				input.type = "text";
				icon.classList.remove("bi-eye-slash");
				icon.classList.add("bi-eye");
			} else {
				input.type = "password";
				icon.classList.remove("bi-eye");
				icon.classList.add("bi-eye-slash");
			}
		}
	</script>
</body>
</html>
