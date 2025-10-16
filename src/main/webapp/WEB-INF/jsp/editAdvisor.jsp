<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
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
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold">แก้ไขข้อมูลส่วนตัว</h5>
		<hr>

		<!-- SweetAlert สำหรับแสดงข้อความสำเร็จ -->
		<c:if test="${not empty success}">
			<script>
				Swal.fire({
					icon: 'success',
					title: 'สำเร็จ',
					text: '${success}',
					confirmButtonText: 'ตกลง',
					confirmButtonColor: '#28a745'
				});
			</script>
		</c:if>

		<!-- SweetAlert สำหรับแสดงข้อความ error -->
		<c:if test="${not empty error}">
			<script>
				Swal.fire({
					icon: 'error',
					title: 'เกิดข้อผิดพลาด',
					text: '${error}',
					confirmButtonText: 'ตกลง',
					confirmButtonColor: '#d33'
				});
			</script>
		</c:if>

		<form id="editAdvisorForm"
			action="${pageContext.request.contextPath}/advisor/updateProfile"
			method="post">

			<!-- แถวคำนำหน้า + ชื่อ + นามสกุล -->
			<div class="name-row">
				<div>
					<label class="form-label">คำนำหน้า :</label> <select id="prefix"
						name="adv_prefix" class="form-select">
						<option value="">-- เลือก --</option>
						<option value="อ.ดร."
							${advisor.adv_prefix == 'อ.ดร.' ? 'selected' : ''}>อ.ดร.</option>
						<option value="ผศ.อ."
							${advisor.adv_prefix == 'ผศ.อ.' ? 'selected' : ''}>ผศ.อ.</option>
						<option value="ผศ.ดร."
							${advisor.adv_prefix == 'ผศ.ดร.' ? 'selected' : ''}>ผศ.ดร.</option>
						<option value="รศ.ดร."
							${advisor.adv_prefix == 'รศ.ดร.' ? 'selected' : ''}>รศ.ดร.</option>
						<option value="ศ.ดร."
							${advisor.adv_prefix == 'ศ.ดร.' ? 'selected' : ''}>ศ.ดร.</option>
					</select> <span id="prefixError" class="error-message"></span>
				</div>

				<div>
					<label class="form-label">ชื่อ :</label> <input type="text"
						id="firstName" name="adv_firstName" class="form-control"
						value="${advisor.adv_firstName}"> <span
						id="firstNameError" class="error-message"></span>
				</div>

				<div>
					<label class="form-label">นามสกุล :</label> <input type="text"
						id="lastName" name="adv_lastName" class="form-control"
						value="${advisor.adv_lastName}"> <span id="lastNameError"
						class="error-message"></span>
				</div>
			</div>

			<!-- Email -->
			<div class="mb-3">
				<label class="form-label">Email :</label> <input type="email"
					id="email" name="adv_email" class="form-control"
					value="${advisor.adv_email}"> <span id="emailError"
					class="error-message"></span>
			</div>

			<!-- Password -->
			<div class="row">
				<div class="col-md-6 mb-3">
					<label class="form-label">รหัสผ่าน: <small
						class="text-muted">(เว้นว่างหากไม่ต้องการเปลี่ยน)</small></label>
					<div class="input-group">
						<input type="password" id="adv_password" name="adv_password"
							class="form-control" placeholder="กรอกรหัสผ่านใหม่"> <span
							class="input-group-text"> <i class="bi bi-eye-slash"
							onclick="togglePassword(this, 'adv_password')"></i>
						</span>
					</div>
					<span id="passwordError" class="error-message"></span> <label
						class="form-label mt-2">ยืนยันรหัสผ่าน:</label>
					<div class="input-group">
						<input type="password" id="confirm_password"
							name="confirm_password" class="form-control"
							placeholder="ยืนยันรหัสผ่านใหม่"> <span
							class="input-group-text"> <i class="bi bi-eye-slash"
							onclick="togglePassword(this, 'confirm_password')"></i>
						</span>
					</div>
					<span id="confirmPasswordError" class="error-message"></span>
				</div>
			</div>

			<!-- ปุ่ม -->
			<div class="btn-group">
				<button type="submit" class="btn btn-success">บันทึก</button>
				<a href="${pageContext.request.contextPath}/advisor/profile"
					class="btn btn-danger">ยกเลิก</a>
			</div>
		</form>
	</div>

	<script>
		// Debug: แสดง URL ที่จะ submit
		console.log("Context Path:", "${pageContext.request.contextPath}");
		console.log("Form will submit to:", document.getElementById('editAdvisorForm').action);

		// Toggle password visibility
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

		// Validation
		const form = document.getElementById('editAdvisorForm');
		const prefixInput = document.getElementById('prefix');
		const firstNameInput = document.getElementById('firstName');
		const lastNameInput = document.getElementById('lastName');
		const emailInput = document.getElementById('email');
		const passwordInput = document.getElementById('adv_password');
		const confirmPasswordInput = document.getElementById('confirm_password');

		const prefixError = document.getElementById('prefixError');
		const firstNameError = document.getElementById('firstNameError');
		const lastNameError = document.getElementById('lastNameError');
		const emailError = document.getElementById('emailError');
		const passwordError = document.getElementById('passwordError');
		const confirmPasswordError = document.getElementById('confirmPasswordError');

		function validatePrefix() {
			const prefix = prefixInput.value.trim();
			prefixError.textContent = "";
			if (!prefix) { 
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
		        firstNameError.textContent = "*ชื่อห้ามมีช่องว่าง";
		        return false;
		    }
		    else if (firstName.length < 1 || firstName.length > 50) {
		        firstNameError.textContent = "*ชื่อต้องมีความยาวระหว่าง 1-50 ตัวอักษร";
		        return false;
		    }
		    // เช็คว่ามีแต่ตัวอักษรไทยหรืออังกฤษเท่านั้น (ไม่อนุญาตให้มีช่องว่าง)
		    else if (!/^[ก-๙]+$/.test(firstName)) {
		        firstNameError.textContent = "*ชื่อต้องเป็นภาษาไทยเท่านั้น";
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
		        lastNameError.textContent = "*นามสกุลห้ามมีช่องว่าง";
		        return false;
		    }
		    else if (lastName.length < 1 || lastName.length > 50) {
		        lastNameError.textContent = "*นามสกุลต้องมีความยาวระหว่าง 1-50 ตัวอักษร";
		        return false;
		    }
		    // เช็คว่ามีแต่ตัวอักษรไทยหรืออังกฤษเท่านั้น (ไม่อนุญาตให้มีช่องว่าง)
		    else if (!/^[ก-๙]+$/.test(lastName)) {
		        lastNameError.textContent = "*นามสกุลต้องเป็นภาษาไทยเท่านั้น";
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
		    const password = passwordInput.value;
		    passwordError.textContent = "";
		    
		    // ถ้าไม่ได้กรอก (เว้นว่างไว้) ให้ผ่าน เพราะอาจจะไม่ต้องการเปลี่ยนรหัสผ่าน
		    if (password === "") {
		        return true;
		    }
		    
		    const trimmedPassword = password.trim();
		    
		    // เช็คห้ามมีช่องว่าง
		    if (/\s/.test(password)) {
		        passwordError.textContent = "*รหัสผ่านห้ามมีช่องว่าง";
		        return false;
		    }
		    // เช็คห้ามมีภาษาไทย
		    else if (/[ก-๙]/.test(password)) {
		        passwordError.textContent = "*รหัสผ่านห้ามเป็นตัวอักษรภาษาไทย";
		        return false;
		    }
		    // เช็คความยาว
		    else if (trimmedPassword.length < 5) {
		        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวอย่างน้อย 5 ตัวอักษร";
		        return false;
		    }
		    else if (trimmedPassword.length > 12) {
		        passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวไม่เกิน 12 ตัวอักษร";
		        return false;
		    }
		    
		    return true;
		}

		// Validate confirm password
		function validateConfirmPassword() {
		    const password = passwordInput.value;
		    const confirmPassword = confirmPasswordInput.value;
		    confirmPasswordError.textContent = "";
		    
		    // ถ้าไม่ได้กรอกรหัสผ่านทั้งคู่ ให้ผ่าน
		    if (password === "" && confirmPassword === "") {
		        return true;
		    }
		    
		    // ถ้ากรอกรหัสผ่านใหม่แล้ว ต้องกรอกยืนยันรหัสผ่านด้วย
		    if (password !== "" && confirmPassword === "") {
		        confirmPasswordError.textContent = "*กรุณายืนยันรหัสผ่าน";
		        return false;
		    }
		    
		    // ถ้ากรอกยืนยันรหัสผ่าน แต่ไม่ได้กรอกรหัสผ่านใหม่
		    if (password === "" && confirmPassword !== "") {
		        confirmPasswordError.textContent = "*กรุณากรอกรหัสผ่านใหม่ก่อน";
		        return false;
		    }
		    
		    // เช็คว่าตรงกันหรือไม่
		    if (password !== confirmPassword) {
		        confirmPasswordError.textContent = "*รหัสผ่านไม่ตรงกัน";
		        return false;
		    }
		    
		    return true;
		}

		// Event listeners
		prefixInput.addEventListener('change', validatePrefix);
		firstNameInput.addEventListener('blur', validateFirstName);
		lastNameInput.addEventListener('blur', validateLastName);
		emailInput.addEventListener('blur', validateEmail);
		passwordInput.addEventListener('blur', function() {
		    validatePassword();
		    // ถ้ามีการกรอกยืนยันรหัสผ่านแล้ว ให้ตรวจสอบใหม่
		    if (confirmPasswordInput.value) {
		        validateConfirmPassword();
		    }
		});
		confirmPasswordInput.addEventListener('blur', validateConfirmPassword);

		// Form submit handler
		form.addEventListener('submit', function(e) {
			e.preventDefault();
			
			console.log("Form submit triggered");

			const isValid = validatePrefix() && 
							validateFirstName() && 
							validateLastName() &&
							validateEmail() && 
							validatePassword() &&
							validateConfirmPassword();
			
			console.log("Validation result:", isValid);

			if (isValid) {
				console.log("Submitting form to:", form.action);
				console.log("Form data:", new FormData(form));
				
				// ใช้ native submit
				HTMLFormElement.prototype.submit.call(form);
			} else {
				console.log("Validation failed");
			}
		});
	</script>
</body>
</html>