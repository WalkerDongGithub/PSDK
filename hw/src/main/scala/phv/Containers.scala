package psdk.hw.phv

import chisel3._
import chisel3.util._

/**
 *  Containers
 *
 * 是一组可能拥有不同长度和数量的数据容器
 * 拥有一个特定的编址方式，通过编址方式来进行读写
 * Simple PHV 提供了一个简单的用 Containers 实现 PHV 的样例，可以用于测试或学习
 */
abstract class Containers extends Bundle {

  def read(address: Int) : UInt
  def read(address: UInt) : UInt
  def write(address: UInt, data: UInt) : Unit
  def write(address: Int, data: UInt) : Unit
  def totalLength : Int
  def addressLength : Int
  def containerNum : Int
  def from(phv: Containers) : Unit
  def from(phv: UInt) : Unit

}
