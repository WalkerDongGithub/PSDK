package pisa

import chisel3._
import template.HashUnit

class SimpleHashUnit
(
  override val inputLength: Int,
  override val outputLength: Int,
  override val passCycle: Int) extends HashUnit(inputLength, outputLength, passCycle) {
  require(passCycle == 1)
  private val split = Wire(Vec(inputLength / outputLength, UInt(outputLength.W)))
  private val result = Reg(UInt(outputLength.W))
  for (i <- 0 until inputLength / outputLength) {
    split(i) := io.in((i + 1) * outputLength - 1, i * outputLength)
  }
  result := split.reduce(_ + _)
  io.out := result
}
