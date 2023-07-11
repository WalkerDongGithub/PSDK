package hw.simple_pisa.containers

import chisel3._
import hw.template.containers.{Containers, ContainersWithFixedOutputLength, SymmetricReadAndWriteContainers}

class SimpleTranslator extends SymmetricReadAndWriteContainers with ContainersWithFixedOutputLength {

  private val translator = Vec(containerNum, UInt(fixedOutputLength.W))
  override def read(address: Int): UInt = {
    translator(address)
  }

  override def read(address: UInt): UInt = {
    translator(address)
  }

  override def write(address: UInt, data: UInt): Unit = {
    translator(address) := data
  }

  override def write(address: Int, data: UInt): Unit = {
    translator(address) := data
  }

  override def totalLength: Int = translator.getWidth

  override def addressLength: Int = 8

  override def containerNum: Int = 1 << addressLength
}
