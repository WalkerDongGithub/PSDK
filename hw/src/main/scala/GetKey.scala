package psdk.hw
import chisel3._
import chisel3.util._
import psdk.hw.phv._


class GetKeyMapperBundle[PHV <: Containers, Key <: Containers](phv: PHV, key: Key) extends Bundle {
  val in = Input(Vec(key.containerNum, UInt(phv.addressLength.W)))
  val out = Output(new Key)

  def crossBar(): Unit = {
    for (i <- 0 until key.containerNum) {
        out.write(i, phv.read(in(i)))
    }
  }

}


class GetKey[PHV<: Containers, Key<: Containers](phvGen: PHV, keyGen: Key) extends PHVPassModule[PHV] {

  private val key = Reg(new Key)

  val keyOut = IO(new GetKeyMapperBundle(phv, key))

}
