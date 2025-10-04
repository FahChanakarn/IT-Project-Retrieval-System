<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<header class="header">
	<div class="container-fluid px-4">
		<div class="row align-items-center">
			<!-- โลโก้ -->
			<div class="col-auto d-flex align-items-center">
				<img
					src="${pageContext.request.contextPath}/assets/images/LOGOIT.png"
					alt="IT Logo" class="logo-img">
			</div>

			<!-- ชื่อระบบตรงกลาง -->
			<div class="col text-center">
				<div class="header-title mb-0">
					<div class="sliding-text">
						<span>ระบบสืบค้นโครงงานสารสนเทศ</span> <span>IT Project Retrieval System</span>
					</div>
				</div>
			</div>

			<!-- เมนู -->
			<div class="col-auto d-flex align-items-center flex-wrap gap-3">

				<c:choose>

					<c:when test="${not empty sessionScope.student}">
						<a href="${pageContext.request.contextPath}/" class="nav-link">หน้าหลัก</a>

						<div class="dropdown">
							<a class="nav-link dropdown-toggle" href="#" role="button"
								data-bs-toggle="dropdown"> จัดการโครงงาน </a>
							<ul class="dropdown-menu">
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/editAbstract">
										แก้ไขบทคัดย่อ </a></li>

								<li><c:choose>
										<c:when
											test="${not empty sessionScope.student.project and sessionScope.student.project.approveStatus == 'approved'}">
											<a class="dropdown-item"
												href="${pageContext.request.contextPath}/student496/upload">
												อัปโหลดไฟล์ </a>
										</c:when>
										<c:otherwise>
											<a class="dropdown-item disabled" href="#"
												title="ต้องได้รับอนุมัติก่อน"> อัปโหลดไฟล์ </a>
										</c:otherwise>
									</c:choose></li>
							</ul>
						</div>

						<div class="dropdown">
							<a class="nav-link dropdown-toggle d-flex align-items-center"
								href="#" role="button" data-bs-toggle="dropdown"> <c:choose>
									<c:when test="${not empty sessionScope.student.stu_image}">
										<img
											src="${pageContext.request.contextPath}/profileImage/${sessionScope.student.stu_image}"
											width="30" height="30" class="rounded-circle me-2">
									</c:when>
									<c:otherwise>
										<img
											src="${pageContext.request.contextPath}/assets/images/default-profile.png"
											width="30" height="30" class="rounded-circle me-2">
									</c:otherwise>
								</c:choose> ${sessionScope.student.stu_firstName}
							</a>
							<ul class="dropdown-menu dropdown-menu-end">
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/editProfile">แก้ไขข้อมูลส่วนตัว</a></li>
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/logout">ออกจากระบบ</a></li>
							</ul>
						</div>
					</c:when>


					<c:when test="${not empty sessionScope.admin}">
						<a href="${pageContext.request.contextPath}/"
							class="nav-link text-white">หน้าหลัก</a>

						<ul class="nav d-flex flex-wrap align-items-center gap-3 mb-0">
							<li class="nav-item dropdown"><a
								class="nav-link dropdown-toggle text-white" href="#"
								role="button" data-bs-toggle="dropdown">จัดการโครงงาน</a>
								<ul class="dropdown-menu">
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/admin/listProjects">รายการโครงงานทั้งหมด</a></li>
									<li><a class="dropdown-item" href="">รายการโครงงานของนักศึกษาในที่ปรึกษา</a></li>
								</ul></li>

							<li class="nav-item dropdown"><a
								class="nav-link dropdown-toggle text-white" href="#"
								role="button" data-bs-toggle="dropdown">ข้อมูลอาจารย์</a>
								<ul class="dropdown-menu">
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/admin/listAdvisors">รายชื่ออาจารย์</a></li>
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/admin/addAdvisorForm">เพิ่มอาจารย์</a></li>
								</ul></li>

							<li class="nav-item dropdown"><a
								class="nav-link dropdown-toggle text-white" href="#"
								role="button" data-bs-toggle="dropdown">ข้อมูลนักศึกษา</a>
								<ul class="dropdown-menu">
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/admin/importStudentFile">
											Import ข้อมูลนักศึกษา</a></li>
								</ul></li>

							<li class="nav-item dropdown"><a
								class="nav-link dropdown-toggle text-white d-flex align-items-center"
								href="#" role="button" data-bs-toggle="dropdown"> <i
									class="bi bi-person fs-5 me-2"></i>
									${sessionScope.admin.adv_prefix}
									${sessionScope.admin.adv_firstName}
							</a>
								<ul class="dropdown-menu dropdown-menu-end">
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/logout">ออกจากระบบ</a></li>
								</ul></li>
						</ul>
					</c:when>

					<c:when test="${not empty sessionScope.advisor}">
						<a href="${pageContext.request.contextPath}/"
							class="nav-link text-white">หน้าหลัก</a>

						<ul class="nav d-flex flex-wrap align-items-center gap-3 mb-0">
							<li class="nav-item dropdown"><a
								class="nav-link dropdown-toggle text-white" href="#"
								role="button" data-bs-toggle="dropdown"> จัดการโครงงาน </a>
								<ul class="dropdown-menu">
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/advisor/listProjects">
											รายการโครงงาน</a></li>
								</ul></li>

							<li class="nav-item dropdown"><a
								class="nav-link dropdown-toggle text-white d-flex align-items-center"
								href="#" role="button" data-bs-toggle="dropdown"> <i
									class="bi bi-person fs-5 me-2"></i>
									${sessionScope.advisor.adv_prefix}
									${sessionScope.advisor.adv_firstName}
							</a>
								<ul class="dropdown-menu dropdown-menu-end">
									<li><a class="dropdown-item"
										href="${pageContext.request.contextPath}/logout">ออกจากระบบ</a></li>
								</ul></li>
						</ul>
					</c:when>

					<c:when test="${not empty sessionScope.itstudent}">
						<a href="${pageContext.request.contextPath}/" class="nav-link">หน้าหลัก</a>

						<div class="dropdown">
							<a class="nav-link dropdown-toggle d-flex align-items-center"
								href="#" role="button" data-bs-toggle="dropdown">
								${sessionScope.itstudent.stu_firstName} </a>
							<ul class="dropdown-menu dropdown-menu-end">
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/logout">ออกจากระบบ</a></li>
							</ul>
						</div>
					</c:when>

					<c:otherwise>
						<a href="${pageContext.request.contextPath}/" class="nav-link">หน้าหลัก</a>

						<div class="dropdown">
							<a class="nav-link dropdown-toggle" href="#" role="button"
								data-bs-toggle="dropdown"> เข้าสู่ระบบ </a>
							<ul class="dropdown-menu">
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/loginStudent">นักศึกษา</a></li>
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/loginStudent496">นักศึกษา496</a></li>
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/loginAdvisor">อาจารย์ที่ปรึกษา</a></li>
								<li><a class="dropdown-item"
									href="${pageContext.request.contextPath}/loginAdmin">ผู้ดูแลระบบ</a></li>
							</ul>
						</div>
					</c:otherwise>

				</c:choose>
			</div>
		</div>
	</div>
</header>