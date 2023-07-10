# Containers

本节主要介绍容器系统。

容器系统是整个系统重构的最重要的一环，几乎每一级流水中传入的和送出的数据结构、SRAM 
等都使用Containers来进行抽象。我们为不同的结构提供了统一的抽象，我们可以使用这些抽象很轻易的构造出灵活多变的交换机模型。

## 1.1 Containers 简介

在第一版的容器系统中，我们演示了一个违反了单一职责原则设计、但是可以直观展示框架设计的模型。

源码位于src/main/scala/phv/Containers.scala
```scala
abstract class Containers extends Bundle {
  def read(address: Int) : UInt
  def read(address: UInt) : UInt
  def write(address: UInt, data: UInt) : Unit
  def write(address: Int, data: UInt) : Unit
  def totalLength : Int
  def addressLength : Int
  def containerNum : Int
  def from(phv: Containers) : Unit
}
```

带有Selector的PHV可能还需要支持以下方法
```scala
  def read(address: UInt, select: Int) : UInt
  def write(address: UInt, select: Int, data: UInt) : Unit
```
Containers的本质是为一个Bundle提供了如下三种重要的调用接口，read write from
下面为这三种方法进行叙述。

### 1.1 read&write 概述

read和write方法为从Containers中读取、写入数据的连线策略，用户需要为Containers定义寻址模式、返回数据格式等重要信息。
read 和 write 都包括两种寻址调用，混入了ContainersWithSelector特质的容器还支持第三种配合Selector寻址。

1. 传入Scala语言的Int来控制寻址，用于生成固定的地址线进行寻址。
2. 传入Chisel语言的UInt来控制寻址，用于生成全局数据选择器进行寻址。
3. 传入两个参数, Scala 语言的 Int 作为Selector 配合 Chisel 语言的 UInt 来控制寻址，用于生成部分选通，例如四路的GetAddress可能会在不同的段上进行寻址。Int用于控制具体的路号，UInt则负责生成数据选择器。

#### 对称读写容器
注意，read 和 write 的寻址方式不一定相同，我们定义read 和 write寻址方式完全相同的Containers为对称度写的Containers（即通过是否混入 SymmetricReadAndWriteContainers特质来确定），
例如，Key 只在 GetKey 阶段在生成 Key 的过程中才使用了write方法，其余位置的 Key
都作为只读数据存在，其数据读取过程按照 Key 的组织来决定，与装配Key的寻址方法有差异，所以Key不是对称读写的。而诸如PHV、SRAM等结构都是对称读写的，这样编译器才能够准确定位前后的PHV Container位置。

#### 具体案例：Tofino PHV
以一个经典的例子来进行说明。即Tofino架构中的PHV的架构设计：

Tofino使用64个8位Container，96个16位Container，64个32位Container组成
我们在为PHV编址的时候，就需要对三种长度的PHV进行编址
为了硬件寻址模块设计简单，我们从container数量从多到少来依次进行编址

  0- 95（00000000-01011111）为16位Container，前三位为0，1，2

 96-159（01100000-10011111）为 8位Container，前三位为3，4

160-223（10100000-11011111）为32位Container，前三位为5，6

如此我们便可以设计出对应的编址方式，以read(Int) -> UInt为例
```scala
  override def read(address: Int): UInt = {
    val lengthSelect = address >> 5
    val whichContainer = address & 0x1f
    if (lengthSelect <= 2) Tofino16Containers(whichContainer)
    else if (lengthSelect <= 4) Tofino08Containers(whichContainer)
    else Tofino32Containers(whichContainer)
  }
```
其余方法都依照此法来进行设计，我们便定义出了全架构可用的PHV读写方法。

这种设计可以让我们可以更好的实现更细致的单元测试，在开发实际的交换机之前，我们先为交换机配备好开发各个组件所必须的模板器件，如Gateway、GetKey等，
我们对这些模板组件进行严格的测试，此后的开发我们只要确定容器设计是正确的，整体的测试也会比较顺利的进行。


