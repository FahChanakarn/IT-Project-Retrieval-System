<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>อัปโหลดไฟล์เอกสาร</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/uploadFileAndVideo.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<style>
.error-message {
	color: #dc3545;
	font-size: 0.875rem;
	margin-top: 0.25rem;
	display: none;
}

.error-message.show {
	display: block;
}

.form-control.is-invalid, .form-select.is-invalid {
	border-color: #dc3545;
}

.form-control.is-valid, .form-select.is-valid {
	border-color: #198754;
}
</style>
<script>
	function toggleUploadType() {
		const type = document.getElementById("fileType").value;
		const fileGroup = document.getElementById("fileGroup");
		const videoGroup = document.getElementById("videoGroup");
		const labelName = document.getElementById("fileNameLabel");
		const fileNameInput = document.querySelector('input[name="fileName"]');

		// ซ่อน error messages ทั้งหมด
		clearAllErrors();

		if (type === "video") {
			fileGroup.style.display = "none";
			videoGroup.style.display = "block";
			labelName.innerText = "ชื่อวิดีโอ :";
			fileNameInput.placeholder = "ตัวอย่างการใช้งานโปรแกรม";
		} else {
			fileGroup.style.display = "block";
			videoGroup.style.display = "none";
			labelName.innerText = "ชื่อไฟล์ :";
			fileNameInput.placeholder = "เช่น บทที่ 1 บทนำ";
		}
	}

	function showError(inputElement, errorElement, message) {
		inputElement.classList.add('is-invalid');
		inputElement.classList.remove('is-valid');
		errorElement.textContent = message;
		errorElement.classList.add('show');
	}

	function clearError(inputElement, errorElement) {
		inputElement.classList.remove('is-invalid');
		inputElement.classList.add('is-valid');
		errorElement.classList.remove('show');
	}

	function clearAllErrors() {
		document.querySelectorAll('.form-control, .form-select').forEach(input => {
			input.classList.remove('is-invalid', 'is-valid');
		});
		document.querySelectorAll('.error-message').forEach(error => {
			error.classList.remove('show');
		});
	}

	function validateForm(event) {
		event.preventDefault();
		clearAllErrors();
		
		let isValid = true;
		const fileType = document.getElementById("fileType").value;
		const fileNameInput = document.querySelector('input[name="fileName"]');
		const fileName = fileNameInput.value.trim();
		const fileNameError = document.getElementById("fileNameError");
		const fileInput = document.querySelector('input[name="file"]');
		const fileError = document.getElementById("fileError");
		const videoLinkInput = document.querySelector('input[name="videoLink"]');
		const videoLink = videoLinkInput.value.trim();
		const videoError = document.getElementById("videoError");

		// ตรวจสอบชื่อไฟล์
		if (!fileName) {
			showError(fileNameInput, fileNameError, '*กรุณากรอกชื่อไฟล์หรือชื่อวิดีโอ');
			isValid = false;
		} else if (fileName.length < 10 || fileName.length > 100) {
			showError(fileNameInput, fileNameError, 
				'*ชื่อไฟล์ต้องมีความยาว 10-100 ตัวอักษร (ปัจจุบัน: ' + fileName.length + ' ตัวอักษร)');
			isValid = false;
		} else {
			// ตรวจสอบรูปแบบชื่อไฟล์
			const namePattern = /^[ก-๙a-zA-Z0-9\s().]+$/;
			if (!namePattern.test(fileName)) {
				showError(fileNameInput, fileNameError, 
					'*ชื่อไฟล์สามารถมีได้เฉพาะภาษาไทย อังกฤษ ตัวเลข วงเล็บ () และจุด (.) เท่านั้น');
				isValid = false;
			} else {
				clearError(fileNameInput, fileNameError);
			}
		}

		// ตรวจสอบการอัปโหลดไฟล์
		if (fileType === "file") {
			if (!fileInput.files || fileInput.files.length === 0) {
				showError(fileInput, fileError, '*กรุณาเลือกไฟล์ที่ต้องการอัปโหลด');
				isValid = false;
			} else {
				const file = fileInput.files[0];
				const fileNameLower = file.name.toLowerCase();
				
				if (!fileNameLower.endsWith('.pdf')) {
					showError(fileInput, fileError, '*กรุณาอัปโหลดเฉพาะไฟล์ .pdf เท่านั้น');
					fileInput.value = '';
					isValid = false;
				} else {
					// ตรวจสอบขนาดไฟล์
					const maxSize = 10 * 1024 * 1024; // 10MB
					if (file.size > maxSize) {
						showError(fileInput, fileError, '*ขนาดไฟล์ต้องไม่เกิน 10 MB');
						isValid = false;
					} else {
						clearError(fileInput, fileError);
					}
				}
			}
		}

		// ตรวจสอบลิงก์วิดีโอ
		if (fileType === "video") {
			if (!videoLink) {
				showError(videoLinkInput, videoError, '*กรุณากรอกลิงก์วิดีโอ');
				isValid = false;
			} else {
				try {
					new URL(videoLink);
					clearError(videoLinkInput, videoError);
				} catch (e) {
					showError(videoLinkInput, videoError, 
						'*โปรดระบุ URL ที่ถูกต้อง เช่น https://example.com');
					isValid = false;
				}
			}
		}

		// ถ้าผ่านการตรวจสอบทั้งหมด ส่งฟอร์ม
		if (isValid) {
			event.target.submit();
		}
	}

	// ตรวจสอบไฟล์ทันทีเมื่อเลือก
	document.addEventListener('DOMContentLoaded', function() {
		const fileInput = document.querySelector('input[name="file"]');
		const fileError = document.getElementById("fileError");
		
		if (fileInput) {
			fileInput.addEventListener('change', function(e) {
				const file = e.target.files[0];
				if (file) {
					const fileName = file.name.toLowerCase();
					if (!fileName.endsWith('.pdf')) {
						showError(fileInput, fileError, '*กรุณาเลือกไฟล์ .pdf เท่านั้น');
						e.target.value = '';
					} else {
						const maxSize = 10 * 1024 * 1024;
						if (file.size > maxSize) {
							showError(fileInput, fileError, '*ขนาดไฟล์ต้องไม่เกิน 10 MB');
							e.target.value = '';
						} else {
							clearError(fileInput, fileError);
						}
					}
				}
			});
		}

		// Real-time validation สำหรับชื่อไฟล์
		const fileNameInput = document.querySelector('input[name="fileName"]');
		const fileNameError = document.getElementById("fileNameError");
		
		if (fileNameInput) {
			fileNameInput.addEventListener('input', function(e) {
				const value = e.target.value.trim();
				if (value.length > 0 && value.length < 10) {
					showError(fileNameInput, fileNameError, 
						'*ชื่อไฟล์ต้องมีความยาวอย่างน้อย 10 ตัวอักษร (ปัจจุบัน: ' + value.length + ' ตัวอักษร)');
				} else if (value.length > 100) {
					showError(fileNameInput, fileNameError, 
						'*ชื่อไฟล์ต้องไม่เกิน 100 ตัวอักษร (ปัจจุบัน: ' + value.length + ' ตัวอักษร)');
				} else if (value.length >= 10) {
					const namePattern = /^[ก-๙a-zA-Z0-9\s().]+$/;
					if (!namePattern.test(value)) {
						showError(fileNameInput, fileNameError, 
							'*ชื่อไฟล์สามารถมีได้เฉพาะภาษาไทย อังกฤษ ตัวเลข วงเล็บ () และจุด (.) เท่านั้น');
					} else {
						clearError(fileNameInput, fileNameError);
					}
				}
			});
		}
	});
</script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold">จัดการโครงงาน / อัปโหลดไฟล์เอกสาร</h5>
		<hr>

		<form action="${pageContext.request.contextPath}/student496/upload"
			method="post" enctype="multipart/form-data"
			onsubmit="validateForm(event)">
			<div class="row mb-3">
				<div class="col-md-3">
					<label class="form-label">เลือกประเภทไฟล์ :</label> <select
						class="form-select" name="fileType" id="fileType"
						onchange="toggleUploadType()">
						<option value="file">ไฟล์เอกสาร</option>
						<option value="video">ลิงก์วิดีโอ</option>
					</select>
				</div>
				<div class="col-md-4">
					<label class="form-label" id="fileNameLabel">ชื่อไฟล์ :</label> <input
						type="text" name="fileName" class="form-control"
						placeholder="เช่น บทที่ 1 บทนำ">
					<div id="fileNameError" class="error-message"></div>
					<small class="text-muted">*ความยาว 10-100 ตัวอักษร
						(ภาษาไทย/อังกฤษ/ตัวเลข/วงเล็บ/จุด)*</small>
				</div>
				<div class="col-md-5" id="fileGroup">
					<label class="form-label">เลือกไฟล์ที่ต้องการอัปโหลด :</label> <input
						type="file" name="file" class="form-control" accept=".pdf">
					<div id="fileError" class="error-message"></div>
					<small class="text-muted">*เฉพาะไฟล์ .pdf (ไม่เกิน 10 MB)*</small>
				</div>
				<div class="col-md-5" id="videoGroup" style="display: none;">
					<label class="form-label">ลิงก์วิดีโอ :</label> <input type="text"
						name="videoLink" class="form-control"
						placeholder="https://example.com" />
					<div id="videoError" class="error-message"></div>
					<small class="text-muted">*URL ที่ถูกต้อง เช่น
						https://youtube.com/watch?v=xxxxx*</small>
				</div>
			</div>

			<div class="d-flex gap-2">
				<button type="submit" class="btn btn-success rounded">อัปโหลด</button>
				<a href="${pageContext.request.contextPath}/student496/upload"
					class="btn btn-danger rounded">ยกเลิก</a>
			</div>
		</form>

		<br>
		<h5 class="fw-bold text-danger">อัปโหลดไฟล์เอกสาร</h5>
		<hr class="mb-4 mt-2">

		<table class="table table-bordered text-center align-middle">
			<thead class="table-info">
				<tr>
					<th>ลำดับ</th>
					<th>ชื่อไฟล์</th>
					<th>เอกสาร</th>
					<th>วันที่อัปโหลด</th>
					<th>แก้ไข</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${empty uploadList}">
						<tr>
							<td colspan="5" class="text-center text-muted py-4"><i
								class="bi bi-file-earmark-x fs-1 d-block mb-2"></i>
								ยังไม่มีไฟล์ที่อัปโหลด</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach var="item" items="${uploadList}" varStatus="loop">
							<tr>
								<td>${loop.index + 1}</td>
								<td>${item.filename}</td>
								<td><c:choose>
										<c:when test="${item.filetype == 'file'}">
											<a class="btn btn-primary btn-sm"
												href="${pageContext.request.contextPath}/download/file/${item.fileId}/${item.filename}"
												target="_blank">ดูเอกสาร</a>
										</c:when>
										<c:otherwise>
											<a class="btn btn-primary btn-sm" href="${item.filepath}"
												target="_blank">ดูวิดีโอ</a>
										</c:otherwise>
									</c:choose></td>
								<td><fmt:formatDate value="${item.sendDate}"
										pattern="dd-MM-yyyy HH:mm" /></td>
								<td><a
									href="${pageContext.request.contextPath}/student496/editFileAndVideo/${item.fileId}"
									class="btn btn-success btn-sm"> <i
										class="bi bi-pencil-square"></i>
								</a></td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>

	</div>

	<!-- แสดง popup หากมีพารามิเตอร์ success -->
	<c:if test="${not empty param.success}">
		<script>
			Swal.fire({
				icon : 'success',
				title : 'ไฟล์ของคุณถูกแก้ไขเรียบร้อย !',
				showConfirmButton : false,
				timer : 2000
			})
		</script>
	</c:if>

	<!-- แสดง popup หากอัปโหลดสำเร็จ -->
	<c:if test="${not empty param.upload}">
		<script>
			Swal.fire({
				icon : 'success',
				title : 'อัปโหลดไฟล์สำเร็จ !',
				text : 'ไฟล์ของคุณถูกอัปโหลดเรียบร้อยแล้ว',
				showConfirmButton : false,
				timer : 2000
			})
		</script>
	</c:if>

	<!-- แสดง popup หากอัปโหลดไม่สำเร็จ -->
	<c:if test="${not empty param.error}">
		<script>
			Swal.fire({
				icon : 'error',
				title : 'เกิดข้อผิดพลาด !',
				text : 'ไม่สามารถอัปโหลดไฟล์ได้ กรุณาลองใหม่อีกครั้ง',
				showConfirmButton : true
			})
		</script>
	</c:if>
</body>
</html>