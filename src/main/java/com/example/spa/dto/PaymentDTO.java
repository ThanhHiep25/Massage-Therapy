package com.example.spa.dto;

public class PaymentDTO {
	private String vnp_ResponseCode;
	
	private String vnp_TransactionNo;
	
	private String vnp_Amount;
	
	private String vnp_BankCode;
	
	private String vnp_BankTranNo;
	
	private String data;
	
	
	public PaymentDTO() {
		super();
	}

	

	public PaymentDTO(String vnp_ResponseCode, String vnp_TransactionNo, String vnp_Amount, String vnp_BankCode,
			String vnp_BankTranNo, String data) {
		super();
		this.vnp_ResponseCode = vnp_ResponseCode;
		this.vnp_TransactionNo = vnp_TransactionNo;
		this.vnp_Amount = vnp_Amount;
		this.vnp_BankCode = vnp_BankCode;
		this.vnp_BankTranNo = vnp_BankTranNo;
		this.data = data;
	}



	
	public String getVnp_ResponseCode() {
		return vnp_ResponseCode;
	}



	public void setVnp_ResponseCode(String vnp_ResponseCode) {
		this.vnp_ResponseCode = vnp_ResponseCode;
	}



	public String getVnp_TransactionNo() {
		return vnp_TransactionNo;
	}



	public void setVnp_TransactionNo(String vnp_TransactionNo) {
		this.vnp_TransactionNo = vnp_TransactionNo;
	}



	public String getVnp_Amount() {
		return vnp_Amount;
	}



	public void setVnp_Amount(String vnp_Amount) {
		this.vnp_Amount = vnp_Amount;
	}



	public String getVnp_BankCode() {
		return vnp_BankCode;
	}



	public void setVnp_BankCode(String vnp_BankCode) {
		this.vnp_BankCode = vnp_BankCode;
	}



	public String getVnp_BankTranNo() {
		return vnp_BankTranNo;
	}



	public void setVnp_BankTranNo(String vnp_BankTranNo) {
		this.vnp_BankTranNo = vnp_BankTranNo;
	}



	public String getData() {
		return data;
	}



	public void setData(String data) {
		this.data = data;
	}



	@Override
	public String toString() {
		return "PaymentDTO [vnp_ResponseCode=" + vnp_ResponseCode + ", vnp_TransactionNo=" + vnp_TransactionNo
				+ ", vnp_Amount=" + vnp_Amount + ", vnp_BankCode=" + vnp_BankCode + ", vnp_BankTranNo=" + vnp_BankTranNo
				+ ", data=" + data + "]";
	}



	
}
