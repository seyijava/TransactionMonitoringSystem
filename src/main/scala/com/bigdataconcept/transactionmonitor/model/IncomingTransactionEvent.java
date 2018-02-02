package com.bigdataconcept.transactionmonitor.model;

import java.io.Serializable;





public class IncomingTransactionEvent  implements Serializable{
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String debitAccountNumber;
		 private String creditAccountNumber;
		 private String narration;
		 private String paymentChannel;
		 private String transType;
		 private double amount;
		 private double latitude;
		 private double longitude;
		 private String geocordinate;
		 
		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		public String getGeocordinate() {
			return geocordinate;
		}
		public void setGeocordinate(String geocordinate) {
			this.geocordinate = geocordinate;
		}
		public String getDebitAccountNumber() {
			return debitAccountNumber;
		}
		public void setDebitAccountNumber(String debitAccountNumber) {
			this.debitAccountNumber = debitAccountNumber;
		}
		public String getCreditAccountNumber() {
			return creditAccountNumber;
		}
		public void setCreditAccountNumber(String creditAccountNumber) {
			this.creditAccountNumber = creditAccountNumber;
		}
		public String getNarration() {
			return narration;
		}
		public void setNarration(String narration) {
			this.narration = narration;
		}
		public String getPaymentChannel() {
			return paymentChannel;
		}
		public void setPaymentChannel(String paymentChannel) {
			this.paymentChannel = paymentChannel;
		}
		public String getTransType() {
			return transType;
		}
		public void setTransType(String transType) {
			this.transType = transType;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
}
