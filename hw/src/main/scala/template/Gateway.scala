package template

import chisel3._
import chisel3.util._
import psdk.hw.phv.{Containers, KeyAndPHVPassModule, SymmetricReadAndWriteContainers}
import template.Gateway.{constType, fieldType, opCodeLength, typeLength}

object Gateway {
  val no = 0.U
  val eq = 1.U
  val ne = 2.U
  val le = 3.U
  val ge = 4.U
  val lt = 5.U
  val gt = 6.U
  val opCodeLength = 3

  val constType = 0.U
  val fieldType = 1.U
  val typeLength = 1

}

class GatewayInfo(parameterLength: Int) extends Bundle {
  val parameter1 = UInt(parameterLength.W)
  val parameter2 = UInt(parameterLength.W)
  val type1 = UInt(typeLength.W)
  val type2 = UInt(typeLength.W)
  val opCode = UInt(opCodeLength.W)
}
class GatewayMapper(num: Int, parameterLength: Int, outputLength: Int) extends Bundle {
  val gatewayInfos = Input(Vec(num, new GatewayInfo(parameterLength)))
  val output = Output(UInt(outputLength.W))
}

/**
 * Gateway 需要三种 Container，分别定义 PHV，Key 和 translator
 *
 * @param phvGen
 * @param keyGen
 * @param translatorGen
 * @param submoduleNum
 * @param constMaxLengthSupport
 * @param outputLength
 * @tparam PHV
 * @tparam Key
 * @tparam Translator
 */
class Gateway
[
  PHV <: SymmetricReadAndWriteContainers,
  Key <: Containers,
  Translator <: SymmetricReadAndWriteContainers
](
   phvGen: PHV,
   keyGen: Key,
   translatorGen: Translator,
   val submoduleNum: Int,
   val constMaxLengthSupport: Int,
   val outputLength: Int,
   override val passCycle: Int
 ) extends KeyAndPHVPassModule[Key, PHV](passCycle) {

  val gatewayMapper = IO(new GatewayMapper(submoduleNum, key.addressLength, outputLength))
  def decoder(parameter: UInt, typeMark: UInt, outputLength: Int) : UInt = {
    val res = UInt(outputLength.W)
    res := MuxCase(0.U, Array(
      (typeMark === constType, parameter),
      (typeMark === fieldType, key.read(parameter))
    ))
    res
  }

  def comparator(a: UInt, b: UInt, op: UInt): Bool = {
    require(op.getWidth == opCodeLength)
    require(a.getWidth == b.getWidth)
    MuxCase(false.B, Array(
      (op === Gateway.eq, a === b),
      (op === Gateway.ne, a =/= b),
      (op === Gateway.le, a === b || a < b),
      (op === Gateway.ge, a === b || a > b),
      (op === Gateway.lt, a  <  b),
      (op === Gateway.gt, a  >  b)
    ))
  }

  private val submoduleOutput = Reg(Vec(submoduleNum, Bool()))
  private val width = Math.max(constMaxLengthSupport, key.addressLength)
  for (i <- 0 until submoduleNum) {
      val gatewayInfo = gatewayMapper.gatewayInfos(i)
      submoduleOutput(i) := comparator(
        decoder(gatewayInfo.parameter1, gatewayInfo.type1, width),
        decoder(gatewayInfo.parameter2, gatewayInfo.type2, width),
        gatewayInfo.opCode
      )
  }

  private val address = submoduleOutput.asUInt

  private val translator = Reg(new Translator)

  val output = translator.read(address)

  val outputDelay = Reg(Vec(passCycle - 2, UInt(outputLength.W)))
  outputDelay(0) := output
  for (i <- 1 until outputDelay.length) {
    outputDelay(i) := outputDelay(i - 1)
  }
  gatewayMapper.output := outputDelay

}
