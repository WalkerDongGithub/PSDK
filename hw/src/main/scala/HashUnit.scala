package psdk.hw



import chisel3._

class HashUnitIOBundle(inputLength: Int, outputLength: Int) extends Bundle {
  val in = Input(UInt(inputLength.W))
  val out = Output(UInt(outputLength.W))
}
abstract class HashUnit(inputLength: Int, outputLength: Int) extends Module {
  val io = IO(new HashUnitIOBundle(inputLength, outputLength))
}

