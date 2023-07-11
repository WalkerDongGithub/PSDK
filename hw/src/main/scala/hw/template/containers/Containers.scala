package hw.template.containers

import chisel3._
import chisel3.util._

/**
 *  Containers
 *
 * 是一组可能拥有不同长度和数量的数据容器
 * 拥有一个特定的编址方式，通过编址方式来进行读写
 * Simple PHV 提供了一个简单的用 Containers 实现 PHV 的样例，可以用于测试或学习
 */
trait Containers extends Bundle {

  def read(address: Int) : UInt
  def read(address: UInt) : UInt

  def write(address: UInt, data: UInt) : Unit
  def write(address: Int, data: UInt) : Unit

  def totalLength : Int
  def addressLength : Int
  def containerNum : Int

  def info(): Unit = {
    println(this.className + " totalLength:   " + totalLength)
    println(this.className + " addressLength: " + addressLength)
    println(this.className + " containerNum:  " + containerNum)
  }

}

trait ContainersWithFixedOutputLength extends Containers {
  def fixedOutputLength = totalLength / containerNum
}
trait ContainersWithFrom extends Containers {
  def from(phv: Containers) : Unit
}

trait SymmetricReadAndWriteContainers extends ContainersWithFrom {
  override def from(containers: Containers) : Unit = {
    for (i <- 0 until containerNum) {
      this.write(i, containers.read(i))
    }
  }
}

trait ContainersWithSelector extends Containers {
  def read(address: UInt, select: Int) : UInt
  def write(address: UInt, select: Int, data: UInt) : Unit
}

