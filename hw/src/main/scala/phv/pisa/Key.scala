package phv.pisa

import chisel3._
import chisel3.util._
import psdk.hw.phv.Containers

class Key extends Containers {

  private val keyContainerNum = 16
  private val keyContainerLength = 32
  private val keyLength = 128
  private val keyNum = keyLength / keyContainerLength
  private val key32 = Vec(keyContainerNum, UInt(keyContainerLength.W))

  private def get128(): Vec[UInt] = {
    val key128 = Wire(Vec(keyNum, UInt(keyLength.W)))
    for (i <- 0 until key32.length) {
      key128(i / 4)((i % 4 + 1) * 32 - 1, (i % 4) * 32) := key32(i)
    }
    key128
  }
  /**
   * read 方法，接收一个地址作为参数，返回一个UInt作为地址对应的数据
   * read 方法有三个重载版本，一个是传入Int作为参数，这样将防止构建数据选择器，转而使用特定的地址进行寻址
   * 一个是传入UInt作为参数，这样，read函数将根据UInt的位数来确定具体的寻址模式，进而生成数据选择器
   * 一个是传入两个地址参数，一个是UInt类型的地址，一个是使用Int来划分寻址范围，实现部分区域寻址，降低数据选择器的规模
   *
   * @Attention!:
   * read 方法应当配备详细的地址解析协议来详细阐述其传址方式，例如地址0-15表示8位数据，16-31表示16位数据等。
   * 而具体的电路控制应当通过地址线宽度来进行选择，例如如果只传入2位，那么他将只可能读到某四个特定的位置等。
   * read 方法的返回值中也应配备有效的返回协议，例如，或许数据位的高1位也许是数据是否有效的标志，
   * read 方法也应该在合理的位置抛出异常，以提高代码健壮性。
   */
  override def read(address: Int): UInt = {
    get128()(address)
  }

  override def read(address: UInt): UInt = {
    get128()(address)
  }

  override def read(address: UInt, select: Int): UInt = {
    throw Error
  }

  override def write(address: UInt, data: UInt): Unit = {
    key32(address) := data
  }

  override def write(address: Int, data: UInt): Unit = {
    key32(address) := data
  }

  override def write(address: UInt, select: Int, data: UInt): Unit = {
    throw Error
  }

  override def totalLength: Int = {
    return 32 * keyContainerNum
  }

  override def addressLength: Int = {
    log2Ceil(keyNum)
  }

  override def containerNum: Int = {
    keyContainerNum
  }

  override def from(key: Containers): Unit = {
    require(key.isInstanceOf[Key])
    for (j <- 0 until keyContainerNum) {
      this.key32(j) := key.read(j / 4)((j % 4 + 1) * 32, (j % 4) * 32)
    }
  }

  override def from(phv: UInt): Unit = {
    throw Error
  }
}
