package hw.simple_pisa

import chisel3._
import chisel3.util._
import hw.simple_pisa.containers.{SimpleDataFromSram, SimpleKey, SimplePHV, SimpleTranslator}
import hw.template.{Compare, Gateway, GetAddress, GetKey, HashUnit, MatchFactory, ReadData}
import SimpleMatch._
import hw.template.containers.{Containers, KeyAndPHVPassModule, PHVPassModule}
import simple_pisa.containers.{SimpleDataFromSram, SimpleKey, SimplePHV, SimpleTranslator}


object SimpleMatch {
  val phv = new SimplePHV
  val key = new SimpleKey
  val translator = new SimpleTranslator
  val dataFromSram = new SimpleDataFromSram
  val gatewaySubmoduleNum = 16
  val constMaxLength = 32
  val gatewayOutputLength = translator.totalLength / translator.containerNum
  val hashValueLength = 52
  val addressLength = 16
  val wayNum = 4
  val addressNum = 16
  val sramDataLength = 128
}
class SimpleMatch extends MatchFactory[SimplePHV, SimpleKey, SimpleTranslator, SimpleDataFromSram] {


  override def buildGetKey: GetKey[SimplePHV, SimpleKey] = Module(new GetKey(phv, key))

  override def buildGateway: Gateway[SimplePHV, SimpleKey, SimpleTranslator] =
    Module(new Gateway(phv, key, translator, gatewaySubmoduleNum, constMaxLength, gatewayOutputLength, 3))

  override def buildGetAddress: GetAddress[SimpleKey] =
    Module(new SimpleGetAddress)

  override def buildReadData: ReadData[SimplePHV, SimpleKey] =
    Module(new ReadData(phv, key, addressNum, addressLength, dataLength))

  override def buildCompare: Compare[SimpleKey, SimplePHV, SimpleDataFromSram] =
    Module(new Compare(key, phv, dataFromSram, addressNum, sramDataLength, wayNum))

  override def wayNum: Int = SimpleMatch.wayNum

  override def dataLength: Int = sramDataLength
}