package phv.pisa

import chisel3._
import chisel3.util._
import psdk.hw.{Compare, Gateway, GetAddress, GetKey, HashUnit, ReadData}
import psdk.hw.phv.{Containers, KeyAndPHVPassModule, PHVPassModule}


class MatchMapper[PHV <: Containers](val actionNum : Int, val actionLength: Int) extends Bundle {
  val phvIn = Input(new PHV)
  val action = Output(Vec(actionNum, UInt(actionLength.W)))
  val hit = Output(Vec(actionNum, Bool()))
  val phvOut = Output(new PHV)
}
class Match[PHV <: Containers, Key <: Containers, Translator <: Containers, ReadDataMethod <: Containers]
(phvGen: PHV, keyGen: Key, translatorGen: Translator, readDataGen : ReadDataMethod) extends Module {

  private val gatewaySubmoduleNum = 16
  private val constMaxLength = 32
  private val readWayNum = 16
  private val hashValueLength = 52
  private val hashWayNum = 4
  private val addressLength = 15
  private val dataLength = 128
  private val actionNum = 4

  private val getKey = Module(new GetKey(phvGen, keyGen))
  private val gateway = Module(new Gateway(
    phvGen, keyGen, translatorGen, gatewaySubmoduleNum, constMaxLength, readWayNum, 4))
  private val hashUnit = Module(new HashUnit(keyGen.totalLength, hashValueLength * hashWayNum, 4))
  private val getAddress = Module(new GetAddress(hashValueLength * hashWayNum, addressLength, readWayNum))
  private val readData = Module(new ReadData(phvGen, keyGen, readWayNum, addressLength, dataLength))
  private val compare = Module(new Compare(keyGen, phvGen, readDataGen, readWayNum, dataLength, actionNum))

  KeyAndPHVPassModule.cascadeKey(Array(gateway, getAddress, readData, compare))
  PHVPassModule.cascadePHV(Array(getKey, gateway, getAddress, readData, compare))
  getAddress.addressMapper.hashValue := hashUnit.io.out
  readData.readDataMapper.gateway := gateway.gatewayMapper.output
  readData.readDataMapper.readAddress := getAddress.addressMapper.address
  compare.compareMapper.readData := readData.readDataMapper.readData

}
