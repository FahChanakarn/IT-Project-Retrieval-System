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

			<!-- ซอฟต์แวร์ฐานข้อมูล -->
			<div class="mb-3" id="dbSoftwareSection">
				<label class="form-label fw-bold">ซอฟต์แวร์ฐานข้อมูล :</label> <select
					class="form-select" name="typeDBId">
					<option value="" disabled selected>เลือกซอฟต์แวร์ฐานข้อมูล</option>
					<c:forEach var="db" items="${typeDBs}">
						<option value="${db.langId}"
							${db.langId == selectedDBId ? 'selected' : ''}>
							${db.langName}</option>
					</c:forEach>
				</select>
				<div class="text-danger small error-msg" id="typeDBIdError"></div>
			</div>

			<!-- ภาษาที่ใช้ในการพัฒนา -->
			<div class="mb-3">
				<label class="form-label fw-bold">ภาษาที่ใช้ในการพัฒนา :</label><br />
				<c:forEach var="lang" items="${programmingLangs}">
					<c:set var="checked" value="" />
					<c:forEach var="detail" items="${project.projectLangDetails}">
						<c:if test="${detail.programmingLang.langId == lang.langId}">
							<c:set var="checked" value="checked" />
						</c:if>
					</c:forEach>
					<div class="form-check form-check-inline">
						<input class="form-check-input" type="checkbox" name="languageIds"
							value="${lang.langId}" ${checked}> <label
							class="form-check-label">${lang.langName}</label>
					</div>
				</c:forEach>
				<div class="text-danger small error-msg" id="languageError"></div>
				<div class="mt-2">
					<label class="form-label">ภาษาอื่น ๆ:</label> <input type="text"
						class="form-control" name="otherLanguages"
						placeholder="ตัวอย่าง: C#, C++, Swift, PHP">
					<div class="text-danger small error-msg" id="otherLanguagesError"></div>
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

// สร้าง CKEditor
ClassicEditor
    .create(document.querySelector('#thaiAbstract'), { toolbar: ['heading', '|', 'bold', 'italic', '|', 'link', '|', 'fontSize'], fontSize: { options: [10, 12, 14, 'default', 18, 20, 24, 28, 32, 36] } })
    .then(editor => { thaiEditor = editor; })
    .catch(error => console.error(error));

ClassicEditor
    .create(document.querySelector('#engAbstract'), { toolbar: ['heading', '|', 'bold', 'italic', '|', 'link', '|', 'fontSize'], fontSize: { options: [10, 12, 14, 'default', 18, 20, 24, 28, 32, 36] } })
    .then(editor => { engEditor = editor; })
    .catch(error => console.error(error));

// ซ่อน/แสดง ซอฟต์แวร์ฐานข้อมูลตาม projectType
const projectTypeSelect = document.querySelector('select[name="projectType"]');
const dbSoftwareDiv = document.getElementById('dbSoftwareSection');

function toggleDBSoftware() {
    const selectedType = projectTypeSelect.value;
    if (selectedType === 'Testing') {
        dbSoftwareDiv.style.display = 'none';
    } else {
        dbSoftwareDiv.style.display = 'block';
    }
}

// เรียกใช้เมื่อโหลดหน้า
toggleDBSoftware();

// เรียกใช้เมื่อเปลี่ยนประเภทโครงงาน
projectTypeSelect.addEventListener('change', toggleDBSoftware);

document.querySelector('form').addEventListener('submit', function(e) {
    e.preventDefault();
    const form = this;
    form.querySelectorAll('.error-msg').forEach(div => div.textContent = '');
    let valid = true;

    // ชื่อโครงงานภาษาไทย
    const projNameTh = form.projNameTh.value.trim();
    const regexTh = /^[ก-๙A-Za-z0-9(),\s]+$/;
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

    // ซอฟต์แวร์ฐานข้อมูล (ตรวจสอบเฉพาะเมื่อไม่ใช่ Testing)
    const selectedType = form.projectType.value;
    if (selectedType !== 'Testing' && !form.typeDBId.value) { 
        document.getElementById('typeDBIdError').textContent = "*กรุณาเลือกซอฟต์แวร์ฐานข้อมูล"; 
        valid = false; 
    }

    const languages = form.querySelectorAll('input[name="languageIds"]:checked');
    const otherLang = form.otherLanguages.value.trim();
    let languageError = document.getElementById('languageError');
    languageError.textContent = "";

    // ภาษาที่ใช้ในการพัฒนา
    if (languages.length === 0) {
        languageError.textContent = "*กรุณาเลือกภาษาที่ใช้ในการพัฒนาอย่างน้อย 1 ภาษา";
        valid = false;
    } else if (otherLang) {
        const langNames = Array.from(form.querySelectorAll('input[name="languageIds"]'))
            .map(i => i.nextElementSibling.textContent.trim().toLowerCase());
        if (langNames.includes(otherLang.toLowerCase())) {
            languageError.textContent = `ภาษาซ้ำกับรายการภาษาที่เลือก`;
            valid = false;
        }
    }

    // บทคัดย่อ
    const abstractTh = thaiEditor.getData().trim();
    const abstractThPlain = abstractTh.replace(/<[^>]*>/g, ''); // ลบ HTML tags
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
    const abstractEnPlain = abstractEn.replace(/<[^>]*>/g, ''); // ลบ HTML tags
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