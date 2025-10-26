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

			<!-- ✅ เครื่องมือที่ใช้ทดสอบ (แสดงเฉพาะเมื่อ projectType = Testing) -->
			<div class="mb-4" id="testingToolsSection" style="display: none;">
				<label class="form-label fw-bold"> <i
					class="bi bi-check2-square"></i> เครื่องมือที่ใช้ทดสอบ :
				</label> <select class="form-select" id="testingToolSelect"
					onchange="addTestingTool()">
					<option value="">-- เลือกเครื่องมือทดสอบ --</option>
					<c:forEach var="t" items="${allTools}">
						<c:if test="${t.toolType == 'Testing'}">
							<option value="${t.toolsId}">${t.toolsName}</option>
						</c:if>
					</c:forEach>
					<option value="other">อื่น ๆ (เพิ่มใหม่)</option>
				</select>
				<div id="selectedTestingTools" class="selected-tools">
					<div class="empty-tools-message">ยังไม่ได้เลือกเครื่องมือทดสอบ</div>
				</div>
				<div class="text-danger small error-msg" id="testingToolsError"></div>
			</div>

			<!-- ✅ เครื่องมือที่ใช้พัฒนา (ซ่อนเมื่อ projectType = Testing) -->
			<div id="developmentToolsSection">
				<!-- เครื่องมือการเขียนโปรแกรม -->
				<div class="mb-4">
					<label class="form-label fw-bold"> <i
						class="bi bi-code-slash"></i> เครื่องมือการเขียนโปรแกรม :
					</label> <select class="form-select" id="programmingToolSelect"
						onchange="addProgrammingTool()">
						<option value="">-- เลือกเครื่องมือ --</option>
						<c:forEach var="t" items="${allTools}">
							<c:if test="${t.toolType == 'PROGRAMMING'}">
								<option value="${t.toolsId}">${t.toolsName}</option>
							</c:if>
						</c:forEach>
						<option value="other">อื่น ๆ (เพิ่มใหม่)</option>
					</select>
					<div id="selectedProgrammingTools" class="selected-tools">
						<div class="empty-tools-message">ยังไม่ได้เลือกเครื่องมือการเขียนโปรแกรม</div>
					</div>
					<div class="text-danger small error-msg" id="programmingToolsError"></div>
				</div>

				<!-- ระบบจัดการฐานข้อมูล -->
				<div class="mb-4">
					<label class="form-label fw-bold"> <i
						class="bi bi-database"></i> ระบบจัดการฐานข้อมูล :
					</label> <select class="form-select" id="dbmsToolSelect"
						onchange="addDbmsTool()">
						<option value="">-- เลือกฐานข้อมูล --</option>
						<c:forEach var="t" items="${allTools}">
							<c:if test="${t.toolType == 'DBMS'}">
								<option value="${t.toolsId}">${t.toolsName}</option>
							</c:if>
						</c:forEach>
						<option value="other">อื่น ๆ (เพิ่มใหม่)</option>
					</select>
					<div id="selectedDbmsTools" class="selected-tools">
						<div class="empty-tools-message">ยังไม่ได้เลือกระบบจัดการฐานข้อมูล</div>
					</div>
					<div class="text-danger small error-msg" id="dbmsToolsError"></div>
				</div>
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

// ✅ เก็บ Tools ที่เลือกไว้
const selectedTools = {
    programming: new Map(),
    dbms: new Map(),
    testing: new Map()
};

// ✅ โหลด Tools เดิมจากฐานข้อมูล
window.addEventListener('DOMContentLoaded', function() {
    handleProjectTypeChange();
    
    // โหลด tools ที่มีอยู่แล้ว
    <c:forEach var="tool" items="${project.tools}">
        <c:choose>
            <c:when test="${tool.toolType == 'PROGRAMMING'}">
                selectedTools.programming.set('${tool.toolsId}', '${tool.toolsName}');
            </c:when>
            <c:when test="${tool.toolType == 'DBMS'}">
                selectedTools.dbms.set('${tool.toolsId}', '${tool.toolsName}');
            </c:when>
            <c:when test="${tool.toolType == 'Testing'}">
                selectedTools.testing.set('${tool.toolsId}', '${tool.toolsName}');
            </c:when>
        </c:choose>
    </c:forEach>
    
    updateToolsDisplay('programming');
    updateToolsDisplay('dbms');
    updateToolsDisplay('testing');
});

// ✅ จัดการแสดง/ซ่อน Tools ตาม Project Type
function handleProjectTypeChange() {
    const projectType = document.getElementById('projectTypeSelect').value;
    const developmentSection = document.getElementById('developmentToolsSection');
    const testingSection = document.getElementById('testingToolsSection');
    
    if (projectType === 'Testing') {
        developmentSection.style.display = 'none';
        testingSection.style.display = 'block';
    } else {
        developmentSection.style.display = 'block';
        testingSection.style.display = 'none';
    }
}

// ✅ เพิ่ม Programming Tool
function addProgrammingTool() {
    const select = document.getElementById('programmingToolSelect');
    const selectedValue = select.value;
    
    if (!selectedValue) return;
    
    if (selectedValue === 'other') {
        showAddToolDialog('PROGRAMMING');
        select.value = '';
        return;
    }
    
    const selectedOption = select.options[select.selectedIndex];
    const toolName = selectedOption.textContent.trim();
    
    if (!selectedTools.programming.has(selectedValue)) {
        selectedTools.programming.set(selectedValue, toolName);
        updateToolsDisplay('programming');
    }
    
    select.value = '';
}

// ✅ เพิ่ม DBMS Tool
function addDbmsTool() {
    const select = document.getElementById('dbmsToolSelect');
    const selectedValue = select.value;
    
    if (!selectedValue) return;
    
    if (selectedValue === 'other') {
        showAddToolDialog('DBMS');
        select.value = '';
        return;
    }
    
    const selectedOption = select.options[select.selectedIndex];
    const toolName = selectedOption.textContent.trim();
    
    if (!selectedTools.dbms.has(selectedValue)) {
        selectedTools.dbms.set(selectedValue, toolName);
        updateToolsDisplay('dbms');
    }
    
    select.value = '';
}

// ✅ เพิ่ม Testing Tool
function addTestingTool() {
    const select = document.getElementById('testingToolSelect');
    const selectedValue = select.value;
    
    if (!selectedValue) return;
    
    if (selectedValue === 'other') {
        showAddToolDialog('Testing');
        select.value = '';
        return;
    }
    
    const selectedOption = select.options[select.selectedIndex];
    const toolName = selectedOption.textContent.trim();
    
    if (!selectedTools.testing.has(selectedValue)) {
        selectedTools.testing.set(selectedValue, toolName);
        updateToolsDisplay('testing');
    }
    
    select.value = '';
}

// ✅ แสดง Tools ที่เลือกไว้
function updateToolsDisplay(type) {
    let containerId, toolsMap;
    
    if (type === 'programming') {
        containerId = 'selectedProgrammingTools';
        toolsMap = selectedTools.programming;
    } else if (type === 'dbms') {
        containerId = 'selectedDbmsTools';
        toolsMap = selectedTools.dbms;
    } else if (type === 'testing') {
        containerId = 'selectedTestingTools';
        toolsMap = selectedTools.testing;
    }
    
    const container = document.getElementById(containerId);
    container.innerHTML = '';
    
    if (toolsMap.size === 0) {
        const emptyMsg = document.createElement('div');
        emptyMsg.className = 'empty-tools-message';
        emptyMsg.textContent = type === 'programming' 
            ? 'ยังไม่ได้เลือกเครื่องมือการเขียนโปรแกรม'
            : type === 'dbms'
            ? 'ยังไม่ได้เลือกระบบจัดการฐานข้อมูล'
            : 'ยังไม่ได้เลือกเครื่องมือทดสอบ';
        container.appendChild(emptyMsg);
    } else {
        toolsMap.forEach((name, id) => {
            const badge = document.createElement('div');
            badge.className = 'tool-badge';
            
            // สร้าง span แสดงชื่อ
            const nameSpan = document.createElement('span');
            nameSpan.textContent = name;
            
            // สร้างปุ่มลบ
            const removeSpan = document.createElement('span');
            removeSpan.className = 'remove-tool';
            removeSpan.textContent = '×';
            removeSpan.onclick = function() {
                removeTool(type, id);
            };
            
            // สร้าง hidden input
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.value = id;
            
            // กำหนดชื่อ input ตาม type
            if (type === 'programming') {
                hiddenInput.name = 'programmingToolIds';
            } else if (type === 'dbms') {
                hiddenInput.name = 'dbmsToolIds';
            } else if (type === 'testing') {
                hiddenInput.name = 'testingToolIds';
            }
            
            badge.appendChild(nameSpan);
            badge.appendChild(removeSpan);
            badge.appendChild(hiddenInput);
            container.appendChild(badge);
        });
    }
}

// ✅ ลบ Tool
function removeTool(type, toolId) {
    if (type === 'programming') {
        selectedTools.programming.delete(toolId);
        updateToolsDisplay('programming');
    } else if (type === 'dbms') {
        selectedTools.dbms.delete(toolId);
        updateToolsDisplay('dbms');
    } else if (type === 'testing') {
        selectedTools.testing.delete(toolId);
        updateToolsDisplay('testing');
    }
}

// ✅ แสดง Dialog สำหรับเพิ่ม Tool ใหม่
function showAddToolDialog(toolType) {
    Swal.fire({
        title: 'เพิ่ม Tools ใหม่',
        html: `
            <div class="text-start">
                <label class="form-label fw-bold">ชื่อ Tools:</label>
                <input type="text" id="swal-tool-name" class="form-control" placeholder="กรอกชื่อ Tools">
            </div>
        `,
        icon: 'info',
        showCancelButton: true,
        confirmButtonText: 'บันทึก',
        cancelButtonText: 'ยกเลิก',
        preConfirm: () => {
            const toolName = document.getElementById('swal-tool-name').value.trim();
            if (!toolName) {
                Swal.showValidationMessage('กรุณากรอกชื่อ Tools');
                return false;
            }
            return { toolType, toolName };
        }
    }).then((result) => {
        if (result.isConfirmed) {
            saveNewTool(result.value.toolType, result.value.toolName);
        }
    });
}

// ✅ บันทึก Tool ใหม่ลงฐานข้อมูล
function saveNewTool(toolType, toolName) {
    Swal.fire({
        title: 'กำลังบันทึก...',
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });
    
    fetch('${pageContext.request.contextPath}/addNewTool', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'toolType=' + encodeURIComponent(toolType) + '&toolName=' + encodeURIComponent(toolName)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // เพิ่ม option ใหม่ลงใน dropdown
            const selectId = toolType === 'PROGRAMMING' 
                ? 'programmingToolSelect'
                : toolType === 'DBMS'
                ? 'dbmsToolSelect'
                : 'testingToolSelect';
            
            const select = document.getElementById(selectId);
            const newOption = document.createElement('option');
            newOption.value = data.toolId;
            newOption.textContent = toolName;
            
            // แทรกก่อน "อื่น ๆ"
            const otherOption = select.querySelector('option[value="other"]');
            select.insertBefore(newOption, otherOption);
            
            // เพิ่ม Tool ใหม่ลงในรายการที่เลือก
            const type = toolType === 'PROGRAMMING' ? 'programming' : toolType === 'DBMS' ? 'dbms' : 'testing';
            selectedTools[type].set(String(data.toolId), toolName);
            updateToolsDisplay(type);
            
            Swal.fire({ 
                icon: 'success', 
                title: 'เพิ่ม Tools สำเร็จ!', 
                text: 'Tools ถูกเพิ่มลงในรายการแล้ว',
                timer: 2000, 
                showConfirmButton: false 
            });
        } else {
            Swal.fire({ 
                icon: 'error', 
                title: 'เกิดข้อผิดพลาด', 
                text: data.message 
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        Swal.fire({ 
            icon: 'error', 
            title: 'เกิดข้อผิดพลาด', 
            text: 'ไม่สามารถเพิ่ม Tools ได้' 
        });
    });
}

// ✅ Validation และ Submit Form
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

    // ตรวจสอบ Tools
    const projectType = document.getElementById('projectTypeSelect').value;
    
    if (projectType === 'Testing') {
        if (selectedTools.testing.size === 0) {
            document.getElementById('testingToolsError').textContent = "*กรุณาเลือกเครื่องมือทดสอบอย่างน้อย 1 รายการ";
            valid = false;
        }
    } else {
        if (selectedTools.programming.size === 0) {
            document.getElementById('programmingToolsError').textContent = "*กรุณาเลือกเครื่องมือการเขียนโปรแกรมอย่างน้อย 1 รายการ";
            valid = false;
        }
        if (selectedTools.dbms.size === 0) {
            document.getElementById('dbmsToolsError').textContent = "*กรุณาเลือกระบบจัดการฐานข้อมูลอย่างน้อย 1 รายการ";
            valid = false;
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
        Swal.fire({
            title: 'กำลังบันทึกข้อมูล...',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
        
        form.submit();
    }
});
</script>
</body>
</html>