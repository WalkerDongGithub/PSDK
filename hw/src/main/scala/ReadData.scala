package psdk.hw
import chisel3._
import chisel3.util._
import psdk.hw.phv._

class MemReaderMapper(val readNum: Int, val addressLength: Int, val dataLength: Int) extends Bundle {
  val readAddress = Output(Vec(readNum, UInt(addressLength.W)))
  val readData = Input(Vec(readNum, UInt(dataLength.W)))
}

class ReadDataMapper(val readNum: Int, val addressLength: Int, val dataLength: Int) extends Bundle {
  val readAddress = Input(Vec(readNum, UInt(addressLength.W)))
  val readData = Output(Vec(readNum, UInt(dataLength.W)))
}
class ReadData[PHV<: Containers, Key<: Containers]
(val readNum: Int, val addressLength: Int, val dataLength: Int)
  extends KeyAndPHVPassModule[Key, PHV](1) {

  val memReaderMapper = IO(new MemReaderMapper(readNum, addressLength, dataLength))
  val readDataMapper = IO(new ReadDataMapper(readNum, addressLength, dataLength))

  for (i <- 0 until readNum) {
    memReaderMapper.readAddress(i) := readDataMapper.readAddress(i)
    readDataMapper.readData(i) := memReaderMapper.readData(i)
  }

}
