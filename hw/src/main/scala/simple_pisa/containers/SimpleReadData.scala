package simple_pisa.containers

import chisel3.UInt
import psdk.hw.phv.{Containers, ContainersWithSelector, SymmetricReadAndWriteContainers}

class SimpleReadData extends ContainersWithSelector {
  override def read(address: Int): UInt = ???

  override def read(address: UInt): UInt = ???

  override def write(address: UInt, data: UInt): Unit = ???

  override def write(address: Int, data: UInt): Unit = ???

  override def totalLength: Int = ???

  override def addressLength: Int = ???

  override def containerNum: Int = ???

  override def read(address: UInt, select: Int): UInt = ???

  override def write(address: UInt, select: Int, data: UInt): Unit = ???

}
