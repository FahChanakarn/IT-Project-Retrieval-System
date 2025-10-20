<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ดูรายละเอียดบทคัดย่อ</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/viewAbstract.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold">${project.proj_NameTh}/ ดูรายละเอียด</h5>
		<hr>

		<div class="abstract-card">
			<!-- Project Information -->
			<div class="info-row">
				<span class="info-label">ชื่อโครงงาน(ภาษาไทย) :</span> <span
					class="info-value">${project.proj_NameTh}</span>
			</div>

			<div class="info-row">
				<span class="info-label">ชื่อโครงงาน(ภาษาอังกฤษ) :</span> <span
					class="info-value">${project.proj_NameEn}</span>
			</div>

			<div class="info-row">
				<span class="info-label">ผู้จัดทำ :</span>
				<div class="info-value">
					<div class="authors-list">
						<c:forEach items="${project.student496s}" var="student"
							varStatus="status">
							<span class="author-name">
								${student.stu_prefix}${student.stu_firstName}
								${student.stu_lastName} </span>
						</c:forEach>
					</div>
				</div>
			</div>

			<div class="info-row">
				<span class="info-label">อาจารย์ที่ปรึกษา :</span> <span
					class="info-value">
					${project.advisor.adv_prefix}${project.advisor.adv_firstName}
					${project.advisor.adv_lastName} </span>
			</div>

			<div class="info-row">
				<span class="info-label">ภาคเรียน :</span> <span class="info-value">${project.semester}</span>
			</div>

			<!-- Tools Section -->
			<div class="info-row">
				<span class="info-label"> <c:choose>
						<c:when test="${project.projectType == 'Testing'}">
							เครื่องมือที่ใช้ทดสอบ :
						</c:when>
						<c:otherwise>
							เครื่องมือที่ใช้พัฒนา :
						</c:otherwise>
					</c:choose>
				</span>
				<div class="info-value">
					<c:choose>
						<c:when test="${not empty project.tools}">
							<div class="tools-grid">
								<c:forEach items="${project.tools}" var="tool"
									varStatus="status">
									<c:choose>
										<c:when test="${tool.toolType == 'PROGRAMMING'}">
											<div class="tool-card programming">
												<i class="bi bi-code-slash tool-icon"></i> <span
													class="tool-name">${tool.toolsName}</span>
											</div>
										</c:when>
										<c:when test="${tool.toolType == 'DBMS'}">
											<div class="tool-card database">
												<i class="bi bi-database tool-icon"></i> <span
													class="tool-name">${tool.toolsName}</span>
											</div>
										</c:when>
										<c:otherwise>
											<div class="tool-card other">
												<i class="bi bi-gear tool-icon"></i> <span class="tool-name">${tool.toolsName}</span>
											</div>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</div>
						</c:when>
						<c:otherwise>
							<span class="text-muted">ไม่ระบุ</span>
						</c:otherwise>
					</c:choose>
				</div>
			</div>

			<!-- ไม่แสดงปุ่มสำหรับ advisor และ admin -->
			<c:if
				test="${empty sessionScope.advisor and empty sessionScope.admin}">
				<div class="mb-3">
					<c:choose>
						<c:when
							test="${empty sessionScope.itstudent and empty sessionScope.student}">
							<!-- ตรวจสอบว่ามีวิดีโอที่ได้รับอนุมัติให้เผยแพร่หรือไม่ -->
							<c:set var="hasApprovedVideo" value="false" />
							<c:forEach items="${uploadList}" var="file">
								<c:if
									test="${file.filetype == 'video' and file.publishStatus == true}">
									<c:set var="hasApprovedVideo" value="true" />
								</c:if>
							</c:forEach>

							<c:if test="${hasApprovedVideo}">
								<a
									href="${pageContext.request.contextPath}/project/video?projectId=${project.projectId}"
									class="btn btn-success"> <i class="bi bi-play-circle me-1"></i>
									วิดีโอตัวอย่างการใช้งานโปรแกรม
								</a>
							</c:if>
						</c:when>

						<c:otherwise>
							<!-- แสดงปุ่มสำหรับนักศึกษาเท่านั้น -->
							<a
								href="${pageContext.request.contextPath}/student/viewChapter?projectId=${project.projectId}"
								class="btn btn-primary me-2"> <i
								class="bi bi-file-earmark-text me-1"></i> ดูไฟล์เอกสาร
							</a>

							<!-- ตรวจสอบว่ามีวิดีโอที่ได้รับอนุมัติให้เผยแพร่หรือไม่ -->
							<c:set var="hasApprovedVideo" value="false" />
							<c:forEach items="${uploadList}" var="file">
								<c:if
									test="${file.filetype == 'video' and file.publishStatus == true}">
									<c:set var="hasApprovedVideo" value="true" />
								</c:if>
							</c:forEach>

							<c:if test="${hasApprovedVideo}">
								<a
									href="${pageContext.request.contextPath}/project/video?projectId=${project.projectId}"
									class="btn btn-success"> <i class="bi bi-play-circle me-1"></i>
									วิดีโอตัวอย่างการใช้งานโปรแกรม
								</a>
							</c:if>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>

			<!-- Abstract Section -->
			<div class="abstract-section">
				<div class="abstract-title">บทคัดย่อ</div>
				<div class="abstract-content">${project.abstractTh}</div>

				<!-- Keywords Thai -->
				<div class="keywords-section">
					<div class="keywords-label">คำสำคัญ :</div>
					<div class="keywords-content">${project.keywordTh}</div>
				</div>
			</div>

			<!-- English Abstract Section -->
			<c:if test="${not empty project.abstractEn}">
				<div class="abstract-section">
					<div class="abstract-title">Abstract</div>
					<div class="abstract-content">${project.abstractEn}</div>

					<!-- Keywords English -->
					<c:if test="${not empty project.keywordEn}">
						<div class="keywords-section">
							<div class="keywords-label">Keywords :</div>
							<div class="keywords-content">${project.keywordEn}</div>
						</div>
					</c:if>
				</div>
			</c:if>
		</div>
	</div>
</body>
</html>