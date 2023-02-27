package psbp.implementation.function

import psbp.specification.computation.{Computation}

private[psbp] trait Function[C[+_]: Computation, &&[+_, +_]]
    extends psbp.specification.function.Function[[Z, Y] =>> Z => C[Y], &&]:

  private type >--> = [Z, Y] =>> Z => C[Y]

  private lazy val summonedComputation = summon[Computation[C]]

  import summonedComputation.{expressionLift}

  // external defined

  override def functionLift[Z, Y]: (Z => Y) => (Z >--> Y) = `z=>y` =>
    z => expressionLift(`z=>y`(z))
