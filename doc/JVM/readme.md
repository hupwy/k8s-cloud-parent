[调优参数地址java 8](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html)

# 选择收集器

除非您的应用程序有非常严格的暂停时间要求，否则请先运行您的应用程序并允许VM选择收集器。如有必要，请调整堆大小以提高性能。如果性能仍然不能达到您的目标，请使用以下准则作为选择收集器的起点。

- 如果应用程序的数据集较小（最大约100 MB），则使用选项选择串行收集器-XX:+UseSerialGC。
- 如果应用程序将在单个处理器上运行，并且没有暂停时间要求，则让VM选择收集器，或使用选项选择串行收集器-XX:+UseSerialGC。
- 如果（a）峰值应用程序性能是第一要务，并且（b）没有暂停时间要求或可接受的暂停时间为1秒或更长，则让VM选择收集器，或使用选择并行收集器-XX:+UseParallelGC。这些准则仅提供选择收集器的起点，因为性能取决于堆的大小，应用程序维护的实时数据量以及可用处理器的数量和速度。暂停时间对这些因素特别敏感，因此前面提到的1秒阈值仅是近似值：在许多数据大小和硬件组合上，并行收集器的暂停时间将超过1秒。相反，在某些组合上，并发收集器可能无法将暂停时间保持在1秒以内。

如果推荐的收集器未达到所需的性能，请首先尝试调整堆和世代大小以满足所需的目标。如果性能仍然不足，请尝试使用其他收集器：使用并发收集器减少暂停时间，并使用并行收集器增加多处理器硬件的总体吞吐量。

# 并行收集器

并行收集器（在此也称为吞吐量收集器）是类似于串行收集器的分代收集器。主要区别在于使用多个线程来加速垃圾回收。并行收集器通过命令行选项启用-XX:+UseParallelGC。默认情况下使用此选项，次要收集和主要收集都可以并行执行，以进一步减少垃圾收集的开销。

垃圾收集器线程的数量可以通过命令行选项控制 `-XX:ParallelGCThreads=<N> 收集器的分代排列的描述:

https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/img/jsgct_dt_002_armgnt_gn_pl.png

- 最大垃圾回收暂停时间：最大暂停时间目标是使用命令行选项指定的`-XX:MaxGCPauseMillis=<N>`。这被解释为需要<N>毫秒或更少的暂停时间的提示。

  默认情况下，没有最大暂停时间目标，如果指定了暂停时间目标，则会调整堆大小和与垃圾回收相关的其他参数，以使垃圾回收的暂停时间短于指定值。

  这些调整可能导致垃圾收集器降低应用程序的整体吞吐量，并且无法始终满足所需的暂停时间目标。

- 吞吐量：吞吐量目标是根据进行垃圾收集所花费的时间与在垃圾收集之外所花费的时间（称为应用程序时间）来衡量的。

  该目标由命令行选项指定，该选项`-XX:GCTimeRatio=<N>`将垃圾回收时间与应用程序时间之比设置为`1 / (1 + <N>)`。
  例如，`-XX:GCTimeRatio=19`将垃圾收集的目标设置为总时间的1/20或5％。默认值为99，导致垃圾回收的目标时间为1％。

- 足迹：最大堆足迹使用选项指定`-Xmx<N>`。另外，收集器还有一个隐含的目标，即只要满足其他目标，就将堆的大小最小化。

## 目标优先

按照以下顺序解决目标：

1. 最大暂停时间目标
2. 吞吐量目标
3. 最小足迹目标

> 首先达到最大暂停时间目标++++。只有在达到目标后，才能实现吞吐量目标。
>
> 同样，只有在达到前两个目标后，才会考虑足迹目标。

## 世代大小调整

收集器保留的统计信息(例如平均暂停时间)在每个收集结束时更新，然后进行确定目标是否达到的测试，并对世代大小进行任何必要的调整。

唯一的例外是`System.gc()`在保留统计信息和调整世代大小方面，会忽略显式垃圾回收(例如对的调用)。

增长和缩小世代的大小是通过增加作为世代大小的固定百分比来实现的，这样一来，世代就可以朝其期望的大小上移或下移，生长和收缩以不同的速率进行。

默认情况下，世代以20％的增量增长，而以5％的增量缩减。

成长百分比由`-XX:YoungGenerationSizeIncrement=<Y>`年轻一代和`-XX:TenuredGenerationSizeIncrement=<T>`终身代的命令行选项控制。

世代缩小的百分比由命令行标志调整`-XX:AdaptiveSizeDecrementScaleFactor=<D>`。

如果增长增量为X百分比，则收缩的减少量为`X/D`百分比

## 默认堆大小

除非在命令行上指定了初始堆大小和最大堆大小，否则它们将根据计算机上的内存量进行计算。

## 客户端JVM默认的初始和最大堆大小

默认最大堆大小是物理内存的一半，最大物理内存大小为192兆字节(MB)，否则，四分之一的物理内存，最大物理内存大小为1GB。

## 服务器JVM默认初始和最大堆大小

默认的初始堆大小和最大堆大小在服务器JVM上的工作方式与在客户端JVM上的工作方式类似，不同之处在于，默认值可以更高。

- 在32位JVM上，如果有4GB或更多的物理内存，则默认的最大堆大小最大为1GB。

- 在64位JVM上，如果有128GB或更多的物理内存，则默认的最大堆大小最大为32GB。


您始终可以通过直接指定这些值来设置更高或更低的初始堆和最大堆；

## 并发模式故障

CMS收集器使用一个或多个垃圾收集器线程，这些垃圾收集器线程与应用程序线程同时运行，目的是在使用权产生的代充满之前完成其收集。

如前所述，在正常操作中，CMS收集器在应用程序线程仍在运行的情况下执行其大部分跟踪和清除工作，因此应用程序线程仅会看到短暂的暂停。

但是，如果CMS收集器无法在使用权产生的一代填满之前完成对无法访问的对象的回收，或者如果使用权能生成的可用空闲空间块无法满足分配要求，则将暂停应用程序，并使用所有应用程序线程均已停止。

无法同时完成收集的过程称为并发模式失败，表示需要调整CMS收集器参数。

如果并发收集被显式垃圾收集`System.gc()`中断，或者为提供诊断工具信息所需的垃圾收集中断了，则将报告并发模式中断。

## 过多的GC时间和OutOfMemoryError

CMS收集器将`OutOfMemoryError`在垃圾收集上花费太多时间：

如果在垃圾收集中花费了总时间的98％以上，而回收不到2％的堆，则`OutOfMemoryError`抛出，此功能旨在防止应用程序长时间运行，而由于堆太小而几乎没有进展，甚至没有进展。

如有必要，可以通过`-XX:-UseGCOverheadLimit`在命令行中添加选项来禁用此功能，对于暂停时间要求较低的任何应用程序，都应考虑使用此收集器。

CMS收集器通过命令行选项启用`-XX:+UseConcMarkSweepGC`

## 浮动垃圾

与Java HotSpot VM中的所有其他收集器一样，CMS收集器是一个跟踪收集器，它至少标识堆中的所有可访问对象。

在Richard Jones和Rafael D. Lins的出版物《垃圾收集：自动动态内存算法》中，它是一个增量更新收集器。

由于应用程序线程和垃圾收集器线程在主收集期间同时运行，因此垃圾收集器线程跟踪的对象随后可能会在收集过程结束时变得不可访问，尚未回收的此类无法访问的对象称为浮动垃圾。

漂浮垃圾量取决于并发收集周期的持续时间以及应用程序对参考更新（也称为突变）的频率，此外，由于年轻一代和终身一代是独立收集的，因此每个人都是彼此的根源。

作为粗略的指导，请尝试将永久代的大小增加20％，以解决浮动垃圾的问题，在一个并发收集周期结束时，将在下一个收集周期中收集堆中的浮动垃圾。

## 暂停

CMS收集器在并发收集周期中两次暂停应用程序。第一个暂停是将可从根直接访问的对象（例如，来自应用程序线程堆栈和寄存器的对象引用，静态对象等）和从堆中其他位置（例如，年轻代）直接标记为活动状态。此第一个暂停称为初始标记暂停。第二次暂停是在并发跟踪阶段结束时进行的，它查找由于CMS收集器完成了对对象的引用后，应用程序线程对对象中的引用进行了更新而导致并发跟踪遗漏的对象。第二个暂停称为备注暂停。

## 开始并发收集周期

使用串行收集器时，只要永久生成已满，并且收集完成时所有应用程序线程都停止，就会发生主要收集。相反，并发收集的开始必须定时，以使收集可以在终身代变满之前完成。否则，由于并发模式故障，应用程序将观察到更长的暂停。

**有几种启动并发收集的方法**

>  TODO  #######################

根据最近的历史记录，CMS收集器将保留对永久性代用尽之前的剩余时间以及并发收集周期所需时间的估计，使用这些动态估计，开始并发的收集周期，目的是在使用权产生之前用完收集周期，为了安全起见，对这些估计值进行了填充，因为并发模式故障的代价可能很高。

可以使用命令行选项手动调整此值`-XX:CMSInitiatingOccupancyFraction=<N>`，其中`<N>`是占位世代大小的整数百分比（0到100）。

## 并发收集周期通常包括以下步骤：

- 停止所有应用程序线程，从根目录确定可访问的对象集，然后继续所有应用程序线程。
- 在应用程序线程正在执行的同时，使用一个或多个处理器跟踪可访问对象图。
- 同时使用一个处理器回溯自上一步中的跟踪以来修改的对象图的各个部分。
- 停止所有应用程序线程，并追溯自上次检查以来可能已被修改的根和对象图中的部分，然后恢复所有应用程序线程。
- 同时使用一个处理器将无法访问的对象清除到用于分配的空闲列表中。
- 同时调整堆的大小，并使用一个处理器为下一个收集周期准备支持数据结构。

## 推荐选项

要在Java SE 8中使用i-cms，请使用以下命令行选项：

```shell
-XX：+UseConcMarkSweepGC -XX：+CMSIncrementalMode 
-XX：+PrintGCDetails -XX：+PrintGCTimeStamps
```

## 基本故障排除

i-cms自动调整功能使用程序运行时收集的统计信息来计算占空比，以便并发收集在堆变满之前完成。但是，过去的行为并不是未来行为的完美预测器，并且估计值可能并不总是足够准确以防止堆变满。如果出现了太多的完整集合，请尝试一次在表8-2“对i-cms自动起步功能进行故障排除”中的步骤。

表8-2对i-cms自动起步功能进行故障排除

| 步                                   | 选件                                                     |
| ------------------------------------ | -------------------------------------------------------- |
| 1.row增加安全系数。                  | -XX:CMSIncrementalSafetyFactor=<N>                       |
| 2.增加最小占空比。                   | -XX:CMSIncrementalDutyCycleMin=<N>                       |
| 3.禁用自动起搏，并使用固定的占空比。 | XX:-CMSIncrementalPacing -XX:CMSIncrementalDutyCycle=<N> |

> 相对于次要收集暂停时间，初始标记暂停通常较短。
>
> 并发阶段（并发标记，并发预清理和并发扫描）通常持续的时间明显长于次要收集暂停的时间，如示例8-1“ CMS收集器的输出”所示。
>
> 但是请注意，在这些并发阶段中不会暂停应用程序，备注停顿的长度通常可与次要收藏相媲美。
>
> 备注暂停受某些应用程序特性的影响(例如，对象修改率较高，可能会增加此暂停)以及自上次次要收集以来的时间(例如，年轻一代中的更多对象可能会增加此暂停)

# G1 garbage

Garbage-First(G1)垃圾收集器是一种服务器样式的垃圾收集器，适用于具有大内存的多处理器计算机。它试图以高概率满足垃圾收集(GC)暂停时间目标，同时实现高吞吐量。全堆操作(例如全局标记)与应用程序线程同时执行。这样可以防止与堆大小或活动数据大小成比例的中断。

G1收集器通过多种技术实现了高性能和暂停时间目标。

堆被划分为一组大小相等的堆区域，每个堆区域都有一个连续的虚拟内存范围，G1执行并发的全局标记阶段，以确定整个堆中对象的活动性，标记阶段完成后，G1知道哪些区域大部分为空，它首先收集这些区域，这通常会产生大量的自由空间，这就是为什么这种垃圾收集方法称为“垃圾优先”的原因。

顾名思义，G1将其收集和压缩活动集中在可能充满可回收对象（即垃圾）的堆区域。

G1使用暂停预测模型来满足用户定义的暂停时间目标，并根据指定的暂停时间目标选择要收集的区域数。

G1将对象从堆的一个或多个区域复制到堆上的单个区域，并在此过程中压缩并释放内存，撤离是在多处理器上并行执行的，以减少暂停时间并增加吞吐量。

因此，对于每个垃圾收集，G1都会不断减少碎片。这超出了前面两种方法的能力。

CMS(并发标记扫描)垃圾收集不会进行压缩，并行压缩仅执行整个堆压缩，这会导致相当长的暂停时间。

> 重要的是要注意，G1不是实时收集器。它很有可能达到设定的暂停时间目标，但并非绝对确定。根据先前收集的数据，G1估计在目标时间内可以收集多少个区域。因此，收集器具有收集区域成本的合理准确的模型，并且收集器使用此模型来确定要收集哪些区域和多少区域，同时保持在暂停时间目标之内。

G1的首要重点是为运行需要大堆且GC延迟有限的应用程序的用户提供解决方案。

这意味着堆大小约为6GB或更大，并且稳定且可预测的暂停时间低于0.5秒。

 如果应用程序具有以下一个或多个特征，那么今天运行CMS或并行压缩的应用程序将从切换到G1中受益。

- 超过50％的Java堆被实时数据占用。
- 对象分配率或提升率差异很大。
- 该应用程序正在经历不希望的长时间垃圾收集或压缩暂停(长于0.5到1秒)。

计划将G1作为并发标记扫描收集器(CMS)的长期替代产品，将G1与CMS进行比较，可以发现使G1成为更好解决方案的差异。

一个区别是G1是压紧收集器。此外，G1提供的垃圾收集暂停比CMS收集器更具可预测性，并允许用户指定所需的暂停目标。

与CMS一样，G1专为需要较短GC暂停的应用而设计。

G1将堆划分为固定大小的区域(灰色框)，如图9-1“按G1进行堆划分”所示。

**图9-1按G1进行堆划分 **

![jsgct_dt_004_grbg_frst_hp](images/jsgct_dt_004_grbg_frst_hp.png)

在逻辑上，G1是世代相传的。一组空区域被指定为逻辑年轻代。

- 在图中，年轻一代是浅蓝色的。分配是从该逻辑年轻代中完成的，当年轻代已满时，该区域集将被垃圾收集（一个年轻集合）。在某些情况下，可以同时收集一组年轻区域之外的区域（深蓝色的旧区域）。这称为混合集合。
- 在图中，正在收集的区域用红色框标记。该图说明了混合的集合，因为同时收集了年轻区域和旧区域。垃圾收集是一个压缩收集，它将活动对象复制到选定的最初为空的区域。
- 根据幸存对象的年龄，可以将对象复制到幸存者区域（标有“ S”）或复制到旧区域（未具体显示）。
- 标有“ H”的区域包含的绒毛物体大于一个区域的一半，并且经过特殊处理；请参见了Humongous对象和分配了Humongous在垃圾-First垃圾收集。

## 分配(疏散)失败

与CMS一样，G1收集器在应用程序继续运行时会运行其部分收集，并且存在应用程序分配对象的速度快于垃圾收集器可以回收可用空间的风险。

请参见并发模式失败的并发标记扫描（CMS）收集的类似CMS行为。

在G1中，当G1将活动数据从一个区域复制（撤离）到另一区域时，发生故障（Java堆耗尽），复制是为了压缩实时数据。

如果在疏散正在收集垃圾的区域的过程中找不到空闲(空)区域，则发生分配失败(因为没有空间可以从正在疏散的区域分配有生命的物体)，并且停止世界( STW)已完成完整收集

## 暂停

G1暂停应用程序以将活动对象复制到新区域。这些暂停可以是仅收集年轻区域的年轻收集暂停，也可以是疏散年轻和旧区域的混合收集暂停。与CMS一样，在应用程序停止时，有最后的标记或注释暂停以完成标记。CMS也具有初始标记暂停，而G1则作为疏散暂停的一部分来执行初始标记工作。G1在集合的结尾具有清理阶段，该阶段部分为STW，部分为并发。清理阶段的STW部分标识空区域，并确定旧区域作为下一个收集的候选区域。

## 卡表和并发阶段

如果垃圾收集器没有收集整个堆（增量收集），则垃圾收集器需要知道从堆的未收集部分到正在收集的堆部分的指针在哪里。这通常用于分代垃圾收集器，其中堆的未收集部分通常是旧的一代，而堆的收集部分是年轻的一代。用于保存该信息的数据结构（指向年轻一代对象的老一代指针）是一个可记忆的集合。甲卡表是一个特定类型的记录置位的。Java HotSpot VM使用字节数组作为卡表。每个字节称为卡。卡与堆中的地址范围相对应。对卡进行脏污意味着将字节的值更改为脏值 ; 脏值可能包含卡所覆盖的地址范围中从旧一代到年轻一代的新指针。

## 开始并发收集周期

如前所述，无论是young区还是old区，都是混合收集的垃圾。为了收集old区，G1对堆中的活动对象进行了完整的标记。这种标记是通过并发标记阶段完成的。当整个Java堆的占用达到参数的值时，开始并发标记阶段`InitiatingHeapOccupancyPercent`。使用命令行选项设置此参数的值`-XX:InitiatingHeapOccupancyPercent=<NN>`。默认值为`InitiatingHeapOccupancyPercent=45`。

## 暂停时间目标

使用标记为G1设置暂停时间目标 MaxGCPauseMillis。G1使用预测模型来确定在该目标暂停时间内可以完成多少垃圾收集工作。在收集结束时，G1选择要在下一个收集（收集集）中收集的区域。集合集将包含年轻区域（其大小的总和决定逻辑年轻代的大小）。G1部分地通过选择集合集中的年轻区域的数量来控制GC暂停的长度。您可以与其他垃圾回收器一样，在命令行上指定年轻代的大小，但是这样做可能会妨碍G1达到目标暂停时间的能力。除了暂停时间目标之外，您还可以指定可能发生暂停的时间段的长度。您可以在此时间段内指定最小的变数用法（GCPauseIntervalMillis）以及暂停时间目标。默认值为MaxGCPauseMillis200毫秒。GCPauseIntervalMillis（0）的默认值等于时间跨度上没有要求。