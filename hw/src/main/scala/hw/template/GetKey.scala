package hw.template

import chisel3._
import hw.template.containers.{Containers, PHVPassModule}


class GetKeyMapper[PHV <: Containers, Key <: Containers](phv: PHV, key: Key) extends Bundle {
  val in = Input(Vec(key.containerNum, UInt(phv.addressLength.W)))
  val out = Output(new Key)

  def crossBar(): GetKeyMapper[PHV, Key] = {
    for (i <- 0 until key.containerNum) {
      out.write(i, phv.read(in(i)))
    }
    this
  }

}

/**
 * GetKey Module
 * Get Key 需要接受两个泛型参数，分别是 PHV 和 Key 的 Container 类型。
 * 最终生成的结果即生成一个 CrossBar，动作是传入一组PHVContainer 的地址，读取 PHV Container 的数据进入 Key 中
 * 这里的配置为
 * @param phvGen
 * @param keyGen
 * @tparam PHV
 * @tparam Key
 */
class GetKey[PHV<: Containers, Key<: Containers](phvGen: PHV, keyGen: Key) extends PHVPassModule[PHV](1) {

  private val key = Reg(new Key)

  val keyOut = IO(new GetKeyMapper(phv, key).crossBar())

}
