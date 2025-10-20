<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายการโครงงานทั้งหมด</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/listStudentProject.css">
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
		<h5 class="fw-bold">จัดการโครงงาน / รายการโครงงานทั้งหมด</h5>
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

		<!-- ✅ เพิ่ม c:choose เหมือน listProjectStudent -->
		<c:choose>
			<c:when test="${empty projectGroups}">
				<div class="alert alert-warning text-center mt-4">
					ไม่พบข้อมูลโครงงานสำหรับภาคเรียนที่เลือก</div>
			</c:when>
			<c:otherwise>
				<table class="table table-bordered align-middle text-center">
					<thead class="table-light">
						<tr>
							<th style="width: 8%;">รหัสนักศึกษา</th>
							<th style="width: 14%;">ชื่อ - สกุล</th>
							<th style="width: 22%;">หัวข้อโครงงาน</th>
							<th style="width: 10%;">รายละเอียด</th>
							<th style="width: 11%;">อนุมัติการอัปโหลด</th>
							<th style="width: 11%;">การทดสอบระบบ</th>
							<th style="width: 8%;">ลบโครงงาน</th>
						</tr>
					</thead>
					<tbody>
						<!-- ✅ เปลี่ยนจาก studentProjects เป็น projectGroups -->
						<c:forEach var="group" items="${projectGroups}">
							<c:set var="studentCount" value="${group.students.size()}" />
							<c:forEach var="student" items="${group.students}"
								varStatus="status">
								<tr>
									<td>${student.studentId}</td>
									<td>${student.prefix}${student.firstName}
										${student.lastName}</td>

									<!-- Merge Cell สำหรับหัวข้อโครงงาน -->
									<c:if test="${status.index == 0}">
										<td rowspan="${studentCount}">${group.projectName}</td>
									</c:if>

									<!-- Merge Cell สำหรับปุ่มรายละเอียด -->
									<c:if test="${status.index == 0}">
										<td rowspan="${studentCount}"><a
											class="btn btn-primary btn-sm"
											href="${pageContext.request.contextPath}/admin/viewProjectDetail?projectId=${group.projectId}">
												รายละเอียด </a></td>
									</c:if>

									<!-- Merge Cell สำหรับการอนุมัติ -->
									<c:if test="${status.index == 0}">
										<td rowspan="${studentCount}"><c:choose>
												<c:when test="${group.approveStatus == 'approved'}">
													<button type="button" class="btn btn-success btn-sm"
														disabled>
														<i class="fas fa-check"></i> อนุมัติแล้ว
													</button>
												</c:when>
												<c:otherwise>
													<button type="button"
														class="btn btn-warning btn-sm approve-btn"
														data-project-id="${group.projectId}"
														data-project-name="${group.projectName}">
														<i class="fas fa-clock"></i> รออนุมัติ
													</button>
												</c:otherwise>
											</c:choose></td>
									</c:if>

									<!-- Merge Cell สำหรับการทดสอบระบบ -->
									<c:if test="${status.index == 0}">
										<td rowspan="${studentCount}"><c:choose>
												<c:when test="${group.testingStatus == '1'}">
													<button type="button"
														class="btn btn-success btn-sm testing-btn"
														data-project-id="${group.projectId}"
														data-project-name="${group.projectName}"
														data-current-status="1">
														<i class="fas fa-check-circle"></i> ถูกทดสอบแล้ว
													</button>
												</c:when>
												<c:otherwise>
													<button type="button"
														class="btn btn-secondary btn-sm testing-btn"
														data-project-id="${group.projectId}"
														data-project-name="${group.projectName}"
														data-current-status="0">
														<i class="fas fa-times-circle"></i> ยังไม่ถูกทดสอบ
													</button>
												</c:otherwise>
											</c:choose></td>
									</c:if>

									<!-- Merge Cell สำหรับปุ่มลบโครงงาน -->
									<c:if test="${status.index == 0}">
										<td rowspan="${studentCount}">
											<button type="button"
												class="btn btn-danger btn-sm delete-btn"
												data-project-id="${group.projectId}"
												data-project-name="${group.projectName}">
												<i class="fas fa-trash-alt"></i> ลบ
											</button>
										</td>
									</c:if>
								</tr>
							</c:forEach>
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
		$(document).ready(function() {
			// ========== การอนุมัติโครงงาน ==========
			$(".approve-btn").click(function() {
				var btn = $(this);
				var projectId = btn.data("project-id");
				var projectName = btn.data("project-name") || 'ไม่ระบุชื่อ';

				Swal.fire({
					title: 'ยืนยันการอนุมัติ',
					html: 'คุณต้องการอนุมัติโครงงาน<br><strong>"' + projectName + '"</strong><br>หรือไม่?',
					icon: 'warning',
					showCancelButton: true,
					confirmButtonColor: '#28a745',
					cancelButtonColor: '#6c757d',
					confirmButtonText: 'ยืนยัน',
					cancelButtonText: 'ยกเลิก',
					reverseButtons: true
				}).then((result) => {
					if (result.isConfirmed) {
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
							url: "${pageContext.request.contextPath}/admin/approveUploadAjax",
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
										btn.removeClass('btn-warning').addClass('btn-success');
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

			// ========== การทดสอบระบบ ==========
			$(".testing-btn").click(function() {
				var btn = $(this);
				var projectId = btn.data("project-id");
				var projectName = btn.data("project-name") || 'ไม่ระบุชื่อ';
				var currentStatus = btn.data("current-status");
				var newStatus = currentStatus === "1" ? "0" : "1";
				var actionText = newStatus === "1" ? "ทำเครื่องหมายว่าถูกทดสอบแล้ว" : "ทำเครื่องหมายว่ายังไม่ถูกทดสอบ";

				Swal.fire({
					title: 'ยืนยันการเปลี่ยนสถานะ',
					html: 'คุณต้องการ<strong>' + actionText + '</strong><br>สำหรับโครงงาน<br><strong>"' + projectName + '"</strong><br>หรือไม่?',
					icon: 'question',
					showCancelButton: true,
					confirmButtonColor: '#007bff',
					cancelButtonColor: '#6c757d',
					confirmButtonText: 'ยืนยัน',
					cancelButtonText: 'ยกเลิก',
					reverseButtons: true
				}).then((result) => {
					if (result.isConfirmed) {
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
							url: "${pageContext.request.contextPath}/admin/updateTestingStatusAjax",
							method: "POST",
							data: { 
								projectId: projectId,
								testingStatus: newStatus
							},
							success: function(response) {
								if (response === "success") {
									Swal.fire({
										title: 'สำเร็จ!',
										text: 'อัปเดตสถานะการทดสอบเรียบร้อยแล้ว',
										icon: 'success',
										confirmButtonText: 'ตกลง'
									}).then(() => {
										if (newStatus === "1") {
											btn.html('<i class="fas fa-check-circle"></i> ถูกทดสอบแล้ว');
											btn.removeClass('btn-secondary').addClass('btn-success');
											btn.data("current-status", "1");
										} else {
											btn.html('<i class="fas fa-times-circle"></i> ยังไม่ถูกทดสอบ');
											btn.removeClass('btn-success').addClass('btn-secondary');
											btn.data("current-status", "0");
										}
									});
								} else {
									Swal.fire({
										title: 'เกิดข้อผิดพลาด',
										text: 'ไม่สามารถอัปเดตสถานะได้ กรุณาลองใหม่',
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

			// ========== การลบโครงงาน ==========
			$(".delete-btn").click(function() {
				var btn = $(this);
				var projectId = btn.data("project-id");
				var projectName = btn.data("project-name") || 'ไม่ระบุชื่อ';

				Swal.fire({
					title: 'ยืนยันการลบโครงงาน',
					html: 'คุณแน่ใจหรือไม่ที่จะลบโครงงาน<br><strong>"' + projectName + '"</strong><br><span class="text-danger">การดำเนินการนี้ไม่สามารถย้อนกลับได้</span>',
					icon: 'warning',
					showCancelButton: true,
					confirmButtonColor: '#dc3545',
					cancelButtonColor: '#6c757d',
					confirmButtonText: 'ยืนยันการลบ',
					cancelButtonText: 'ยกเลิก',
					reverseButtons: true
				}).then((result) => {
					if (result.isConfirmed) {
						Swal.fire({
							title: 'กำลังลบข้อมูล...',
							text: 'โปรดรอสักครู่',
							icon: 'info',
							allowOutsideClick: false,
							showConfirmButton: false,
							willOpen: () => {
								Swal.showLoading();
							}
						});

						$.ajax({
							url: "${pageContext.request.contextPath}/admin/deleteProject",
							method: "POST",
							data: { projectId: projectId },
							success: function(response) {
								if (response === "success") {
									Swal.fire({
										title: 'ลบสำเร็จ!',
										text: 'ลบโครงงานเรียบร้อยแล้ว',
										icon: 'success',
										confirmButtonText: 'ตกลง'
									}).then(() => {
										location.reload();
									});
								} else {
									Swal.fire({
										title: 'เกิดข้อผิดพลาด',
										text: 'ไม่สามารถลบโครงงานได้ กรุณาลองใหม่',
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

		// แสดง Alert เมื่อกลับมาจากการดำเนินการ
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