package hw.template

import chisel3._
import hw.template.containers.{Containers, KeyAndPHVPassModule}

class MemReaderMapper(val readNum: Int, val addressLength: Int, val dataLength: Int) extends Bundle {
  val readAddress = Output(Vec(readNum, UInt(addressLength.W)))
  val readData = Input(Vec(readNum, UInt(dataLength.W)))
}

class ReadDataMapper(val readNum: Int, val addressLength: Int, val dataLength: Int) extends Bundle {
  val readAddress = Input(Vec(readNum, UInt(addressLength.W)))
  val gateway = Input(Vec(readNum, Bool()))
  val readData = Output(Vec(readNum, UInt(dataLength.W)))
}
class ReadData[PHV<: Containers, Key<: Containers]
(phvGen: PHV, keyGen: Key, val readNum: Int, val addressLength: Int, val dataLength: Int)
  extends KeyAndPHVPassModule[Key, PHV](1) {

  val memReaderMapper = IO(new MemReaderMapper(readNum, addressLength, dataLength))
  val readDataMapper = IO(new ReadDataMapper(readNum, addressLength, dataLength))

  val invalidAddress = (1 << (addressLength - 1)).U
  for (i <- 0 until readNum) {
    memReaderMapper.readAddress(i) := Mux(readDataMapper.gateway(i), readDataMapper.readAddress(i), invalidAddress)
    readDataMapper.readData(i) := memReaderMapper.readData(i)
  }

}
