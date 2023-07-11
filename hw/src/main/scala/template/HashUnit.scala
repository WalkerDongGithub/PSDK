package template

import chisel3._

class HashUnitMapper(inputLength: Int, outputLength: Int) extends Bundle {
  val in = Input(UInt(inputLength.W))
  val out = Output(UInt(outputLength.W))
}

class HashUnit
(val inputLength: Int, val outputLength: Int, val passCycle: Int)
  extends Module {
  val io = IO(new HashUnitMapper(inputLength, outputLength))
}

