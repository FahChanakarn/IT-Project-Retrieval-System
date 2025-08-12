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

	<div class="container mt-4">
		<h5>${project.proj_NameTh}/รายละเอียด</h5>
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
			<a
				href="${pageContext.request.contextPath}/viewAbstract?projectId=${project.projectId}"
				class="btn btn-primary btn-section">บทคัดย่อ</a>
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
						<td class="text-center"><c:choose>
								<c:when test="${file.filetype eq 'video'}">
									<a
										href="${pageContext.request.contextPath}/project/video?projectId=${project.projectId}&fileId=${file.fileId}"
										class="btn btn-success btn-sm"> ดูวิดีโอ </a>
								</c:when>

								<c:otherwise>
									<a
										href="${pageContext.request.contextPath}/download/file/${file.fileId}"
										class="btn btn-success btn-sm" target="_blank">
										ดูไฟล์เอกสาร </a>
								</c:otherwise>
							</c:choose></td>

						<td class="text-center">
							<button type="button" class="btn btn-link p-0 toggle-publish"
								data-file-id="${file.fileId}" data-current="${file.status}">
								<i
									class="bi ${file.status eq 'เผยแพร่' ? 'bi-toggle-on text-success' : 'bi-toggle-off text-secondary'} fs-3"></i>
							</button>
						</td>

					</tr>
				</c:forEach>

			</tbody>
		</table>
	</div>

	<script>
(function () {
  const base = '${pageContext.request.contextPath}';
  document.querySelectorAll('.toggle-publish').forEach(btn => {
    btn.addEventListener('click', async function () {
      const fileId = this.dataset.fileId;
      const now = (this.dataset.current || '').trim(); // 'เผยแพร่' หรืออย่างอื่น
      const nextIsPublish = now !== 'เผยแพร่'; // toggle

      // ล็อกปุ่มกันกดรัว
      this.disabled = true;

      try {
        const res = await fetch(base + '/advisor/document/togglePublish', {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({ fileId: Number(fileId), published: nextIsPublish })
        });
        if (!res.ok) throw new Error('HTTP ' + res.status);

        // อัปเดตไอคอนในหน้า
        const icon = this.querySelector('i');
        if (nextIsPublish) {
          icon.classList.remove('bi-toggle-off','text-secondary');
          icon.classList.add('bi-toggle-on','text-success');
          this.dataset.current = 'เผยแพร่';
        } else {
          icon.classList.remove('bi-toggle-on','text-success');
          icon.classList.add('bi-toggle-off','text-secondary');
          this.dataset.current = 'ไม่เผยแพร่';
        }
      } catch (e) {
        alert('อัปเดตสถานะเผยแพร่ไม่สำเร็จ');
        console.error(e);
      } finally {
        this.disabled = false;
      }
    });
  });
})();
</script>
</body>
</html>
