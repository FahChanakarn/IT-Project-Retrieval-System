<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<title>แก้ไขบทคัดย่อ</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/editAbstract.css">
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
	<div class="container mt-4">
		<div class="path-title">${project.proj_NameTh}/แก้ไขบทคัดย่อ</div>

		<form action="${pageContext.request.contextPath}/updateAbstract"
			method="post">
			<input type="hidden" name="projectId" value="${project.projectId}">

			<div class="mb-3">
				<label class="form-label fw-bold">ผู้จัดทำ :</label>
				<c:forEach var="student" items="${project.student496s}">
						${student.stu_firstName} ${student.stu_lastName}
				</c:forEach>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">ภาคเรียน :</label>
				${project.semester}
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">ชื่อโครงงานภาษาไทย :</label> <input
					type="text" class="form-control" name="projNameTh"
					value="${project.proj_NameTh}">
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">ชื่อโครงงานภาษาอังกฤษ :</label> <input
					type="text" class="form-control" name="projNameEn"
					value="${project.proj_NameEn}">
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">ประเภทโครงงาน :</label> <select
					class="form-select" name="projectType">
					<c:forEach var="type" items="${projectTypes}">
						<option value="${type}"
							${type == project.projectType ? 'selected' : ''}>${type}</option>
					</c:forEach>
				</select>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">ซอฟต์แวร์ฐานข้อมูล :</label> <select
					class="form-select" name="typeDBId">
					<c:forEach var="db" items="${typeDBs}">
						<option value="${db.typeId}"
							${db.typeId == project.typeDB.typeId ? 'selected' : ''}>${db.softwareName}</option>
					</c:forEach>
				</select>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">ภาษาที่ใช้ในการพัฒนา :</label><br>
				<c:forEach var="lang" items="${programmingLangs}">
					<c:set var="isChecked" value="false" />
					<c:forEach var="detail" items="${project.projectLangDetails}">
						<c:if test="${detail.programmingLang.langId == lang.langId}">
							<c:set var="isChecked" value="true" />
						</c:if>
					</c:forEach>
					<div class="form-check form-check-inline">
						<input class="form-check-input" type="checkbox" name="languageIds"
							value="${lang.langId}" ${isChecked ? 'checked' : ''}> <label
							class="form-check-label">${lang.langName}</label>
					</div>
				</c:forEach>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">บทคัดย่อ (ภาษาไทย) :</label>
				<textarea class="form-control" name="abstractTh" rows="5" required>${project.abstractTh}</textarea>
				<div class="form-text text-danger">*กรุณากรอกบทคัดย่อความยาวไม่เกิน
					1000 ตัวอักษร*</div>

				<label class="form-label fw-bold">บทคัดย่อ (ภาษาอังกฤษ) :</label>
				<textarea class="form-control" name="abstractEn" rows="5" required>${project.abstractEn}</textarea>
				<div class="form-text text-danger">*กรุณากรอกบทคัดย่อความยาวไม่เกิน
					1000 ตัวอักษร*</div>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">คำสำคัญ (ภาษาไทย) :</label> <input
					type="text" class="form-control" name="keywordTh"
					value="${project.keywordTh}" required>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">คำสำคัญ (ภาษาอังกฤษ) :</label> <input
					type="text" class="form-control" name="keywordEn"
					value="${project.keywordEn}">
			</div>


			<div class="text-center">
				<button type="submit" class="btn btn-success">บันทึก</button>
				<a href="${pageContext.request.contextPath}/searchProjects"
					class="btn btn-danger">ยกเลิก</a>
			</div>
		</form>
	</div>
</body>
</html>
