package psdk.hw.phv

import chisel3._
import chisel3.util._



object SimplePHV {
  val width = 32
  val num = 8
  val addressLength = log2Ceil(num)
  def ContentType = Vec(num, UInt(width.W))
}

class SimplePHV extends Containers {

  def addressLength = SimplePHV.addressLength
  def dataLength = SimplePHV.width
  override def totalLength = SimplePHV.ContentType.getWidth
  override def containerNum = SimplePHV.num

  private val content = SimplePHV.ContentType

  override def read(address: Int): UInt = {
    content(address)
  }
  override def read(address: UInt): UInt = {
    content(address(addressLength - 1, 0))
  }

  override def write(address: UInt, data: UInt): Unit = {
    if (data.getWidth >= dataLength) {
      content(address(addressLength - 1, 0)) := data(dataLength - 1, 0)
    } else {
      content(address(addressLength - 1, 0))(data.getWidth - 1, 0) := data
      content(address(addressLength - 1, 0))(dataLength - 1, data.getWidth) := 0.U
    }
  }

  override def write(address: Int, data: UInt): Unit = {
    content(address) := data
  }

  override def from(phv: Containers): Unit = {
    require(phv.isInstanceOf[SimplePHV])
    for (i <- 0 until SimplePHV.num) {
      write(i, phv.read(i))
    }
  }

  override def from(phv: UInt): Unit = {
    require(phv.getWidth == totalLength)
    for (i <- 0 until SimplePHV.num) {
      write(i, phv((i + 1) * SimplePHV.width - 1, i * SimplePHV.width))
    }
  }

}
