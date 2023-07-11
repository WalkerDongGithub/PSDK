package sw.simulater.template

import chisel3._
import hw.template.containers._

class MatchMapper[PHV <: Containers](val actionNum : Int, val actionLength: Int) extends Bundle {
  val phvIn = Input(new PHV)
  val action = Output(Vec(actionNum, UInt(actionLength.W)))
  val hit = Output(Vec(actionNum, Bool()))
  val phvOut = Output(new PHV)
}

trait MatchFactory[
  PHV <: SymmetricReadAndWriteContainers,
  Key <: ContainersWithFixedOutputLength,
  Translator <: ContainersWithFixedOutputLength,
  DataFromSram <: ContainersWithSelector with ContainersWithFixedOutputLength
] extends Module {
  def buildGetKey: GetKey[PHV, Key]

  def buildGateway: Gateway[PHV, Key, Translator]

  def buildGetAddress: GetAddress[Key]

  def buildReadData: ReadData[PHV, Key]

  def buildCompare: Compare[Key, PHV, DataFromSram]

  def wayNum: Int
  def dataLength : Int

  private val getKey = buildGetKey
  private val gateway = buildGateway
  private val getAddress = buildGetAddress
  private val readData = buildReadData
  private val compare = buildCompare

  val io = IO(new MatchMapper[PHV](wayNum, dataLength))
  getKey.phvIO.in := io.phvIn

  /**
   *         ->        gateway
   *  getKey                        -> readData -> compare
   *         ->      getAddress
   */
  KeyAndPHVPassModule.cascadeKey(Array(gateway, readData, compare))
  PHVPassModule.cascadePHV(Array(getKey, gateway, readData, compare))
  readData.readDataMapper.gateway := gateway.gatewayMapper.output
  readData.readDataMapper.readAddress := getAddress.addressMapper.address
  compare.compareMapper.dataFromSram := readData.readDataMapper.readData

  io.action := compare.compareMapper.action
  io.hit := compare.compareMapper.hit
  io.phvOut := compare.phvIO.out

}
