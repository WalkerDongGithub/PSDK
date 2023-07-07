package psdk.hw

import chisel3._
import chisel3.util._
import psdk.hw.phv.{Containers, KeyAndPHVPassModule}


/**
 * 传入一个哈希值，传出一组地址，代表
 * @param hashWidth
 * @param addressLength
 * @param addressNum
 */
class GetAddressMapper(hashWidth: Int, addressLength: Int, addressNum: Int) extends Bundle {
  val hashValue = Input(UInt(hashWidth.W))
  val address = Output(Vec(addressNum, UInt(addressLength.W)))
}
class GetAddress[Key <: Containers, PHV <: Containers]
(val hashWidth: Int, val addressLength: Int, val addressNum: Int)
  extends KeyAndPHVPassModule[Key, PHV](2) {

  val addressMapper = IO(new GetAddressMapper(hashWidth, addressLength, addressNum))

}
