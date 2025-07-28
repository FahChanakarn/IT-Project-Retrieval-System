<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login - อาจารย์ที่ปรึกษา</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<!-- Google Fonts: Kanit -->
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<!-- Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css">
<!-- CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/loginStudent496.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

	<!-- Header -->
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="login-wrapper">
		<img src="${pageContext.request.contextPath}/assets/images/login.png"
			class="login-icon" alt="login icon" />

		<!-- ซ้าย -->
		<div class="login-left">
			<h4>Welcome To</h4>
			<h1>
				<strong>Information Technology<br>Project <br>Retrieval<br>System
				</strong>
			</h1>
		</div>

		<!-- ขวา -->
		<div class="login-right">
			<h5 class="mb-4">
				<strong>เข้าสู่ระบบด้วยบัญชีอาจารย์</strong>
			</h5>

			<form action="loginAdvisor" method="post">
				<div class="mb-3">
					<label for="email" class="form-label text-danger">อีเมล</label> <input
						type="email" name="email" id="email" class="form-control"
						placeholder="example@email.com" required>
				</div>

				<div class="mb-3">
					<label for="password" class="form-label text-danger">รหัสผ่าน</label>
					<input type="password" name="password" id="password"
						class="form-control" required>
				</div>

				<c:if test="${not empty error}">
					<div class="alert alert-danger">${error}</div>
				</c:if>

				<button type="submit" class="btn btn-login mt-2">เข้าสู่ระบบ</button>
			</form>
			<c:if test="${loginFailed}">
				<script>
        Swal.fire({
            icon: 'error',
            title: 'ไม่สามารถเข้าสู่ระบบได้',
            text: 'กรุณาตรวจสอบอีเมลหรือรหัสผ่าน',
            confirmButtonText: 'ตกลง'
        });
    </script>
			</c:if>

			<c:if test="${loginSuccess}">
				<script>
        Swal.fire({
            icon: 'success',
            title: 'เข้าสู่ระบบสำเร็จ',
            showConfirmButton: false,
            timer: 2000
        }).then(() => {
            window.location.href = '<c:url value="/"/>';
        });
    </script>
			</c:if>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
