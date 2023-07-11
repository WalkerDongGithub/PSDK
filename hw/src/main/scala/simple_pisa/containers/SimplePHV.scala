package simple_pisa.containers

import chisel3._
import psdk.hw.phv.{Containers, SymmetricReadAndWriteContainers}

/**
 * Simple PHV 就是Tofino PHV除去了 8位和 16位的Container，一方面言简意赅、另一方面使用简单的结构可以简化测试，
 * 从而快速交付敏捷开发的测试环节。
 */
object SimplePHV {
  val num = 64
  val bit = 32
}
class SimplePHV extends SymmetricReadAndWriteContainers {


  private val num = SimplePHV.num
  private val bit = SimplePHV.bit
  private val containers = Vec(num, UInt(bit.W))
  override def read(address: Int): UInt = {
    containers(address & 0x3f)
  }

  override def read(address: UInt): UInt = {
    containers(address(addressLength - 1, 0))
  }

  override def write(address: UInt, data: UInt): Unit = {
    containers(address(addressLength - 1, 0)) := data(bit - 1, 0)
  }

  override def write(address: Int, data: UInt): Unit = {
    containers(address & 0x3f) := data(bit - 1, 0)
  }
  override def totalLength: Int = bit * num
  override def addressLength: Int = 6
  override def containerNum: Int = num

}
