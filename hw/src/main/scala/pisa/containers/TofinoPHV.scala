package pisa.containers

import chisel3._
import chisel3.util._
import psdk.hw.phv.{ContainersWithSelector, SymmetricReadAndWriteContainers}

/**
 * Tofino PHV
 * 由 64 个8 位Container，96个16位Container，64个32位Container组成
 * 按照先 16 然后 8，再然后 32位进行编址，
 */
class TofinoPHV extends ContainersWithSelector with SymmetricReadAndWriteContainers {


  private val Tofino08 = 64
  private val Tofino16 = 96
  private val Tofino32 = 64

  private val Tofino08Containers = Vec(Tofino08, UInt(8.W))
  private val Tofino16Containers = Vec(Tofino16, UInt(16.W))
  private val Tofino32Containers = Vec(Tofino32, UInt(32.W))


  /**
   * 寻址方式，首先编址16位Container，随后编址8位Container，随后编址32位Container，对称寻址
   */
  override def read(address: Int): UInt = {
    val lengthSelect = address >> 5
    val whichContainer = address & 0x1f
    if (lengthSelect <= 2) Tofino16Containers(whichContainer)
    else if (lengthSelect <= 4) Tofino08Containers(whichContainer)
    else Tofino32Containers(whichContainer)
  }

  override def read(address: UInt): UInt = {
    val lengthSelect = address(7, 5)
    val whichContainer = address(4, 0)
    val b0 = Wire(Bool())
    b0 := false.B
    val b1 = Wire(Bool())
    b1 := true.B
    val res = MuxLookup(lengthSelect, 0.U, Array(
      0.U -> Tofino16Containers(Cat(b0, b0, whichContainer)),
      1.U -> Tofino16Containers(Cat(b0, b1, whichContainer)),
      2.U -> Tofino16Containers(Cat(b1, b0, whichContainer)),
      3.U -> Tofino08Containers(Cat(b0, whichContainer)),
      4.U -> Tofino08Containers(Cat(b1, whichContainer)),
      5.U -> Tofino32Containers(Cat(b0, whichContainer)),
      6.U -> Tofino32Containers(Cat(b1, whichContainer))
    ))
    res
  }

  /**
   * 使用select进行固定container位数进行特殊方式的寻址，0代表寻址16位，1代表寻址8位，2代表寻址32位
   * 此时address将从0起，而不是按照一般寻址方式
   */
  override def read(address: UInt, select: Int): UInt = {
    if (select == 0) Tofino16Containers(address)
    else if (select == 1) Tofino08Containers(address)
    else if (select == 2) Tofino32Containers(address)
    else throw Error
  }

  override def write(address: UInt, data: UInt): Unit = {
    val lengthSelect = address(7, 5)
    val whichContainer = address(4, 0)
    val b0 = Wire(Bool())
    b0 := false.B
    val b1 = Wire(Bool())
    b1 := true.B
    when (lengthSelect === 0.U) {
      Tofino16Containers(Cat(b0, b0, whichContainer)) := data(15, 0)
    } .elsewhen (lengthSelect === 1.U) {
      Tofino16Containers(Cat(b0, b1, whichContainer)) := data(15, 0)
    } .elsewhen (lengthSelect === 2.U) {
      Tofino16Containers(Cat(b1, b0, whichContainer)) := data(15, 0)
    } .elsewhen (lengthSelect === 3.U) {
      Tofino08Containers(Cat(b0, whichContainer)) := data(7, 0)
    } .elsewhen (lengthSelect === 4.U) {
      Tofino08Containers(Cat(b1, whichContainer)) := data(7, 0)
    } .elsewhen (lengthSelect === 5.U) {
      Tofino32Containers(Cat(b0, whichContainer)) := data(31, 0)
    } .elsewhen (lengthSelect === 6.U) {
      Tofino32Containers(Cat(b1, whichContainer)) := data(31, 0)
    }
  }

  override def write(address: Int, data: UInt): Unit = {
    val lengthSelect = address >> 5
    val whichContainer = address & 0x1f
    if (lengthSelect <= 2) Tofino16Containers(whichContainer) := data(15, 0)
    else if (lengthSelect <= 4) Tofino08Containers(whichContainer) := data(7, 0)
    else Tofino32Containers(whichContainer) := data(31, 0)

  }

  override def write(address: UInt, select: Int, data: UInt): Unit = {
    if (select == 0) Tofino16Containers(address) := data(15, 0)
    else if (select == 1) Tofino08Containers(address) := data(7, 0)
    else if (select == 2) Tofino32Containers(address) := data(31, 0)
    else throw Error
  }

  override def totalLength: Int = Tofino08 * 8 + Tofino16 * 16 + Tofino32 * 32

  override def addressLength: Int = 8

  override def containerNum: Int = Tofino08 + Tofino16 + Tofino32

}
