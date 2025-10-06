<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
		<h5 class="fw-bold text-danger">จัดการโครงงาน /
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

		<table class="table table-bordered align-middle text-center">
			<thead class="table-light">
				<tr>
					<th style="width: 10%;">รหัสนักศึกษา</th>
					<th style="width: 20%;">ชื่อ - สกุล</th>
					<th style="width: 30%;">หัวข้อโครงงาน</th>
					<th style="width: 15%;">รายละเอียด</th>
					<th style="width: 15%;">อนุมัติการอัปโหลด</th>
					<th style="width: 10%;">ลบข้อมูล</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="project" items="${studentProjects}">
					<c:forEach var="student" items="${project.student496s}">
						<tr>
							<td>${student.stuId}</td>
							<td>${student.stu_prefix}${student.stu_firstName}
								${student.stu_lastName}</td>
							<td>${project.proj_NameTh}</td>
							<td><a class="btn btn-primary btn-sm"
								href="${pageContext.request.contextPath}/admin/viewProjectDetail?projectId=${project.projectId}">
									รายละเอียด </a></td>
							<td>
								<form method="post"
									action="${pageContext.request.contextPath}/admin/approveUpload">
									<input type="hidden" name="projectId"
										value="${project.projectId}" />

									<!-- ตรวจสอบสถานะการอนุมัติ -->
									<c:choose>
										<c:when
											test="${project.approveStatus == '1' || project.approveStatus == 'approved'}">
											<button type="button" class="btn btn-success btn-sm" disabled>
												<i class="fas fa-check"></i> อนุมัติแล้ว
											</button>
										</c:when>
										<c:otherwise>
											<button type="submit" class="btn btn-success btn-sm"
												onclick="return confirm('คุณต้องการอนุมัติโครงงานนี้หรือไม่?')">
												<i class="fas fa-check-circle"></i> รอดำเนินการอนุมัติ
											</button>
										</c:otherwise>
									</c:choose>
								</form>
							</td>
							<td>
								<button type="button" class="btn btn-danger btn-sm"
									onclick="confirmDelete(${project.projectId}, this)"
									data-project-name="<c:out value='${project.proj_NameTh}' escapeXml='true'/>">
									<i class="fas fa-trash"></i> ลบ
								</button>
							</td>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>

		<!-- Pagination (หากมี) -->
		<nav aria-label="Page navigation">
			<ul class="pagination justify-content-center">
				<c:forEach var="i" begin="1" end="${totalPages}">
					<li class="page-item ${i == currentPage ? 'active' : ''}"><a
						class="page-link" href="?page=${i}">${i}</a></li>
				</c:forEach>
			</ul>
		</nav>
	</div>

	<!-- Hidden Form สำหรับส่งข้อมูลลบ -->
	<form id="deleteForm" method="POST" style="display: none;"
		action="${pageContext.request.contextPath}/admin/removeStudent">
		<input type="hidden" id="deleteProjectId" name="projectId" />
	</form>

	<script>
		function confirmDelete(projectId, buttonElement) {
			// ดึงชื่อโครงงานจาก data attribute
			const projectName = buttonElement.getAttribute('data-project-name') || 'ไม่ระบุชื่อ';
			
			console.log('Project ID:', projectId);
			console.log('Project Name:', projectName);
			
			Swal.fire({
				title: 'ยืนยันการลบ',
				html: 'คุณต้องการลบโครงงาน<br><strong>"' + projectName + '"</strong><br>และข้อมูลที่เกี่ยวข้องทั้งหมดหรือไม่?',
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#d33',
				cancelButtonColor: '#6c757d',
				confirmButtonText: 'ใช่, ลบเลย!',
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
					
					// ส่งข้อมูลผ่าน form
					document.getElementById('deleteProjectId').value = projectId;
					document.getElementById('deleteForm').submit();
				}
			});
		}

		// ลบ function escapeHtml ออก

		// แสดง Alert เมื่อกลับมาจากการลบ
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