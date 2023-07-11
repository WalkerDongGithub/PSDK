package template

import chisel3._
import psdk.hw.phv.{Containers, KeyAndPHVPassModule}


/**
 * 传入一个哈希值，传出一组地址，代表
 * @param hashWidth
 * @param addressLength
 * @param addressNum
 */
class GetAddressMapper[Key<: Containers](keyGen : Key, addressLength: Int, addressNum: Int) extends Bundle {
  val key = Input(new Key)
  val address = Output(Vec(addressNum, UInt(addressLength.W)))
}
class GetAddress[Key<: Containers]
(keyGen: Key, val addressLength: Int, val addressNum: Int)
  extends Module {
  val addressMapper = IO(new GetAddressMapper(keyGen, addressLength, addressNum))

}
