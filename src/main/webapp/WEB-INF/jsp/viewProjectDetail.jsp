<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายละเอียดโครงงานนักศึกษา</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<!-- CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/viewProjectDetail.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-4">
		<h5>รายการโครงงาน / รายละเอียด</h5>
		<hr>

		<!-- ข้อมูลโครงงาน -->
		<div class="mb-3">
			<p>
				<strong>ชื่อโครงงาน :</strong> ${project.proj_NameTh}
			</p>
			<p>
				<strong>ผู้จัดทำ :</strong>
				<c:forEach var="s" items="${project.student496s}" varStatus="loop">
                    ${s.stu_prefix}${s.stu_firstName} ${s.stu_lastName}<c:if
						test="${!loop.last}">, </c:if>
				</c:forEach>
			</p>
			<p>
				<strong>ภาคเรียน :</strong> ${project.semester}
			</p>
		</div>

		<!-- ปุ่ม -->
		<div class="mb-4">
			<a href="#" class="btn btn-primary btn-section">บทคัดย่อ</a> <a
				href="#" class="btn btn-primary">วิดีโอ</a>
		</div>

		<!-- ตารางไฟล์เอกสาร -->
		<table class="table table-bordered">
			<thead>
				<tr>
					<th>ลำดับ</th>
					<th>ชื่อไฟล์</th>
					<th>เอกสาร</th>
					<th>เผยแพร่</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="file" items="${project.documentFiles}"
					varStatus="loop">
					<tr>
						<td>${loop.index + 1}</td>
						<td>${file.filename}</td>
						<td><a
							href="${pageContext.request.contextPath}/download/file/${file.fileId}"
							class="btn btn-success btn-sm" target="_blank"> ดูไฟล์เอกสาร
						</a></td>
						<td><input type="checkbox"
							disabled 
             ${file.status=='เผยแพร่' ? 'checked' : ''}>
						</td>
					</tr>
				</c:forEach>

			</tbody>
		</table>
	</div>
</body>
</html>
