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

<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/importStudent.css">

<!-- SweetAlert -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<style>
.duplicate-list {
	max-height: 300px;
	overflow-y: auto;
	text-align: left;
	padding: 1rem;
	background-color: #fff3cd;
	border-radius: 0.375rem;
	margin-top: 1rem;
}

.duplicate-item {
	padding: 0.5rem;
	margin-bottom: 0.5rem;
	background-color: white;
	border-left: 4px solid #dc3545;
	border-radius: 0.25rem;
	display: flex;
	align-items: center;
	gap: 0.5rem;
}

.duplicate-item i {
	color: #dc3545;
	font-size: 1.2rem;
}

.import-table {
	font-size: 0.9rem;
}

.import-table thead {
	position: sticky;
	top: 0;
	background-color: #d1ecf1;
	z-index: 10;
}

.table-container {
	max-height: 500px;
	overflow-y: auto;
	border: 1px solid #dee2e6;
	border-radius: 0.375rem;
}
</style>
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
					<button type="submit" class="btn btn-success me-2">
						<i class="bi bi-upload me-1"></i>นำเข้า
					</button>
					<button type="button"
						onclick="location.href='${pageContext.request.contextPath}/'"
						class="btn btn-danger">
						<i class="bi bi-x-circle me-1"></i>ยกเลิก
					</button>
				</div>
			</form>

			<!-- ✅ แสดงรายการนักศึกษาที่ Import แล้ว -->
			<c:if test="${not empty importedStudents}">
				<div class="mt-5">
					<h5 class="fw-bold text-success">
						<i class="bi bi-list-check"></i> รายการที่นำเข้าแล้ว
						(${selectedSemester})
					</h5>
					<hr class="mb-3">

					<div class="table-container">
						<table class="table table-bordered">
							<thead style="background-color: #F4A460; color: white;">
								<tr>
									<th>รหัส น.ศ.</th>
									<th>ชื่อ - นามสกุล</th>
									<th>ที่ปรึกษา</th>
									<th>หัวข้อโครงงาน</th>
									<th>ประเภท</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${empty importedStudents}">
										<tr>
											<td colspan="5" class="text-center">ไม่มีข้อมูลนักศึกษาในภาคเรียนนี้</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach var="student" items="${importedStudents}">
											<tr>
												<!-- ✅ ใช้ fn:contains() กับ List -->
												<c:set var="isDuplicate" value="false" />
												<c:forEach var="dupId" items="${duplicateStudents}">
													<c:if test="${dupId eq student.stuId}">
														<c:set var="isDuplicate" value="true" />
													</c:if>
												</c:forEach>

												<td
													style="${isDuplicate ? 'color: red; font-weight: bold;' : ''}">
													${student.stuId}</td>
												<td>${student.stu_prefix}${student.stu_firstName}
													${student.stu_lastName}</td>
												<td>${student.project.advisor.adv_prefix}
													${student.project.advisor.adv_firstName}
													${student.project.advisor.adv_lastName}</td>
												<td>${student.project.proj_NameTh}</td>
												<td>${student.project.projectType}</td>
											</tr>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>

					<div class="mt-2 text-muted">
						<small> <i class="bi bi-info-circle"></i>
							แสดงข้อมูลทั้งหมด ${importedStudents.size()} รายการ
						</small>
					</div>
				</div>
			</c:if>
		</div>
	</div>

	<!-- ✅ ย้าย JavaScript มาไว้ก่อน SweetAlert -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

	<script>
		// ฟังก์ชันสำหรับเปลี่ยนภาคเรียน
		function changeSemester() {
			document.getElementById('fileInput').value = '';
			document.getElementById('fileInputError').style.display = 'none';
			document.getElementById('importForm').submit();
		}

		// ตรวจสอบไฟล์
		document
				.getElementById('fileInput')
				.addEventListener(
						'change',
						function() {
							if (this.value !== '') {
								document.getElementById('fileInputError').style.display = 'none';
							}
						});

		// ตรวจสอบก่อน submit
		document
				.getElementById('importForm')
				.addEventListener(
						'submit',
						function(e) {
							if (e.submitter && e.submitter.type !== 'submit') {
								return true;
							}

							const fileInput = document
									.getElementById('fileInput').value;
							document.getElementById('fileInputError').style.display = 'none';

							if (fileInput === '') {
								document.getElementById('fileInputError').style.display = 'block';
								e.preventDefault();
								return false;
							}

							return true;
						});
	</script>

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
			}).then(function() {
				window.scrollTo({
					top : document.body.scrollHeight,
					behavior : 'smooth'
				});
			});
		</script>
	</c:if>

	<!-- ✅ SweetAlert สำหรับพบรหัสนักศึกษาซ้ำ (แก้ไขใหม่) -->
	<c:if test="${not empty duplicateStudents}">
		<script>
			console.log('Duplicate students detected');

			// ✅ ใช้ JSTL สร้าง array โดยตรง
			var duplicates = [];
			<c:forEach var="dupId" items="${duplicateStudents}">
			duplicates.push('${dupId}'.trim());
			</c:forEach>

			console.log('Parsed duplicates:', duplicates);

			// สร้าง HTML
			var duplicateHTML = '<div class="duplicate-list">';
			for (var i = 0; i < duplicates.length; i++) {
				if (duplicates[i]) {
					duplicateHTML += '<div class="duplicate-item">';
					duplicateHTML += '<i class="bi bi-exclamation-triangle-fill"></i>';
					duplicateHTML += '<span><strong>รหัส:</strong> '
							+ duplicates[i] + '</span>';
					duplicateHTML += '</div>';
				}
			}
			duplicateHTML += '</div>';

			Swal.fire(
					{
						icon : 'warning',
						title : 'พบรหัสนักศึกษาซ้ำ',
						html : '<p class="mb-2">${success}</p>' + '<hr>'
								+ '<p class="text-danger mb-2"><strong>'
								+ '<i class="bi bi-exclamation-circle"></i> '
								+ 'พบรหัสนักศึกษาซ้ำ ' + duplicates.length
								+ ' รายการ:' + '</strong></p>' + duplicateHTML
								+ '<p class="text-muted mt-3 small">'
								+ '<i class="bi bi-info-circle"></i> '
								+ 'รายการที่ซ้ำจะไม่ถูกนำเข้าในระบบ' + '</p>',
						confirmButtonText : 'ตกลง',
						confirmButtonColor : '#ffc107',
						width : '600px',
						customClass : {
							htmlContainer : 'text-start'
						}
					}).then(function() {
				window.scrollTo({
					top : document.body.scrollHeight,
					behavior : 'smooth'
				});
			});
		</script>
	</c:if>
</body>
</html>