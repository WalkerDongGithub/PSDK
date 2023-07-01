package psdk.hw

import chisel3._
import chisel3.util._



class GetAddressMapperBundle(hashWidth: Int, addressLength: Int, addressNum: Int) extends Bundle {
  val hashValue = Input(UInt(hashWidth.W))
  val address = Output(Vec(addressNum, UInt(addressLength.W)))
}
class GetAddress(val hashWidth: Int, val addressLength: Int, val addressNum: Int) extends Module {

  val addressMapper = IO(new GetAddressMapperBundle(hashWidth, addressLength, addressNum))

}
