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
	href="${pageContext.request.contextPath}/assets/css/Home.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
</head>
<body>

	<!-- Header -->
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<!-- Main Content -->
	<div class="container mt-4">
		<div class="row">
			<!-- Sidebar -->
			<div class="col-md-3">
				<form id="filterForm"
					action="${pageContext.request.contextPath}/filterProjects"
					method="get">
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
									<c:if test="${not empty selectedAdvisorIds && selectedAdvisorIds.contains(advisor.advisorId)}">checked</c:if>>
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

					<div class="filter-section">
						<div class="filter-title">ซอฟต์แวร์ฐานข้อมูล</div>
						<c:forEach var="db" items="${dbmsLangs}">
							<div class="form-check">
								<input class="form-check-input" type="checkbox" name="databases"
									value="${db.langName}" id="db_${db.langId}"
									<c:if test="${not empty selectedDatabases && selectedDatabases.contains(db.langName)}">checked</c:if>>
								<label class="form-check-label" for="db_${db.langId}">${db.langName}</label>
							</div>
						</c:forEach>
					</div>

					<div class="filter-section">
						<div class="filter-title">ภาษาที่ใช้</div>
						<c:forEach var="lang" items="${programmingLangs}">
							<c:if test="${lang.langType.name() == 'PROGRAMMING'}">
								<div class="form-check">
									<input class="form-check-input filter-input" type="checkbox"
										name="languages" value="${lang.langName}"
										id="lang_${lang.langId}"
										<c:if test="${not empty selectedLanguages && selectedLanguages.contains(lang.langName)}">checked</c:if>>
									<label class="form-check-label" for="lang_${lang.langId}">${lang.langName}</label>
								</div>
							</c:if>
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

			<!-- Main -->
			<div class="col-md-9">
				<div class="search-container">
					<h3 class="mb-4">ค้นหาโครงงาน</h3>
					<form id="searchForm"
						action="${pageContext.request.contextPath}/searchProjects"
						method="get">
						<div class="position-relative mb-5">
							<input type="text" id="searchInput"
								class="form-control search-box"
								placeholder="ระบุชื่อโครงงาน หรือคำค้นหา" name="keyword">
							<i class="bi bi-search search-icon" id="searchIcon"
								style="cursor: pointer;"></i>
						</div>
					</form>

					<!-- แสดงจำนวนโปรเจค -->
					<c:if test="${not empty projects}">
						<div class="mb-3">
							<small class="text-muted">แสดง ${projects.size()} จาก
								${totalProjects} โครงงาน (หน้า ${currentPage} จาก ${totalPages})</small>
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
										<a
											href="${pageContext.request.contextPath}/viewAbstract?projectId=${project.projectId}"
											class="btn btn-primary btn-sm">ดูรายละเอียด</a>
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
										<c:when test="${i == currentPage - 3 || i == currentPage + 3}">
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
	<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />


	<script>
// เก็บค่า keyword สำหรับ search pagination
const currentKeyword = "${not empty keyword ? keyword : ''}";
const totalPages = ${not empty totalPages ? totalPages : 1};

// ฟังก์ชันสำหรับเปลี่ยนหน้า
function goToPage(page) {
  console.log("goToPage called with page:", page);
  console.log("currentKeyword:", currentKeyword);
  console.log("totalPages:", totalPages);
  
  if (page < 1 || page > totalPages) {
    console.log("Page out of range");
    return;
  }
  
  // ตรวจสอบว่ามีการ search หรือ filter
  if (currentKeyword && currentKeyword.trim() !== "") {
    // กรณี search
    const url = "${pageContext.request.contextPath}/searchProjects?keyword=" + 
                encodeURIComponent(currentKeyword) + "&page=" + page;
    console.log("Navigating to search:", url);
    window.location.href = url;
  } else {
    // กรณี filter หรือหน้าปกติ
    const form = document.getElementById("filterForm");
    const formData = new FormData(form);
    const params = new URLSearchParams();
    
    // เพิ่มค่าจาก form
    for (let [key, value] of formData.entries()) {
      if (value && value.trim() !== "") {
        params.append(key, value);
      }
    }
    
    // เพิ่ม page
    params.append("page", page);
    
    // ตรวจสอบว่ามี filter หรือไม่
    const paramsString = params.toString();
    console.log("Params:", paramsString);
    
    if (paramsString === "page=" + page) {
      // ไม่มี filter -> ไปหน้าแรก
      const url = "${pageContext.request.contextPath}/?page=" + page;
      console.log("Navigating to home:", url);
      window.location.href = url;
    } else {
      // มี filter
      const url = "${pageContext.request.contextPath}/filterProjects?" + paramsString;
      console.log("Navigating to filter:", url);
      window.location.href = url;
    }
  }
}

document.addEventListener("DOMContentLoaded", function() {
  document.querySelectorAll(".form-select, .form-check-input").forEach(el => {
    el.addEventListener("change", () => document.getElementById("filterForm").submit());
  });

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