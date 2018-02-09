package com.surfilter.tmms.enums;

public enum ProtocolTypes {
	HTTP(1,"HTTP"),
	HTTPS(2,"HTTPS"),
	TCP(3,"TCP"),
	UDP(4,"UDP"),
	SOCKET5(5,"SOCKET5"),
	MAIL(6,"MAIL"),
	DNS(7,"DNS"),
	FTP(8, "FTP"),
	NTP(9, "NTP"),
	SNMP(10, "SNMP"),
	SSH(11, "SSH");
    
	
	//编号
    private Integer type;
	//端口
	private String descript;

	private ProtocolTypes(Integer type, String descript) {
		this.type = type;
		this.descript = descript;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}
}
