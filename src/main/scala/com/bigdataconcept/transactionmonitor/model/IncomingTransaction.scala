package com.bigdataconcept.transactionmonitor.model

import java.sql.Date
import com.google.gson.Gson;
import org.apache.spark.{SparkConf, SparkContext}


import com.beust.jcommander.{JCommander, Parameter}
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger
import java.sql.Date;



case class IncomingTransaction(debitAccount: String,creditAccount: String, narration:  String, paymentChannel: String, tranactionType: String, transactionAmount: Double, latitude: Double,  longitude: Double,  ipaddress: String,  transactiontimestamp: String, responseCode: String,transactionRef: String)
extends Serializable    


object IncomingTransaction 
{
  
    
    val log = Logger.getLogger(getClass.getName) 
  
  
  def parseIncomingTransactionEvent(event: String) : IncomingTransaction =
  {
    log.info(String.format("Incoming Transaction Event [%s]\n\n\n\n", event));
    var incomingTxData = new Gson().fromJson(event, classOf[IncomingTransaction]);
    return incomingTxData
  }
  
}