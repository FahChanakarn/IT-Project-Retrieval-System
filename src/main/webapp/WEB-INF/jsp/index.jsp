<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Calendar"%>
<%
int thisYear = Calendar.getInstance().get(Calendar.YEAR) + 543;
request.setAttribute("currentYear", thisYear);
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Information Technology - MJU</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<!-- Google Fonts: Kanit -->
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">

<!-- Bootstrap 5 CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">

<!-- Bootstrap Icons -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
<!-- Bootstrap JS -->
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<!-- CSS -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/assets/css/index.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">

</head>
<body>

	<!-- Header -->
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<!-- Main Content -->
	<div class="main-wrapper">
		<div class="row g-0">
			<!-- Sidebar -->
			<div class="col-md-3 sidebar-wrapper">
				<!-- ✅ เปลี่ยนเป็น POST และเอา action ออก (จะใช้ JavaScript submit) -->
				<form id="filterForm"
					action="${pageContext.request.contextPath}/filterProjects"
					method="post">
					<!-- Filter sections -->
					<div class="filter-section">
						<div class="filter-heading-main">
							<i class="bi bi-sliders2-vertical"></i> เงื่อนไขในการค้นหาโครงงาน
						</div>
						<div class="filter-title">ประเภทโครงงาน</div>
						<select class="form-select filter-input" name="projectType">
							<option value="">เลือกประเภทโครงงาน</option>
							<c:forEach var="type" items="${projectTypes}">
								<option value="${type}"
									${type == selectedProjectType ? 'selected' : ''}>${type}</option>
							</c:forEach>
						</select>
					</div>

					<div class="filter-section">
						<div class="filter-title">อาจารย์ที่ปรึกษา</div>
						<c:forEach var="advisor" items="${advisors}">
							<div class="form-check">
								<input class="form-check-input filter-input" type="checkbox"
									name="advisorIds" value="${advisor.advisorId}"
									id="advisor_${advisor.advisorId}"
									<c:if test="${not empty selectedAdvisorIds && selectedAdvisorIds.contains(advisor.advisorId.toString())}">checked</c:if>>
								<label class="form-check-label"
									for="advisor_${advisor.advisorId}">${advisor.adv_prefix}
									${advisor.adv_firstName} ${advisor.adv_lastName}</label>
							</div>
						</c:forEach>
					</div>

					<div class="filter-section">
						<div class="filter-title">ช่วงปีการศึกษา</div>
						<div class="row">
							<div class="col-6">
								<select class="form-select filter-input" name="startYear">
									<option value="">ปีเริ่มต้น</option>
									<c:forEach var="year" begin="2562" end="${currentYear}">
										<option value="${year}"
											<c:if test="${year == selectedStartYear}">selected</c:if>>${year}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-6">
								<select class="form-select filter-input" name="endYear">
									<option value="">ปีสิ้นสุด</option>
									<c:forEach var="year" begin="2562" end="${currentYear}">
										<option value="${year}"
											<c:if test="${year == selectedEndYear}">selected</c:if>>${year}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>

					<!-- ✅ 1. เครื่องมือการเขียนโปรแกรม -->
					<div class="filter-section">
						<div class="filter-title">เครื่องมือการเขียนโปรแกรม</div>
						<c:forEach var="lang" items="${programmingLangs}">
							<div class="form-check">
								<input class="form-check-input filter-input" type="checkbox"
									name="languages" value="${lang.toolsName}"
									id="lang_${lang.toolsId}"
									<c:if test="${not empty selectedLanguages && selectedLanguages.contains(lang.toolsName)}">checked</c:if>>
								<label class="form-check-label" for="lang_${lang.toolsId}">${lang.toolsName}</label>
							</div>
						</c:forEach>
					</div>

					<!-- ✅ 2. เครื่องมือการทดสอบ (ใหม่) -->
					<div class="filter-section">
						<div class="filter-title">เครื่องมือการทดสอบ</div>
						<c:forEach var="testTool" items="${testingTools}">
							<div class="form-check">
								<input class="form-check-input filter-input" type="checkbox"
									name="testingTools" value="${testTool.toolsName}"
									id="test_${testTool.toolsId}"
									<c:if test="${not empty selectedTestingTools && selectedTestingTools.contains(testTool.toolsName)}">checked</c:if>>
								<label class="form-check-label" for="test_${testTool.toolsId}">${testTool.toolsName}</label>
							</div>
						</c:forEach>
					</div>

					<!-- ✅ 3. ซอฟต์แวร์ฐานข้อมูล -->
					<div class="filter-section">
						<div class="filter-title">ซอฟต์แวร์ฐานข้อมูล</div>
						<c:forEach var="db" items="${dbmsLangs}">
							<div class="form-check">
								<input class="form-check-input filter-input" type="checkbox"
									name="databases" value="${db.toolsName}" id="db_${db.toolsId}"
									<c:if test="${not empty selectedDatabases && selectedDatabases.contains(db.toolsName)}">checked</c:if>>
								<label class="form-check-label" for="db_${db.toolsId}">${db.toolsName}</label>
							</div>
						</c:forEach>
					</div>

					<div class="filter-section">
						<div class="filter-title">สถานะการทดสอบ</div>
						<div class="form-check">
							<input class="form-check-input filter-input" type="radio"
								name="testingStatus" value="1" id="tested"
								<c:if test="${selectedTestingStatus == '1'}">checked</c:if>>
							<label class="form-check-label" for="tested">ถูกทดสอบแล้ว</label>
						</div>
						<div class="form-check">
							<input class="form-check-input filter-input" type="radio"
								name="testingStatus" value="0" id="notTested"
								<c:if test="${selectedTestingStatus == '0'}">checked</c:if>>
							<label class="form-check-label" for="notTested">ยังไม่ถูกทดสอบ</label>
						</div>
					</div>
				</form>
			</div>

			<!-- Main Content -->
			<div class="col-md-9">
				<div class="container">
					<div class="search-container">
						<h3 class="mb-3">ค้นหาโครงงาน</h3>
						<form id="searchForm"
							action="${pageContext.request.contextPath}/searchProjects"
							method="get">
							<div class="position-relative mb-5">
								<input type="text" id="searchInput"
									class="form-control search-box"
									placeholder="ระบุชื่อโครงงาน หรือคำค้นหา" name="keyword"
									value="${keyword}"> <i class="bi bi-search search-icon"
									id="searchIcon" style="cursor: pointer;"></i>
							</div>
						</form>

						<!-- แสดงจำนวนโปรเจค -->
						<c:if test="${not empty projects}">
							<div class="mb-3">
								<small class="text-muted">แสดง ${projects.size()} จาก
									${totalProjects} โครงงาน (หน้า ${currentPage} จาก
									${totalPages})</small>
							</div>
						</c:if>

						<c:forEach var="project" items="${projects}">
							<div class="card mb-3 project-card">
								<div class="card-body">
									<div class="row">
										<div class="col-md-9">
											<h5 class="card-title">${project.proj_NameTh}</h5>
											<h5 class="card-title">${project.proj_NameEn}</h5>

											<c:forEach var="student" items="${project.student496s}">
												<p class="card-text mb-0">ผู้จัดทำ: ${student.stuId}
													${student.stu_prefix}${student.stu_firstName}
													${student.stu_lastName}</p>
											</c:forEach>

											<p class="card-text mb-0">ประเภทโครงงาน:
												${project.projectType}</p>

											<p class="card-text mb-0">อาจารย์ที่ปรึกษา:
												${project.advisor.adv_prefix}
												${project.advisor.adv_firstName}
												${project.advisor.adv_lastName}</p>

											<p class="card-text mb-0">ภาคเรียน: ${project.semester}</p>
										</div>

										<div class="col-md-3 text-end align-self-center">
											<!-- ✅ เปลี่ยนเป็น POST -->
											<form
												action="${pageContext.request.contextPath}/viewAbstract"
												method="POST" style="display: inline;">
												<input type="hidden" name="projectId"
													value="${project.projectId}">
												<button type="submit" class="btn btn-primary btn-sm">ดูรายละเอียด</button>
											</form>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>

						<!-- Pagination -->
						<c:if test="${totalPages > 1}">
							<nav aria-label="Project pagination">
								<ul class="pagination justify-content-center">
									<!-- Previous Button -->
									<li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
										<a class="page-link" href="#"
										onclick="goToPage(${currentPage - 1}); return false;"
										aria-label="Previous"> <span aria-hidden="true">&laquo;</span>
									</a>
									</li>

									<!-- Page Numbers -->
									<c:forEach var="i" begin="1" end="${totalPages}">
										<c:choose>
											<c:when test="${i == currentPage}">
												<li class="page-item active"><span class="page-link">${i}</span>
												</li>
											</c:when>
											<c:when
												test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
												<li class="page-item"><a class="page-link" href="#"
													onclick="goToPage(${i}); return false;">${i}</a></li>
											</c:when>
											<c:when
												test="${i == currentPage - 3 || i == currentPage + 3}">
												<li class="page-item disabled"><span class="page-link">...</span>
												</li>
											</c:when>
										</c:choose>
									</c:forEach>

									<!-- Next Button -->
									<li
										class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
										<a class="page-link" href="#"
										onclick="goToPage(${currentPage + 1}); return false;"
										aria-label="Next"> <span aria-hidden="true">&raquo;</span>
									</a>
									</li>
								</ul>
							</nav>
						</c:if>

						<c:if test="${empty projects}">
							<div class="alert alert-warning mt-3">
								<strong>ไม่พบข้อมูลโครงงานที่ค้นหา</strong>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- ✅ Welcome Popup Modal -->
	<div class="modal fade" id="welcomeModal" tabindex="-1"
		aria-labelledby="welcomeModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content">
				<div class="modal-header bg-primary text-white">
					<h5 class="modal-title" id="welcomeModalLabel">
						<i class="bi bi-info-circle-fill me-2"></i>ขั้นตอนการดำเนินการ
					</h5>
					<button type="button" class="btn-close btn-close-white"
						data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div class="alert alert-info mb-4">
						<i class="bi bi-hand-thumbs-up-fill me-2"></i> <strong>ยินดีต้อนรับ!</strong>
						กรุณาดำเนินการตามขั้นตอนด้านล่าง
					</div>

					<div class="steps-container">
						<div class="step-item mb-3">
							<div class="d-flex align-items-start">
								<div class="step-number">1</div>
								<div class="step-content">
									<h6 class="fw-bold mb-2">แก้ไขบทคัดย่อและข้อมูลโครงงาน</h6>
									<p class="text-muted mb-0">เมื่อเข้าสู่ระบบแล้ว
										กรุณาแก้ไขบทคัดย่อและข้อมูลโครงงานของท่าน</p>
								</div>
							</div>
						</div>

						<div class="step-item mb-3">
							<div class="d-flex align-items-start">
								<div class="step-number">2</div>
								<div class="step-content">
									<h6 class="fw-bold mb-2">รอการอนุมัติจากอาจารย์ที่ปรึกษา</h6>
									<p class="text-muted mb-0">เมื่อแก้ไขเสร็จสิ้น
										ต้องรอดำเนินการอนุมัติในการอัปโหลดไฟล์เอกสารจากอาจารย์ที่ปรึกษาของท่าน</p>
								</div>
							</div>
						</div>

						<div class="step-item mb-3">
							<div class="d-flex align-items-start">
								<div class="step-number">3</div>
								<div class="step-content">
									<h6 class="fw-bold mb-2">อัปโหลดไฟล์เอกสารและวิดีโอ</h6>
									<p class="text-muted mb-0">หากอาจารย์ที่ปรึกษาของท่านได้ทำการอนุมัติแล้ว
										ท่านจึงจะสามารถอัปโหลดไฟล์เอกสาร
										และวิดีโอตัวอย่างการใช้งานโปรแกรมได้</p>
								</div>
							</div>
						</div>
					</div>

					<div class="alert alert-warning mt-4 mb-0">
						<i class="bi bi-exclamation-triangle-fill me-2"></i> <strong>หมายเหตุ:</strong>
						ท่านสามารถแก้ไขข้อมูลส่วนตัวของตนเองได้ ตรงเมนู <strong>"แก้ไขข้อมูลส่วนตัว"</strong>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary"
						data-bs-dismiss="modal">
						<i class="bi bi-check-circle me-2"></i>เข้าใจแล้ว
					</button>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />

	<script>
// ✅ เก็บค่าสำหรับตรวจสอบว่าเป็นการ search หรือ filter
const currentKeyword = "${not empty keyword ? keyword : ''}";
const hasFilter = ${not empty selectedProjectType or not empty selectedAdvisorIds or not empty selectedSemesters or not empty selectedLanguages or not empty selectedTestingTools or not empty selectedDatabases or not empty selectedTestingStatus or not empty selectedStartYear or not empty selectedEndYear};
const totalPages = ${not empty totalPages ? totalPages : 1};

// ✅ ฟังก์ชันสำหรับเปลี่ยนหน้า
function goToPage(page) {
  if (page < 1 || page > totalPages) {
    return;
  }
  
  // ✅ ถ้ามีการ search
  if (currentKeyword && currentKeyword.trim() !== "") {
    window.location.href = "${pageContext.request.contextPath}/searchProjects?keyword=" + 
                           encodeURIComponent(currentKeyword) + "&page=" + page;
  } 
  // ✅ ถ้ามีการ filter
  else if (hasFilter) {
    window.location.href = "${pageContext.request.contextPath}/filterProjects?page=" + page;
  } 
  // ✅ หน้าปกติ (ไม่มีทั้ง search และ filter)
  else {
    window.location.href = "${pageContext.request.contextPath}/searchProjects?page=" + page;
  }
}

document.addEventListener("DOMContentLoaded", function() {
  // ✅ แสดง popup เมื่อ login เข้ามาใหม่
  <c:if test="${showWelcomePopup}">
    const modalElement = document.getElementById('welcomeModal');
    if (modalElement) {
      var welcomeModal = new bootstrap.Modal(modalElement);
      welcomeModal.show();
    }
  </c:if>
  
  // ✅ Filter form auto submit (เมื่อเปลี่ยนค่า filter)
  document.querySelectorAll(".form-select, .form-check-input").forEach(el => {
    el.addEventListener("change", () => {
      document.getElementById("filterForm").submit();
    });
  });

  // ✅ Search functionality
  const searchInput = document.getElementById("searchInput");
  const searchIcon = document.getElementById("searchIcon");

  searchInput.addEventListener("keypress", function(event) {
    if (event.key === "Enter") {
      event.preventDefault();
      if (searchInput.value.trim() !== "") {
        document.getElementById("searchForm").submit();
      } else {
        alert("กรุณาระบุชื่อโครงงาน หรือคำค้นหา");
      }
    }
  });

  searchIcon.addEventListener("click", function() {
    if (searchInput.value.trim() !== "") {
      document.getElementById("searchForm").submit();
    } else {
      alert("กรุณาระบุชื่อโครงงาน หรือคำค้นหา");
    }
  });
});
</script>
</body>
</html>