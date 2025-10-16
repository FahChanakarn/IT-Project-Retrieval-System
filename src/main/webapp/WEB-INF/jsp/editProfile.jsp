<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<title>แก้ไขข้อมูลส่วนตัว</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
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

<!-- SweetAlert2 -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

</head>
<body>

	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<div class="path-title">${projectName}/แก้ไขข้อมูลส่วนตัว</div>

		<div class="edit-profile-box">
			<form id="editProfileForm"
				action="${pageContext.request.contextPath}/updateProfile"
				method="post" enctype="multipart/form-data">
				<div class="profile-section">
					<!-- ✅ แสดงรูปโปรไฟล์ -->
					<c:choose>
						<c:when test="${not empty sessionScope.student.stu_image}">
							<img
								src="${pageContext.request.contextPath}/profileImage/${sessionScope.student.stu_image}"
								class="profile-img" alt="Profile Image">
						</c:when>
						<c:otherwise>
							<img
								src="${pageContext.request.contextPath}/assets/images/default-profile.png"
								class="profile-img" alt="Default Profile">
						</c:otherwise>
					</c:choose>

					<div class="upload-wrapper">
						<input type="file" name="imageFile" id="imageFile"
							class="form-control-file" accept=".jpg,.jpeg,.png"> <small
							class="text-muted">ควรมีขนาด 256 x 256 px ขึ้นไป เป็นไฟล์
							.jpg หรือ .png</small>
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
						<label>ชื่อ</label> <input
							type="text" class="form-control" id="firstName"
							name="stu_firstName"
							value="${sessionScope.student.stu_firstName}"> <span
							id="firstNameError" class="error-message"></span>
					</div>
					<div class="col-md-6">
						<label>นามสกุล</label> <input
							type="text" class="form-control" id="lastName"
							name="stu_lastName" value="${sessionScope.student.stu_lastName}">
						<span id="lastNameError" class="error-message"></span>
					</div>
				</div>

				<div class="row mt-3">
					<div class="col-md-6">
						<label>รหัสผ่านใหม่ (ถ้าต้องการเปลี่ยน)</label>
						<div class="input-group">
							<input type="password" class="form-control" name="stu_password"
								id="password" placeholder="เว้นว่างไว้ถ้าไม่ต้องการเปลี่ยน">
							<button class="btn btn-outline-secondary" type="button"
								onclick="togglePassword('password')">
								<i class="bi bi-eye"></i>
							</button>
						</div>
						<span id="passwordError" class="error-message"></span>
					</div>
					<div class="col-md-6">
						<label>ยืนยันรหัสผ่านใหม่</label>
						<div class="input-group">
							<input type="password" class="form-control" id="confirmPassword"
								placeholder="เว้นว่างไว้ถ้าไม่ต้องการเปลี่ยน">
							<button class="btn btn-outline-secondary" type="button"
								onclick="togglePassword('confirmPassword')">
								<i class="bi bi-eye"></i>
							</button>
						</div>
						<span id="confirmPasswordError" class="error-message"></span>
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

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
	<script>
document.addEventListener('DOMContentLoaded', function() {
    // Elements
    const form = document.getElementById('editProfileForm');
    const firstNameInput = document.getElementById('firstName');
    const lastNameInput = document.getElementById('lastName');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const imageFileInput = document.getElementById('imageFile');
    
    const firstNameError = document.getElementById('firstNameError');
    const lastNameError = document.getElementById('lastNameError');
    const passwordError = document.getElementById('passwordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    // Toggle password visibility
    function togglePassword(id) {
        const input = document.getElementById(id);
        const icon = input.nextElementSibling.querySelector('i');
        if (input.type === "password") {
            input.type = "text";
            icon.classList.remove('bi-eye');
            icon.classList.add('bi-eye-slash');
        } else {
            input.type = "password";
            icon.classList.remove('bi-eye-slash');
            icon.classList.add('bi-eye');
        }
    }
    window.togglePassword = togglePassword;

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
        // เช็คว่ามีแต่ตัวอักษรไทยเท่านั้น
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
        // เช็คว่ามีแต่ตัวอักษรไทยเท่านั้น
        else if (!/^[ก-๙]+$/.test(lastName)) {
            lastNameError.textContent = "*นามสกุลต้องเป็นภาษาไทยเท่านั้น";
            return false;
        }
        
        // อัพเดทค่าที่ trim แล้วกลับไปที่ input
        lastNameInput.value = lastName;
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
        else if (trimmedPassword.length > 15) {
            passwordError.textContent = "*กรุณากรอกรหัสผ่านความยาวไม่เกิน 15 ตัวอักษร";
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

    // Setup image preview and validation
    function setupImagePreview() {
        const profileImg = document.querySelector('.profile-img');

        imageFileInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const allowedTypes = ['image/jpeg','image/jpg','image/png'];
                if (!allowedTypes.includes(file.type)) {
                    Swal.fire({
                        icon: 'error',
                        title: 'ไฟล์ไม่ถูกต้อง',
                        text: 'กรุณาเลือกไฟล์ JPG หรือ PNG เท่านั้น',
                        confirmButtonText: 'ตกลง'
                    });
                    e.target.value = '';
                    return;
                }

                const maxSize = 5 * 1024 * 1024;
                if (file.size > maxSize) {
                    Swal.fire({
                        icon: 'error',
                        title: 'ไฟล์ใหญ่เกินไป',
                        text: 'ไฟล์รูปภาพต้องมีขนาดไม่เกิน 5MB',
                        confirmButtonText: 'ตกลง'
                    });
                    e.target.value = '';
                    return;
                }

                const reader = new FileReader();
                reader.onload = function(e) {
                    profileImg.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    setupImagePreview();

    // Event listeners
    firstNameInput.addEventListener('blur', validateFirstName);
    lastNameInput.addEventListener('blur', validateLastName);
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
        
        const isValid = validateFirstName() && 
                        validateLastName() &&
                        validatePassword() && 
                        validateConfirmPassword();
        
        if (isValid) {
            // Submit form
            form.submit();
        }
    });

    // SweetAlert2 สำหรับ success
    <c:if test="${not empty success}">
        Swal.fire({
            icon: 'success',
            title: 'สำเร็จ',
            text: '${success}',
            confirmButtonText: 'ตกลง'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '${pageContext.request.contextPath}/searchProjects';
            }
        });
    </c:if>

    // SweetAlert2 สำหรับ error
    <c:if test="${not empty error}">
        Swal.fire({
            icon: 'error',
            title: 'เกิดข้อผิดพลาด',
            text: '${error}',
            confirmButtonText: 'ตกลง'
        });
    </c:if>
});
</script>
</body>
</html>