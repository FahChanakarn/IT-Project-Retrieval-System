<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login - นักศึกษา 496</title>
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
	href="${pageContext.request.contextPath}/assets/css/loginStudent496.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<!-- SweetAlert -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="login-wrapper">
		<img src="${pageContext.request.contextPath}/assets/images/login.png"
			class="login-icon" alt="login icon" />

		<div class="login-left">
			<h4>Welcome To</h4>
			<h1>
				<strong>Information Technology<br>Project <br>Retrieval<br>System
				</strong>
			</h1>
		</div>

		<div class="login-right">
			<h5 class="mb-4">
				<strong>เข้าสู่ระบบด้วยบัญชีนักศึกษา496</strong>
			</h5>

			<form id="loginForm" action="loginStudent496" method="post">
				<div class="mb-3">
					<label for="stuId" class="form-label text-danger">รหัสนักศึกษา</label>
					<input type="text" name="stuId" id="stuId" class="form-control"
						placeholder="กรอกรหัสนักศึกษา">
					<div id="stuIdError" class="text-danger mt-1 small"></div>
				</div>

				<div class="mb-3">
					<label for="password" class="form-label text-danger">รหัสผ่าน</label>
					<input type="password" name="password" id="password"
						class="form-control" placeholder="กรอกรหัสผ่าน" maxlength="100">
					<div id="passwordError" class="text-danger mt-1 small"></div>
				</div>

				<button type="submit" class="btn btn-login mt-2">เข้าสู่ระบบ</button>
			</form>
		</div>
	</div>

	<script>
	// Elements
	const stuInput = document.getElementById("stuId");
	const passwordInput = document.getElementById("password");
	const stuIdError = document.getElementById("stuIdError");
	const passwordError = document.getElementById("passwordError");

	// Validate student ID
	function validateStuId() {
	    const stuId = stuInput.value.trim();
	    stuIdError.textContent = "";
	    
	    // เช็คห้ามว่าง
	    if (stuId === "") {
	        stuIdError.textContent = "*กรุณากรอกรหัสนักศึกษา";
	        return false;
	    }
	    // เช็คว่าเป็นช่องว่างทั้งหมดหรือไม่
	    else if (stuId.replace(/\s/g, '') === "") {
	        stuIdError.textContent = "*รหัสนักศึกษาต้องมีตัวอักษร ไม่สามารถเป็นช่องว่างเท่านั้น";
	        return false;
	    }
	    // เช็คห้ามมีช่องว่าง
	    else if (/\s/.test(stuId)) {
	        stuIdError.textContent = "*รหัสนักศึกษาห้ามมีช่องว่าง";
	        return false;
	    }
	    // เช็คว่าเป็นตัวเลขเท่านั้น
	    else if (!/^\d+$/.test(stuId)) {
	        stuIdError.textContent = "*รหัสนักศึกษาต้องเป็นตัวเลขเท่านั้น";
	        return false;
	    }
	    // เช็คความยาว (ปรับตามระบบของคุณ เช่น 10 หลัก)
	    else if (stuId.length !== 10) {
	        stuIdError.textContent = "*รหัสนักศึกษาต้องมี 10 หลัก";
	        return false;
	    }
	    
	    // อัพเดทค่าที่ trim แล้วกลับไปที่ input
	    stuInput.value = stuId;
	    return true;
	}

	// Validate password
	function validatePassword() {
	    const password = passwordInput.value.trim();
	    passwordError.textContent = "";

	    // เช็คห้ามว่าง
	    if (password === "") {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่าน";
	        return false;
	    }
	    // เช็คความยาวขั้นต่ำ
	    else if (password.length < 5) {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวอย่างน้อย 5 ตัวอักษร";
	        return false;
	    }
	    // ปรับความยาวสูงสุดเป็น 100 เพราะ BCrypt hash ยาว ~60 ตัวอักษร
	    else if (password.length > 100) {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวไม่เกิน 100 ตัวอักษร";
	        return false;
	    } 
	    // เช็คห้ามมีช่องว่าง
	    else if (/\s/.test(password)) {
	        passwordError.textContent = "*รหัสผ่านห้ามมีช่องว่าง";
	        return false;
	    } 
	    // เช็คห้ามมีภาษาไทย
	    else if (/[ก-๙]/.test(password)) {
	        passwordError.textContent = "*รหัสผ่านห้ามเป็นตัวอักษรภาษาไทย";
	        return false;
	    }

	    return true;
	}

	// Real-time validation
	stuInput.addEventListener("blur", validateStuId);
	passwordInput.addEventListener("blur", validatePassword);

	// Validate before submit
	document.getElementById("loginForm").addEventListener("submit", function(event) {
	    const validStuId = validateStuId();
	    const validPassword = validatePassword();
	    if (!validStuId || !validPassword) {
	        event.preventDefault();
	    }
	});
	</script>

	<!-- SweetAlert for Login Failed -->
	<c:if test="${loginFailed}">
		<script>
	    Swal.fire({
	        icon: 'error',
	        title: 'ไม่สามารถเข้าสู่ระบบได้',
	        text: '${not empty errorMessage ? errorMessage : "กรุณาตรวจสอบรหัสนักศึกษา หรือรหัสผ่าน"}',
	        confirmButtonText: 'ตกลง',
	        confirmButtonColor: '#d33'
	    });
	</script>
	</c:if>

	<!-- SweetAlert for Login Success -->
	<c:if test="${loginSuccess}">
		<script>
	    Swal.fire({
	        icon: 'success',
	        title: 'เข้าสู่ระบบสำเร็จ',
	        text: 'กำลังเข้าสู่หน้าหลัก...',
	        showConfirmButton: false,
	        timer: 2000,
	        timerProgressBar: true
	    }).then(() => {
	        window.location.href = '${redirectUrl}';
	    });
	</script>
	</c:if>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>