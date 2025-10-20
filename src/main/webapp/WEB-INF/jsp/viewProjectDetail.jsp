<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="isAdmin" value="${userRole == 'admin'}" />
<c:set var="baseUrl" value="${isAdmin ? '/admin' : '/advisor'}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายละเอียดโครงงานนักศึกษา</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
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

	<div class="container mt-5">
		<h5>${project.proj_NameTh} / รายละเอียด</h5>
		<hr>

		<!-- ข้อมูลโครงงาน -->
		<div class="mb-3">
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

		<!-- ปุ่มดูบทคัดย่อ -->
		<div class="mb-3">
			<a
				href="${pageContext.request.contextPath}/viewAbstract?projectId=${project.projectId}"
				class="btn btn-primary"> <i class="bi bi-file-text"></i>
				ดูบทคัดย่อ
			</a>
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
				<c:choose>
					<c:when test="${empty project.documentFiles}">
						<tr>
							<td colspan="4" class="text-center text-muted py-4"><i
								class="bi bi-folder2-open fs-1 d-block mb-2"></i>
								ขณะนี้ยังไม่มีไฟล์ที่ถูกอัปโหลด</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach var="file" items="${project.documentFiles}"
							varStatus="loop">
							<tr>
								<td>${loop.index + 1}</td>
								<td>${file.filename}</td>
								<td class="text-center"><c:choose>
										<c:when test="${file.filetype eq 'video'}">
											<!-- ✅ เปิดลิงก์ YouTube โดยตรง -->
											<a href="${file.filepath}" class="btn btn-success btn-sm"
												target="_blank" rel="noopener noreferrer"> <i
												class="bi bi-youtube"></i> ดูวิดีโอ
											</a>
										</c:when>
										<c:otherwise>
											<a
												href="${pageContext.request.contextPath}/download/file/${file.fileId}/${file.filename}"
												class="btn btn-success btn-sm" target="_blank"> <i
												class="bi bi-file-earmark-pdf"></i> ดูไฟล์เอกสาร
											</a>
										</c:otherwise>
									</c:choose></td>
								<td class="text-center">
									<button type="button" class="btn btn-link p-0 toggle-publish"
										data-file-id="${file.fileId}"
										data-published="${file.publishStatus}">
										<i
											class="bi fs-3 ${file.publishStatus ? 'bi-toggle-on text-success' : 'bi-toggle-off text-secondary'}"></i>
									</button>
								</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>

	<script>
(function () {
    const base = '${pageContext.request.contextPath}';
    
    // Toggle publish status
    document.querySelectorAll('.toggle-publish').forEach(btn => {
        btn.addEventListener('click', async function () {
            const fileId = Number(this.dataset.fileId);
            const current = this.dataset.published === 'true';
            const nextPublish = !current;

            this.disabled = true;

            try {
                const params = new URLSearchParams();
                params.append("fileId", fileId);
                params.append("published", nextPublish);

                const res = await fetch(base + '/advisor/document/togglePublish', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: params.toString()
                });

                const text = await res.text();
                if (text !== "success") throw new Error("Server returned " + text);

                const icon = this.querySelector('i');
                if (nextPublish) {
                    icon.classList.remove('bi-toggle-off','text-secondary');
                    icon.classList.add('bi-toggle-on','text-success');
                } else {
                    icon.classList.remove('bi-toggle-on','text-success');
                    icon.classList.add('bi-toggle-off','text-secondary');
                }
                this.dataset.published = nextPublish;

            } catch (e) {
                console.error('เกิดข้อผิดพลาด:', e);
                alert('อัปเดตสถานะเผยแพร่ไม่สำเร็จ');
            } finally {
                this.disabled = false;
            }
        });
    });

})();
</script>

</body>
</html>