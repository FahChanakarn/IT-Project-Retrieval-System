<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<title>แก้ไขข้อมูลส่วนตัว</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
	rel="stylesheet">
<link
	href="${pageContext.request.contextPath}/assets/css/editProfile.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/header.css"
	rel="stylesheet">
</head>
<body>

	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<div class="path-title">${projectName} / แก้ไขข้อมูลส่วนตัว</div>

		<c:if test="${not empty success}">
			<div class="alert alert-success text-center" role="alert">${success}</div>
		</c:if>
		<c:if test="${not empty error}">
			<div class="alert alert-danger text-center" role="alert">${error}</div>
		</c:if>

		<div class="edit-profile-box">
			<form action="${pageContext.request.contextPath}/updateProfile"
				method="post" enctype="multipart/form-data">
				<div class="profile-section">
					<c:choose>
						<c:when test="${not empty sessionScope.student.stu_image}">
							<img
								src="${pageContext.request.contextPath}/${sessionScope.student.stu_image}"
								class="profile-img">
						</c:when>
						<c:otherwise>
							<img
								src="${pageContext.request.contextPath}/assets/images/default-profile.png"
								class="profile-img">
						</c:otherwise>
					</c:choose>

					<div class="upload-wrapper">
						<input type="file" name="imageFile" class="form-control-file"
							accept=".jpg,.jpeg,.png"> <small class="text-muted">ควรมีขนาด
							256 x 256 px ขึ้นไป เป็นไฟล์ .jpg หรือ .png</small>
					</div>
				</div>


				<h4 class="section-title mt-4">ข้อมูลส่วนตัว</h4>

				<div class="row mt-3">
					<div class="col-md-6">
						<label>รหัสนักศึกษา</label> <input type="text"
							class="form-control" value="${sessionScope.student.stuId}"
							readonly>
					</div>
				</div>

				<div class="row mt-3">
					<div class="col-md-6">
						<label>ชื่อ</label> <input type="text" class="form-control"
							name="stu_firstName"
							value="${sessionScope.student.stu_firstName}" required>
					</div>
					<div class="col-md-6">
						<label>นามสกุล</label> <input type="text" class="form-control"
							name="stu_lastName" value="${sessionScope.student.stu_lastName}"
							required>
					</div>
				</div>

				<div class="row mt-3">
					<div class="col-md-6">
						<label>รหัสผ่าน</label>
						<div class="input-group">
							<input type="password" class="form-control" name="stu_password"
								id="password" value="${sessionScope.student.stu_password}"
								required>
							<button class="btn btn-outline-secondary" type="button"
								onclick="togglePassword('password')">
								<i class="bi bi-eye"></i>
							</button>
						</div>
					</div>
					<div class="col-md-6">
						<label>ยืนยันรหัสผ่าน</label>
						<div class="input-group">
							<input type="password" class="form-control" id="confirmPassword"
								required>
							<button class="btn btn-outline-secondary" type="button"
								onclick="togglePassword('confirmPassword')">
								<i class="bi bi-eye"></i>
							</button>
						</div>
					</div>
				</div>

				<div class="text-center mt-4">
					<button type="submit" class="btn btn-success">บันทึก</button>
					<a href="${pageContext.request.contextPath}/searchProjects"
						class="btn btn-danger">ยกเลิก</a>
				</div>
			</form>
		</div>
	</div>

	<footer class="footer mt-5">
		<div class="container">Information Technology Division, Faculty
			of Science, Maejo University ©</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
	<script>
		function togglePassword(id) {
			const input = document.getElementById(id);
			input.type = input.type === "password" ? "text" : "password";
		}
	</script>
</body>
</html>
