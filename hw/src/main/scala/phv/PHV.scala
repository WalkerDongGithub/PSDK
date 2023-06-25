package psdk.hw
package phv

import chisel3._
import chisel3.util._
class ContainersIOBundle[C <: Containers](c: C) extends Bundle {
  val in = Input(c)
  val out = Output(c)

  def pass: ContainersIOBundle[C] = {
    c.from(in)
    out.from(c)
    this
  }

}

trait PHVPassModule[PHV <: Containers] extends Module {
  protected val phv = Reg(new PHV)
  val phvIO = IO(new ContainersIOBundle(phv).pass)
}

trait KeyPassModule[Key <: Containers] extends Module {
  protected val key = Reg(new Key)
  val keyIO = IO(new ContainersIOBundle(key))
}