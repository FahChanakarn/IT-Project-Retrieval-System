<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>เพิ่มข้อมูลอาจารย์</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
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

		<form id="advisorForm"
			action="${pageContext.request.contextPath}/admin/addAdvisor"
			method="post">

			<!-- แถวคำนำหน้า ชื่อ นามสกุล -->
			<div class="name-row">
				<div>
					<label class="form-label">คำนำหน้า :</label> <select id="prefix"
						name="adv_prefix" class="form-select">
						<option value="">-- เลือก --</option>
						<option value="อ.ดร.">อ.ดร.</option>
						<option value="ผศ.อ.">ผศ.อ.</option>
						<option value="ผศ.ดร.">ผศ.ดร.</option>
						<option value="รศ.ดร.">รศ.ดร.</option>
						<option value="ศ.ดร.">ศ.ดร.</option>
					</select> <span id="prefixError" class="error-message"></span>
				</div>

				<div>
					<label class="form-label">ชื่อ :</label> <input type="text"
						id="firstName" name="adv_firstName" class="form-control">
					<span id="firstNameError" class="error-message"></span>
				</div>

				<div>
					<label class="form-label">นามสกุล :</label> <input type="text"
						id="lastName" name="adv_lastName" class="form-control"> <span
						id="lastNameError" class="error-message"></span>
				</div>
			</div>

			<div class="mb-3">
				<label class="form-label">Email :</label> <input type="email"
					id="email" name="adv_email" class="form-control"> <span
					id="emailError" class="error-message"></span>
			</div>

			<div class="mb-3">
				<label class="form-label">รหัสผ่าน :</label> <input type="password"
					id="password" name="adv_password" class="form-control"> <span
					id="passwordError" class="error-message"></span>
			</div>

			<div class="btn-group">
				<button type="submit" class="btn btn-success">บันทึก</button>
				<a href="${pageContext.request.contextPath}/admin/listAdvisors"
					class="btn btn-danger">ยกเลิก</a>
			</div>
		</form>
	</div>

	<script>
	// Get form elements
	const form = document.getElementById('advisorForm');
	const prefixInput = document.getElementById('prefix');
	const firstNameInput = document.getElementById('firstName');
	const lastNameInput = document.getElementById('lastName');
	const emailInput = document.getElementById('email');
	const passwordInput = document.getElementById('password');

	// Get error message elements
	const prefixError = document.getElementById('prefixError');
	const firstNameError = document.getElementById('firstNameError');
	const lastNameError = document.getElementById('lastNameError');
	const emailError = document.getElementById('emailError');
	const passwordError = document.getElementById('passwordError');

	// Validate prefix
	function validatePrefix() {
	    const prefix = prefixInput.value.trim();
	    prefixError.textContent = "";
	    if (prefix === "") {
	        prefixError.textContent = "*กรุณาเลือกคำนำหน้า";
	        return false;
	    }
	    return true;
	}

	// Validate first name
	function validateFirstName() {
	    const firstName = firstNameInput.value.trim();
	    firstNameError.textContent = "";
	    
	    if (firstName === "") {
	        firstNameError.textContent = "*กรุณากรอกชื่อ";
	        return false;
	    } 
	    // เช็คว่าเป็นช่องว่างทั้งหมดหรือไม่
	    else if (firstName.replace(/\s/g, '') === "") {
	        firstNameError.textContent = "*ชื่อต้องมีตัวอักษร ไม่สามารถเป็นช่องว่างเท่านั้น";
	        return false;
	    }
	    // เช็คห้ามมีช่องว่างคั่นกลาง
	    else if (/\s/.test(firstName)) {
	        firstNameError.textContent = "*ชื่อห้ามมีช่องว่าง กรุณากรอกเป็นคำเดียว";
	        return false;
	    }
	    else if (firstName.length < 1 || firstName.length > 50) {
	        firstNameError.textContent = "*ชื่อต้องมีความยาวระหว่าง 1-50 ตัวอักษร";
	        return false;
	    }
	    // เช็คว่ามีแต่ตัวอักษรไทยหรืออังกฤษเท่านั้น (ไม่อนุญาตให้มีช่องว่าง)
	    else if (!/^[ก-๙a-zA-Z]+$/.test(firstName)) {
	        firstNameError.textContent = "*ชื่อสามารถเป็นภาษาไทยหรืออังกฤษเท่านั้น";
	        return false;
	    }
	    
	    // อัพเดทค่าที่ trim แล้วกลับไปที่ input
	    firstNameInput.value = firstName;
	    return true;
	}

	// Validate last name
	function validateLastName() {
	    const lastName = lastNameInput.value.trim();
	    lastNameError.textContent = "";
	    
	    if (lastName === "") {
	        lastNameError.textContent = "*กรุณากรอกนามสกุล";
	        return false;
	    }
	    // เช็คว่าเป็นช่องว่างทั้งหมดหรือไม่
	    else if (lastName.replace(/\s/g, '') === "") {
	        lastNameError.textContent = "*นามสกุลต้องมีตัวอักษร ไม่สามารถเป็นช่องว่างเท่านั้น";
	        return false;
	    }
	    // เช็คห้ามมีช่องว่างคั่นกลาง
	    else if (/\s/.test(lastName)) {
	        lastNameError.textContent = "*นามสกุลห้ามมีช่องว่าง กรุณากรอกเป็นคำเดียว";
	        return false;
	    }
	    else if (lastName.length < 1 || lastName.length > 50) {
	        lastNameError.textContent = "*นามสกุลต้องมีความยาวระหว่าง 1-50 ตัวอักษร";
	        return false;
	    }
	    // เช็คว่ามีแต่ตัวอักษรไทยหรืออังกฤษเท่านั้น (ไม่อนุญาตให้มีช่องว่าง)
	    else if (!/^[ก-๙a-zA-Z]+$/.test(lastName)) {
	        lastNameError.textContent = "*นามสกุลสามารถเป็นภาษาไทยหรืออังกฤษเท่านั้น";
	        return false;
	    }
	    
	    // อัพเดทค่าที่ trim แล้วกลับไปที่ input
	    lastNameInput.value = lastName;
	    return true;
	}

	// Validate email
	function validateEmail() {
	    const email = emailInput.value.trim();
	    emailError.textContent = "";
	    
	    if (email === "") {
	        emailError.textContent = "*กรุณากรอกอีเมล";
	        return false;
	    }
	    // เช็คห้ามมีช่องว่าง
	    else if (/\s/.test(email)) {
	        emailError.textContent = "*อีเมลห้ามมีช่องว่าง";
	        return false;
	    }
	    // เช็คห้ามมีภาษาไทย
	    else if (/[ก-๙]/.test(email)) {
	        emailError.textContent = "*อีเมลห้ามมีตัวอักษรภาษาไทย";
	        return false;
	    }
	    
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
	    
	    // อัพเดทค่าที่ trim แล้วกลับไปที่ input
	    emailInput.value = email;
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
	    // เช็คความยาว
	    else if (password.length < 5) {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวอย่างน้อย 5 ตัวอักษร";
	        return false;
	    }
	    else if (password.length > 12) {
	        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวไม่เกิน 12 ตัวอักษร";
	        return false;
	    }
	    
	    // อัพเดทค่าที่ trim แล้วกลับไปที่ input
	    passwordInput.value = password;
	    return true;
	}

	// Add blur event listeners for real-time validation
	prefixInput.addEventListener('change', validatePrefix);
	firstNameInput.addEventListener('blur', validateFirstName);
	lastNameInput.addEventListener('blur', validateLastName);
	emailInput.addEventListener('blur', validateEmail);
	passwordInput.addEventListener('blur', validatePassword);

	// Form submit validation
	form.addEventListener('submit', function(e) {
	    e.preventDefault();

	    // Run all validations
	    const isPrefixValid = validatePrefix();
	    const isFirstNameValid = validateFirstName();
	    const isLastNameValid = validateLastName();
	    const isEmailValid = validateEmail();
	    const isPasswordValid = validatePassword();

	    // If all validations pass, submit the form
	    if (isPrefixValid && isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid) {
	        form.submit();
	    }
	});
	</script>
</body>
</html>