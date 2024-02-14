package com.sumaengine;

public class SumaResponse {
	
	private String idSuma;
	private String Response;
	private String ResponseStatus;
	
	public SumaResponse() {
		this.idSuma = "0";
		this.ResponseStatus = "0";
		this.Response = "0x000000";
	}
	
	public String getIdSuma() {
		return idSuma;
	}
	
	public String getResponse() {
		return Response;
	}
	
	public String getResponseStatus() {
		return ResponseStatus;
	}
	
	public void setIdSuma(String paramIdSuma) {
		this.idSuma = paramIdSuma;
	}
	
	public void setResponse(String paramResponse) {
		this.Response = paramResponse;
	}
	
	public void setResponseStatus(String paramResponseStatus) {
		this.ResponseStatus = paramResponseStatus;
	}
	
	

}
