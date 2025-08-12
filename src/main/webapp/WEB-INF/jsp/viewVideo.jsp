<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<title>วิดีโอโครงงาน</title>

<!-- Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<!-- ฟอนต์/สไตล์ส่วนกลาง -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<!-- CSS แยกของหน้านี้ -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/viewVideo.css">

<%-- helper: แปลง YouTube URL -> embed URL --%>
<%!private String toEmbedUrl(String url) {
		if (url == null)
			return null;
		url = url.trim();
		if (url.isEmpty())
			return null;
		try {
			if (url.contains("youtu.be/")) {
				String id = url.substring(url.lastIndexOf("/") + 1);
				int q = id.indexOf('?');
				if (q > -1)
					id = id.substring(0, q);
				return "https://www.youtube.com/embed/" + id;
			}
			if (url.contains("/shorts/")) {
				String id = url.substring(url.indexOf("/shorts/") + 8);
				int q = id.indexOf('?');
				if (q > -1)
					id = id.substring(0, q);
				return "https://www.youtube.com/embed/" + id;
			}
			if (url.contains("watch?v=")) {
				String id = url.substring(url.indexOf("watch?v=") + 8);
				int amp = id.indexOf('&');
				if (amp > -1)
					id = id.substring(0, amp);
				return "https://www.youtube.com/embed/" + id;
			}
			if (url.matches("^[A-Za-z0-9_-]{11}$")) {
				return "https://www.youtube.com/embed/" + url;
			}
		} catch (Exception ignore) {
		}
		return null;
	}%>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-4">
		<!-- ลิงก์กลับ -->
		<div class="mb-2">
			<a class="link-back"
				href="${pageContext.request.contextPath}/advisor/viewProjectDetail?projectId=${project.projectId}">
				← กลับสู่รายละเอียด </a>
		</div>

		<!-- หัวข้อ -->
		<h5 class="page-title">
			<c:out value="${project.proj_NameTh}" />
			/ วิดีโอตัวอย่างการใช้งานโปรแกรม
		</h5>
		<hr />

		<!-- 1) ถ้า Controller ส่ง videoDoc มา ใช้อันนั้นก่อน -->
		<c:set var="videoPath" value="" />
		<c:if
			test="${not empty videoDoc and videoDoc.filetype eq 'video' and not empty videoDoc.filepath}">
			<c:set var="videoPath" value="${videoDoc.filepath}" />
		</c:if>

		<!-- 2) ถ้ายังว่าง ให้ค้นจาก project.documentFiles เอา “ตัวแรกที่ตรงเงื่อนไข” -->
		<c:if test="${empty videoPath and not empty project.documentFiles}">
			<c:forEach items="${project.documentFiles}" var="df">
				<!-- เงื่อนไข: เป็นวิดีโอ, มีลิงก์, (ทางเลือก) เผยแพร่แล้ว -->
				<c:if
					test="${empty videoPath 
                     and df.filetype eq 'video' 
                     and not empty df.filepath}">
					<!-- ถ้าต้องการบังคับเฉพาะที่เผยแพร่ ให้เพิ่ม and df.status eq 'Published' ในบรรทัดบน -->
					<c:set var="videoPath" value="${df.filepath}" />
				</c:if>
			</c:forEach>
		</c:if>

		<!-- 3) สร้าง embed URL -->
		<%
		String raw = (String) pageContext.getAttribute("videoPath");
		String embed = toEmbedUrl(raw);
		request.setAttribute("embedUrl", embed);
		%>

		<c:choose>
			<c:when test="${not empty embedUrl}">
				<div class="video-wrapper">
					<div class="ratio ratio-16x9">
						<iframe src="${embedUrl}" title="Project Video" frameborder="0"
							allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
							allowfullscreen></iframe>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-warning mt-4" role="alert">
					ไม่พบวิดีโอ</div>
			</c:otherwise>
		</c:choose>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
