package tasks.monads

import u04.monads.Monads.Monad
import u04.monads.Monads.Monad

/**
  * Exercise 6: 
    This module contains the implementation of a Try monad, which is a monad that 
    represents a computation that may fail. 
    Try to follow these steps:
    - Look at the implementation of Try, that is similar to the one of Optional
    - Try go define the Monad instance for Try
      - flatMap should consider only the Success case
      - in case of Failure, it should return the exception (fail fast)
    - Verify that the main works as expected
  */
object Ex6TryModel:
  private enum TryImpl[A]:
    case Success(value: A)
    case Failure(exception: Throwable)

  opaque type Try[A] = TryImpl[A]

  def success[A](value: A): Try[A] = TryImpl.Success(value)
  def failure[A](exception: Throwable): Try[A] = TryImpl.Failure(exception)
  def exec[A](expression: => A): Try[A] = try success(expression) catch failure(_)

  extension [A](m: Try[A]) 
    def getOrElse[B >: A](other: B): B = m match
      case TryImpl.Success(value) => value
      case TryImpl.Failure(_) => other

  given Monad[Try] with
    override def unit[A](value: A): Try[A] = success(value)
    extension [A](m: Try[A]) 
      override def flatMap[B](f: A => Try[B]): Try[B] = m match
        case TryImpl.Success(value) => f(value)
        case TryImpl.Failure(exception) => failure(exception)
      
@main def main: Unit = 
  import Ex6TryModel.*

  val result = for 
    a <- success(10)
    b <- success(30)
  yield a + b

  val resultM = 
    success(10).flatMap(a => 
      success(30).map(b => 
        a + b))

  println("1  => " + (result.getOrElse(-1) == 40))
  println("1M => " + (resultM.getOrElse(-1) == 40))

  val result2 = for 
    a <- success(10)
    b <- failure(new RuntimeException("error"))
    c <- success(30)
  yield a + c

  val result2M = 
    success(10).flatMap(a => 
      failure(new RuntimeException("error")).flatMap(b => 
        success(30).map(c => a + c)))
  println("2  => " + (result2.getOrElse(-1) == -1))
  println("2M => " + (result2M.getOrElse(-1) == -1))

  val result3 = for
    a <- exec(10)
    b <- exec(new RuntimeException("error"))
    c <- exec(30)
  yield a + c

  val result3M = 
    exec(10).flatMap(a => 
      exec(new RuntimeException("error")).flatMap(b => 
        exec(30).map(c => a + c)))

  println("3  => " + (result3.getOrElse(-1) == 40))
  println("3M => " + (result3.getOrElse(-1) == 40))

  println("4  => " + (success(20).map(_ + 10).getOrElse(-1) == 30))