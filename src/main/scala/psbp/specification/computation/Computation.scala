package psbp.specification.computation

private[psbp] trait Computation[C[+_]]:

  private[psbp] def expressionLift[Z]: Z => C[Z]

  private[psbp] def bind[Z, Y]: C[Z] => (Z => C[Y]) => C[Y]
