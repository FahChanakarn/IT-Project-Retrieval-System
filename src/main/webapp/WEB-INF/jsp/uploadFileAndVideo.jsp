<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>อัปโหลดไฟล์เอกสาร</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/uploadFileAndVideo.css">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
	function toggleUploadType() {
		const type = document.getElementById("fileType").value;
		const fileGroup = document.getElementById("fileGroup");
		const videoGroup = document.getElementById("videoGroup");
		const labelName = document.getElementById("fileNameLabel");
		const fileInput = document.querySelector('input[name="file"]');
		const fileNameInput = document.querySelector('input[name="fileName"]'); // ✅ input ชื่อไฟล์

		if (type === "video") {
			fileGroup.style.display = "none";
			videoGroup.style.display = "block";
			labelName.innerText = "ชื่อวิดีโอ :";
			fileInput.removeAttribute("required");
			fileNameInput.placeholder = "ตัวอย่างการใช้งานโปรแกรม"; // ✅ เปลี่ยน placeholder
		} else {
			fileGroup.style.display = "block";
			videoGroup.style.display = "none";
			labelName.innerText = "ชื่อไฟล์ :";
			fileInput.setAttribute("required", "true");
			fileNameInput.placeholder = "เช่น บทที่ 1 บทนำ"; // ✅ คืนค่าเดิม
		}
	}
</script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-4">
		<h5 class="fw-bold text-danger">${project.proj_NameTh}/
			จัดการโครงงาน / อัปโหลดไฟล์เอกสาร</h5>
		<hr>

		<form action="${pageContext.request.contextPath}/student496/upload"
			method="post" enctype="multipart/form-data">
			<div class="mb-3">
				<label class="form-label">เลือกประเภทไฟล์ :</label> <select
					class="form-select w-auto d-inline" name="fileType" id="fileType"
					onchange="toggleUploadType()" required>
					<option value="file">ไฟล์เอกสาร</option>
					<option value="video">ลิงก์วิดีโอ</option>
				</select>
			</div>

			<div class="mb-3">
				<label class="form-label" id="fileNameLabel">ชื่อไฟล์ :</label> <input
					type="text" name="fileName" class="form-control"
					placeholder="เช่น บทที่ 1 บทนำ" required>
			</div>

			<div class="mb-3" id="fileGroup">
				<label class="form-label">เลือกไฟล์ที่ต้องการอัปโหลด :</label> <input
					type="file" name="file" class="form-control" accept=".pdf" required>
				<small class="text-danger">กรุณาอัปโหลดเฉพาะไฟล์ .pdf
					เท่านั้น</small>
			</div>

			<div class="mb-3" id="videoGroup" style="display: none;">
				<label class="form-label">ลิงก์วิดีโอ :</label> <input type="url"
					name="videoLink" class="form-control"
					placeholder="https://example.com" pattern="https?://.+" />
			</div>

			<div class="btn-group">
				<button type="submit" class="btn btn-success">อัปโหลด</button>
				<a href="#" class="btn btn-danger">ยกเลิก</a>
			</div>
		</form>

		<br>
		<h5 class="text-danger fw-bold">อัปโหลด</h5>
		<hr class="mt-5">

		<table class="table table-bordered text-center align-middle">
			<thead class="table-light">
				<tr>
					<th>ลำดับ</th>
					<th>ชื่อไฟล์</th>
					<th>เอกสาร</th>
					<th>สถานะการอัปโหลด</th>
					<th>แก้ไข</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${uploadList}" varStatus="loop">
					<tr>
						<td>${loop.index + 1}</td>
						<td>${item.filename}</td>
						<!-- ✅ แก้ตรงนี้ -->
						<td><c:choose>
								<c:when test="${item.filetype == 'file'}">
									<a class="btn btn-primary btn-sm"
										href="${pageContext.request.contextPath}/download/file/${item.fileId}"
										target="_blank">ดูเอกสาร</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-primary btn-sm" href="${item.filepath}"
										target="_blank">ดูวิดีโอ</a>
								</c:otherwise>
							</c:choose></td>
						<td class="text-success fw-bold">${item.status}</td>
						<!-- ✅ สถานะจาก Model -->
						<td><a
							href="${pageContext.request.contextPath}/student496/editFile/${item.fileId}"
							class="btn btn-success btn-sm"> <i
								class="bi bi-pencil-square"></i>
						</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

	</div>
</body>
</html>
