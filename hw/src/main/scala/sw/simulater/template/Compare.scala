package sw.simulater.template

import chisel3._
import hw.template.containers.{Containers, ContainersWithFixedOutputLength, ContainersWithSelector}

class CompareMapper[DataFromSram <: Containers, Key <: Containers]
(val dataFromSramGen: DataFromSram, val keyGen: Key, val readNum: Int, val actionLength: Int, val actionNum: Int) extends Bundle {
  val dataFromSram = Input(new DataFromSram)
  val key = Input(new Key)
  val defaultAction = Input(Vec(actionNum, UInt(actionLength.W)))
  val action = Output(Vec(actionNum, UInt(actionLength.W)))
  val hit = Output(Vec(actionNum, Bool()))
}

/**
    Compare 模块，用于match表中数据与查出的数据进行比对

    硬性规定，这里的ReadDataInterface的逻辑是硬性的
    当 read 参数是 UInt address 配合一个Int Selector时，意味着对第selector路key做匹配，匹配 key 为address
    返回值为两部分，高一位为是否hit，低若干位为对应的value值。
 */
class Compare[Key <: Containers, PHV <: Containers, DataFromSram <: ContainersWithSelector with ContainersWithFixedOutputLength]
(val keyGen: Key, val phvGen: PHV, val dataFromSramGen: DataFromSram, val readNum: Int, val dataLength: Int, val outputNum: Int)
  extends containers.PHVPassModule[PHV](1) {

  val compareMapper = IO(new CompareMapper(dataFromSramGen, keyGen, readNum, dataLength, outputNum))

  for (i <- 0 until outputNum) {
    val compareResult = compareMapper.dataFromSram.read(compareMapper.key.read(i), i)
    val hit = compareResult(compareMapper.dataFromSram.fixedOutputLength - 1).asBool
    when (hit) {
      compareMapper.action(i) := compareResult
    } .otherwise {
      compareMapper.action(i) := compareMapper.defaultAction(i)
    }
    compareMapper.hit(i) := hit
  }

}
