<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/Header.css">
<head>
<meta charset="UTF-8">
<title>Login - Student 496</title>
<!-- Google Fonts: Kanit -->
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/loginStudent496.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
</head>
<body>
	<!-- Header -->
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
	<div class="login-wrapper">
		<img src="${pageContext.request.contextPath}/assets/images/login.png"
			class="login-icon" alt="login icon" />
		<!-- ซ้าย -->
		<div class="login-left">
			<h4>Welcome To</h4>
			<h1>
				<strong>Information Technology<br>Project <br>Retrieval<br>System
				</strong>
			</h1>
		</div>

		<!-- ขวา -->
		<div class="login-right">
			<h5 class="mb-4">
				<strong>เข้าสู่ระบบด้วยบัญชีนักศึกษา496</strong>
			</h5>
			<form action="loginStudent496" method="post">
				<div class="mb-3">
					<label for="stuId" class="form-label text-danger">รหัสนักศึกษา</label>
					<select class="form-select" name="stuId" required>
						<option value="">เลือกรหัสนักศึกษา</option>
						<c:forEach var="stu" items="${studentList}">
							<option value="${stu.stuId}">${stu.stuId}</option>
						</c:forEach>
					</select>
				</div>
				<div class="mb-3">
					<label for="password" class="form-label text-danger">รหัสผ่าน</label>
					<input type="password" name="password" class="form-control"
						required>
				</div>
				<c:if test="${not empty error}">
					<div class="alert alert-danger">${error}</div>
				</c:if>
				<button type="submit" class="btn btn-login mt-2">เข้าสู่ระบบ</button>
			</form>
		</div>
	</div>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>