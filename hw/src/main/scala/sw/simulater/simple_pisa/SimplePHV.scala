package sw.simulater.simple_pisa

import sw.simulater.template.containers

class SimplePHV extends containers.SymmetricReadAndWriteContainers {

  private val containers = Array.fill(containerNum) { 0 }
  override def read(address: Int): Int = containers(address)

  override def write(address: Int, data: Int): Unit = {
    containers(address) = data
  }

  override def totalLength: Int = containerNum * 32

  override def addressLength: Int = 6

  override def containerNum: Int = 64
}
