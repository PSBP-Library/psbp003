# Program Specification Based Programming

This is lesson 003 of a "Program Specification Based Programming" course.

All comments are welcome at luc.duponcheel[at]gmail.com.

## Introduction

Below the, yet to be defined, declared `trait` members so far are briefly repeated.

```scala
private[psbp] trait Function[>-->[-_, +_], &&[+_, +_]]:

  // external declared

  def functionLift[Z, Y]: (Z => Y) => (Z >--> Y)

  def functionFromTuple2Lift[Z, Y, X]: (Tuple2[Z, Y] => X) => ((Z && Y) >--> X)

  def functionFromTuple3Lift[Z, Y, X, W]: (Tuple3[Z, Y, X] => W) => ((Z && Y && X) >--> W)

  // ...
```

and

```scala
private[psbp] trait IfThenElse[
    >-->[-_, +_]: [>-->[-_, +_]] =>> LocalDefinition[>-->, &&],
    &&[+_, +_]
]:

  // internal declared

  extension [Z, Y](`z>-t->y`: => Z >--> Y)
    private[psbp] def OrElse(`z>-f->y`: => Z >--> Y): (Z && Boolean) >--> Y
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

### Functions and Expressions

Functions are defined using expressions.

- Functions are denotational artifacts: at development time they (are supposed to) have a meaning.
- Expressions are operational artifacts: at runtime they (somehow) are evaluated to yield a result value.

For side effect free expressions it does not matter how they are evaluated as far as the yielded result value is
concerned.

A function application, obtained by replacing the parameter of the function by an argument, is an expression that, when
evaluated to yield a result value, evaluates the correpondingly updated expression defining the function. 

One aspect of top-down expression evaluation is the following: continuing with evaluating an outer expression by binding
the result value yielded by evaluating an inner expression to an expression evaluation continuation yields a expression
that, when evaluating it, yields the result value of the outer expression.

### Programs and Computations

In this course, much in the same way as functions are defined using expressions, specified program concepts are
implemented using specified computation concepts.

Much in the same way as functions resp. expressions:

- Programs are denotational artifacts: at development time they (are supposed to) have a meaning.
- Computations are operational artifacts: at runtime they are executed.

Recall that programs are values of type `Z >--> Y` that are defined in terms of program concepts that are specified in
`trait Program`. 

All implementations of the `trait Program` specification implicitly implement those programs. 

Computations are values of type `C[Y]` that are defined in terms of computation concepts that are specified in
`trait Computation`.

All implementations of the `trait Computation` specification implicitly implement those computations. 

In this course all implicit program implementations are values of type `Z => C[Y]`. 

Below the basic computation concepts are specified.

```scala
package psbp.specification.computation

private[psbp] trait Computation[C[+_]]:

  // internal declared

  private[psbp] def expressionLift[Z]: Z => C[Z]

  extension [Z, Y](`c[z]`: C[Z]) private[psbp] def >=(`z=>c[y]`: => Z => C[Y]): C[Y]
```

In other words, expressions can be lifted to computations and, much in the same way as with top-down expression
evaluation, continuing with executing an outer computation by binding the result value yielded by executing an inner
computation to a computation valued continuation function yields a computation that, when executing it, yields the
result value of the outer computation.

Expressions are language level defined artifacts. 

Computations are library level defined artifacts. 

Computation execution is defined by the implementations of the specified computation concepts.

Computation execution potentially has side effects.

### Partially implementing `Function` in terms of `Computation`

```scala
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
```

### Updating `SequentialComposition` and `Computation`

For technical reasons we always define extension members in terms of declared method members.

Therefore we declare an extra internal method member `sequentialComposition` in `SequentialComposition`

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

and we define an extra internal method member `bind` in `Computation`.

```scala
package psbp.specification.computation

private[psbp] trait Computation[C[+_]]:

  // internal declared

  private[psbp] def expressionLift[Z]: Z => C[Z]

  private[psbp] def bind[Z, Y]: C[Z] => (Z => C[Y]) => C[Y]

  // internal defined

  extension [Z, Y](`c[z]`: C[Z])
    private[psbp] def >=(`z=>c[y]`: => Z => C[Y]): C[Y] = bind(`c[z]`)(`z=>c[y]`)
```

### Implementing `SequentialComposition` in terms of `Computation`

```scala
package psbp.implementation.algorithm

import psbp.specification.computation.{Computation}

private[psbp] trait SequentialComposition[C[+_]: Computation]
    extends psbp.specification.algorithm.SequentialComposition[[Z, Y] =>> Z => C[Y]]:

  // internal defined

  private[psbp] override def sequentialComposition[Z, Y, X](
      `z=>c[y]`: Z => C[Y],
      `y=>c[x]`: => Y => C[X]
  ): Z => C[X] =
    z => `z=>c[y]`(z) >= { y => `y=>c[x]`(y) }
```

It is, in our opinion, sometimes more natural to use `Z >--> Y`, as in `Function`, and sometimes more natural to use
`Z => C[Y]`, as in `SequentialComposition`.

## Conclusion

We have defined some `trait Program` members in terms of `trait Computation` members.














