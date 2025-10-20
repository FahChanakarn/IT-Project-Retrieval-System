<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Import Student</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">

<!-- Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/importStudent.css">

<!-- SweetAlert -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-4 pt-4 fix-import-student-content">
		<div class="mx-auto">
			<h5>
				<strong>ข้อมูลนักศึกษา / เพิ่มข้อมูลโครงงาน</strong>
			</h5>
			<hr class="mb-4">

			<form method="post" enctype="multipart/form-data" id="importForm"
				action="${pageContext.request.contextPath}/admin/importStudentFile">

				<div class="d-flex align-items-center gap-3 flex-wrap mb-3">
					<!-- เลือกภาคเรียน -->
					<div class="d-flex align-items-center gap-2">
						<label for="semester" class="form-label fw-bold mb-0">เลือกภาคเรียน:</label>
						<select name="semester" id="semester" class="form-select w-auto"
							onchange="changeSemester()">
							<c:forEach var="sem" items="${semesterList}">
								<option value="${sem}"
									<c:if test="${sem == selectedSemester}">selected</c:if>>
									${sem}</option>
							</c:forEach>
						</select>
					</div>

					<!-- ดูไฟล์ตัวอย่าง -->
					<div>
						<a
							href="${pageContext.request.contextPath}/assets/files/example_excel.xlsx"
							class="btn btn-outline-info btn-sm" target="_blank"> <i
							class="bi bi-eye me-1"></i>ดูตัวอย่างไฟล์ Excel
						</a>
					</div>
				</div>

				<!-- เลือกไฟล์ Excel -->
				<div class="mb-4">
					<label class="form-label fw-bold">ไฟล์ที่ต้องการนำเข้า :</label> <input
						type="file" class="form-control" name="file" id="fileInput"
						accept=".xlsx,.xls">
					<div id="fileInputError" class="text-danger mt-1"
						style="display: none;">
						<small>*กรุณาเลือกไฟล์ Excel ที่ต้องการนำเข้า</small>
					</div>
				</div>

				<!-- ปุ่ม -->
				<div class="text-center">
					<button type="submit" class="btn btn-success me-2">นำเข้า</button>
					<button type="button"
						onclick="location.href='${pageContext.request.contextPath}/'"
						class="btn btn-danger">ยกเลิก</button>
				</div>
			</form>
		</div>
	</div>

	<!-- SweetAlert สำหรับข้อผิดพลาด -->
	<c:if test="${not empty error}">
		<script>
			Swal.fire({
				icon : 'error',
				title : 'เกิดข้อผิดพลาด',
				text : '${error}',
				confirmButtonText : 'ตกลง',
				confirmButtonColor : '#d33'
			});
		</script>
	</c:if>

	<!-- SweetAlert สำหรับสำเร็จ (ไม่มีข้อมูลซ้ำ) -->
	<c:if test="${not empty success and empty duplicateStudents}">
		<script>
			Swal.fire({
				icon : 'success',
				title : 'นำเข้าข้อมูลสำเร็จ',
				text : '${success}',
				confirmButtonText : 'ตกลง',
				confirmButtonColor : '#28a745'
			});
		</script>
	</c:if>

	<!-- SweetAlert สำหรับพบรหัสนักศึกษาซ้ำ -->
	<c:if test="${not empty duplicateStudents}">
		<script>
			Swal
					.fire({
						icon : 'warning',
						title : 'พบรหัสนักศึกษาซ้ำ',
						html : '<p>${success}</p><hr><p class="text-danger"><strong>รหัสนักศึกษาที่ซ้ำ:</strong></p><p>${duplicateStudents}</p>',
						confirmButtonText : 'ตกลง',
						confirmButtonColor : '#ffc107',
						width : '600px'
					});
		</script>
	</c:if>

	<script>
		// ฟังก์ชันสำหรับเปลี่ยนภาคเรียน (ไม่ส่งไฟล์)
		function changeSemester() {
			// ล้างค่าไฟล์และชื่อไฟล์
			document.getElementById('fileInput').value = '';

			// ซ่อน error messages
			document.getElementById('fileInputError').style.display = 'none';

			// Submit form โดยไม่ต้องตรวจสอบไฟล์
			document.getElementById('importForm').submit();
		}

		document
				.getElementById('fileInput')
				.addEventListener(
						'change',
						function() {
							if (this.value !== '') {
								document.getElementById('fileInputError').style.display = 'none';
							}
						});

		// ตรวจสอบก่อน submit form
		document
				.getElementById('importForm')
				.addEventListener(
						'submit',
						function(e) {
							// ถ้ามาจากการเปลี่ยน semester ให้ผ่านไปเลย
							if (e.submitter && e.submitter.type !== 'submit') {
								return true;
							}

							const fileInput = document
									.getElementById('fileInput').value;
							let hasError = false;

							// ซ่อน error messages ทั้งหมดก่อน
							document.getElementById('fileInputError').style.display = 'none';

							// ตรวจสอบการเลือกไฟล์
							if (fileInput === '') {
								document.getElementById('fileInputError').style.display = 'block';
								hasError = true;
							}

							if (hasError) {
								e.preventDefault();
								return false;
							}

							return true;
						});
	</script>
</body>
</html>