package phv.pisa


import chisel3._
import chisel3.util._
import psdk.hw.phv.Containers

class PHV extends Containers {

  private val c08 = 16
  private val c16 = 32
  private val c32 = 16
  private val container08 = Vec(c08, UInt( 8.W))
  private val container16 = Vec(c16, UInt(16.W))
  private val container32 = Vec(c32, UInt(32.W))

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
     if (address < c16) {
       return container16(address)
     } else if (address < c08 + c16) {
       return container08(address - c08)
     } else {
       return container32(address - c08 - c16)
     }
  }

  override def read(address: UInt): UInt = {
    val is16 = address(5) === 0.U && address(4) === 0.U
    val is08 = address(5) === 1.U && address(4) === 0.U
    val is32 = address(5) === 1.U && address(4) === 1.U
    val result = UInt(32.W)
    when (is16) {
      result := container16(address(4, 0))
    } .elsewhen (is08) {
      result := container08(address(3, 0))
    } .otherwise {
      result := container32(address(3, 0))
    }
    result

  }

  override def read(address: UInt, select: Int): UInt = {
    throw Error
  }

  override def write(address: UInt, data: UInt): Unit = {
    val is16 = address(5) === 0.U && address(4) === 0.U
    val is08 = address(5) === 1.U && address(4) === 0.U
    val is32 = address(5) === 1.U && address(4) === 1.U
    val result = UInt(32.W)
    when (is16) {
      container16(address(4, 0)) := data
    }.elsewhen(is08) {
      container08(address(3, 0)) := data
    }.otherwise {
      container32(address(3, 0)) := data
    }

  }

  override def write(address: Int, data: UInt): Unit = {
    if (address < c16) {
      container16(address) := data
    } else if (address < c08 + c16) {
      container08(address - c08) := data
    } else {
      container32(address - c08 - c16) := data
    }
  }

  override def write(address: UInt, select: Int, data: UInt): Unit = {
    throw Error
  }

  override def totalLength: Int = c08 * 8 + c16 * 16 + c32 * 32

  override def addressLength: Int = 6

  override def containerNum: Int = c08 + c16 + c32

  override def from(phv: Containers): Unit = {
    require(phv.isInstanceOf[PHV])
    for (i <- 0 until containerNum) {
      this.write(i, phv.read(i))
    }
  }

  override def from(phv: UInt): Unit = {
    throw Error
  }
}
