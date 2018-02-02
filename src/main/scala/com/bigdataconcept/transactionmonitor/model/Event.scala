package com.bigdataconcept.transactionmonitor.model





 case class TransactionHistory(debitAccount: String,creditAccount: String, narration:  String, paymentChannel: String, tranactionType: String, transactionAmount: Double, latitude: Double,  longitude: Double,  ipaddress: String,  transactiontimestamp: java.sql.Date, responseCode: String,status: String,
         min: Int, hour: Int, day: Int, month: Int, year: Int,transRef:String)  extends Serializable
         
         
    case class PaymentChannelAggregated(id: String, paymentChannel: String,transactionCounts: Int)  extends Serializable
    
    
    case class Config(kafkaBrokerAddress:  String, kafkatopic: String, windowLenght: Long, slideInterval: Long, checkPointDir: String ,cassandrauserName: String , cassandraHost: String, cassandraPassword: String) extends Serializable