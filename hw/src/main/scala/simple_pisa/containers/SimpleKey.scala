package simple_pisa.containers

import chisel3._
import chisel3.util._
import psdk.hw.phv.{Containers, ContainersWithFixedOutputLength, ContainersWithFrom}

class SimpleKey extends ContainersWithFrom with ContainersWithFixedOutputLength {

  private val keyContainerNum = 16
  private val keyContainerLength = 32
  private val keyLength = 128
  private val keyNum = keyLength / keyContainerLength
  private val key32 = Vec(keyContainerNum, UInt(keyContainerLength.W))


  override def read(address: Int): UInt = {
    key32.asUInt(address * 128 + 127, address * 128)
  }

  override def read(address: UInt): UInt = {
    val key128 = Wire(Vec(keyNum, UInt(keyLength.W)))
    for (i <- 0 until keyNum) {
      key128(i) := this.read(i)
    }
    key128(address)
  }

  override def write(address: UInt, data: UInt): Unit = {
    key32(address) := data
  }

  override def write(address: Int, data: UInt): Unit = {
    key32(address) := data
  }


  override def totalLength: Int = key32.getWidth

  override def addressLength: Int = log2Ceil(keyNum)

  override def containerNum: Int = keyContainerNum


  override def from(key: Containers): Unit = {
    require(key.isInstanceOf[SimpleKey])
    this := key.asInstanceOf[SimpleKey]
  }

}
