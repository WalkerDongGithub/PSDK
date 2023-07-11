package hw.simple_pisa

import chisel3._
import chisel3.util._
import hw.simple_pisa.SimpleMatch._
import hw.template.GetAddress

class SimpleGetAddress
  extends GetAddress(key, wayNum, addressLength, addressNum) {

  override def hashUnit(key: UInt): UInt = {
    val inputLength = key.getWidth
    val outputLength = SimpleMatch.hashValueLength
    val split = Wire(Vec(inputLength / outputLength, UInt(outputLength.W)))
    val result = Reg(UInt(outputLength.W))
    for (i <- 0 until inputLength / outputLength) {
      split(i) := key((i + 1) * outputLength - 1, i * outputLength)
    }
    result := split.reduce(_ + _)
    result
  }

  private val onChipAddressLength = 10
  private val chipSelectAddressLength = 6


  private val index =
    Reg(
      Vec(wayNum,
        Vec(addressInOneWay,
          Vec(chipSelectAddressLength, UInt(log2Ceil((chipSelectAddressLength) + 1).W)))))

  override def getAddress(hashValue: UInt, wayId: Int): Vec[UInt] = {
    val onChipAddress = Reg(Vec(addressInOneWay, UInt(onChipAddressLength.W)))
    val chipSelectAddress = Reg(Vec(addressInOneWay, Vec(chipSelectAddressLength, Bool())))
    val result = Wire(Vec(addressInOneWay, UInt(addressLength.W)))
    for (i <- 0 until addressInOneWay) {
      onChipAddress(i) := hashValue((i + 1) * onChipAddressLength - 1, i * onChipAddressLength)
      for (j <- 0 until chipSelectAddressLength) {
        chipSelectAddress(i)(j) := hashValue(hashValue.getWidth - 1, onChipAddress.getWidth)(index(wayId)(i)(j))
      }
      result(i) := Cat(chipSelectAddress(i).asUInt, onChipAddress(i))
    }
    result
  }
}
