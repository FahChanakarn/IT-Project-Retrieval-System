<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>รายละเอียดโครงงาน</title>
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
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/viewChapterPDFFile.css">

<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw bold">${project.proj_NameTh} / ดูไฟล์เอกสาร</h5>
		<hr>
		
		<div class="mb-3">
			<p>
				<strong>ชื่อโครงงาน :</strong>
				<c:out value="${project.proj_NameTh}" default="ไม่ระบุ" />
			</p>
			<p>
				<strong>ผู้จัดทำ :</strong>
				<c:choose>
					<c:when test="${not empty project.student496s}">
						<c:forEach var="s" items="${project.student496s}"
							varStatus="status">
							<c:out value="${s.stu_prefix}" />
							<c:out value="${s.stu_firstName}" />
							<c:out value="${s.stu_lastName}" />
							<c:if
								test="${status.index lt (fn:length(project.student496s) - 1)}">, </c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						ไม่พบข้อมูลผู้จัดทำ
					</c:otherwise>
				</c:choose>
			</p>
			<p>
				<strong>ภาคเรียน :</strong>
				<c:out value="${project.semester}" default="ไม่ระบุ" />
			</p>
		</div>

		<!-- ปุ่มบทคัดย่อ + ปุ่มวิดีโอ -->
		<div class="mb-4">
			<a
				href="${pageContext.request.contextPath}/viewAbstract"
				class="btn btn-primary me-2"> <i class="bi bi-file-earmark-text me-1"></i>
				บทคัดย่อ
			</a> <a
				href="${pageContext.request.contextPath}/project/video"
				class="btn btn-success"> <i
				class="bi bi-play-circle me-1"></i> วิดีโอตัวอย่างการใช้งานโปรแกรม
			</a>
		</div>

		<!-- แสดงข้อมูลการหมดอายุ -->
		<div class="alert alert-info mb-3">
			<i class="bi bi-info-circle-fill"></i> <strong>หมายเหตุ:</strong>
			ไฟล์ที่ดาวน์โหลดจะมีอายุการใช้งาน <strong>14 วัน</strong>
			หลังจากนั้นจะไม่สามารถเปิดดูได้ ต้องดาวน์โหลดใหม่เท่านั้น
		</div>

		<!-- ตารางไฟล์เอกสาร -->
		<table class="table table-bordered table-hover">
			<thead class="table-light">
				<tr>
					<th style="width: 80px;">ลำดับ</th>
					<th>ชื่อไฟล์</th>
					<th style="width: 180px;" class="text-center">เอกสาร</th>
					<th style="width: 200px;" class="text-center">ดาวน์โหลดแบบปลอดภัย</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty project.documentFiles}">
						<c:forEach var="file" items="${project.documentFiles}"
							varStatus="loop">
							<c:if test="${file.filetype eq 'file'}">
								<tr>
									<td class="text-center"><c:out value="${loop.index + 1}" /></td>
									<td><i class="bi bi-file-earmark-pdf text-danger"></i> <c:out
											value="${file.filename}" default="ไม่ระบุชื่อไฟล์" /></td>
									<td class="text-center"><a href="javascript:void(0)"
										class="btn btn-success btn-sm"
										onclick="openPDFViewer(${file.fileId}, '${fn:replace(fn:escapeXml(file.filename), '\'', '\\\'')}')">
											<i class="bi bi-eye"></i> ดูไฟล์
									</a></td>
									<td class="text-center">
										<button class="btn btn-download-secure btn-sm"
											onclick="downloadSecurePDF(${file.fileId}, '${fn:escapeXml(file.filename)}')"
											id="download-btn-${file.fileId}">
											<i class="bi bi-shield-check"></i> ดาวน์โหลด (14 วัน)
										</button>
										<div class="loading-spinner" id="loading-${file.fileId}"
											style="display: none;">
											<div class="spinner-border spinner-border-sm text-success"
												role="status">
												<span class="visually-hidden">กำลังดาวน์โหลด...</span>
											</div>
										</div>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="4" class="text-center text-muted"><i
								class="bi bi-inbox"></i> ยังไม่มีไฟล์เผยแพร่</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>

		<!-- คำอธิบายเพิ่มเติม -->
		<div class="card mt-3">
			<div class="card-body">
				<h6 class="card-title">
					<i class="bi bi-question-circle text-primary"></i>
					ข้อมูลเกี่ยวกับการดาวน์โหลดแบบปลอดภัย
				</h6>
				<ul class="mb-0">
					<li><strong>ไฟล์ที่ดาวน์โหลดมีอายุ 14 วัน</strong> -
						หลังจากนั้นจะไม่สามารถเปิดดูได้</li>
					<li><strong>หมดอายุแล้ว</strong> -
						ไฟล์จะปิดอัตโนมัติและแจ้งให้ดาวน์โหลดใหม่</li>
				</ul>
			</div>
		</div>
	</div>

	<!-- PDF Viewer Modal -->
	<div class="modal fade" id="pdfViewerModal" tabindex="-1"
		aria-labelledby="pdfViewerModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="pdfViewerModalLabel">
						<i class="bi bi-file-pdf text-danger"></i> PDF Viewer
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body p-0">
					<div id="pdf-viewer" style="min-height: 600px;"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">
						<i class="bi bi-x-circle"></i> ปิด
					</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Download Info Modal (ปรับปรุงแล้ว) -->
	<div class="modal fade" id="downloadInfoModal" tabindex="-1"
		aria-labelledby="downloadInfoModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header bg-success text-white">
					<h5 class="modal-title" id="downloadInfoModalLabel">
						<i class="bi bi-shield-check"></i> ดาวน์โหลดแบบปลอดภัย
					</h5>
					<button type="button" class="btn-close btn-close-white"
						data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div class="download-info">
						<h6>
							<i class="bi bi-file-earmark-pdf text-danger"></i> <span
								id="download-filename"></span>
						</h6>

						<div class="alert alert-warning mt-3">
							<div class="mb-2">
								<i class="bi bi-exclamation-triangle-fill text-warning"></i> <strong>เอกสารที่ดาวน์โหลดจะหมดอายุจริง!</strong>
							</div>
							<div class="expiry-details" id="expiry-details-container">
								<!-- จะถูกเติมด้วย JavaScript -->
							</div>
						</div>

						<div class="token-info mt-3">
							<small> <i class="bi bi-info-circle"></i>
								ไฟล์นี้ได้รับการป้องกันด้วยระบบตรวจสอบวันหมดอายุอัตโนมัติ
								หากไฟล์หมดอายุแล้ว จะไม่สามารถเปิดดูได้
							</small>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">
						<i class="bi bi-x-circle"></i> ปิด
					</button>
					<button type="button" class="btn btn-success"
						id="start-download-btn" onclick="startSecureDownload()">
						<i class="bi bi-download"></i> เริ่มดาวน์โหลด
					</button>
				</div>
			</div>
		</div>
	</div>

	<script>
		// ตัวแปรเก็บ context path
		const contextPath = '${pageContext.request.contextPath}';
		let currentFileId = null; // เก็บ fileId ปัจจุบัน
		
		// เปิด PDF viewer แบบง่าย (ใช้ browser default)
		function openPDFViewer(fileId, filename) {
			console.log('📖 Opening PDF viewer for file:', fileId, filename);
			
			const pdfUrl = contextPath + "/download/file/" + fileId + "/" + encodeURIComponent(filename);
			console.log('🔗 PDF URL:', pdfUrl);
			
			// ตั้งชื่อ modal
			document.getElementById('pdfViewerModalLabel').textContent = filename;
			
			// ใช้ iframe แทน Adobe API
			const pdfViewer = document.getElementById('pdf-viewer');
			pdfViewer.innerHTML = '<iframe src="' + pdfUrl + '" width="100%" height="600px" frameborder="0" style="border-radius: 8px;"></iframe>';
			
			// แสดง modal
			const modal = new bootstrap.Modal(document.getElementById('pdfViewerModal'));
			modal.show();
		}

		// ฟังก์ชันดาวน์โหลด (แสดง modal ก่อน)
		function downloadSecurePDF(fileId, filename) {
		    console.log('🔐 Preparing secure download for file:', fileId, filename);
		    
		    currentFileId = fileId;
		    
		    // อัปเดตชื่อไฟล์ใน modal
		    const filenameDisplay = document.getElementById('download-filename');
		    if (filenameDisplay) {
		        filenameDisplay.textContent = filename || 'ไฟล์';
		    }
		    
		    // แสดงข้อมูลการหมดอายุ (ทดสอบ 1 นาที)
		    const today = new Date();
		    const expiryDate = new Date(today);
		    expiryDate.setMinutes(expiryDate.getMinutes() + 1); // เปลี่ยนเป็น 1 นาที
		    
		    const thaiMonths = ['มกราคม', 'กุมภาพันธ์', 'มีนาคม', 'เมษายน', 'พฤษภาคม', 'มิถุนายน',
		                       'กรกฎาคม', 'สิงหาคม', 'กันยายน', 'ตุลาคม', 'พฤศจิกายน', 'ธันวาคม'];
		    
		    const expiryDay = expiryDate.getDate();
		    const expiryMonth = expiryDate.getMonth();
		    const expiryYear = expiryDate.getFullYear() + 543;
		    const expiryHour = String(expiryDate.getHours()).padStart(2, '0');
		    const expiryMinute = String(expiryDate.getMinutes()).padStart(2, '0');
		    const expirySecond = String(expiryDate.getSeconds()).padStart(2, '0');
		    
		    const expiryStr = expiryDay + ' ' + thaiMonths[expiryMonth] + ' ' + expiryYear + 
		                     ' เวลา ' + expiryHour + ':' + expiryMinute + ':' + expirySecond + ' น.';
		    
		    // คำนวณเวลาที่เหลือ
		    const diffMs = expiryDate - today;
		    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
		    const diffHours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
		    const diffSeconds = Math.floor((diffMs % (1000 * 60)) / 1000);
		    
		    let timeLeftStr = '';
		    if (diffDays > 0) {
		        timeLeftStr = diffDays + ' วัน ' + diffHours + ' ชั่วโมง ' + diffMinutes + ' นาที';
		    } else if (diffHours > 0) {
		        timeLeftStr = diffHours + ' ชั่วโมง ' + diffMinutes + ' นาที ' + diffSeconds + ' วินาที';
		    } else if (diffMinutes > 0) {
		        timeLeftStr = diffMinutes + ' นาที ' + diffSeconds + ' วินาที';
		    } else {
		        timeLeftStr = diffSeconds + ' วินาที';
		    }
		    
		    // อัปเดตข้อมูลใน modal
		    const expiryDetailsContainer = document.getElementById('expiry-details-container');
		    if (expiryDetailsContainer) {
		        expiryDetailsContainer.innerHTML = 
		            '<div class="mb-1">📅 <strong>วันหมดอายุ:</strong> ' + expiryStr + '</div>' +
		            '<div class="mb-1">⏰ <strong>เหลืออีก:</strong> ' + timeLeftStr + '</div>';
		    }
		    
		    // แสดง modal
		    const modal = new bootstrap.Modal(document.getElementById('downloadInfoModal'));
		    modal.show();
		}
		// เริ่มดาวน์โหลดจริง
		function startSecureDownload() {
			if (!currentFileId) {
				alert('ไม่พบข้อมูลไฟล์');
				return;
			}
			
			const btn = document.getElementById('start-download-btn');
			const originalText = btn.innerHTML;
			
			// แสดง loading
			btn.disabled = true;
			btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>กำลังดาวน์โหลด...';
			
			// ดาวน์โหลดไฟล์
			window.location.href = contextPath + '/download/secure/' + currentFileId;
			
			// รีเซ็ตปุ่มหลัง 3 วินาที
			setTimeout(function() {
				btn.disabled = false;
				btn.innerHTML = originalText;
				
				// ปิด modal
				const modal = bootstrap.Modal.getInstance(document.getElementById('downloadInfoModal'));
				if (modal) {
					modal.hide();
				}
				
				// แสดงข้อความสำเร็จ
				showSuccessToast('ดาวน์โหลดไฟล์สำเร็จ! ไฟล์มีอายุ 14 วัน');
			}, 3000);
		}

		// แสดง toast notification
		function showSuccessToast(message) {
			// สร้าง toast element ถ้ายังไม่มี
			let toastContainer = document.getElementById('toast-container');
			if (!toastContainer) {
				toastContainer = document.createElement('div');
				toastContainer.id = 'toast-container';
				toastContainer.className = 'position-fixed bottom-0 end-0 p-3';
				toastContainer.style.zIndex = '11';
				document.body.appendChild(toastContainer);
			}
			
			const toastId = 'toast-' + Date.now();
			const toastHTML = `
				<div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
					<div class="toast-header bg-success text-white">
						<i class="bi bi-check-circle-fill me-2"></i>
						<strong class="me-auto">สำเร็จ</strong>
						<button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
					</div>
					<div class="toast-body">
						${message}
					</div>
				</div>
			`;
			
			toastContainer.insertAdjacentHTML('beforeend', toastHTML);
			
			const toastElement = document.getElementById(toastId);
			const toast = new bootstrap.Toast(toastElement, { delay: 4000 });
			toast.show();
			
			// ลบ toast หลังจากซ่อน
			toastElement.addEventListener('hidden.bs.toast', function() {
				toastElement.remove();
			});
		}

		// เมื่อปิด PDF modal ให้ล้าง iframe
		const pdfModal = document.getElementById('pdfViewerModal');
		if (pdfModal) {
			pdfModal.addEventListener('hidden.bs.modal', function() {
				const pdfViewer = document.getElementById('pdf-viewer');
				if (pdfViewer) {
					pdfViewer.innerHTML = '';
				}
			});
		}

		// Log สำหรับ debug
		console.log("✅ PDF Viewer with Secure Expiring Download initialized");
		console.log("🔧 Context Path:", contextPath);
		console.log("📅 Expiry Period: 14 days from download");
	</script>
</body>
</html>