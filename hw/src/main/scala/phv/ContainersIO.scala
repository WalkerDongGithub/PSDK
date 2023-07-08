package psdk.hw
package phv

import chisel3._
import chisel3.util._
class ContainersIOBundle[C <: Containers](c: C) extends Bundle {
  val in = Input(c)
  val out = Output(c)

  def pass(passCycle: Int): ContainersIOBundle[C] = {
    val containers = Reg(Vec(passCycle, new C))
    containers(0).from(c)
    for (i <- 1 until passCycle) {
      containers(i).from(containers(i - 1))
    }
    out.from(containers(passCycle - 1))
    this
  }
}

abstract class PHVPassModule[PHV <: Containers]
(val passCycle: Int) extends Module {
  protected val phv = Reg(new PHV)
  val phvIO = IO(new ContainersIOBundle(phv).pass(passCycle))
}

abstract class KeyAndPHVPassModule
[Key <: Containers, PHV <: Containers]
(override val passCycle: Int) extends PHVPassModule[PHV](passCycle) {
  protected val key = Reg(new Key)
  val keyIO = IO(new ContainersIOBundle(key).pass(passCycle))
}

object KeyAndPHVPassModule {
  def cascadeKey[Key <: Containers, PHV <: Containers, AModule <: KeyAndPHVPassModule[Key, PHV]]
  (modules : Array[AModule]): Unit = {
    for (i <- 1 until modules.length) {
      modules(i).keyIO.in.from(modules(i - 1).keyIO.out)
    }
  }
}

object PHVPassModule {
  def cascadePHV[PHV <: Containers, AModule <: PHVPassModule[PHV]]
  (modules: Array[AModule]): Unit = {
    for (i <- 1 until modules.length) {
      modules(i).phvIO.in.from(modules(i - 1).phvIO.out)
    }
  }
}