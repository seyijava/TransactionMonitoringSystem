package com.bigdataconcept.transactionmonitor.model
import java.sql.Date

case class PaymentChannelAggregate(transactionCount: Integer,paymentChannel: String, duration: Integer, transactionDate: Date)
extends Serializable    