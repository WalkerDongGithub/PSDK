package simple_pisa.containers

import chisel3.UInt
import psdk.hw.phv.{Containers, ContainersWithFixedOutputLength, SymmetricReadAndWriteContainers}

class SimpleTranslator extends SymmetricReadAndWriteContainers with ContainersWithFixedOutputLength {

  override def read(address: Int): UInt = {

  }

  override def read(address: UInt): UInt = ???

  override def write(address: UInt, data: UInt): Unit = ???

  override def write(address: Int, data: UInt): Unit = ???

  override def totalLength: Int = ???

  override def addressLength: Int = 16

  override def containerNum: Int = ???
}
