# Program Specification Based Programming

This is lesson 003 of a "Program Specification Based Programming" course.

All comments are welcome at luc.duponcheel[at]gmail.com.

## Introduction

Below the yet to be defined `trait` members so far declared are briefly repeated.

```scala
private[psbp] trait Function[>-->[-_, +_], &&[+_, +_]]:

  // external declared

  def functionLift[Z, Y]: (Z => Y) => (Z >--> Y)

  def functionFromTuple2Lift[Z, Y, X]: (Tuple2[Z, Y] => X) => ((Z && Y) >--> X)
```

and

```scala
private[psbp] trait IfThenElse[
    >-->[-_, +_]: [>-->[-_, +_]] =>> LocalDefinition[>-->, &&],
    &&[+_, +_]
]:

  // internal declared

  extension [Z, Y](`z>-t->y`: => Z >--> Y)
    private[psbp] def Or(`z>-f->y`: => Z >--> Y): (Z && Boolean) >--> Y
```

and

```scala
private[psbp] trait SequentialComposition[>-->[-_, +_]]:

  // external declared

  extension [Z, Y, X](`z>-->y`: Z >--> Y) def >-->(`y>-->x`: => Y >--> X): Z >--> X
```

and

```scala
private[psbp] trait Product[>-->[-_, +_], &&[+_, +_]]:

  // external declared

  def `(z&&y)>-->z`[Z, Y]: (Z && Y) >--> Z

  def `(z&&y)>-->y`[Z, Y]: (Z && Y) >--> Y

  extension [Z, Y, X](`z>-->y`: Z >--> Y) def &&(`z>-->x`: => Z >--> X): Z >--> (Y && X)
```

## Content

### Functions resp. Expressions and Programs resp. Computations

Functions are defined using expressions.

- Functions are denotational artifacts: at development time they have a meaning.
- Expressions are operational artifacts: at runtime they are evaluated to yield a result value.

For side effect free expressions it does not matter how the expression is evaluated as far as the yieded result value is
concerned.

Top-down expression evaluation is done as follows: continuing with evaluating an outer expression by binding the result
value yielded by evaluating an inner expression to an expression evaluation continuation yields a expression that, when
evaluating it, yields the result value of the outer expression. 

A function application, obtained by replacing the parameter of the function by an argument, is an expression that, when
evaluated to yield a result value, evaluates the expression defining the function. 

Much in the same way as specific functions are defined using specific expressions, programs are defined using
computations in this course.

Recall that programs are values of type `Z >--> Y` that are defined in terms of program concepts that are specified in
`trait Program`. 

All implementations of the `trait Program` specification implicitly implement those programs. 

In this course all such program implementations are values of type `Z => C[Y]` where values of type `C[Y]` are
computations.

Much in the same way as functions resp. expressions:

- Programs are denotational artifacts: at development time they have a meaning.
- Computations are operational artifacts: at runtime they are executed.

Below the basic computation concepts are specified.

```scala
package psbp.specification

private[psbp] trait Computation[C[+_]]:

  private[psbp] def expressionLift[Z]: Z => C[Z]

  private[psbp] def bind[Z, Y]: C[Z] => (Z => C[Y]) => C[Y]
```

In other words, expressions can be lifted to computations and, just like with evaluating expressions, continuing with
executing an outer computation by binding the result value yielded by executing an inner computation to a computation
valued continuation function yields a computation that, when executing it, yields the result value of the outer
computation. 

### Partially implementating `Function`

```scala
package psbp.implementation.function

import psbp.specification.computation.{Computation}

private[psbp] trait Function[C[+_]: Computation, &&[+_, +_]]
    extends psbp.specification.function.Function[[Z, Y] =>> Z => C[Y], &&]:

  private type >--> = [Z, Y] =>> Z => C[Y]

  private lazy val summonedComputation = summon[Computation[C]]

  import summonedComputation.{expressionLift}

  // external overridden defined

  override def functionLift[Z, Y]: (Z => Y) => (Z >--> Y) = `z=>y` =>
    z => expressionLift(`z=>y`(z))
```

### Updating `SequentialComposition`

For technical reasons we never overriding define declared `extension`'s.

Therefore we declare an extra internal member `sequentialComposition` in `SequentialComposition`.

```scala
package psbp.specification.algorithm

private[psbp] trait SequentialComposition[>-->[-_, +_]]:

  // external defined

  extension [Z, Y, X](`z>-->y`: Z >--> Y)
    def >-->(`y>-->x`: => Y >--> X): Z >--> X = sequentialComposition(`z>-->y`, `y>-->x`)

  // internal declared

  private[psbp] def sequentialComposition[Z, Y, X](
      `z>-->y`: Z >--> Y,
      `y>-->x`: => Y >--> X
  ): Z >--> X
```

### Implementating `SequentialComposition`

```scala
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
```

## Conclusion

We defined some `trait Program`  members in terms of `trait Computation` members.















