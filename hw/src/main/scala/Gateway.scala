package psdk.hw

import chisel3._
import chisel3.util._
import psdk.hw.Gateway.{constType, fieldType, opCodeLength, typeLength}
import psdk.hw.phv.{Containers, KeyPassModule, PHVPassModule}

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

class GatewayInfoBundle(parameterLength: Int) extends Bundle {
  val parameter1 = UInt(parameterLength.W)
  val parameter2 = UInt(parameterLength.W)
  val type1 = UInt(typeLength.W)
  val type2 = UInt(typeLength.W)
  val opCode = UInt(opCodeLength.W)
}
class GatewayMapperBundle(num: Int, parameterLength: Int, outputLength: Int) extends Bundle {
  val gatewayInfos = Input(Vec(num, new GatewayInfoBundle(parameterLength)))
  val output = Output(UInt(outputLength.W))
}

abstract class Gateway
[
  PHV <: Containers,
  Key <: Containers,
  Translator <: Containers
](
   phvGen: PHV,
   keyGen: Key,
   translatorGen: Translator,
   val submoduleNum: Int,
   val constMaxLengthSupport: Int,
   val outputLength: Int
 ) extends PHVPassModule[PHV] with KeyPassModule[Key] {

  val gatewayMapper = IO(new GatewayMapperBundle(submoduleNum, key.addressLength, outputLength))
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

  gatewayMapper.output := translator.read(address)

}
