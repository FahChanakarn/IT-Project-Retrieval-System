<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<title>วิดีโอตัวอย่างการใช้งานโปรแกรม</title>

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

	<div class="container mt-5">

		<!-- หัวข้อ -->
		<h5 class="page-title">
			<c:out value="${project.proj_NameTh}" />
			/ วิดีโอตัวอย่างการใช้งานโปรแกรม
		</h5>
		<hr />

		<c:set var="videoPath" value="" />

		<!-- ✅ เช็คว่าเป็นวิดีโอ + publishStatus = true (อนุมัติ) -->
		<c:if
			test="${not empty videoDoc 
              and videoDoc.filetype eq 'video' 
              and not empty videoDoc.filepath 
              and videoDoc.publishStatus == true}">
			<c:set var="videoPath" value="${videoDoc.filepath}" />
		</c:if>

		<!-- ✅ ถ้ายังไม่เจอ ให้หาจาก documentFiles (เฉพาะที่ publishStatus = true) -->
		<c:if test="${empty videoPath and not empty project.documentFiles}">
			<c:forEach items="${project.documentFiles}" var="df">
				<c:if
					test="${empty videoPath
              and df.filetype eq 'video'
              and not empty df.filepath
              and df.publishStatus == true}">
					<c:set var="videoPath" value="${df.filepath}" />
				</c:if>
			</c:forEach>
		</c:if>

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
				<div
					class="alert alert-light border shadow-sm text-center py-5 mt-4">
					<i class="bi bi-camera-video text-secondary fs-1 d-block mb-3"></i>
					<h5 class="text-muted mb-2">ไม่พบข้อมูลวิดีโอ</h5>
					<c:if test="${not empty errorMessage}">
						<p class="text-danger mt-2 mb-0">${errorMessage}</p>
					</c:if>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>