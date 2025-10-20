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

	<!-- ✅ ตรวจสอบ URL parameter และแสดง SweetAlert -->
	<script>
	document.addEventListener('DOMContentLoaded', function() {
	    const urlParams = new URLSearchParams(window.location.search);
	    
	    if (urlParams.get('success') === 'true') {
	        Swal.fire({
	            icon: 'success',
	            title: 'สำเร็จ!',
	            text: 'บันทึกข้อมูลเรียบร้อยแล้ว',
	            showConfirmButton: false,
	            timer: 2000
	        }).then(() => {
	            // ✅ ลบ parameter ออกจาก URL (ไม่ให้แสดงซ้ำเมื่อ refresh)
	            window.history.replaceState({}, document.title, window.location.pathname);
	        });
	    }
	    
	    if (urlParams.get('error') === 'true') {
	        Swal.fire({
	            icon: 'error',
	            title: 'เกิดข้อผิดพลาด',
	            text: 'ไม่สามารถบันทึกข้อมูลได้ กรุณาลองใหม่อีกครั้ง',
	            confirmButtonText: 'ตกลง'
	        }).then(() => {
	            // ✅ ลบ parameter ออกจาก URL
	            window.history.replaceState({}, document.title, window.location.pathname);
	        });
	    }
	});
	</script>

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
					class="form-select" name="projectType" id="projectTypeSelect"
					onchange="handleProjectTypeChange()">
					<c:forEach var="type" items="${projectTypes}">
						<option value="${type}"
							${type == project.projectType ? 'selected' : ''}>${type}</option>
					</c:forEach>
				</select>
			</div>

			<!-- ✅ เครื่องมือการทดสอบ (แสดงเฉพาะเมื่อ projectType = Testing) -->
			<div class="mb-4" id="testingToolsSection" style="display: none;">
				<label class="form-label fw-bold"> <i
					class="bi bi-check2-square"></i> เครื่องมือการทดสอบ :
				</label>
				<div id="testingToolsContainer">
					<c:set var="hasTestingTools" value="false" />
					<c:forEach var="tool" items="${project.tools}">
						<c:if test="${tool.toolType == 'Testing'}">
							<c:set var="hasTestingTools" value="true" />
							<div class="tool-item">
								<select class="form-select" name="testingToolIds">
									<option value="">-- เลือกเครื่องมือทดสอบ --</option>
									<c:forEach var="t" items="${allTools}">
										<c:if test="${t.toolType == 'Testing'}">
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

					<c:if test="${!hasTestingTools}">
						<div class="tool-item">
							<select class="form-select" name="testingToolIds">
								<option value="">-- เลือกเครื่องมือทดสอบ --</option>
								<c:forEach var="t" items="${allTools}">
									<c:if test="${t.toolType == 'Testing'}">
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
						onclick="addTestingTool()">
						<i class="bi bi-plus-circle"></i> เพิ่มเครื่องมือทดสอบ
					</button>
				</div>
				<div class="text-danger small error-msg" id="testingToolsError"></div>
			</div>

			<!-- ✅ เครื่องมือการเขียนโปรแกรม (ซ่อนเมื่อ projectType = Testing) -->
			<div class="mb-4" id="programmingToolsSection">
				<label class="form-label fw-bold"> <i
					class="bi bi-code-slash"></i> เครื่องมือการเขียนโปรแกรม :
				</label>
				<div id="programmingToolsContainer">
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

			<!-- ✅ ระบบจัดการฐานข้อมูล (ซ่อนเมื่อ projectType = Testing) -->
			<div class="mb-4" id="dbmsToolsSection">
				<label class="form-label fw-bold"> <i class="bi bi-database"></i>
					ระบบจัดการฐานข้อมูล :
				</label>
				<div id="dbmsToolsContainer">
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

			<!-- ✅ เพิ่ม Tools ใหม่ -->
			<div class="mb-4 border p-3 rounded bg-light">
				<label class="form-label fw-bold"> <i
					class="bi bi-plus-square"></i> เพิ่ม Tools ใหม่ลงในระบบ :
				</label>
				<div class="row g-2">
					<div class="col-md-4">
						<label class="form-label small">ประเภท Tools:</label> <select
							class="form-select" id="newToolType" name="newToolType"
							onchange="toggleNewToolInput()">
							<option value="">-- เลือกประเภท --</option>
							<option value="PROGRAMMING">Programming</option>
							<option value="DBMS">DBMS</option>
							<option value="Testing">Testing</option>
						</select>
					</div>
					<div class="col-md-6">
						<label class="form-label small">ชื่อ Tools:</label> <input
							type="text" class="form-control" id="newToolName"
							name="newToolName" placeholder="กรอกชื่อ Tools" disabled>
					</div>
					<div class="col-md-2 d-flex align-items-end">
						<button type="button" class="btn btn-success w-100"
							onclick="addNewToolToDatabase()" id="btnAddNewTool" disabled>
							<i class="bi bi-save"></i> เพิ่ม
						</button>
					</div>
				</div>
				<small class="text-muted"> <i class="bi bi-info-circle"></i>
					เมื่อเพิ่ม Tools ใหม่สำเร็จ จะแสดงใน Dropdown ด้านบนอัตโนมัติ
				</small>
			</div>

			<!-- บทคัดย่อ -->
			<div class="mb-3">
				<label class="form-label fw-bold">บทคัดย่อ (ภาษาไทย) :</label>
				<a href="${pageContext.request.contextPath}/assets/files/sample_abstract.pdf"
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

	<select id="testingToolTemplate" style="display: none;">
		<option value="">-- เลือกเครื่องมือทดสอบ --</option>
		<c:forEach var="t" items="${allTools}">
			<c:if test="${t.toolType == 'Testing'}">
				<option value="${t.toolsId}">${t.toolsName}</option>
			</c:if>
		</c:forEach>
	</select>

	<script>
let thaiEditor, engEditor;

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

// ✅ จัดการแสดง/ซ่อน Tools ตาม Project Type
function handleProjectTypeChange() {
    const projectType = document.getElementById('projectTypeSelect').value;
    const programmingSection = document.getElementById('programmingToolsSection');
    const dbmsSection = document.getElementById('dbmsToolsSection');
    const testingSection = document.getElementById('testingToolsSection');
    
    if (projectType === 'Testing') {
        programmingSection.style.display = 'none';
        dbmsSection.style.display = 'none';
        testingSection.style.display = 'block';
    } else {
        programmingSection.style.display = 'block';
        dbmsSection.style.display = 'block';
        testingSection.style.display = 'none';
    }
}

// ✅ เรียกใช้ตอน page load
window.addEventListener('DOMContentLoaded', function() {
    handleProjectTypeChange();
});

// ✅ Toggle input field เมื่อเลือกประเภท Tools
function toggleNewToolInput() {
    const toolType = document.getElementById('newToolType').value;
    const toolNameInput = document.getElementById('newToolName');
    const addButton = document.getElementById('btnAddNewTool');
    
    if (toolType !== '') {
        toolNameInput.disabled = false;
        addButton.disabled = false;
    } else {
        toolNameInput.disabled = true;
        toolNameInput.value = '';
        addButton.disabled = true;
    }
}

// ✅ เพิ่ม Tools ใหม่ลงฐานข้อมูล (ไม่รีเฟรชหน้า)
function addNewToolToDatabase() {
    const toolType = document.getElementById('newToolType').value;
    const toolName = document.getElementById('newToolName').value.trim();
    
    if (!toolType) {
        Swal.fire({ icon: 'warning', title: 'กรุณาเลือกประเภท Tools', timer: 2000 });
        return;
    }
    
    if (!toolName) {
        Swal.fire({ icon: 'warning', title: 'กรุณากรอกชื่อ Tools', timer: 2000 });
        return;
    }
    
    // ส่งข้อมูลไปบันทึก
    fetch('${pageContext.request.contextPath}/addNewTool', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'toolType=' + encodeURIComponent(toolType) + '&toolName=' + encodeURIComponent(toolName)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // เพิ่ม option ใหม่ลงใน template ที่เกี่ยวข้อง
            const newOption = document.createElement('option');
            newOption.value = data.toolId;
            newOption.textContent = toolName;
            
            if (toolType === 'PROGRAMMING') {
                document.getElementById('programmingToolTemplate').appendChild(newOption.cloneNode(true));
            } else if (toolType === 'DBMS') {
                document.getElementById('dbmsToolTemplate').appendChild(newOption.cloneNode(true));
            } else if (toolType === 'Testing') {
                document.getElementById('testingToolTemplate').appendChild(newOption.cloneNode(true));
            }
            
            // รีเซ็ตฟอร์มเพิ่ม Tools
            document.getElementById('newToolType').value = '';
            document.getElementById('newToolName').value = '';
            document.getElementById('newToolName').disabled = true;
            document.getElementById('btnAddNewTool').disabled = true;
            
            Swal.fire({ 
                icon: 'success', 
                title: 'เพิ่ม Tools สำเร็จ!', 
                text: 'สามารถเลือก Tools ใหม่ได้ใน Dropdown แล้ว', 
                timer: 2000, 
                showConfirmButton: false 
            });
        } else {
            Swal.fire({ icon: 'error', title: 'เกิดข้อผิดพลาด', text: data.message });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        Swal.fire({ icon: 'error', title: 'เกิดข้อผิดพลาด', text: 'ไม่สามารถเพิ่ม Tools ได้' });
    });
}

function addProgrammingTool() {
    const container = document.getElementById('programmingToolsContainer');
    const div = document.createElement('div');
    div.className = 'tool-item';
    
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
}

function addDbmsTool() {
    const container = document.getElementById('dbmsToolsContainer');
    const div = document.createElement('div');
    div.className = 'tool-item';
    
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
}

function addTestingTool() {
    const container = document.getElementById('testingToolsContainer');
    const div = document.createElement('div');
    div.className = 'tool-item';
    
    const templateSelect = document.getElementById('testingToolTemplate');
    const newSelect = templateSelect.cloneNode(true);
    newSelect.removeAttribute('id');
    newSelect.removeAttribute('style');
    newSelect.name = 'testingToolIds';
    newSelect.className = 'form-select';
    
    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'btn btn-danger btn-sm btn-remove';
    removeBtn.onclick = function() { removeToolItem(this); };
    removeBtn.innerHTML = '<i class="bi bi-trash"></i>';
    
    div.appendChild(newSelect);
    div.appendChild(removeBtn);
    container.appendChild(div);
}

function removeToolItem(btn) {
    const container = btn.closest('.tool-item').parentElement;
    btn.closest('.tool-item').remove();
    
    if (container.children.length === 0) {
        if (container.id === 'programmingToolsContainer') {
            addProgrammingTool();
        } else if (container.id === 'dbmsToolsContainer') {
            addDbmsTool();
        } else if (container.id === 'testingToolsContainer') {
            addTestingTool();
        }
    }
}

document.querySelector('form').addEventListener('submit', function(e) {
    e.preventDefault();
    const form = this;
    form.querySelectorAll('.error-msg').forEach(div => div.textContent = '');
    let valid = true;

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

    // ✅ ตรวจสอบตาม Project Type
    const projectType = document.getElementById('projectTypeSelect').value;
    
    if (projectType === 'Testing') {
        // ต้องเลือก Testing Tools
        const testingTools = Array.from(form.querySelectorAll('select[name="testingToolIds"]'))
            .map(s => s.value)
            .filter(v => v !== "");
        
        if (testingTools.length === 0) {
            document.getElementById('testingToolsError').textContent = "*กรุณาเลือกเครื่องมือทดสอบอย่างน้อย 1 รายการ";
            valid = false;
        } else {
            const uniqueTestingTools = new Set(testingTools);
            if (uniqueTestingTools.size !== testingTools.length) {
                document.getElementById('testingToolsError').textContent = "*มีการเลือกเครื่องมือซ้ำกัน กรุณาตรวจสอบ";
                valid = false;
            }
        }
    } else {
        // ต้องเลือก Programming Tools
        const progTools = Array.from(form.querySelectorAll('select[name="programmingToolIds"]'))
            .map(s => s.value)
            .filter(v => v !== "");
        
        if (progTools.length === 0) {
            document.getElementById('programmingToolsError').textContent = "*กรุณาเลือกเครื่องมือการเขียนโปรแกรมอย่างน้อย 1 รายการ";
            valid = false;
        } else {
            const uniqueProgTools = new Set(progTools);
            if (uniqueProgTools.size !== progTools.length) {
                document.getElementById('programmingToolsError').textContent = "*มีการเลือกเครื่องมือซ้ำกัน กรุณาตรวจสอบ";
                valid = false;
            }
        }

        // ต้องเลือก DBMS Tools
        const dbmsToolsList = Array.from(form.querySelectorAll('select[name="dbmsToolIds"]'))
            .map(s => s.value)
            .filter(v => v !== "");
        
        if (dbmsToolsList.length === 0) {
            document.getElementById('dbmsToolsError').textContent = "*กรุณาเลือกระบบจัดการฐานข้อมูลอย่างน้อย 1 รายการ";
            valid = false;
        } else {
            const uniqueDbmsTools = new Set(dbmsToolsList);
            if (uniqueDbmsTools.size !== dbmsToolsList.length) {
                document.getElementById('dbmsToolsError').textContent = "*มีการเลือกฐานข้อมูลซ้ำกัน กรุณาตรวจสอบ";
                valid = false;
            }
        }
    }

    const abstractTh = thaiEditor.getData().trim();
    
    if (!abstractTh || abstractTh === '<p>&nbsp;</p>' || abstractTh === '<p></p>') { 
        document.getElementById('thaiAbstractError').textContent = "*กรุณากรอกบทคัดย่อภาษาไทย"; 
        valid = false; 
    }
    else if (abstractTh.length < 500 || abstractTh.length > 1500) { 
        document.getElementById('thaiAbstractError').textContent = "*บทคัดย่อภาษาไทย ต้องมีความยาว 500–1500 ตัวอักษร"; 
        valid = false; 
    }

    const abstractEn = engEditor.getData().trim();
    
    if (!abstractEn || abstractEn === '<p>&nbsp;</p>' || abstractEn === '<p></p>') { 
        document.getElementById('engAbstractError').textContent = "*กรุณากรอกบทคัดย่อภาษาอังกฤษ"; 
        valid = false; 
    }
    else if (abstractEn.length < 500 || abstractEn.length > 1500) { 
        document.getElementById('engAbstractError').textContent = "*บทคัดย่อภาษาอังกฤษ ต้องมีความยาว 500–1500 ตัวอักษร"; 
        valid = false; 
    }

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
        // ✅ แสดง loading indicator ขณะบันทึก
        Swal.fire({
            title: 'กำลังบันทึกข้อมูล...',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
        
        // ✅ ส่งฟอร์ม
        form.submit();
    }
});
</script>
</body>
</html>