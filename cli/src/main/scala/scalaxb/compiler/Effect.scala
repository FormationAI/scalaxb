package scalaxb.compiler

import java.io.Serializable

/** The generated effect type */
sealed abstract class Effect extends Product with Serializable

object Effect {
  /** Generates blocking calls that return `scala.Either` */
  case object Blocking extends Effect
  /** Generates asynchronous calls that return `scala.concurrent.Future` */
  case object Future extends Effect
  /** Generates asynchronous calls that return an `F[_]` with a `cats.effect.Sync` instance */
  case object CatsEffect extends Effect
}
