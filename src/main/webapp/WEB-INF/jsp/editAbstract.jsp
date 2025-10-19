<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="th">
<head>
<meta charset="UTF-8">
<title>แก้ไขบทคัดย่อ</title>
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/ITLOGO.jpg">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Kanit&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/editAbstract.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script
	src="https://cdn.ckeditor.com/ckeditor5/39.0.0/classic/ckeditor.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

	<div class="container mt-5">
		<h5 class="fw-bold">แก้ไขบทคัดย่อ</h5>
		<hr>

		<form action="${pageContext.request.contextPath}/updateAbstract"
			method="post">
			<input type="hidden" name="projectId" value="${project.projectId}">

			<!-- ผู้จัดทำ -->
			<div class="mb-3">
				<c:forEach var="student" items="${project.student496s}"
					varStatus="status">
					<c:choose>
						<c:when test="${project.student496s.size() > 1}">
							<span class="fw-bold">ผู้จัดทำคนที่ ${status.index + 1}:</span> ${student.stuId} ${student.stu_firstName} ${student.stu_lastName}
                    </c:when>
						<c:otherwise>
							<span class="fw-bold">ผู้จัดทำ:</span> ${student.stuId} ${student.stu_firstName} ${student.stu_lastName}
                    </c:otherwise>
					</c:choose>
					<br />
				</c:forEach>
			</div>

			<!-- ชื่อโครงงานภาษาไทย -->
			<div class="mb-3">
				<label class="form-label fw-bold">ชื่อโครงงานภาษาไทย :</label> <input
					type="text" class="form-control" name="projNameTh"
					value="${project.proj_NameTh != null ? project.proj_NameTh : ''}"
					placeholder="กรอกชื่อโครงงานภาษาไทย">
				<div class="text-danger small error-msg" id="projNameThError"></div>
			</div>

			<!-- ชื่อโครงงานภาษาอังกฤษ -->
			<div class="mb-3">
				<label class="form-label fw-bold">ชื่อโครงงานภาษาอังกฤษ :</label> <input
					type="text" class="form-control" name="projNameEn"
					value="${project.proj_NameEn != null ? project.proj_NameEn : ''}"
					placeholder="กรอกชื่อโครงงานภาษาอังกฤษ">
				<div class="text-danger small error-msg" id="projNameEnError"></div>
			</div>

			<!-- ประเภทโครงงาน -->
			<div class="mb-3">
				<label class="form-label fw-bold">ประเภทโครงงาน :</label> <select
					class="form-select" name="projectType">
					<c:forEach var="type" items="${projectTypes}">
						<option value="${type}"
							${type == project.projectType ? 'selected' : ''}>${type}</option>
					</c:forEach>
				</select>
			</div>

			<!-- เครื่องมือการเขียนโปรแกรม -->
			<div class="mb-4">
				<label class="form-label fw-bold"> <i
					class="bi bi-code-slash"></i> เครื่องมือการเขียนโปรแกรม :
				</label>
				<div id="programmingToolsContainer">
					<!-- แสดง tools ที่เลือกไว้แล้ว (PROGRAMMING) -->
					<c:set var="hasProgTools" value="false" />
					<c:forEach var="tool" items="${project.tools}">
						<c:if test="${tool.toolType == 'PROGRAMMING'}">
							<c:set var="hasProgTools" value="true" />
							<div class="tool-item">
								<select class="form-select" name="programmingToolIds">
									<option value="">-- เลือกเครื่องมือ --</option>
									<c:forEach var="t" items="${allTools}">
										<c:if test="${t.toolType == 'PROGRAMMING'}">
											<option value="${t.toolsId}"
												${t.toolsId == tool.toolsId ? 'selected' : ''}>
												${t.toolsName}</option>
										</c:if>
									</c:forEach>
								</select>
								<button type="button" class="btn btn-danger btn-sm btn-remove"
									onclick="removeToolItem(this)">
									<i class="bi bi-trash"></i>
								</button>
							</div>
						</c:if>
					</c:forEach>

					<!-- ถ้าไม่มี tool ที่เลือกไว้ ให้แสดง dropdown เปล่า 1 อัน -->
					<c:if test="${!hasProgTools}">
						<div class="tool-item">
							<select class="form-select" name="programmingToolIds">
								<option value="">-- เลือกเครื่องมือ --</option>
								<c:forEach var="t" items="${allTools}">
									<c:if test="${t.toolType == 'PROGRAMMING'}">
										<option value="${t.toolsId}">${t.toolsName}</option>
									</c:if>
								</c:forEach>
							</select>
							<button type="button" class="btn btn-danger btn-sm btn-remove"
								onclick="removeToolItem(this)">
								<i class="bi bi-trash"></i>
							</button>
						</div>
					</c:if>
				</div>
				<div class="add-tool-section">
					<button type="button" class="btn btn-sm btn-outline-primary"
						onclick="addProgrammingTool()">
						<i class="bi bi-plus-circle"></i> เพิ่มเครื่องมือ
					</button>
				</div>
				<div class="text-danger small error-msg" id="programmingToolsError"></div>
			</div>

			<!-- ระบบจัดการฐานข้อมูล -->
			<div class="mb-4">
				<label class="form-label fw-bold"> <i class="bi bi-database"></i>
					ระบบจัดการฐานข้อมูล :
				</label>
				<div id="dbmsToolsContainer">
					<!-- แสดง tools ที่เลือกไว้แล้ว (DBMS) -->
					<c:set var="hasDbmsTools" value="false" />
					<c:forEach var="tool" items="${project.tools}">
						<c:if test="${tool.toolType == 'DBMS'}">
							<c:set var="hasDbmsTools" value="true" />
							<div class="tool-item">
								<select class="form-select" name="dbmsToolIds">
									<option value="">-- เลือกฐานข้อมูล --</option>
									<c:forEach var="t" items="${allTools}">
										<c:if test="${t.toolType == 'DBMS'}">
											<option value="${t.toolsId}"
												${t.toolsId == tool.toolsId ? 'selected' : ''}>
												${t.toolsName}</option>
										</c:if>
									</c:forEach>
								</select>
								<button type="button" class="btn btn-danger btn-sm btn-remove"
									onclick="removeToolItem(this)">
									<i class="bi bi-trash"></i>
								</button>
							</div>
						</c:if>
					</c:forEach>

					<!-- ถ้าไม่มี tool ที่เลือกไว้ ให้แสดง dropdown เปล่า 1 อัน -->
					<c:if test="${!hasDbmsTools}">
						<div class="tool-item">
							<select class="form-select" name="dbmsToolIds">
								<option value="">-- เลือกฐานข้อมูล --</option>
								<c:forEach var="t" items="${allTools}">
									<c:if test="${t.toolType == 'DBMS'}">
										<option value="${t.toolsId}">${t.toolsName}</option>
									</c:if>
								</c:forEach>
							</select>
							<button type="button" class="btn btn-danger btn-sm btn-remove"
								onclick="removeToolItem(this)">
								<i class="bi bi-trash"></i>
							</button>
						</div>
					</c:if>
				</div>
				<div class="add-tool-section">
					<button type="button" class="btn btn-sm btn-outline-primary"
						onclick="addDbmsTool()">
						<i class="bi bi-plus-circle"></i> เพิ่มฐานข้อมูล
					</button>
				</div>
				<div class="text-danger small error-msg" id="dbmsToolsError"></div>
			</div>

			<!-- บทคัดย่อ -->
			<div class="mb-3">
				<label class="form-label fw-bold">บทคัดย่อ (ภาษาไทย) :</label> <a
					href="${pageContext.request.contextPath}/assets/files/sample_abstract.pdf"
					target="_blank"> ตัวอย่างการเขียนบทคัดย่อและคำสำคัญ (PDF) </a>
				<textarea id="thaiAbstract" class="form-control" name="abstractTh"
					rows="5">${project.abstractTh != null ? project.abstractTh : ''}</textarea>
				<div class="text-danger small error-msg" id="thaiAbstractError"></div>

				<label class="form-label fw-bold mt-3">บทคัดย่อ (ภาษาอังกฤษ)
					:</label>
				<textarea id="engAbstract" class="form-control" name="abstractEn"
					rows="5">${project.abstractEn != null ? project.abstractEn : ''}</textarea>
				<div class="text-danger small error-msg" id="engAbstractError"></div>
			</div>

			<!-- คำสำคัญ -->
			<div class="mb-3">
				<label class="form-label fw-bold">คำสำคัญ (ภาษาไทย) :</label> <input
					type="text" class="form-control" name="keywordTh"
					value="${project.keywordTh != null ? project.keywordTh : ''}"
					placeholder="กรอกคำสำคัญภาษาไทย">
				<div class="text-danger small error-msg" id="keywordThError"></div>
			</div>

			<div class="mb-3">
				<label class="form-label fw-bold">คำสำคัญ (ภาษาอังกฤษ) :</label> <input
					type="text" class="form-control" name="keywordEn"
					value="${project.keywordEn != null ? project.keywordEn : ''}"
					placeholder="กรอกคำสำคัญภาษาอังกฤษ">
				<div class="text-danger small error-msg" id="keywordEnError"></div>
			</div>

			<!-- ปุ่ม -->
			<div class="text-center">
				<button type="submit" class="btn btn-success">บันทึก</button>
				<a href="${pageContext.request.contextPath}/searchProjects"
					class="btn btn-danger">ยกเลิก</a>
			</div>
		</form>
	</div>

	<!-- ✅ Hidden Templates สำหรับ clone -->
	<select id="programmingToolTemplate" style="display: none;">
		<option value="">-- เลือกเครื่องมือ --</option>
		<c:forEach var="t" items="${allTools}">
			<c:if test="${t.toolType == 'PROGRAMMING'}">
				<option value="${t.toolsId}">${t.toolsName}</option>
			</c:if>
		</c:forEach>
	</select>

	<select id="dbmsToolTemplate" style="display: none;">
		<option value="">-- เลือกฐานข้อมูล --</option>
		<c:forEach var="t" items="${allTools}">
			<c:if test="${t.toolType == 'DBMS'}">
				<option value="${t.toolsId}">${t.toolsName}</option>
			</c:if>
		</c:forEach>
	</select>

	<script>
// ✅ ประกาศตัวแปรก่อนทุกอย่าง
let thaiEditor, engEditor;

// สร้าง CKEditor
ClassicEditor
    .create(document.querySelector('#thaiAbstract'), { 
        toolbar: ['heading', '|', 'bold', 'italic', '|', 'link']
    })
    .then(editor => { thaiEditor = editor; })
    .catch(error => console.error('CKEditor Thai Error:', error));

ClassicEditor
    .create(document.querySelector('#engAbstract'), { 
        toolbar: ['heading', '|', 'bold', 'italic', '|', 'link']
    })
    .then(editor => { engEditor = editor; })
    .catch(error => console.error('CKEditor English Error:', error));

// เพิ่มเครื่องมือการเขียนโปรแกรม
function addProgrammingTool() {
    console.log('addProgrammingTool called');
    
    const container = document.getElementById('programmingToolsContainer');
    const div = document.createElement('div');
    div.className = 'tool-item';
    
    // ✅ Clone จาก template
    const templateSelect = document.getElementById('programmingToolTemplate');
    const newSelect = templateSelect.cloneNode(true);
    newSelect.removeAttribute('id');
    newSelect.removeAttribute('style');
    newSelect.name = 'programmingToolIds';
    newSelect.className = 'form-select';
    
    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'btn btn-danger btn-sm btn-remove';
    removeBtn.onclick = function() { removeToolItem(this); };
    removeBtn.innerHTML = '<i class="bi bi-trash"></i>';
    
    div.appendChild(newSelect);
    div.appendChild(removeBtn);
    container.appendChild(div);
    console.log('New programming dropdown added');
}

// เพิ่มระบบจัดการฐานข้อมูล
function addDbmsTool() {
    console.log('addDbmsTool called');
    
    const container = document.getElementById('dbmsToolsContainer');
    const div = document.createElement('div');
    div.className = 'tool-item';
    
    // ✅ Clone จาก template
    const templateSelect = document.getElementById('dbmsToolTemplate');
    const newSelect = templateSelect.cloneNode(true);
    newSelect.removeAttribute('id');
    newSelect.removeAttribute('style');
    newSelect.name = 'dbmsToolIds';
    newSelect.className = 'form-select';
    
    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'btn btn-danger btn-sm btn-remove';
    removeBtn.onclick = function() { removeToolItem(this); };
    removeBtn.innerHTML = '<i class="bi bi-trash"></i>';
    
    div.appendChild(newSelect);
    div.appendChild(removeBtn);
    container.appendChild(div);
    console.log('New DBMS dropdown added');
}

// ลบรายการ tool
function removeToolItem(btn) {
    const container = btn.closest('.tool-item').parentElement;
    btn.closest('.tool-item').remove();
    
    // ถ้าไม่มีรายการเหลือเลย ให้เพิ่ม 1 รายการเปล่า
    if (container.children.length === 0) {
        if (container.id === 'programmingToolsContainer') {
            addProgrammingTool();
        } else {
            addDbmsTool();
        }
    }
}

document.querySelector('form').addEventListener('submit', function(e) {
    e.preventDefault();
    const form = this;
    form.querySelectorAll('.error-msg').forEach(div => div.textContent = '');
    let valid = true;

    // ชื่อโครงงานภาษาไทย
    const projNameTh = form.projNameTh.value.trim();
    const regexTh = /^[ก-๙A-Za-z0-9(),.\s]+$/;
    if (!projNameTh) { 
        document.getElementById('projNameThError').textContent = "*กรุณากรอกชื่อโครงงานภาษาไทย"; 
        valid = false; 
    }
    else if (!regexTh.test(projNameTh)) { 
        document.getElementById('projNameThError').textContent = "*ชื่อโครงงานภาษาไทย สามารถเป็นภาษาไทย อังกฤษ และตัวเลขได้ อักขระพิเศษ () , เท่านั้น"; 
        valid = false; 
    }

    // ชื่อโครงงานภาษาอังกฤษ
    const projNameEn = form.projNameEn.value.trim();
    const regexEn = /^[A-Za-z0-9(),.\s]+$/;
    if (!projNameEn) { 
        document.getElementById('projNameEnError').textContent = "*กรุณากรอกชื่อโครงงานภาษาอังกฤษ"; 
        valid = false; 
    }
    else if (!regexEn.test(projNameEn)) { 
        document.getElementById('projNameEnError').textContent = "*ชื่อโครงงานภาษาอังกฤษ สามารถเป็นอังกฤษ และตัวเลขได้ อักขระพิเศษ () , . เท่านั้น"; 
        valid = false; 
    }

    // ตรวจสอบเครื่องมือการเขียนโปรแกรม
    const progTools = Array.from(form.querySelectorAll('select[name="programmingToolIds"]'))
        .map(s => s.value)
        .filter(v => v !== "");
    
    if (progTools.length === 0) {
        document.getElementById('programmingToolsError').textContent = "*กรุณาเลือกเครื่องมือการเขียนโปรแกรมอย่างน้อย 1 รายการ";
        valid = false;
    } else {
        // ตรวจสอบการเลือกซ้ำ
        const uniqueProgTools = new Set(progTools);
        if (uniqueProgTools.size !== progTools.length) {
            document.getElementById('programmingToolsError').textContent = "*มีการเลือกเครื่องมือซ้ำกัน กรุณาตรวจสอบ";
            valid = false;
        }
    }

    // ตรวจสอบระบบจัดการฐานข้อมูล
    const dbmsToolsList = Array.from(form.querySelectorAll('select[name="dbmsToolIds"]'))
        .map(s => s.value)
        .filter(v => v !== "");
    
    if (dbmsToolsList.length === 0) {
        document.getElementById('dbmsToolsError').textContent = "*กรุณาเลือกระบบจัดการฐานข้อมูลอย่างน้อย 1 รายการ";
        valid = false;
    } else {
        // ตรวจสอบการเลือกซ้ำ
        const uniqueDbmsTools = new Set(dbmsToolsList);
        if (uniqueDbmsTools.size !== dbmsToolsList.length) {
            document.getElementById('dbmsToolsError').textContent = "*มีการเลือกฐานข้อมูลซ้ำกัน กรุณาตรวจสอบ";
            valid = false;
        }
    }

    // บทคัดย่อ
    const abstractTh = thaiEditor.getData().trim();
    const abstractThPlain = abstractTh.replace(/<[^>]*>/g, '');
    const hasEnglishInThaiAbstract = /[A-Za-z]/.test(abstractThPlain);
    
    if (abstractTh.length < 500 || abstractTh.length > 1500) { 
        document.getElementById('thaiAbstractError').textContent = "*บทคัดย่อภาษาไทย ต้องมีความยาว 500–1000 ตัวอักษร"; 
        valid = false; 
    }
    else if (hasEnglishInThaiAbstract) {
        document.getElementById('thaiAbstractError').textContent = "*บทคัดย่อภาษาไทย ต้องกรอกเป็นภาษาไทยเท่านั้น"; 
        valid = false;
    }

    const abstractEn = engEditor.getData().trim();
    const abstractEnPlain = abstractEn.replace(/<[^>]*>/g, '');
    const hasThaiInEngAbstract = /[ก-๙]/.test(abstractEnPlain);
    
    if (abstractEn.length < 500 || abstractEn.length > 1500) { 
        document.getElementById('engAbstractError').textContent = "*บทคัดย่อภาษาอังกฤษ ต้องมีความยาว 500–1000 ตัวอักษร"; 
        valid = false; 
    }
    else if (hasThaiInEngAbstract) {
        document.getElementById('engAbstractError').textContent = "*บทคัดย่อภาษาอังกฤษ ต้องกรอกเป็นภาษาอังกฤษเท่านั้น"; 
        valid = false;
    }

    // คำสำคัญ
    const keywordTh = form.keywordTh.value.trim();
    const hasEnglishInKeywordTh = /[A-Za-z]/.test(keywordTh);
    
    if (!keywordTh) { 
        document.getElementById('keywordThError').textContent = "*กรุณากรอกคำสำคัญภาษาไทย"; 
        valid = false; 
    }
    else if (hasEnglishInKeywordTh) {
        document.getElementById('keywordThError').textContent = "*คำสำคัญภาษาไทย ต้องกรอกเป็นภาษาไทยเท่านั้น"; 
        valid = false;
    }
    
    const keywordEn = form.keywordEn.value.trim();
    const hasThaiInKeywordEn = /[ก-๙]/.test(keywordEn);
    
    if (!keywordEn) { 
        document.getElementById('keywordEnError').textContent = "*กรุณากรอกคำสำคัญภาษาอังกฤษ"; 
        valid = false; 
    }
    else if (hasThaiInKeywordEn) {
        document.getElementById('keywordEnError').textContent = "*คำสำคัญภาษาอังกฤษ ต้องกรอกเป็นภาษาอังกฤษเท่านั้น"; 
        valid = false;
    }

    if (valid) {
        Swal.fire({ icon: 'success', title: 'บันทึกข้อมูลสำเร็จ', showConfirmButton: false, timer: 1500 })
            .then(() => { form.submit(); });
    }
});
</script>
</body>
</html>