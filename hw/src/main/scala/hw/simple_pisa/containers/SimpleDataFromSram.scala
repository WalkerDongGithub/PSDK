package hw.simple_pisa.containers

import chisel3._
import chisel3.util._
import hw.simple_pisa.SimpleMatch
import SimpleMatch._
import hw.template.containers.{Containers, ContainersWithFixedOutputLength, ContainersWithSelector, SymmetricReadAndWriteContainers}

class SimpleDataFromSram extends ContainersWithSelector with ContainersWithFixedOutputLength {

  private val dataFromSram = Vec(wayNum, Vec(addressNum / wayNum, UInt(sramDataLength.W))

  override def read(address: Int): UInt = throw Error

  override def read(address: UInt): UInt = throw Error

  override def write(address: UInt, data: UInt): Unit = throw Error

  override def write(address: Int, data: UInt): Unit = throw Error

  override def totalLength: Int = dataFromSram.getWidth

  override def addressLength: Int = sramDataLength

  override def containerNum: Int = dataFromSram.length

  override def read(address: UInt, select: Int): UInt = {
    val data = dataFromSram(select)
    val result = MuxCase(0.U, Array.tabulate(addressNum / wayNum) {
      x => (data(x) === address(sramDataLength - 1, 0), Cat(true.B, data(x))
    })
    result
  }

  override def write(address: UInt, select: Int, data: UInt): Unit = throw Error

}
