<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="isAdmin" value="${userRole == 'admin'}" />
<c:set var="baseUrl" value="${isAdmin ? '/admin' : '/advisor'}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายการโครงงานของนักศึกษา</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/listProjectStudent.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold">จัดการโครงงาน /
			รายการโครงงานทั้งหมด</h5>
		<hr>

		<!-- แสดง Alert Messages -->
		<c:if test="${not empty param.success}">
			<div class="alert alert-success alert-dismissible fade show"
				role="alert">
				${param.success}
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
		</c:if>

		<c:if test="${not empty param.error}">
			<div class="alert alert-danger alert-dismissible fade show"
				role="alert">
				${param.error}
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
		</c:if>

		<div class="mb-3"
			style="display: flex; align-items: center; gap: 8px;">
			<label class="form-label fw-bold">ภาคเรียน :</label> <select
				class="form-select custom-semester-select"
				onchange="location = '?semester=' + this.value;">
				<c:forEach var="sem" items="${semesterList}">
					<option value="${sem}" ${sem == selectedSemester ? 'selected' : ''}>${sem}</option>
				</c:forEach>
			</select>
		</div>

		<c:choose>
			<c:when test="${empty projects}">
				<div class="alert alert-warning text-center mt-4">
					ไม่พบข้อมูลโครงงานสำหรับภาคเรียนที่เลือก</div>
			</c:when>
			<c:otherwise>
				<table class="table table-bordered align-middle text-center">
					<thead class="table-light">
						<tr>
							<th style="width: 10%;">รหัสนักศึกษา</th>
							<th style="width: 20%;">ชื่อ - สกุล</th>
							<th style="width: 30%;">หัวข้อโครงงาน</th>
							<th style="width: 15%;">รายละเอียด</th>
							<th style="width: 15%;">อนุมัติการอัปโหลด</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="row" items="${projects}">
							<tr>
								<td>${row[0]}</td>
								<td>${row[1]} ${row[2]}</td>
								<td>${row[3]}</td>
								<td><a class="btn btn-primary btn-sm"
									href="${pageContext.request.contextPath}/viewAbstract?projectId=${row[4]}">
										รายละเอียด </a></td>
								<td><c:choose>
										<c:when test="${row[5] == 'approved'}">
											<button type="button" class="btn btn-success btn-sm" disabled>
												<i class="fas fa-check"></i> อนุมัติแล้ว
											</button>
										</c:when>
										<c:otherwise>
											<button type="button"
												class="btn btn-success btn-sm approve-btn"
												data-project-id="${row[4]}" data-project-name="${row[3]}">
												<i class="fas fa-check-circle"></i> รอดำเนินการอนุมัติ
											</button>
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>

				<!-- Pagination -->
				<nav aria-label="Page navigation">
					<ul class="pagination justify-content-center">
						<c:forEach var="i" begin="1" end="${totalPages}">
							<li class="page-item ${i == currentPage ? 'active' : ''}"><a
								class="page-link" href="?page=${i}&semester=${selectedSemester}">${i}</a>
							</li>
						</c:forEach>
					</ul>
				</nav>
			</c:otherwise>
		</c:choose>
	</div>

	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script>
		const baseUrl = '${baseUrl}';
		
		$(document).ready(function() {
			$(".approve-btn").click(function() {
				var btn = $(this);
				var projectId = btn.data("project-id");
				var projectName = btn.data("project-name") || 'ไม่ระบุชื่อ';

				Swal.fire({
					title: 'ยืนยันการอนุมัติ',
					html: 'คุณต้องการอนุมัติโครงงาน<br><strong>"' + projectName + '"</strong><br>หรือไม่?',
					icon: 'warning',
					showCancelButton: true,
					confirmButtonColor: '#d33',
					cancelButtonColor: '#6c757d',
					confirmButtonText: 'ใช่, อนุมัติเลย!',
					cancelButtonText: 'ยกเลิก',
					reverseButtons: true
				}).then((result) => {
					if (result.isConfirmed) {
						// แสดง loading
						Swal.fire({
							title: 'กำลังดำเนินการ...',
							text: 'โปรดรอสักครู่',
							icon: 'info',
							allowOutsideClick: false,
							showConfirmButton: false,
							willOpen: () => {
								Swal.showLoading();
							}
						});

						$.ajax({
							url: "${pageContext.request.contextPath}" + baseUrl + "/approveUploadAjax",
							method: "POST",
							data: { projectId: projectId },
							success: function(response) {
								if (response === "success") {
									Swal.fire({
										title: 'สำเร็จ!',
										text: 'อนุมัติโครงงานเรียบร้อยแล้ว',
										icon: 'success',
										confirmButtonText: 'ตกลง'
									}).then(() => {
										btn.html('<i class="fas fa-check"></i> อนุมัติแล้ว');
										btn.prop("disabled", true);
									});
								} else {
									Swal.fire({
										title: 'เกิดข้อผิดพลาด',
										text: 'ไม่สามารถอนุมัติได้ กรุณาลองใหม่',
										icon: 'error',
										confirmButtonText: 'ตกลง'
									});
								}
							},
							error: function() {
								Swal.fire({
									title: 'เกิดข้อผิดพลาด',
									text: 'เกิดข้อผิดพลาดในการส่งข้อมูล',
									icon: 'error',
									confirmButtonText: 'ตกลง'
								});
							}
						});
					}
				});
			});
		});

		// แสดง Alert เมื่อกลับมาจากการอนุมัติ
		window.addEventListener('DOMContentLoaded', function() {
			const urlParams = new URLSearchParams(window.location.search);
			if (urlParams.get('success')) {
				Swal.fire({
					title: 'สำเร็จ!',
					text: urlParams.get('success'),
					icon: 'success',
					confirmButtonText: 'ตกลง'
				});
			} else if (urlParams.get('error')) {
				Swal.fire({
					title: 'เกิดข้อผิดพลาด',
					text: urlParams.get('error'),
					icon: 'error',
					confirmButtonText: 'ตกลง'
				});
			}
		});
	</script>
</body>
</html>