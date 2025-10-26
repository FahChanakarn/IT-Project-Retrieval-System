<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>แก้ไขไฟล์เอกสารหรือวิดีโอ</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/editFilendVideo.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
	// ✅ เก็บรายชื่อไฟล์ที่มีอยู่แล้วในระบบ (ยกเว้นไฟล์ปัจจุบัน)
	const existingFileNames = [
		<c:forEach var="item" items="${allFiles}" varStatus="status">
			<c:if test="${item.fileId != file.fileId}">
				"${item.filename}"<c:if test="${!status.last}">,</c:if>
			</c:if>
		</c:forEach>
	];

	// ✅ ฟังก์ชันตรวจสอบชื่อซ้ำ
	function isDuplicateFileName(fileName) {
		const trimmedName = fileName.trim();
		return existingFileNames.some(existing => 
			existing.trim().toLowerCase() === trimmedName.toLowerCase()
		);
	}

	// ✅ ฟังก์ชันแสดง/ซ่อนช่องอัปโหลดไฟล์ใหม่
	function toggleFileUpload() {
		const fileUploadSection = document.getElementById('fileUploadSection');
		const editBtn = document.getElementById('editFileBtn');
		const cancelBtn = document.getElementById('cancelEditBtn');
		
		if (fileUploadSection.style.display === 'none') {
			fileUploadSection.style.display = 'block';
			editBtn.style.display = 'none';
			cancelBtn.style.display = 'inline-block';
		} else {
			fileUploadSection.style.display = 'none';
			editBtn.style.display = 'inline-block';
			cancelBtn.style.display = 'none';
			// ล้างไฟล์ที่เลือก
			document.querySelector('input[name="newFile"]').value = '';
			clearError(document.querySelector('input[name="newFile"]'), 'fileError');
		}
	}

	document.addEventListener('DOMContentLoaded', function() {
		const form = document.querySelector('form');
		const filenameInput = document.getElementById('filename');
		const fileInput = document.querySelector('input[name="newFile"]');
		const videoLinkInput = document.querySelector('input[name="videoLink"]');
		const fileType = '${file.filetype}';

		// ฟังก์ชันแสดง error message
		window.showError = function(input, errorId, message) {
			input.classList.add('is-invalid');
			input.classList.remove('is-valid');
			const errorElement = document.getElementById(errorId);
			if (errorElement) {
				errorElement.textContent = message;
				errorElement.classList.add('show');
			}
		}

		// ฟังก์ชันล้าง error message
		window.clearError = function(input, errorId) {
			input.classList.remove('is-invalid');
			input.classList.add('is-valid');
			const errorElement = document.getElementById(errorId);
			if (errorElement) {
				errorElement.classList.remove('show');
			}
		}

		// ฟังก์ชันล้าง error ทั้งหมด
		function clearAllErrors() {
			document.querySelectorAll('.form-control').forEach(input => {
				input.classList.remove('is-invalid', 'is-valid');
			});
			document.querySelectorAll('.error-message').forEach(error => {
				error.classList.remove('show');
			});
		}

		// ตรวจสอบไฟล์ PDF
		if (fileInput) {
			fileInput.addEventListener('change', function(e) {
				const file = e.target.files[0];
				if (file) {
					const fileName = file.name.toLowerCase();
					if (!fileName.endsWith('.pdf')) {
						showError(fileInput, 'fileError', '*กรุณาเลือกไฟล์ .pdf เท่านั้น');
						e.target.value = '';
						return;
					}

					const maxSize = 10 * 1024 * 1024; // 10MB
					if (file.size > maxSize) {
						showError(fileInput, 'fileError', '*ขนาดไฟล์ต้องไม่เกิน 10 MB');
						e.target.value = '';
					} else {
						clearError(fileInput, 'fileError');
					}
				}
			});
		}

		// Real-time validation สำหรับชื่อไฟล์
		if (filenameInput) {
			filenameInput.addEventListener('input', function(e) {
				const value = e.target.value.trim();
				if (value.length > 0 && value.length < 10) {
					showError(filenameInput, 'filenameError', 
						'*ชื่อไฟล์ต้องมีความยาวอย่างน้อย 10 ตัวอักษร (ปัจจุบัน: ' + value.length + ' ตัวอักษร)');
				} else if (value.length > 100) {
					showError(filenameInput, 'filenameError', 
						'*ชื่อไฟล์ต้องไม่เกิน 100 ตัวอักษร (ปัจจุบัน: ' + value.length + ' ตัวอักษร)');
				} else if (value.length >= 10) {
					const namePattern = /^[ก-๙a-zA-Z0-9\s().]+$/;
					if (!namePattern.test(value)) {
						showError(filenameInput, 'filenameError', 
							'*ชื่อไฟล์สามารถมีได้เฉพาะภาษาไทย อังกฤษ ตัวเลข วงเล็บ () และจุด (.) เท่านั้น');
					} 
					// ✅ ตรวจสอบชื่อซ้ำแบบ Real-time
					else if (isDuplicateFileName(value)) {
						showError(filenameInput, 'filenameError', 
							'*ชื่อไฟล์นี้มีอยู่ในระบบแล้ว กรุณาใช้ชื่ออื่น');
					}
					else {
						clearError(filenameInput, 'filenameError');
					}
				}
			});
		}

		// Real-time validation สำหรับลิงก์วิดีโอ
		if (videoLinkInput) {
			videoLinkInput.addEventListener('input', function(e) {
				const value = e.target.value.trim();
				if (value.length > 0) {
					try {
						new URL(value);
						clearError(videoLinkInput, 'videoError');
					} catch (error) {
						showError(videoLinkInput, 'videoError', 
							'*โปรดระบุ URL ที่ถูกต้อง เช่น https://youtube.com/watch?v=xxxxx');
					}
				}
			});
		}

		// Validation ฟอร์มเมื่อกดบันทึก
		form.addEventListener('submit', function(e) {
			e.preventDefault();
			clearAllErrors();
			
			let isValid = true;
			const filename = filenameInput.value.trim();

			// ตรวจสอบชื่อไฟล์
			if (!filename) {
				showError(filenameInput, 'filenameError', 
					fileType === 'video' ? '*กรุณากรอกชื่อวิดีโอ' : '*กรุณากรอกชื่อไฟล์');
				isValid = false;
			} else if (filename.length < 10 || filename.length > 100) {
				showError(filenameInput, 'filenameError', 
					'*ชื่อไฟล์ต้องมีความยาว 10-100 ตัวอักษร (ปัจจุบัน: ' + filename.length + ' ตัวอักษร)');
				isValid = false;
			} else {
				const namePattern = /^[ก-๙a-zA-Z0-9\s().]+$/;
				if (!namePattern.test(filename)) {
					showError(filenameInput, 'filenameError', 
						'*ชื่อไฟล์สามารถมีได้เฉพาะภาษาไทย อังกฤษ ตัวเลข วงเล็บ () และจุด (.) เท่านั้น');
					isValid = false;
				} 
				// ✅ ตรวจสอบชื่อซ้ำ
				else if (isDuplicateFileName(filename)) {
					showError(filenameInput, 'filenameError', 
						'*ชื่อไฟล์นี้มีอยู่ในระบบแล้ว กรุณาใช้ชื่ออื่น');
					isValid = false;
				}
				else {
					clearError(filenameInput, 'filenameError');
				}
			}

			// ตรวจสอบลิงก์วิดีโอ
			if (fileType === 'video' && videoLinkInput) {
				const videoLink = videoLinkInput.value.trim();
				if (!videoLink) {
					showError(videoLinkInput, 'videoError', '*กรุณากรอกลิงก์วิดีโอ');
					isValid = false;
				} else {
					try {
						new URL(videoLink);
						clearError(videoLinkInput, 'videoError');
					} catch (error) {
						showError(videoLinkInput, 'videoError', 
							'*โปรดระบุ URL ที่ถูกต้อง เช่น https://youtube.com/watch?v=xxxxx');
						isValid = false;
					}
				}
			}

			// ส่งฟอร์มถ้าผ่านการตรวจสอบ
			if (isValid) {
				form.submit();
			}
		});
	});
</script>
<style>
.current-file-display {
	background-color: #f8f9fa;
	border: 1px solid #dee2e6;
	border-radius: 0.375rem;
	padding: 1rem;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.file-name-clickable {
	color: #0d6efd;
	text-decoration: none;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 0.5rem;
	font-weight: 500;
}

.file-name-clickable:hover {
	text-decoration: underline;
	color: #0a58ca;
}

.file-name-clickable i {
	font-size: 1.5rem;
	color: #dc3545;
}

#fileUploadSection {
	display: none;
	margin-top: 1rem;
	padding: 1rem;
	background-color: #fff3cd;
	border: 1px dashed #ffc107;
	border-radius: 0.375rem;
}

.error-message {
	color: #dc3545;
	font-size: 0.875rem;
	margin-top: 0.25rem;
	display: none;
}

.error-message.show {
	display: block;
}

.form-control.is-invalid {
	border-color: #dc3545;
}

.form-control.is-valid {
	border-color: #198754;
}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5>แก้ไขไฟล์หรือวิดีโอ</h5>
		<hr>
		<form
			action="${pageContext.request.contextPath}/student496/updateFileAndVideo"
			method="post" enctype="multipart/form-data">
			<input type="hidden" name="id" value="${file.fileId}" />

			<div class="mb-3">
				<label for="filename" class="form-label"> <c:choose>
						<c:when test="${file.filetype == 'video'}">
							ชื่อวิดีโอ :
						</c:when>
						<c:otherwise>
							ชื่อไฟล์ :
						</c:otherwise>
					</c:choose>
				</label> <input type="text" class="form-control" name="name" id="filename"
					value="${file.filename}">
				<div id="filenameError" class="error-message"></div>
				<small class="text-muted">*ความยาว 10-100 ตัวอักษร
					(ภาษาไทย/อังกฤษ/ตัวเลข/วงเล็บ/จุด)*</small>
			</div>

			<c:choose>
				<c:when test="${file.filetype == 'video'}">
					<div class="mb-3">
						<label class="form-label">ลิงก์วิดีโอ :</label> <input type="text"
							class="form-control" name="videoLink" value="${file.filepath}">
						<div id="videoError" class="error-message"></div>
						<small class="text-muted">*กรุณาระบุ URL ที่ถูกต้อง เช่น
							https://youtube.com/watch?v=xxxxx*</small>
					</div>
				</c:when>
				<c:otherwise>
					<!-- ✅ แสดงไฟล์ปัจจุบัน - คลิกดูได้โดยตรง -->
					<div class="mb-3">
						<label class="form-label">ไฟล์ปัจจุบัน :</label>
						<div class="current-file-display">
							<a class="file-name-clickable"
								href="${pageContext.request.contextPath}/download/file/${file.fileId}/${file.filename}"
								target="_blank"> <i class="bi bi-file-earmark-pdf-fill"></i>
								<span>${file.filepath}</span>
							</a>
							<div>
								<button type="button" class="btn btn-primary"
									id="editFileBtn" onclick="toggleFileUpload()">
									<i class="bi bi-pencil-square"></i> แก้ไขไฟล์
								</button>
								<button type="button" class="btn btn-secondary"
									id="cancelEditBtn" onclick="toggleFileUpload()"
									style="display: none;">
									<i class="bi bi-x-circle"></i> ยกเลิก
								</button>
							</div>
						</div>
					</div>

					<!-- ✅ ส่วนอัปโหลดไฟล์ใหม่ (ซ่อนไว้) -->
					<div id="fileUploadSection">
						<label class="form-label"> <i class="bi bi-upload"></i>
							เลือกไฟล์ใหม่ :
						</label> <input type="file" class="form-control" name="newFile"
							accept=".pdf">
						<div id="fileError" class="error-message"></div>
						<small class="text-muted d-block mt-1"> <i
							class="bi bi-info-circle"></i> *เฉพาะไฟล์ .pdf (ไม่เกิน 10 MB)*
						</small>
					</div>
				</c:otherwise>
			</c:choose>

			<div class="d-flex gap-2 mt-4">
				<button type="submit" class="btn btn-success">
					<i class="bi bi-save"></i> บันทึก
				</button>
				<a href="${pageContext.request.contextPath}/student496/upload"
					class="btn btn-danger"> <i class="bi bi-x-circle"></i> ยกเลิก
				</a>
			</div>
		</form>
	</div>

	<!-- แสดง popup หากแก้ไขสำเร็จ -->
	<c:if test="${not empty param.success}">
		<script>
			Swal.fire({
				icon: 'success',
				title: 'แก้ไขสำเร็จ !',
				text: 'ไฟล์ของคุณถูกแก้ไขเรียบร้อยแล้ว',
				showConfirmButton: false,
				timer: 2000
			})
		</script>
	</c:if>

</body>
</html>