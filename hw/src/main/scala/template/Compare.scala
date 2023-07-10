package template

import chisel3._
import psdk.hw.phv.{Containers, ContainersWithSelector, PHVPassModule}

class CompareMapper[ReadData <: Containers, Key <: Containers]
(val readDataGen: ReadData, val keyGen: Key, val readNum: Int, val actionLength: Int, val actionNum: Int) extends Bundle {
  val readData = Input(new ReadData)
  val key = Input(new Key)
  val defaultAction = Input(Vec(actionNum, UInt(actionLength.W)))
  val action = Output(Vec(actionNum, UInt(actionLength.W)))
  val hit = Output(Vec(actionNum, Bool()))
}

class Compare[Key <: Containers, PHV <: Containers, ReadDataMethod <: ContainersWithSelector]
(val keyGen: Key, val phvGen: PHV, val readDataGen: ReadDataMethod, val readNum: Int, val dataLength: Int, val actionNum: Int)
  extends PHVPassModule[PHV](1) {

  val compareMapper = IO(new CompareMapper(readDataGen, keyGen, readNum, dataLength, actionNum))

  for (i <- 0 until actionNum) {
    val hit = compareMapper.readData.read(compareMapper.key.read(i), i) =/= -1.U
    when (hit) {
      compareMapper.action(i) := compareMapper.readData.read(actionNum + i)
    } .otherwise {
      compareMapper.action(i) := compareMapper.defaultAction(i)
    }
    compareMapper.hit(i) := hit
  }

}
