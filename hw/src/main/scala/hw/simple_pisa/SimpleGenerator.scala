package hw.simple_pisa

import chisel3.emitVerilog
import chisel3.stage.ChiselStage

object SimpleGenerator extends App {
  (new ChiselStage).emitVerilog(new SimpleMatch, args)
}
