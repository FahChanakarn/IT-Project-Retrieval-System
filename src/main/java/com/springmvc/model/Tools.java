package com.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name = "tools")
public class Tools {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tools_id")
	private int toolsId;

	@Column(name = "tools_name", length = 100, unique = true, nullable = false)
	private String toolsName;

	@Enumerated(EnumType.STRING) // ✅ เก็บ enum เป็นข้อความ เช่น "PROGRAMMING", "DBMS"
	@Column(name = "tools_type", length = 50, nullable = false)
	private ToolsType toolType;

	// Enum ภายใน class
	public enum ToolsType {
		PROGRAMMING, DBMS
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Tools))
			return false;
		Tools tools = (Tools) o;
		return toolsId == tools.toolsId;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(toolsId);
	}

	public Tools() {
	}

	public Tools(int toolsId, String toolsName, ToolsType toolType) {
		super();
		this.toolsId = toolsId;
		this.toolsName = toolsName;
		this.toolType = toolType;
	}

	public int getToolsId() {
		return toolsId;
	}

	public void setToolsId(int toolsId) {
		this.toolsId = toolsId;
	}

	public String getToolsName() {
		return toolsName;
	}

	public void setToolsName(String toolsName) {
		this.toolsName = toolsName;
	}

	public ToolsType getToolType() {
		return toolType;
	}

	public void setToolType(ToolsType toolType) {
		this.toolType = toolType;
	}
}
