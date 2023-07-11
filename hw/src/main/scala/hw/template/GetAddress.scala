package hw.template

import chisel3._
import hw.template.containers.{Containers, KeyAndPHVPassModule}


/**
 * 传入一个哈希值，传出一组地址，代表
 *
 * @param hashWidth
 * @param addressLength
 * @param addressNum
 */
class GetAddressMapper[Key<: Containers](keyGen : Key, addressLength: Int, addressNum: Int) extends Bundle {
  val key = Input(new Key)
  val address = Output(Vec(addressNum, UInt(addressLength.W)))
}
abstract class GetAddress[Key<: Containers]
(keyGen: Key, val wayNum: Int, val addressLength: Int, val addressNum: Int)
  extends Module {
  val addressMapper = IO(new GetAddressMapper(keyGen, addressLength, addressNum))

  def hashUnit(key: UInt): UInt

  def getAddress(hashValue: UInt, wayId: Int) : Vec[UInt]

  val addressInOneWay = addressNum / wayNum

  for (i <- 0 until wayNum) {
    val hashValue = hashUnit(addressMapper.key.read(i))
    val addresses = getAddress(hashValue, i)
    require(addresses(0).getWidth == addressLength)
    for (j <- 0 until addressInOneWay) {
      addressMapper.address(i * addressInOneWay + j) := addresses(j)
    }
  }



}
