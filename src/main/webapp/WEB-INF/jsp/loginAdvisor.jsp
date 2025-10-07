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

			<form id="loginForm" action="loginAdvisor" method="post">
				<div class="mb-3">
					<label for="email" class="form-label text-danger">อีเมล</label> <input
						type="text" name="email" id="email" class="form-control"
						placeholder="example@email.com">
					<!-- error message -->
					<div id="emailError" class="text-danger mt-1 small"></div>
				</div>

				<div class="mb-3">
					<label for="password" class="form-label text-danger">รหัสผ่าน</label>
					<input type="password" name="password" id="password"
						class="form-control">
					<!-- error message -->
					<div id="passwordError" class="text-danger mt-1 small"></div>
				</div>

				<c:if test="${not empty error}">
					<div class="alert alert-danger">${error}</div>
				</c:if>

				<button type="submit" class="btn btn-login mt-2">เข้าสู่ระบบ</button>
			</form>

			<!-- Alert เมื่อ login ไม่ผ่าน -->
			<c:if test="${loginFailed}">
				<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
				<script>
			        Swal.fire({
			            icon: 'error',
			            title: 'ไม่สามารถเข้าสู่ระบบได้',
			            text: 'กรุณาตรวจสอบอีเมลหรือรหัสผ่าน',
			            confirmButtonText: 'ตกลง'
			        });
			    </script>
			</c:if>

			<!-- Alert เมื่อ login สำเร็จ -->
			<c:if test="${loginSuccess}">
				<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
				<script>
        Swal.fire({
            icon: 'success',
            title: 'เข้าสู่ระบบสำเร็จ',
            text: 'กำลังเข้าสู่หน้าหลัก...',
            showConfirmButton: false,
            timer: 2000,
            timerProgressBar: true
        }).then(() => {
            window.location.href = '<c:url value="/"/>';
        });
    			</script>
			</c:if>
		</div>
	</div>

	<!-- ตรวจสอบ email & password แบบ real-time -->
	<script>
	const emailInput = document.getElementById("email");
	const passwordInput = document.getElementById("password");
	const emailError = document.getElementById("emailError");
	const passwordError = document.getElementById("passwordError");

	// ตรวจสอบอีเมล
	function validateEmail() {
	    const email = emailInput.value.trim();
	    emailError.textContent = "";

	    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	    if (!emailRegex.test(email)) {
	        emailError.textContent = "*กรุณากรอกอีเมลให้ถูกต้อง";
	        return false;
	    }

	    // ดึงเฉพาะส่วนหน้า @
	    const localPart = email.split("@")[0];

	    if (localPart.length < 4) {
	        emailError.textContent = "*ชื่ออีเมลต้องมีความยาวอย่างน้อย 4 ตัวอักษร";
	        return false;
	    } else if (localPart.length > 16) {
	        emailError.textContent = "*ชื่ออีเมลต้องมีความยาวไม่เกิน 16 ตัวอักษร";
	        return false;
	    } 
	    // เช็คห้ามมีภาษาไทย
	    else if (/[ก-๙]/.test(email)) {
	        emailError.textContent = "*อีเมลห้ามมีตัวอักษรภาษาไทย";
	        return false;
	    }
	    // เช็คห้ามมีช่องว่าง
	    else if (/\s/.test(email)) {
	        emailError.textContent = "*อีเมลห้ามมีช่องว่าง";
	        return false;
	    }

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
	    // เช็คความยาว
	    else if (password.length < 5) {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวอย่างน้อย 5 ตัวอักษร";
	        return false;
	    } 
	    else if (password.length > 12) {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวไม่เกิน 12 ตัวอักษร";
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

	// เช็ค real-time ตอนกรอก
	emailInput.addEventListener("input", validateEmail);
	passwordInput.addEventListener("input", validatePassword);

	// เช็คอีกครั้งก่อน submit
	document.getElementById("loginForm").addEventListener("submit", function(event) {
	    const validEmail = validateEmail();
	    const validPassword = validatePassword();

	    if (!validEmail || !validPassword) {
	        event.preventDefault(); // ไม่ส่งฟอร์มถ้ามี error
	    }
	});
	</script>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
