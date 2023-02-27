package psbp.implementation.algorithm

import psbp.specification.computation.{Computation}

private[psbp] trait SequentialComposition[C[+_]: Computation]
    extends psbp.specification.algorithm.SequentialComposition[[Z, Y] =>> Z => C[Y]]:

  private type >--> = [Z, Y] =>> Z => C[Y]

  private lazy val summonedComputation = summon[Computation[C]]

  import summonedComputation.{bind}

  // internal overridden defined

  private[psbp] override def sequentialComposition[Z, Y, X](
      `z>-->y`: Z >--> Y,
      `y>-->x`: => Y >--> X
  ): Z >--> X =
    z => bind(`z>-->y`(z))(`y>-->x`)
