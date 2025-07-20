<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>แก้ไขไฟล์เอกสารหรือวิดีโอ</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/editFilendVideo.css">
<!-- Font -->
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5>แก้ไขไฟล์หรือวิดีโอ</h5>
		<hr>
		<form
			action="${pageContext.request.contextPath}/student496/updateFileAndVideo"
			method="post" enctype="multipart/form-data">
			<input type="hidden" name="id" value="${file.fileId}" />

			<div class="mb-3">
				<label for="name" class="form-label">ชื่อไฟล์/วิดีโอ</label> <input
					type="text" class="form-control" name="name" id="filename"
					value="${file.filename}" required>
			</div>

			<c:choose>
				<c:when test="${file.filetype == 'video'}">
					<div class="mb-3">
						<label class="form-label">ลิงก์วิดีโอ (YouTube)</label> <input
							type="url" class="form-control" name="videoLink"
							value="${file.filepath}">
					</div>
				</c:when>
				<c:otherwise>
					<div class="mb-3">
						<label class="form-label">เลือกไฟล์ที่ต้องการอัปโหลด :</label> <input
							type="file" class="form-control" name="newFile" accept=".pdf">
						<small class="text-danger">*กรุณาอัปโหลดเฉพาะไฟล์ .pdf
							เท่านั้น*</small>
					</div>
				</c:otherwise>
			</c:choose>

			<button type="submit" class="btn btn-success">บันทึก</button>
			<a href="${pageContext.request.contextPath}/backToUploadPage"
				class="btn btn-danger">ยกเลิก</a>
		</form>
	</div>

	<!-- แสดง popup หากมีพารามิเตอร์ success -->
	<c:if test="${not empty param.success}">
		<script>
			Swal.fire({
				icon : 'success',
				title : 'ไฟล์ของคุณถูกแก้ไขเรียบร้อย !',
				showConfirmButton : false,
				timer : 2000
			})
		</script>
	</c:if>

</body>
</html>
